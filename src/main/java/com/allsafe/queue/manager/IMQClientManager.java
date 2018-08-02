package com.allsafe.queue.manager;

import com.allsafe.queue.enums.CallBackStatus;
import com.allsafe.queue.message.DefaultMessage;


/**
 * @name IMQClientManager 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 消息队列客户端管理者接口:定义管理者行为规范
 * @version 1.0
 */
public interface IMQClientManager {

  /**
   * 插入新消息
   * 
   * @param message
   */
  public void addMessage(DefaultMessage message);

  /**
   * 回调消息执行结果
   * 
   * @param message
   */
  public void callBack(CallBackStatus status, DefaultMessage message);
}
