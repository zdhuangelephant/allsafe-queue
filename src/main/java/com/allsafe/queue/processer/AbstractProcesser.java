package com.allsafe.queue.processer;

import java.util.List;

import com.allsafe.queue.callback.IMQCallBacker;
import com.allsafe.queue.consumer.AbstractConsumer;
import com.allsafe.queue.puller.IPuller;



/**
 * @name AbstractProcesser 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 拉取執行者主干方法
 * @version 1.0
 * @param <M>
 * @param <C>
 */
public abstract class AbstractProcesser<M, C> implements IProcesser {

  protected IPuller<M, C> puller;

  protected AbstractConsumer<M, C> consumer;

  /** serialVersionUID */
  private static final long serialVersionUID = 7288418811941866646L;

  @Override
  public void process() {
    M pull;
    while (null != (pull = puller.pull())) {
      consumer.consumer(pull, getCallBack(pull));
    }
  }

  @Override
  public void processList() {
    List<M> pullList;
    while (null != (pullList = puller.pullList())) {
      consumer.consumerList(pullList, getCallBackList(pullList));
    }
  }

  public abstract IMQCallBacker<C> getCallBack(M pull);

  public abstract IMQCallBacker<List<C>> getCallBackList(List<M> pullList);

}
