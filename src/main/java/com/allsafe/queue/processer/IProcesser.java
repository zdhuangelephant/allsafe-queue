package com.allsafe.queue.processer;

import java.io.Serializable;

import com.allsafe.queue.annotation.ProcesserHandler;

/**
 * @name IProcesser 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 消息处理中继器
 * @version 1.0
 */
public interface IProcesser extends Serializable, Cloneable {

  public void initialize(ProcesserHandler annotation);
  
  public void process();

  public void processList();

}
