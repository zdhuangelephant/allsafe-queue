package com.allsafe.queue.message;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.allsafe.queue.enums.CallBackStatus;
import com.allsafe.queue.manager.IMQClientManager;
import com.allsafe.queue.util.LoggerUtil;
import com.allsafe.queue.util.QueueMessage;
import com.google.common.collect.Sets;


/**
 * @name DefaultMessage 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 默认任务载体
 * @version 1.0
 */
public class DefaultMessage implements IMessage {
  @JSONField(serialize = false, deserialize = false)
  private static final int INIT = 0, PUSHED = 1;
  @JSONField(serialize = false, deserialize = false)
  private IMQClientManager mqClientManager;
  private Set<UUID> upperSet = Sets.newConcurrentHashSet();
  private Set<DefaultMessage> followedSet = Sets.newConcurrentHashSet();
  private UUID messageId;
  private String messageName;
  private AtomicInteger state = new AtomicInteger(INIT);
  private AtomicInteger failedCount = new AtomicInteger(0);
  private AtomicInteger processCount = new AtomicInteger(0);
  private AtomicInteger deadLetterCount = new AtomicInteger(Integer.MAX_VALUE);
  private Date sendTime;
  private Date receiveTime;
  private String sendFromClass;
  private String messageBodyTypeName;
  private String messageBodyJson;
  private String sendServerIP;
  private String sendServerName;
  private boolean saved;
  private String routeKey;
  private Map<String, Boolean> consumerExcutedResultDic;
  private String traceId;
  private String myId;
  public String getMessageBodyTypeName() {
    return messageBodyTypeName;
  }

  public void setMessageBodyTypeName(String messageBodyTypeName) {
    this.messageBodyTypeName = messageBodyTypeName;
  }

  public String getMessageBodyJson() {
    return messageBodyJson;
  }

  public void setMessageBodyJson(String messageBodyJson) {
    this.messageBodyJson = messageBodyJson;
  }

  public UUID getMessageId() {
    return messageId;
  }

  public String getMessageName() {
    return messageName;
  }

  public int getFailedCount() {
    return failedCount.get();
  }

  public void setFailedCount(int failedCount) {
    this.failedCount = new AtomicInteger(failedCount);
  }

  public void addFailedCount() {
    this.failedCount.incrementAndGet();
  }

  public int getProcessCount() {
    return processCount.get();
  }

  public void setProcessCount(int processCount) {
    this.processCount = new AtomicInteger(processCount);
  }

  public void addProcessCount() {
    this.processCount.incrementAndGet();
  }

  public int getDeadLetterCount() {
    return deadLetterCount.get();
  }

  public void setDeadLetterCount(int deadLetterCount) {
    this.deadLetterCount = new AtomicInteger(deadLetterCount);
  }

  public void addDeadLetterCount() {
    this.deadLetterCount.incrementAndGet();
  }

  public Date getSendTime() {
    return sendTime;
  }

  public void setSendTime(Date sendTime) {
    this.sendTime = sendTime;
  }

  public Date getReceiveTime() {
    return receiveTime;
  }

  public void setReceiveTime(Date receiveTime) {
    this.receiveTime = receiveTime;
  }

  public String getSendFromClass() {
    return sendFromClass;
  }

  public void setSendFromClass(String sendFromClass) {
    this.sendFromClass = sendFromClass;
  }

  public String getSendServerIP() {
    return sendServerIP;
  }

  public void setSendServerIP(String sendServerIP) {
    this.sendServerIP = sendServerIP;
  }

  public String getSendServerName() {
    return sendServerName;
  }

  public void setSendServerName(String sendServerName) {
    this.sendServerName = sendServerName;
  }

  public boolean isSaved() {
    return saved;
  }

  public void setSaved(boolean saved) {
    this.saved = saved;
  }

  public String getRouteKey() {
    return routeKey;
  }

  public void setRouteKey(String routeKey) {
    this.routeKey = routeKey;
  }

  public Map<String, Boolean> getConsumerExcutedResultDic() {
    return consumerExcutedResultDic;
  }

  public void setConsumerExcutedResultDic(Map<String, Boolean> consumerExcutedResultDic) {
    this.consumerExcutedResultDic = consumerExcutedResultDic;
  }

  public void setMessageId(UUID messageId) {
    this.messageId = messageId;
  }

  public void setMessageName(String messageName) {
    this.messageName = messageName;
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }

  public void callBack() {
    if (null != followedSet && followedSet.size() > 0) for (DefaultMessage folower : followedSet)
      folower.notifyUpperOver(this.messageId);
  }

  private void notifyUpperOver(UUID messageId) {
    if (null != messageId && null != upperSet) upperSet.remove(messageId);
    push();
  }

  public void push() {
    synchronized (this) {
      if (upperSet.size() == 0 && state.compareAndSet(INIT, PUSHED)) {
        mqClientManager.addMessage(this);
        // _recordMessage0(this);
      }
    }
  }

  protected void _recordMessage0(DefaultMessage message) {
    QueueMessage entity = new QueueMessage(CallBackStatus.INIT, message);
    LoggerUtil.messageInfo(entity);
  }

  public IMQClientManager getMqClientManager() {
    return mqClientManager;
  }

  public void setMqClientManager(IMQClientManager mqClientManager) {
    this.mqClientManager = mqClientManager;
  }

  public void addUpper(UUID upperId) {
    this.upperSet.add(upperId);
  }

  public void addFollower(DefaultMessage follower) {
    this.followedSet.add(follower);
  }

public String getTraceId() {
	return traceId;
}

public void setTraceId(String traceId) {
	this.traceId = traceId;
}

public String getMyId() {
	return myId;
}

public void setMyId(String myId) {
	this.myId = myId;
}
}
