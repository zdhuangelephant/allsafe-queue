package com.allsafe.queue.util;

import com.alibaba.fastjson.JSON;
import com.allsafe.queue.enums.CallBackStatus;
import com.allsafe.queue.message.DefaultMessage;

public class QueueMessage extends MessageEntity<DefaultMessage> {

  public QueueMessage(CallBackStatus status, DefaultMessage pojo) {
    super(pojo, pojo.getMessageId().toString(), pojo.getMessageName());
    setResult(status.toString());
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }

}
