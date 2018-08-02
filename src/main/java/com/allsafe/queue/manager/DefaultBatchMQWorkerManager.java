package com.allsafe.queue.manager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.allsafe.queue.callback.IMQCallBacker;
import com.allsafe.queue.enums.CallBackStatus;
import com.allsafe.queue.message.DefaultMessage;
import com.allsafe.queue.util.StringUtils;
import com.allsafe.queue.worker.AbstractDefaultWorker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


/**
 * @name DefaultBatchMQWorkerManager 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 批处理消息执行者
 * @version 1.0
 */
public class DefaultBatchMQWorkerManager extends DefaultMQWorkerManager {

  private int batchLimit;

  public DefaultBatchMQWorkerManager(AbstractDefaultWorker worker,
      AbstractMessageQueueManager messageQueueManager, ScheduledThreadPoolExecutor scheduler,
      int limit) {
    super(worker, messageQueueManager, scheduler);
    this.batchLimit = limit;
  }

  @Override
  public void doMain() {
    if (_alive) {
      DefaultMessage message;
      Map<String, List<DefaultMessage>> messageContainer = Maps.newConcurrentMap();
      try {
        while (_alive && (message = _messageQueueManager.getAndRemoveQueueMessage()) != null) {
          if (StringUtils.isNotBlank(message.getMessageName())) {
            List<DefaultMessage> messageLst = messageContainer.get(message.getMessageName());
            if (null == messageLst) {
              messageLst = Lists.newArrayList();
              messageContainer.put(message.getMessageName(), messageLst);
            }
            messageLst.add(message);
            if (messageLst.size() == batchLimit) {
              getWorker(message.getMessageName()).excute(messageLst,
                  new IMQCallBacker<List<DefaultMessage>>() {
                    @Override
                    public void onSuccess(List<DefaultMessage> message) {}

                    @Override
                    public void onFail(CallBackStatus staus, List<DefaultMessage> messageLst) {
                      for (DefaultMessage message : messageLst) {
                        _messageQueueManager.callBack(staus, message);
                      }
                    }
                  });
              for (DefaultMessage _message : messageLst)
                _message.callBack();
              messageLst = Lists.newArrayList();
              messageContainer.put(message.getMessageName(), messageLst);
            }
          }
        }
        for (String name : messageContainer.keySet()) {
          List<DefaultMessage> messageLst = messageContainer.get(name);
          if (null != messageLst && messageLst.size() > 0) {
            getWorker(name).excute(messageLst, new IMQCallBacker<List<DefaultMessage>>() {
              @Override
              public void onSuccess(List<DefaultMessage> messageList) {
                for (DefaultMessage message : messageList) {
                  _messageQueueManager.callBack(CallBackStatus.SUCCESS, message);
                }
              }

              @Override
              public void onFail(CallBackStatus staus, List<DefaultMessage> messageLst) {
                for (DefaultMessage message : messageLst) {
                  _messageQueueManager.callBack(staus, message);
                }
              }
            });
            for (DefaultMessage _message : messageLst)
              _message.callBack();
          }
        }
        messageContainer = null;
      } catch (Exception e) {
        onException(e);
      }
    } else {
      this.cancel();
    }
  }

}
