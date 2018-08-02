package com.allsafe.queue.consumer;

import java.util.List;

import com.allsafe.queue.base.AbstractMethodWithCallBack;
import com.allsafe.queue.callback.IMQCallBacker;


/**
 * @name AbstractConsumer 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 消息拉取消费者主干方法
 * @version 1.0
 * @param <M>
 * @param <C>
 */
public abstract class AbstractConsumer<M, C> extends AbstractMethodWithCallBack<M, C>
    implements
      IConsumer<M, C> {

  /** serialVersionUID */
  private static final long serialVersionUID = -5722235088701001203L;

  @Override
  public void consumer(M message, IMQCallBacker<C> callback) {
    excute0(message, callback);
  }

  @Override
  public void consumerList(List<M> messageList, IMQCallBacker<List<C>> callback) {
    excute0(messageList, callback);
  }

}
