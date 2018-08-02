package com.allsafe.queue.client;

import java.util.UUID;

import com.allsafe.queue.client.AbstractMQClient.MessageBox;


/**
 * @name IMQClient 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description MQ客户端接口:定义客户端行为
 * @version 1.0
 */
public interface IMQClient {

  public UUID sendMessage(String messageName, Object message);

  public void sendMessage(MessageBox messageBox);
}
