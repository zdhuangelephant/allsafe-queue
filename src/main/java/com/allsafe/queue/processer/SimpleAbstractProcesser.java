package com.allsafe.queue.processer;

import java.util.List;

import com.allsafe.queue.callback.IMQCallBacker;
import com.allsafe.queue.consumer.AbstractConsumer;
import com.allsafe.queue.puller.IPuller;

/**
 * @name SimpleAbstractProcesser 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 消息处理器处理行为链
 * @version 1.0
 * @param <M>
 * @param <C>
 */
public abstract class SimpleAbstractProcesser<M, C> extends AbstractConsumer<M, C>
    implements
      IPuller<M, C>,
      IProcesser {

  /** serialVersionUID */
  private static final long serialVersionUID = -3317602615979495370L;

  @Override
  public void process() {
    M pull;
    while (null != (pull = pull())) {
      consumer(pull, getCallBack(pull));
    }
  }

  @Override
  public void processList() {
    List<M> pullList;
    while (null != (pullList = pullList())) {
      consumerList(pullList, getCallBackList(pullList));
    }
  }

  public abstract IMQCallBacker<C> getCallBack(M pull);

  public abstract IMQCallBacker<List<C>> getCallBackList(List<M> pullList);
}
