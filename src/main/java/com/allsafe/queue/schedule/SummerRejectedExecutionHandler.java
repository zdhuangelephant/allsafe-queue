package com.allsafe.queue.schedule;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import com.allsafe.queue.util.LoggerUtil;

public class SummerRejectedExecutionHandler implements RejectedExecutionHandler {
  @Override
  public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
    LoggerUtil.error("Task " + r.toString() + " rejected from " + executor.toString(),
        new RejectedExecutionException());
  }

}
