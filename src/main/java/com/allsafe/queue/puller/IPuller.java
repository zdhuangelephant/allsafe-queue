package com.allsafe.queue.puller;

import java.io.Serializable;
import java.util.List;


/**
 * @name IPuller 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 消息拉取执行者/回调执行者
 * @version 1.0
 * @param <M>
 * @param <C>
 */
public interface IPuller<M, C> extends Serializable, Cloneable {

  /**
   * 业务逻辑执行
   * 
   * @param message
   */
  public M pull();

  /**
   * 业务逻辑批处理执行
   * 
   * @param message
   */
  public List<M> pullList();

  /**
   * 执行完毕后回调删除
   * 
   * @param m
   */
  public void delete(C c);

  /**
   * 执行完毕后回调删除
   * 
   * @param mlist
   */
  public void delete(List<C> clist);

}
