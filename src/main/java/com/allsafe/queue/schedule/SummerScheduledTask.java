package com.allsafe.queue.schedule;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import com.allsafe.queue.schedule.task.ISummerTask;


/**
 * @name SummerScheduledTask 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 支持周期调度的task类
 * @version 1.0
 */
public abstract class SummerScheduledTask implements ISummerTask {

  /**
   * 当前任务状态,
   * 已知三种状态 
   * 初始化:NEW 
   * 运行中:RUNNING 
   * 已取消:CANCELLED 
   * 正常执行状态为NEW RUNNING,
   * 1.当取消任务时,首先将_cancel标志位置为true.
   * 2.当任务为执行中 RUNNING状态时,不去处理 
   * 3.当任务为初始化 NEW状态时,如果发现_cancel标志位被置为true,则将任务状态置为已取消 CANCELLED并取消任务 
   * 4.当任务为已取消 ANCELLED状态时,不做任何操作,直接返回
   * 
   * 已知状态流转: NEW -> RUNNING -> NEW NEW -> CANCELLED
   */
  private volatile AtomicInteger state = new AtomicInteger(NEW);
  private static final int NEW = 0;
  private static final int RUNNING = 0x1 << 1;
  private static final int CANCELLED = 0x1 << 2;

  private ScheduledThreadPoolExecutor _scheduler;

  public SummerScheduledTask(ScheduledThreadPoolExecutor scheduler) {
    this._scheduler = scheduler;
  }

  private volatile boolean _cancel = false;

  /**
   * 将_cancel标志位置为true,标志任务已经被取消
   */
  public void cancel() {
    _cancel = true;
  }

  public SummerScheduledTask() {}

  @Override
  public void run() {
    // 判断任务是否已取消,如果取消,置为取消状态并取消
    if (_cancel && state.compareAndSet(NEW, CANCELLED)) {
      _scheduler.remove(this);
    }

    // 如果已被置为取消状态,则什么都不做,返回
    if (state.compareAndSet(CANCELLED, CANCELLED)) return;

    // 将任务状态从NEW置为RUNNING
    if (state.compareAndSet(NEW, RUNNING)) {
      try {
        doMain();
      } catch (Exception e) {
        onException(e);
      } finally {
        // 任务执行完成后,将状态重新置回NEW
        state.compareAndSet(RUNNING, NEW);
      }
    }

  }

  public abstract void doMain();

//  public abstract void onException(Throwable t);

}
