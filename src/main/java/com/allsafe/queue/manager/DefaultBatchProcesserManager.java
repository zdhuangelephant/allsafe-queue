package com.allsafe.queue.manager;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import com.allsafe.queue.processer.IProcesser;


/**
 * @name DefaultBatchProcesserManager 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 拉取消息批处理管理者
 * @version 1.0
 */
class DefaultBatchProcesserManager extends DefaultProcesserManager {

  /**
   * 构造函数，入参为具体执行业务逻辑
   * 
   * @param worker
   * @param messageQueueManager
   */
  public DefaultBatchProcesserManager(IProcesser processer, ScheduledThreadPoolExecutor scheduler) {
    super(processer, scheduler);
  }

  @Override
  public void doMain() {
    if (isAlive()) {
      try {
        getProcesser().processList();
      } catch (Exception e) {
        onException(e);
      }
    } else {
      this.cancel();
    }
  }

}
