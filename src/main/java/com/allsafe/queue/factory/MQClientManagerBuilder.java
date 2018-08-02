package com.allsafe.queue.factory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.allsafe.queue.manager.AbstractMessageQueueManager;
import com.allsafe.queue.manager.DefaultMessageQueueObserver;
import com.allsafe.queue.message.IMessage;
import com.allsafe.queue.model.ContainerParamModel;
import com.allsafe.queue.model.ObserverParamModel;
import com.allsafe.queue.model.WorkerParamModel;
import com.allsafe.queue.util.LoggerUtil;
import com.allsafe.queue.worker.AbstractDefaultWorker;


/**
 * @name MQClientManagerBuilder 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 消息队列容器创建
 * @version 1.0
 */
public class MQClientManagerBuilder {

  /**
   * 初始化整个消息处理容器 一个队列对应一个worker 一个observer
   * 
   * @param worker
   */
  @SuppressWarnings("unchecked")
  public static AbstractMessageQueueManager initQueueContainer(String workerTypeName,
      String queueManagerTypeName) {
    try {
      Class<? extends AbstractDefaultWorker> workerType =
          (Class<? extends AbstractDefaultWorker>) Class.forName(workerTypeName);
      Class<? extends AbstractMessageQueueManager> queueManagerType =
          (Class<? extends AbstractMessageQueueManager>) Class.forName(queueManagerTypeName);
      return initQueueContainer(workerType, queueManagerType);
    } catch (Exception e) {
      LoggerUtil.error("初始化队列失败.", e);
      return null;
    }
  }

  /**
   * 初始化整个消息处理容器 一个队列对应一个worker 一个observer
   * 
   * @param worker
   */
  @SuppressWarnings("unchecked")
  public static AbstractMessageQueueManager initQueueContainer(Queue<IMessage> queue,
      ContainerParamModel queueContainerModel, String workerTypeName, String queueManagerTypeName) {
    try {
      Class<? extends AbstractDefaultWorker> workerType =
          (Class<? extends AbstractDefaultWorker>) Class.forName(workerTypeName);
      Class<? extends AbstractMessageQueueManager> queueManagerType =
          (Class<? extends AbstractMessageQueueManager>) Class.forName(queueManagerTypeName);
      return initQueueContainer(queue, queueContainerModel, workerType, queueManagerType);
    } catch (Exception e) {
      LoggerUtil.error("初始化队列失败.", e);
      return null;
    }
  }

  /**
   * 初始化整个消息处理容器 一个队列对应一个worker 一个observer
   * 
   * @param worker
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public static AbstractMessageQueueManager initQueueContainer(
      Class<? extends AbstractDefaultWorker> workerType,
      Class<? extends AbstractMessageQueueManager> queueManagerType) {
    try {
      AbstractDefaultWorker worker = workerType.newInstance();
      Queue<IMessage> queue = new ConcurrentLinkedQueue<IMessage>();
      ContainerParamModel queueContainerModel = new ContainerParamModel();
      return initQueueContainer(queue, worker, queueContainerModel, queueManagerType);
    } catch (Exception e) {
      LoggerUtil.error("初始化队列失败.", e);
      e.printStackTrace();
      return null;
    }
  }

  /**
   * 初始化整个消息处理容器 一个队列对应一个worker 一个observer
   * 
   * @param worker
   */
  public static AbstractMessageQueueManager initQueueContainer(Queue<IMessage> queue,
      ContainerParamModel queueContainerModel, Class<? extends AbstractDefaultWorker> workerType,
      Class<? extends AbstractMessageQueueManager> queueManagerType) {
    try {
      AbstractDefaultWorker worker = workerType.newInstance();
      return initQueueContainer(queue, worker, queueContainerModel, queueManagerType);
    } catch (Exception e) {
      LoggerUtil.error("初始化队列失败.", e);
      return null;
    }
  }

  /**
   * 初始化整个消息处理容器 一个队列对应一个worker 一个observer
   * 
   * @param worker
   */
  public static AbstractMessageQueueManager initQueueContainer(AbstractDefaultWorker worker,
      Class<? extends AbstractMessageQueueManager> queueManagerType) {
    Queue<IMessage> queue = new ConcurrentLinkedQueue<IMessage>();
    ContainerParamModel queueContainerModel = new ContainerParamModel();
    return initQueueContainer(queue, worker, queueContainerModel, queueManagerType);
  }

  /**
   * 初始化整个消息处理容器 一个队列对应一个worker 一个observer
   * 
   * @param queue
   * @param worker
   * @param queueContainerModel
   */
  public static AbstractMessageQueueManager initQueueContainer(Queue<IMessage> queue,
      AbstractDefaultWorker worker, ContainerParamModel queueContainerModel,
      Class<? extends AbstractMessageQueueManager> queueManagerType) {

    if (null == worker) {
      LoggerUtil.error("创建消息队列容器失败，worker传入为空", null);
      return null;
    }

    // 默认队列为ConcurrentLinkedQueue
    if (null == queue) {
      queue = new ConcurrentLinkedQueue<IMessage>();
    }

    // 使用默认参数构造queueContainerModel
    if (null == queueContainerModel) {
      queueContainerModel = new ContainerParamModel();
    }

    // 创建队列容器，容器中包含一套queue、observer、worker
    return createQueueContainer(queue, worker, queueContainerModel, queueManagerType);

  }

  /**
   * 创建容器
   * 
   * @param queue
   * @param worker
   * @param queueContainerModel
   */
  private static AbstractMessageQueueManager createQueueContainer(Queue<IMessage> queue,
      AbstractDefaultWorker worker, ContainerParamModel queueContainerModel,
      Class<? extends AbstractMessageQueueManager> queueManagerType) {

    AbstractMessageQueueManager messageQueueManager = null;
    try {
      // 1、队列容器需要的参数初始化
      if (null == queueContainerModel) {
        queueContainerModel = new ContainerParamModel();
      }

      // 1、构造MessageQueueManager
      messageQueueManager =
          queueManagerType.getConstructor(Queue.class, ContainerParamModel.class).newInstance(
              queue, queueContainerModel);

      // 2、构造观察者，并且执行work
      // 构造执行者参数模型
      WorkerParamModel queueWorkerParamModel =
          createQueueWorkerParamModel(queueContainerModel, worker);
      // 构造观察者参数模型
      ObserverParamModel queueObserverParamModel =
          createQueueObserverParamModel(queueContainerModel);
      DefaultMessageQueueObserver.start(queueObserverParamModel, queueWorkerParamModel,
          messageQueueManager);

    } catch (Exception e) {
      LoggerUtil.error("创建队列容器失败.", e);
    }
    return messageQueueManager;
  }

  /**
   * 构造执行者参数模型
   * 
   * @param queueContainerModel
   * @param worker
   * @return
   */
  private static WorkerParamModel createQueueWorkerParamModel(
      ContainerParamModel queueContainerModel, AbstractDefaultWorker worker) {
    WorkerParamModel queueWorkerParamModel = new WorkerParamModel();
    queueWorkerParamModel.setMessageHandlerPath(queueContainerModel.getScanPath());
    queueWorkerParamModel.setWorkerRateTime(queueContainerModel.getWorkerRateTime());
    queueWorkerParamModel.setWorkerCount(queueContainerModel.getWorkerCount());
    queueWorkerParamModel.setWoker(worker);
    return queueWorkerParamModel;
  }

  /**
   * 构造观察者参数模型
   * 
   * @param queueContainerModel
   * @return
   */
  private static ObserverParamModel createQueueObserverParamModel(
      ContainerParamModel queueContainerModel) {
    ObserverParamModel queueObserverParamModel = new ObserverParamModel();
    queueObserverParamModel.setMode(queueContainerModel.getWorkerMode());
    queueObserverParamModel.setObRateTime(queueContainerModel.getObserverRateTime());
    queueObserverParamModel.setBatchLimit(queueContainerModel.getWorkerbBatchLimit());
    return queueObserverParamModel;
  }

}
