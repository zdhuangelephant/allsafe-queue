package com.allsafe.queue.consumer;

import java.io.Serializable;
import java.util.List;

import com.allsafe.queue.callback.IMQCallBacker;


/**
 * @name IConsumer 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 消息拉取消费者
 * @version 1.0
 * @param <M>
 * @param <T>
 */
public interface IConsumer<M, T> extends Serializable, Cloneable {

  /**
   * 消费单个消息
   * 
   * @param message
   */
  public void consumer(M message, IMQCallBacker<T> callback);

  /**
   * 消费一组消息
   * 
   * @param messageList
   */
  public void consumerList(List<M> messageList, IMQCallBacker<List<T>> callback);

}
