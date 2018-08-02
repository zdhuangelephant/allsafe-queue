/**
 * 
 */
package com.allsafe.queue.schedule.task;

/**
 * @name ISummerTask 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description TODO
 * @version 1.0
 */
public interface ISummerTask extends Runnable {
  
  public void onException(Throwable t);
}
