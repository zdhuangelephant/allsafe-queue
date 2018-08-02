package com.allsafe.queue.message;

import java.util.Date;
import java.util.UUID;


/**
 * @name IMessage 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description Message抽象接口
 * @version 1.0
 */
public interface IMessage {

  /**
   * 消息ID:消息唯一标识
   * 
   * @return message_id
   */
  public UUID getMessageId();

  /**
   * 消息名:消息路由/分类凭证
   * 
   * @return message_name
   */
  public String getMessageName();

  /**
   * 消息体:实际用于消费的消息体
   * 
   * @return message_body
   */
  public String getMessageBodyJson();

  /**
   * 发送时间
   * 
   * @return send_time
   */
  public Date getSendTime();

  /**
   * 接收时间
   * 
   * @return receive_time
   */
  public Date getReceiveTime();

}
