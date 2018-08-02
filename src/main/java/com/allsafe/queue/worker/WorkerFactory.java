package com.allsafe.queue.worker;

import java.util.Map;

import com.allsafe.queue.manager.DefaultMessageQueueObserver.MultipleWorkHandler;
import com.allsafe.queue.manager.DefaultMessageQueueObserver.MultipleWorker;
import com.allsafe.queue.util.CloneUtil;
import com.allsafe.queue.util.LoggerUtil;


/**
 * @name WorkerFactory 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description  worker创建工厂
 * @version 1.0
 */
public class WorkerFactory {

  /**
   * 创建一个新的worker，采用clone的方式创建
   * 
   * @param worker
   */
  public static AbstractDefaultWorker createWork(AbstractDefaultWorker worker) {
    if (null == worker) {
      return null;
    }
    try {
      // 克隆方式进行创建
      return CloneUtil.deepClone(worker);
    } catch (Exception e) {
      LoggerUtil.error("worker深度拷贝异常", e);
      return null;
    }
  }

  /**
   * 创建一组新的worker，采用clone的方式创建
   * 
   * @param worker
   */
  public static MultipleWorkHandler createWork(MultipleWorkHandler workerMap) {
    if (null == workerMap) {
      return null;
    }
    try {
      // 克隆方式进行创建
      MultipleWorkHandler _workMap = new MultipleWorkHandler();
      for (Map.Entry<String, MultipleWorker> workerList : workerMap.entrySet())
        for (AbstractDefaultWorker worker : workerList.getValue())
          _workMap.registWorker(workerList.getKey(), CloneUtil.deepClone(worker));
      return _workMap;
    } catch (Exception e) {
      LoggerUtil.error("worker深度拷贝异常", e);
      return null;
    }
  }

}
