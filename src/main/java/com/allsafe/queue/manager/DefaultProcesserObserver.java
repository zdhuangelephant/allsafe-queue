package com.allsafe.queue.manager;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.allsafe.queue.annotation.ProcesserHandler;
import com.allsafe.queue.contants.QueueConstants;
import com.allsafe.queue.enums.WeightEnum;
import com.allsafe.queue.model.ContainerParamModel;
import com.allsafe.queue.model.ObserverParamModel;
import com.allsafe.queue.model.ProcesserParamModel;
import com.allsafe.queue.model.ProcesserParamModel.Processer;
import com.allsafe.queue.processer.IProcesser;
import com.allsafe.queue.processer.ProcesserFactory;
import com.allsafe.queue.schedule.SummerScheduledTask;
import com.allsafe.queue.schedule.SummerScheduledThreadPoolExecutor;
import com.allsafe.queue.schedule.SummerTaskExecutor.SummerThreadFactoryBuilder;
import com.allsafe.queue.util.LoggerUtil;
import com.allsafe.queue.util.QueueUtil;
import com.allsafe.queue.util.ScanPath;
import com.allsafe.queue.util.StringUtils;


/**
 * @name DefaultProcesserObserver 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 拉取消息处理调度者
 * @version 1.0
 */
public class DefaultProcesserObserver extends SummerScheduledTask {

  SummerThreadFactoryBuilder _summerThreadFactoryBuilder = new SummerThreadFactoryBuilder();
  {
    _summerThreadFactoryBuilder.setNameFormat("allsafe-queue-default");
    _summerThreadFactoryBuilder.setDaemon(false);
  }

  ScheduledThreadPoolExecutor _scheduExec = new SummerScheduledThreadPoolExecutor(
      DEFAULT_PROCESSER_COUNT, _summerThreadFactoryBuilder.build());

  /** 观察者相关参数模型 */
  ObserverParamModel _observerParamModel;

  /** 执行者相关参数模型 */
  ProcesserParamModel _processerParamModel;

  /** processer list */
  List<DefaultProcesserManager> _processerList = new Vector<DefaultProcesserManager>();

  /** DEFAULT_WORKER_COUNT processerlist数量 */
  static final int DEFAULT_PROCESSER_COUNT = Runtime.getRuntime().availableProcessors() * 2;

  /** DEFAULT_SCAN_PATH 默认扫描路径 */
  static final String DEFAULT_SCAN_PATH = "com.xiaodou";

  static final long DELAY = 2000;

  /**
   * 构造函数，观察者和执行者的基本参数设定
   * 
   * @param queueObserverParamModel
   * @param queueWorkerParamModel
   * @param messageQueueManager
   */
  private DefaultProcesserObserver(ObserverParamModel observerParamModel,
      ProcesserParamModel processerParamModel) {
    this._observerParamModel = observerParamModel;
    this._processerParamModel = processerParamModel;
    if (null == this._observerParamModel)
      this._observerParamModel = createQueueObserverParamModel();
    if (null == this._processerParamModel) this._processerParamModel = new ProcesserParamModel();
    new ScanPath(StringUtils.isBlank(_processerParamModel.getProcesserPath())
        ? DEFAULT_SCAN_PATH
        : _processerParamModel.getProcesserPath()) {
      @Override
      protected void processClass(Class<?> clazz) {
        if (clazz.getAnnotation(ProcesserHandler.class) != null
            && QueueUtil.getSuperSet(clazz).contains(IProcesser.class)) {
          ProcesserHandler annotation = clazz.getAnnotation(ProcesserHandler.class);
          WeightEnum weight = annotation.weight();
          try {
            IProcesser newInstance = (IProcesser) clazz.newInstance();
            newInstance.initialize(annotation);
            _processerParamModel.addProcesser(newInstance, weight);
          } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            LoggerUtil.error("初始化processer发生异常." + clazz.getName(), e);
          }
        }
      }
    };
    createCoreProcesserList();
    _scheduExec.scheduleWithFixedDelay(this, DELAY, observerParamModel.getObRateTime(),
        TimeUnit.MILLISECONDS);
  }

  /**
   * 队列观察者启动
   * 
   * @param queueObserverParamModel
   * @param messageQueueManager
   */
  public static void start(ObserverParamModel observerParamModel,
      ProcesserParamModel processerParamModel) {
    new DefaultProcesserObserver(observerParamModel, processerParamModel);
  }

  /**
   * 队列观察者启动
   * 
   * @param queueObserverParamModel
   * @param messageQueueManager
   */
  public static void start(ProcesserParamModel processerParamModel) {
    new DefaultProcesserObserver(createQueueObserverParamModel(), processerParamModel);
  }

  public static void startBatch(ProcesserParamModel processerParamModel) {
    ObserverParamModel createQueueObserverParamModel = createQueueObserverParamModel();
    createQueueObserverParamModel.setMode(2);
    new DefaultProcesserObserver(createQueueObserverParamModel, processerParamModel);
  }

  /**
   * 队列观察者启动
   * 
   * @param queueObserverParamModel
   * @param messageQueueManager
   */
  public static void start() {
    ObserverParamModel createQueueObserverParamModel = createQueueObserverParamModel();
    new DefaultProcesserObserver(createQueueObserverParamModel, new ProcesserParamModel());
  }

  public static void startBatch() {
    ObserverParamModel createQueueObserverParamModel = createQueueObserverParamModel();
    createQueueObserverParamModel.setMode(2);
    new DefaultProcesserObserver(createQueueObserverParamModel, new ProcesserParamModel());
  }

  /**
   * 构造观察者参数模型
   * 
   * @param queueContainerModel
   * @return
   */
  private static ObserverParamModel createQueueObserverParamModel() {
    ContainerParamModel queueContainerModel = new ContainerParamModel();
    ObserverParamModel queueObserverParamModel = new ObserverParamModel();
    queueObserverParamModel.setMode(queueContainerModel.getWorkerMode());
    queueObserverParamModel.setObRateTime(queueContainerModel.getObserverRateTime());
    queueObserverParamModel.setBatchLimit(queueContainerModel.getWorkerbBatchLimit());
    return queueObserverParamModel;
  }

  /**
   * 构造核心processer
   */
  private void createCoreProcesserList() {
    int totalCount = 0;
    for (int i = 0; i < _processerParamModel.getProcesserList().size()
        && _processerList.size() < DEFAULT_PROCESSER_COUNT * 2; i++) {
      Processer processerWrapper = _processerParamModel.getProcesserList().get(i);
      Integer count =
          (processerWrapper.getWeight() * DEFAULT_PROCESSER_COUNT)
              / _processerParamModel.getTotalWeight();
      for (int j = 0; j < count; j++) {
        if (totalCount > 2 * DEFAULT_PROCESSER_COUNT) {
          LoggerUtil.error("系统初始化核心processer出现异常！！", null);
          break;
        }
        try {
          _processerList.add(createWorker(processerWrapper));
        } catch (Exception e) {
          LoggerUtil.error("创建某个核心processer异常！！", e);
          totalCount++;
          continue;
        }
      }
    }
  }

  /**
   * 创建processer 并执行
   * 
   * @param processerWrapper
   * 
   * @return
   */
  private DefaultProcesserManager createWorker(Processer processerWrapper) {
    IProcesser processer = ProcesserFactory.createProcesser(processerWrapper.getProcesser());
    if (null == processer) {
      return null;
    }
    DefaultProcesserManager processerManager = null;
    // 注册指定模式的 processer manager
    if (_observerParamModel.getMode() == QueueConstants.NORMAL_MODE)
      processerManager = new DefaultProcesserManager(processer, _scheduExec);
    if (_observerParamModel.getMode() == QueueConstants.BATCH_MODE)
      processerManager = new DefaultBatchProcesserManager(processer, _scheduExec);
    // 启动processer
    _scheduExec.scheduleWithFixedDelay(processerManager, DELAY,
        _processerParamModel.getProcesserRateTime(), TimeUnit.MILLISECONDS);
    return processerManager;
  }

  @Override
  public void onException(Throwable t) {
    LoggerUtil.error("拉取消息处理调度者", t);
  }

  @Override
  public void doMain() {
    // TODO 監控待完善
  }

}
