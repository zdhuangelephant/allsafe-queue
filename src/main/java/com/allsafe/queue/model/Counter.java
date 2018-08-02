package com.allsafe.queue.model;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @name Counter 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 计数器
 * @version 1.0
 */
public class Counter {

  private final AtomicInteger totalCount = new AtomicInteger(0);

  private final AtomicInteger failCount = new AtomicInteger(0);
  
  public Integer getTotalCount() {
    return totalCount.get();
  }

  public Integer getFailCount() {
    return failCount.get();
  }

  void onFail() {
    failCount.incrementAndGet();
  }

  void onExcute() {
    totalCount.incrementAndGet();
  }
}
