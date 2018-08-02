package com.allsafe.queue.callback;

import com.allsafe.queue.enums.CallBackStatus;


/**
 * @name IMQCallBacker 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description MQ消息执行回调接口
 * @version 1.0
 * @param <T>
 */
public interface IMQCallBacker<T> {

  /**
   * 成功回调
   * 
   * @param message 消息体
   */
  public void onSuccess(T message);

  /**
   * 失败回调
   * 
   * @param staus 回调状态
   * @param message 消息体
   */
  public void onFail(CallBackStatus staus, T message);

}
