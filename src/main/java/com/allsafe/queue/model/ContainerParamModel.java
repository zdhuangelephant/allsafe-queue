package com.allsafe.queue.model;

import com.allsafe.queue.server.ContainerDefaultParam;


/**
 * @name ContainerParamModel 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 队列容器参数模型
 * @version 1.0
 */
public class ContainerParamModel {

  /** 队列最大值容量 达到最大容量后 消息入库或者写入文件 */
  private int queueMaxCapacity;

  /** 观察者执行的频率 */
  private int observerRateTime;

  /** worker执行的频率 */
  private int workerRateTime;

  /** workerMode worker执行的模式 */
  private int workerMode;

  /** workerbBatchLimit work批處理模式單次執行數量上限 */
  private int workerbBatchLimit;

  /** workerCount work数量 */
  private int workerCount;

  /** 扫描路径 */
  private String scanPath;

  public String getScanPath() {
    return scanPath;
  }

  public void setScanPath(String scanPath) {
    this.scanPath = scanPath;
  }

  public int getWorkerMode() {
    return workerMode <= 0 ? ContainerDefaultParam.WORKER_MODE : workerMode;
  }

  public void setWorkerMode(int workerMode) {
    this.workerMode = workerMode;
  }

  public int getWorkerbBatchLimit() {
    return workerbBatchLimit <= 0 ? ContainerDefaultParam.WORKER_BATCH_LIMIT : workerbBatchLimit;
  }

  public void setWorkerbBatchLimit(int workerbBatchLimit) {
    this.workerbBatchLimit = workerbBatchLimit;
  }

  public int getQueueMaxCapacity() {
    return queueMaxCapacity <= 0 ? ContainerDefaultParam.QUEUE_DEFAULT_MAX_SIZE : queueMaxCapacity;
  }

  public void setQueueMaxCapacity(int queueMaxCapacity) {
    this.queueMaxCapacity = queueMaxCapacity;
  }

  public int getObserverRateTime() {
    return observerRateTime <= 0 ? ContainerDefaultParam.OBSERVER_RATE_TIME : observerRateTime;
  }

  public void setObserverRateTime(int observerRateTime) {
    this.observerRateTime = observerRateTime;
  }

  public int getWorkerRateTime() {
    return workerRateTime <= 0 ? ContainerDefaultParam.WORKER_RATE_TIME : workerRateTime;
  }

  public void setWorkerRateTime(int workerRateTime) {
    this.workerRateTime = workerRateTime;
  }

  public int getWorkerCount() {
    return workerCount <= 0 ? ContainerDefaultParam.WORKER_COUNT : workerCount;
  }

  public void setWorkerCount(int workerCount) {
    this.workerCount = workerCount;
  }

}
