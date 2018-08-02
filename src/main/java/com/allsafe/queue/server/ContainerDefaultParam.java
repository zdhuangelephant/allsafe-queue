package com.allsafe.queue.server;


/**
 * @name ContainerDefaultParam 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 队列容器相关默认参数
 * @version 1.0
 */
public interface ContainerDefaultParam {

  /** 队列默认最大值 */
  public final static int QUEUE_DEFAULT_MAX_SIZE = 1000000;

  /** 观察者执行的频率 （默认值：15秒） */
  public final static int OBSERVER_RATE_TIME = 15000;

  /** worker执行的频率 （默认值:10ms） */
  public final static int WORKER_RATE_TIME = 10;

  /** WORKER_MODE work默认执行模式 (默认模式:普通模式) */
  public final static int WORKER_MODE = 1;

  /** WORKER_BATCH_LIMIT work批處理模式默認單次處理數量上限 (默认值:16) */
  public final static int WORKER_BATCH_LIMIT = 16;

  public final static int WORKER_COUNT = Runtime.getRuntime().availableProcessors() * 2;
}
