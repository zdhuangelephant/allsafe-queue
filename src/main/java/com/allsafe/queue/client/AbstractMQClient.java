package com.allsafe.queue.client;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.allsafe.queue.factory.MQClientManagerBuilder;
import com.allsafe.queue.manager.AbstractMessageQueueManager;
import com.allsafe.queue.manager.IMQClientManager;
import com.allsafe.queue.message.DefaultMessage;
import com.allsafe.queue.message.IMessage;
import com.allsafe.queue.model.ContainerParamModel;
import com.allsafe.queue.util.Base64Utils;
import com.allsafe.queue.util.CommUtils;
import com.allsafe.queue.util.StringUtils;
import com.allsafe.queue.worker.AbstractDefaultWorker;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;


/**
 * @name AbstractMQClient 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 抽象MQ客户端:定义基础流程
 * @version 1.0
 */
public class AbstractMQClient implements IMQClient {

  public AbstractMQClient(Class<? extends AbstractDefaultWorker> workerType,
      Class<? extends AbstractMessageQueueManager> queueManagerType) {
    this(null, null, workerType, queueManagerType);
  }

  public AbstractMQClient(Queue<IMessage> queue, ContainerParamModel queueContainerModel,
      Class<? extends AbstractDefaultWorker> workerType,
      Class<? extends AbstractMessageQueueManager> queueManagerType) {
    IMQClientManager server =
        MQClientManagerBuilder
            .initQueueContainer(queue, queueContainerModel, workerType, queueManagerType);
    this.server = server;
  }

  private static final String CLASS_NAME = AbstractMQClient.class.getSimpleName();

  /** server 服务端 */
  private IMQClientManager server;

  public IMQClientManager getServer() {
    return server;
  }

  public void setServer(IMQClientManager server) {
    this.server = server;
  }

  @Override
  public UUID sendMessage(String messageName, Object messageBody) {
    return _sendMessage(messageName, messageBody);
  }


  private UUID _sendMessage(String messageName, Object messageBody) {
    DefaultMessage _message = createMessage(messageBody);
    if (_message == null) return null;
    if (StringUtils.isNotBlank(messageName)) _message.setMessageName(messageName);
    _message.push();
    return _message.getMessageId();
  }

  public void sendMessage(MessageBox messageBox) {
    if (null == messageBox || null == messageBox.messageBox) return;
    List<Integer> keys = Lists.newArrayList(messageBox.messageBox.keySet());
    Collections.sort(keys);
    Set<DefaultMessage> dependencyMessageSet = Sets.newHashSet();
    Set<DefaultMessage> firstLevel = Sets.newHashSet();
    for (Integer key : keys) {
      Set<DefaultMessage> currentMessageSet = Sets.newHashSet();
      Set<Message> messageSet = messageBox.messageBox.get(key);
      if (null == messageSet || messageSet.size() == 0) {
        continue;
      }
      for (Message message : messageSet) {
        DefaultMessage dMessage = createMessage(message.message);
//        dMessage.setTraceId(TraceWrapper.getWrapper().getTraceParam().getTraceId());
//        dMessage.setMyId(TraceWrapper.getWrapper().getTraceParam().getMyId());
        if (StringUtils.isNotBlank(message.messageName))
          dMessage.setMessageName(message.messageName);
        if (null == dependencyMessageSet || dependencyMessageSet.size() == 0) {
          firstLevel.add(dMessage);
        } else {
          for (DefaultMessage pMessage : dependencyMessageSet) {
            pMessage.addFollower(dMessage);
            dMessage.addUpper(pMessage.getMessageId());
          }
        }
        currentMessageSet.add(dMessage);
      }
      dependencyMessageSet = currentMessageSet;
    }
    for (DefaultMessage message : firstLevel) {
      message.push();
    }
  }

  /**
   * 构造消息对象
   * 
   * @param obj 待传递消息实体类
   * @return
   */
  protected DefaultMessage createMessage(Object obj) {
    DefaultMessage msg;
    if (obj == null) return null;
    if (obj instanceof DefaultMessage) {
      msg = (DefaultMessage) obj;
      if (msg.getMessageBodyJson() == null) {
        return null;
      }
      if (msg.getMessageBodyTypeName() == null) {
        msg.setMessageBodyTypeName("unset");
      }
      msg.setSaved(false);
    } else {
      msg = new DefaultMessage();

      msg.setDeadLetterCount(0);
      msg.setFailedCount(0);
      msg.setProcessCount(0);

      msg.setSaved(false);
      if (obj instanceof String)
        msg.setMessageBodyJson((String) obj);
      else if (obj instanceof Integer || obj instanceof Short || obj instanceof Character
          || obj instanceof Long || obj instanceof Double || obj instanceof Float)
        msg.setMessageBodyJson(obj.toString());
      else if (obj instanceof byte[])
        msg.setMessageBodyJson(Base64Utils.encode((byte[]) obj));
      else if (obj instanceof Byte)
        msg.setMessageBodyJson(Base64Utils.encode(new byte[] {(Byte) obj}));
      else
        msg.setMessageBodyJson(JSON.toJSONString(obj));
      msg.setMessageBodyTypeName(obj.getClass().getName());
    }
    if (null == msg.getMessageId()) {
      msg.setMessageId(UUID.randomUUID());
    }
    if (null == msg.getMqClientManager()) {
      msg.setMqClientManager(server);
    }
    if (null == msg.getSendTime()) {
      msg.setSendTime(new Date());
    }
    if (null == msg.getSendServerIP()) {
      msg.setSendServerIP(CommUtils.getServerIp());
    }
    if (null == msg.getSendServerName()) {
      msg.setSendServerName(CommUtils.getServerName());
    }
    if (null == msg.getSendFromClass()) {
      StackTraceElement[] stackTrace = (new Throwable()).getStackTrace();
      for (int i = 0; i < stackTrace.length; i++) {
        String stackClassName = stackTrace[i].getClassName();
        if (stackClassName != this.getClass().getSimpleName()) {
          msg.setSendFromClass(stackClassName);
          break;
        }
        msg.setSendFromClass(CLASS_NAME);
      }
    }
//    if(null!=TraceWrapper.getWrapper()&&null!=TraceWrapper.getWrapper().getTraceParam()){
//        msg.setTraceId(TraceWrapper.getWrapper().getTraceParam().getTraceId());
//        msg.setMyId(TraceWrapper.getWrapper().getTraceParam().getMyId());
//    }
    return msg;
  }


   /**
 * @name AbstractMQClient 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 消息盒子
 * @version 1.0
 */
  public static class MessageBox {

    /** FIRST_LEVEL 第一层级 */
    public static final Integer FIRST_LEVEL = 100;
    /** SECOND_LEVEL 第二层级 */
    public static final Integer SECOND_LEVEL = 110;
    /** THIRD_LEVEL 第三层级 */
    public static final Integer THIRD_LEVEL = 120;
    /** FOURTH_LEVEL 第四层级 */
    public static final Integer FOURTH_LEVEL = 130;
    /** FIFTH_LEVEL 第五层级 */
    public static final Integer FIFTH_LEVEL = 140;
    /** SIXTH_LEVEL 第六层级 */
    public static final Integer SIXTH_LEVEL = 150;
    /** SEVENTH_LEVEL 第七层级 */
    public static final Integer SEVENTH_LEVEL = 160;
    /** EIGHTH_LEVEL 第八层级 */
    public static final Integer EIGHTH_LEVEL = 170;
    /** NINETH_LEVEL 第九层级 */
    public static final Integer NINETH_LEVEL = 180;
    /** TENTH_LEVEL 第十层级 */
    public static final Integer TENTH_LEVEL = 190;

    private static final Integer STEPTH = 10;

    private Object _lock = new Object();

    private Integer currentLevel = FIRST_LEVEL;

    private Map<Integer, Set<Message>> messageBox = Maps.newHashMap();

    public void addCurrentLevelMessage(String messageName, Object message) {
      synchronized (_lock) {
        Set<Message> currentLevelSet = messageBox.get(currentLevel);
        if (null == currentLevelSet) {
          currentLevelSet = messageBox.get(currentLevel);
          if (null == currentLevelSet) currentLevelSet = Sets.newHashSet();
          messageBox.put(currentLevel, currentLevelSet);
        }
        currentLevelSet.add(new Message(messageName, message));
      }
    }

    public void addPreLevelMessage(String messageName, Object message) {
      synchronized (_lock) {
        if (this.currentLevel - STEPTH <= FIRST_LEVEL) {
          throw new IllegalArgumentException(
              String
                  .format(
                      "MessageBox:addPreLevelMessage fail, targetLevel should limit between %s and %s, Illegal arg %s",
                      FIRST_LEVEL, Integer.MAX_VALUE, this.currentLevel - STEPTH));
        }
        int preLevel = currentLevel - STEPTH;
        Set<Message> preLevelSet = messageBox.get(preLevel);
        if (null == preLevelSet) {
          preLevelSet = messageBox.get(preLevel);
          if (null == preLevelSet) preLevelSet = Sets.newHashSet();
          messageBox.put(preLevel, preLevelSet);
        }
        preLevelSet.add(new Message(messageName, message));
      }
    }

    public void addNextLevelMessage(String messageName, Object message) {
      synchronized (_lock) {
        if (this.currentLevel + STEPTH >= Integer.MAX_VALUE) {
          throw new IllegalArgumentException(
              String
                  .format(
                      "MessageBox:addNextLevelMessage fail, targetLevel should limit between %s and %s, Illegal arg %s",
                      FIRST_LEVEL, Integer.MAX_VALUE, this.currentLevel + STEPTH));
        }
        int nextLevel = currentLevel + STEPTH;
        Set<Message> nextLevelSet = messageBox.get(nextLevel);
        if (null == nextLevelSet) {
          nextLevelSet = messageBox.get(nextLevel);
          if (null == nextLevelSet) nextLevelSet = Sets.newHashSet();
          messageBox.put(nextLevel, nextLevelSet);
        }
        nextLevelSet.add(new Message(messageName, message));
      }
    }

    public void addTargetLevelMessage(Integer targetLevel, String messageName, Object message) {
      synchronized (_lock) {
        if (null == targetLevel || targetLevel < FIRST_LEVEL || targetLevel > Integer.MAX_VALUE) {
          throw new IllegalArgumentException(
              String
                  .format(
                      "MessageBox:addTargetLevelMessage fail, targetLevel should limit between %s and %s, Illegal arg %s",
                      FIRST_LEVEL, Integer.MAX_VALUE, targetLevel));
        }
        Set<Message> targetLevelSet = messageBox.get(targetLevel);
        if (null == targetLevelSet) {
          targetLevelSet = messageBox.get(targetLevel);
          if (null == targetLevelSet) {
            targetLevelSet = Sets.newHashSet();
          }
          messageBox.put(targetLevel, targetLevelSet);
        }
        targetLevelSet.add(new Message(messageName, message));
      }
    }

    public void switchNextLevel() {
      synchronized (_lock) {
        if (this.currentLevel + STEPTH >= Integer.MAX_VALUE) {
          throw new IllegalArgumentException(
              String
                  .format(
                      "MessageBox:switchNextLevel fail, targetLevel should limit between %s and %s, Illegal arg %s",
                      FIRST_LEVEL, Integer.MAX_VALUE, this.currentLevel + STEPTH));
        }
        currentLevel += STEPTH;
      }
    }

    public void switchPreLevel() {
      synchronized (_lock) {
        if (this.currentLevel - STEPTH <= FIRST_LEVEL) {
          throw new IllegalArgumentException(
              String
                  .format(
                      "MessageBox:switchPreLevel fail, targetLevel should limit between %s and %s, Illegal arg %s",
                      FIRST_LEVEL, Integer.MAX_VALUE, this.currentLevel - STEPTH));
        }
        currentLevel -= STEPTH;
      }
    }

    public void switchToLevel(Integer targetLevel) {
      synchronized (_lock) {
        if (null == targetLevel || targetLevel < FIRST_LEVEL || targetLevel > Integer.MAX_VALUE) {
          throw new IllegalArgumentException(
              String
                  .format(
                      "MessageBox:switchToLevel fail, targetLevel should limit between %s and %s, Illegal arg %s",
                      FIRST_LEVEL, Integer.MAX_VALUE, targetLevel));
        }
        currentLevel = targetLevel;
      }
    }

  }

  private static class Message {
    public Message(String messageName, Object message) {
      this.messageName = messageName;
      this.message = message;
    }

    private String messageName;
    private Object message;
  }
}
