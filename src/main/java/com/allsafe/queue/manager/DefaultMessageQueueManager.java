package com.allsafe.queue.manager;

import java.util.Queue;
import java.util.UUID;

import com.allsafe.queue.message.DefaultMessage;
import com.allsafe.queue.model.ContainerParamModel;
import com.google.common.collect.Queues;


/**
 * @name DefaultMessageQueueManager 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 消息队列管理
 * @version 1.0
 */
public class DefaultMessageQueueManager extends AbstractMessageQueueManager {

  /**
   * 构造函数
   * 
   * @param msQueue
   * @param queueContainerModel
   */
  public DefaultMessageQueueManager(Queue<DefaultMessage> msQueue,
      ContainerParamModel queueContainerModel) {
    super(msQueue, queueContainerModel);
  }

  @Override
  public Queue<UUID> initDependencyQueue() {
    return Queues.newArrayBlockingQueue(20);
  }


}
