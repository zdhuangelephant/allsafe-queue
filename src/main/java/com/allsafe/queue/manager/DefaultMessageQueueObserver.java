package com.allsafe.queue.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.allsafe.queue.annotation.HandlerMessage;
import com.allsafe.queue.callback.IMQCallBacker;
import com.allsafe.queue.contants.QueueConstants;
import com.allsafe.queue.enums.MessageType;
import com.allsafe.queue.message.DefaultMessage;
import com.allsafe.queue.model.ObserverParamModel;
import com.allsafe.queue.model.WorkerParamModel;
import com.allsafe.queue.schedule.SummerScheduledTask;
import com.allsafe.queue.schedule.SummerScheduledThreadPoolExecutor;
import com.allsafe.queue.schedule.SummerTaskExecutor.SummerThreadFactoryBuilder;
import com.allsafe.queue.util.LoggerUtil;
import com.allsafe.queue.util.QueueUtil;
import com.allsafe.queue.util.ScanPath;
import com.allsafe.queue.util.StringUtils;
import com.allsafe.queue.worker.AbstractDefaultWorker;
import com.allsafe.queue.worker.IWorker;
import com.allsafe.queue.worker.WorkerFactory;
import com.google.common.collect.Maps;


/**
 * @name DefaultMessageQueueObserver 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 消息队列观察者
 * @version 1.0
 */
public class DefaultMessageQueueObserver extends SummerScheduledTask {

  SummerThreadFactoryBuilder _summerThreadFactoryBuilder = new SummerThreadFactoryBuilder();
  {
    _summerThreadFactoryBuilder.setNameFormat("xd-queue-default");
    _summerThreadFactoryBuilder.setDaemon(false);
  }

  ScheduledThreadPoolExecutor _scheduExec;

  /** 观察者相关参数模型 */
  ObserverParamModel _queueObserverParamModel;

  /** 执行者相关参数模型 */
  WorkerParamModel _queueWorkerParamModel;

  /** 队列管理器 */
  AbstractMessageQueueManager _messageQueueManager;

  /** worker list */
  List<DefaultMQWorkerManager> _workerList = new Vector<DefaultMQWorkerManager>();

  static final long DELAY = 2000;

  /** DEFAULT_SCAN_PATH 默认扫描路径 */
  static final String DEFAULT_SCAN_PATH = "com.xiaodou";

  /** worker初始化实例对象 */
  AbstractDefaultWorker _worker;

  /** _messageHandler 指定消息监听worker */
  MultipleWorkHandler _messageHandler = new MultipleWorkHandler();

  /**
   * 构造函数，观察者和执行者的基本参数设定
   * 
   * @param queueObserverParamModel
   * @param queueWorkerParamModel
   * @param messageQueueManager
   */
  public DefaultMessageQueueObserver(ObserverParamModel queueObserverParamModel,
      WorkerParamModel queueWorkerParamModel, AbstractMessageQueueManager messageQueueManager) {
    this._queueObserverParamModel = queueObserverParamModel;
    this._queueWorkerParamModel = queueWorkerParamModel;
    this._messageQueueManager = messageQueueManager;
    this._worker = queueWorkerParamModel.getWoker();
    new ScanPath(StringUtils.isBlank(queueWorkerParamModel.getMessageHandlerPath())
        ? DEFAULT_SCAN_PATH
        : queueWorkerParamModel.getMessageHandlerPath()) {
      @Override
      protected void processClass(Class<?> clazz) {
        if (clazz.getAnnotation(HandlerMessage.class) != null
            && QueueUtil.getSuperSet(clazz).contains(IWorker.class)) {
          HandlerMessage handlerMessage = clazz.getAnnotation(HandlerMessage.class);
          String messageName = handlerMessage.value();
          MessageType type = handlerMessage.type();
          if (MessageType.Single.equals(type) && _messageHandler.containsKey(messageName)) {
            LoggerUtil.error(
                "重复监听消息." + clazz.getName() + "与"
                    + _messageHandler.getFirst(messageName).getClass().getName(),
                new RuntimeException());
            return;
          }
          try {
            _messageHandler.registWorker(messageName, (AbstractDefaultWorker) clazz.newInstance());
          } catch (InstantiationException | IllegalAccessException e) {
            LoggerUtil.error("初始化work发生异常." + clazz.getName(), e);
          }
        }
      }
    };
    _scheduExec =
        new SummerScheduledThreadPoolExecutor(queueWorkerParamModel.getWorkerCount(),
            _summerThreadFactoryBuilder.build());
    createCoreWorkerList(queueWorkerParamModel);
    _scheduExec.scheduleWithFixedDelay(this, DELAY, queueObserverParamModel.getObRateTime(),
        TimeUnit.MILLISECONDS);
  }

  /**
   * 队列观察者启动
   * 
   * @param queueObserverParamModel
   * @param messageQueueManager
   */
  public static void start(ObserverParamModel queueObserverParamModel,
      WorkerParamModel queueWorkerParamModel, AbstractMessageQueueManager messageQueueManager) {
    new DefaultMessageQueueObserver(queueObserverParamModel, queueWorkerParamModel,
        messageQueueManager);
  }

  /**
   * 构造核心worker
   */
  private void createCoreWorkerList(WorkerParamModel queueWorkerParamModel) {
    int count = 0;
    while (_workerList.size() < queueWorkerParamModel.getWorkerCount()) {
      if (count > 2 * queueWorkerParamModel.getWorkerCount()) {
        LoggerUtil.error("系统初始化核心worker出现异常！！", null);
        break;
      }
      try {
        _workerList.add(createWorker());
      } catch (Exception e) {
        LoggerUtil.error("创建某个核心worker异常！！", e);
        count++;
        continue;
      }
    }
  }

  /**
   * 创建worker 并执行
   * 
   * @return
   */
  private DefaultMQWorkerManager createWorker() {
    AbstractDefaultWorker worker = WorkerFactory.createWork(_worker);
    if (null == worker) {
      return null;
    }
    DefaultMQWorkerManager workManager = null;
    // 注册指定模式的 worker manager
    if (_queueObserverParamModel.getMode() == QueueConstants.NORMAL_MODE)
      workManager = new DefaultMQWorkerManager(worker, _messageQueueManager, _scheduExec);
    if (_queueObserverParamModel.getMode() == QueueConstants.BATCH_MODE)
      workManager =
          new DefaultBatchMQWorkerManager(worker, _messageQueueManager, _scheduExec,
              _queueObserverParamModel.getBatchLimit());
    // 注册消息监听者
    if (null != _messageHandler && _messageHandler.size() > 0) {
      workManager.set_messageHandler(WorkerFactory.createWork(_messageHandler));
    }
    // 启动worker
    _scheduExec.scheduleWithFixedDelay(workManager, DELAY,
        _queueWorkerParamModel.getWorkerRateTime(), TimeUnit.MILLISECONDS);
    return workManager;
  }

  @Override
  public void onException(Throwable t) {
    LoggerUtil.error("消息观察者发生异常", t);
  }

  @Override
  public void doMain() {
    // TODO 監控待完善
  }

  /**
   * @name MultipleWorkHandler
   * @CopyRright (c) 2017 by zhaodan
   * 
   * @author <a href="mailto:zhaodan@corp.51xiaodou.com">zhaodan</a>
   * @date 2017年4月1日
   * @description 多重消费者管理器
   * @version 1.0
   */
  public static class MultipleWorkHandler {
    private Map<String, MultipleWorker> messageHandler = Maps.newConcurrentMap();

    public void registWorker(String messageName, AbstractDefaultWorker worker) {
      if (messageHandler.containsKey(messageName)) {
        messageHandler.get(messageName).add(worker);
      } else {
        MultipleWorker workList = new MultipleWorker();
        messageHandler.put(messageName, workList);
        workList.add(worker);
      }
    }

    public int size() {
      return messageHandler.size();
    }

    public AbstractDefaultWorker getFirst(String messageName) {
      MultipleWorker list = messageHandler.get(messageName);
      return list != null && list.size() > 0 ? list.get(0) : null;
    }

    public MultipleWorker get(String messageName) {
      return messageHandler.get(messageName);
    }

    public boolean containsKey(String messageName) {
      return messageHandler.containsKey(messageName);
    }

    public Set<Map.Entry<String, MultipleWorker>> entrySet() {
      return messageHandler.entrySet();
    }
  }

  public static class MultipleWorker extends ArrayList<AbstractDefaultWorker> {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    public MultipleWorker() {}

    public MultipleWorker(AbstractDefaultWorker worker) {
      add(worker);
    }

    public void excute(DefaultMessage message, IMQCallBacker<DefaultMessage> imqCallBacker) {
      if (size() == 0) return;
      for (AbstractDefaultWorker worker : this)
        worker.excute(message, imqCallBacker);
    }

    public void excute(List<DefaultMessage> messageLst,
        IMQCallBacker<List<DefaultMessage>> imqCallBacker) {
      if (size() == 0) return;
      for (AbstractDefaultWorker worker : this)
        worker.excute(messageLst, imqCallBacker);
    }
  }

}
