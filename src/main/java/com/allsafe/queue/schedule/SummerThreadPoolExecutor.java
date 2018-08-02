package com.allsafe.queue.schedule;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.allsafe.queue.schedule.task.ISummerTask;
import com.allsafe.queue.util.LoggerUtil;


/**
 * @name SummerThreadPoolExecutor 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description SummerThreadPoolExcutor,重写了beforeExecute方法和afterExecute方法
 * @version 1.0
 */
public class SummerThreadPoolExecutor extends ThreadPoolExecutor {

  public SummerThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
      TimeUnit unit, BlockingQueue<Runnable> workQueue) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
  }

  public SummerThreadPoolExecutor(int corePoolSize, int maxPoolSize, int keepAliveSeconds,
      TimeUnit seconds, BlockingQueue<Runnable> queue, ThreadFactory build,
      RejectedExecutionHandler rejectedExecutionHandler) {
    super(corePoolSize, maxPoolSize, keepAliveSeconds, seconds, queue, build,
        rejectedExecutionHandler);
  }

  @Override
  public void execute(Runnable command) {
    if (command instanceof SummerTask) {
      if (!((SummerTask) command).check()) return;
    }
    super.execute(command);
  }

  @Override
  protected void afterExecute(Runnable r, Throwable t) {
    if (t != null) {
      if (r instanceof ISummerTask) {
        ((ISummerTask) r).onException(t);
      } else {
        LoggerUtil.error("summer task execute exception",
            new SummerTaskExecuteException(Thread.currentThread(), t));
      }
    }
    super.afterExecute(r, t);
  }

}
 