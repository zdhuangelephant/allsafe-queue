package com.allsafe.queue.schedule;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.scheduling.concurrent.ExecutorConfigurationSupport;
import org.springframework.util.Assert;

import com.allsafe.queue.util.LoggerUtil;
import com.google.common.util.concurrent.MoreExecutors;


/**
 * @name SummerTaskExecutor 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 在springframework.sceduling.concurrent.ThreadPoolTaskExcutor基础上做了一些封装 添加了任务有效期机制
 *              添加了TaskExcutor的管理机制
 * @version 1.0
 */
public class SummerTaskExecutor extends ExecutorConfigurationSupport
    implements
      SchedulingTaskExecutor {

  private static final AtomicInteger poolNumber = new AtomicInteger(1);

  private static final long serialVersionUID = 164749256083410948L;

  private final Object poolSizeMonitor = new Object();

  private SummerThreadFactoryBuilder factoryBuilder = new SummerThreadFactoryBuilder();

  public static final class SummerThreadFactoryBuilder {

    private int corePoolSize = 1;

    private int maxPoolSize = Integer.MAX_VALUE;

    private int keepAliveSeconds = 60;

    private boolean allowCoreThreadTimeOut = false;

    private int queueCapacity = Integer.MAX_VALUE;

    private String nameFormat = null;
    private Boolean daemon = null;
    private Integer priority = null;
    private UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        LoggerUtil.error(String.format("UncaughtException throw by Thread %s", t.getName()), e);
      }
    };

    /**
     * Creates a new {@link ThreadFactory} builder.
     */
    public SummerThreadFactoryBuilder() {}

    /**
     * Sets the naming format to use when naming threads ( {@link Thread#setName}) which are created
     * with this ThreadFactory.
     * 
     * @param nameFormat a {@link String#format(String, Object...)}-compatible format String, to
     *        which a unique integer (0, 1, etc.) will be supplied as the single parameter. This
     *        integer will be unique to the built instance of the ThreadFactory and will be assigned
     *        sequentially. For example, {@code "rpc-pool-%d"} will generate thread names like
     *        {@code "rpc-pool-0"}, {@code "rpc-pool-1"}, {@code "rpc-pool-2"}, etc.
     * @return this for the builder pattern
     */
    public SummerThreadFactoryBuilder setNameFormat(String nameFormat) {
      String.format(nameFormat, 0); // fail fast if the format is bad or
      // null
      this.nameFormat = nameFormat;
      return this;
    }

    /**
     * Sets daemon or not for new threads created with this ThreadFactory.
     * 
     * @param daemon whether or not new Threads created with this ThreadFactory will be daemon
     *        threads
     * @return this for the builder pattern
     */
    public SummerThreadFactoryBuilder setDaemon(boolean daemon) {
      this.daemon = daemon;
      return this;
    }

    /**
     * Sets the priority for new threads created with this ThreadFactory.
     * 
     * @param priority the priority for new Threads created with this ThreadFactory
     * @return this for the builder pattern
     */
    public SummerThreadFactoryBuilder setPriority(int priority) {
      // Thread#setPriority() already checks for validity. These error
      // messages
      // are nicer though and will fail-fast.
      checkArgument(priority >= Thread.MIN_PRIORITY, "Thread priority (%s) must be >= %s",
          priority, Thread.MIN_PRIORITY);
      checkArgument(priority <= Thread.MAX_PRIORITY, "Thread priority (%s) must be <= %s",
          priority, Thread.MAX_PRIORITY);
      this.priority = priority;
      return this;
    }

    /**
     * Sets the {@link UncaughtExceptionHandler} for new threads created with this ThreadFactory.
     * 
     * @param uncaughtExceptionHandler the uncaught exception handler for new Threads created with
     *        this ThreadFactory
     * @return this for the builder pattern
     */
    public SummerThreadFactoryBuilder setUncaughtExceptionHandler(
        UncaughtExceptionHandler uncaughtExceptionHandler) {
      this.uncaughtExceptionHandler = checkNotNull(uncaughtExceptionHandler);
      return this;
    }

    /**
     * Sets the backing {@link ThreadFactory} for new threads created with this ThreadFactory.
     * Threads will be created by invoking #newThread(Runnable) on this backing
     * {@link ThreadFactory}.
     * 
     * @param backingThreadFactory the backing {@link ThreadFactory} which will be delegated to
     *        during thread creation.
     * @return this for the builder pattern
     * 
     * @see MoreExecutors
     */
    public SummerThreadFactoryBuilder setThreadFactory(ThreadFactory backingThreadFactory) {
      this.backingThreadFactory = checkNotNull(backingThreadFactory);
      return this;
    }

    /**
     * Returns a new thread factory using the options supplied during the building process. After
     * building, it is still possible to change the options used to build the ThreadFactory and/or
     * build again. State is not shared amongst built instances.
     * 
     * @return the fully constructed {@link ThreadFactory}
     */
    public ThreadFactory build() {
      return build(this);
    }

    /**
     * rewrite #newThread(Runnable) method to #newThread(SummerTask)
     * 
     * @param builder
     * @return
     */
    private ThreadFactory build(SummerThreadFactoryBuilder builder) {
      final String nameFormat = builder.nameFormat;
      final Boolean daemon = builder.daemon;
      final Integer priority = builder.priority;
      final UncaughtExceptionHandler uncaughtExceptionHandler = builder.uncaughtExceptionHandler;
      final ThreadFactory backingThreadFactory =
          (builder.backingThreadFactory != null)
              ? builder.backingThreadFactory
              : new ThreadFactory() {
                private final ThreadGroup group = (System.getSecurityManager() != null) ? System
                    .getSecurityManager().getThreadGroup() : Thread.currentThread()
                    .getThreadGroup();
                private final AtomicInteger threadNumber = new AtomicInteger(1);
                private final String namePrefix = "SummerThreadPool-"
                    + poolNumber.getAndIncrement() + "-thread-";

                public Thread newThread(Runnable r) {
                  Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
                  t.setDaemon(false);
                  t.setPriority(Thread.NORM_PRIORITY);
                  return t;
                }
              };
      final AtomicLong count = (nameFormat != null) ? new AtomicLong(0) : null;
      return new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
          Thread thread = backingThreadFactory.newThread(runnable);
          if (nameFormat != null) {
            thread.setName(String.format(nameFormat, count.getAndIncrement()));
          }
          if (daemon != null) {
            thread.setDaemon(daemon);
          }
          if (priority != null) {
            thread.setPriority(priority);
          }
          if (uncaughtExceptionHandler != null) {
            thread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
          }
          return thread;
        }
      };
    }

    /**
     * The default thread factory
     */
    private ThreadFactory backingThreadFactory;

    public int getCorePoolSize() {
      return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
      this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
      return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
      this.maxPoolSize = maxPoolSize;
    }

    public int getKeepAliveSeconds() {
      return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
      this.keepAliveSeconds = keepAliveSeconds;
    }

    public boolean isAllowCoreThreadTimeOut() {
      return allowCoreThreadTimeOut;
    }

    public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
      this.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
    }

    public int getQueueCapacity() {
      return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
      this.queueCapacity = queueCapacity;
    }
  }

  private SummerThreadPoolExecutor threadPoolExecutor;

  public String getNameFormat() {
    return this.factoryBuilder.nameFormat;
  }

  public void setNameFormat(String nameFormat) {
    this.factoryBuilder.setNameFormat(nameFormat);
  }

  public Boolean getDaemon() {
    return factoryBuilder.daemon;
  }

  public void setDaemon(Boolean daemon) {
    this.factoryBuilder.setDaemon(daemon);
  }

  public Integer getPriority() {
    return factoryBuilder.priority;
  }

  public void setPriority(Integer priority) {
    this.factoryBuilder.setPriority(priority);
  }

  /**
   * 设置ThreadPoolExecutor的core pool size. 只有实例的corePoolSize等于1或者目标corePoolSize大于实例的corePoolSize时
   * ,才触发变更corePoolSize操作
   */
  public void setCorePoolSize(int corePoolSize) {
    synchronized (this.poolSizeMonitor) {
      if (corePoolSize > getMaxPoolSize() || corePoolSize <= 0 || corePoolSize > 30)
        throw new IllegalArgumentException(String.format(
            "Illegal value for corePoolSize : %d, its's should between 0 to 30. maxPoolSize is %d",
            new Object[] {corePoolSize, getMaxPoolSize()}));
      if (this.factoryBuilder.corePoolSize == 1 || corePoolSize > this.factoryBuilder.corePoolSize) {
        this.factoryBuilder.corePoolSize = corePoolSize;
        if (this.threadPoolExecutor != null) {
          this.threadPoolExecutor.setCorePoolSize(corePoolSize);
        }
      }
    }
  }

  /**
   * 获取本实例的corePoolSize
   */
  public int getCorePoolSize() {
    synchronized (this.poolSizeMonitor) {
      return this.factoryBuilder.corePoolSize;
    }
  }

  public void setMaxPoolSize(int maxPoolSize) {
    synchronized (this.poolSizeMonitor) {
      if (maxPoolSize < getCorePoolSize() || maxPoolSize <= 0 || maxPoolSize > 200)
        throw new IllegalArgumentException(
            String
                .format(
                    "Illegal value for maxPoolSize : %d, its's should between 0 to 200. corePoolSize is %d",
                    new Object[] {maxPoolSize, getCorePoolSize()}));
      this.factoryBuilder.maxPoolSize = maxPoolSize;
      if (this.threadPoolExecutor != null) {
        this.threadPoolExecutor.setMaximumPoolSize(maxPoolSize);
      }
    }
  }

  public int getMaxPoolSize() {
    synchronized (this.poolSizeMonitor) {
      return this.factoryBuilder.maxPoolSize;
    }
  }

  public void setKeepAliveSeconds(int keepAliveSeconds) {
    synchronized (this.poolSizeMonitor) {
      this.factoryBuilder.keepAliveSeconds = keepAliveSeconds;
      if (this.threadPoolExecutor != null) {
        this.threadPoolExecutor.setKeepAliveTime(keepAliveSeconds, TimeUnit.SECONDS);
      }
    }
  }

  public int getKeepAliveSeconds() {
    synchronized (this.poolSizeMonitor) {
      return this.factoryBuilder.keepAliveSeconds;
    }
  }

  public void setAllowCoreThreadTimeOut(boolean allowCoreThreadTimeOut) {
    this.factoryBuilder.allowCoreThreadTimeOut = allowCoreThreadTimeOut;
  }

  public void setQueueCapacity(int queueCapacity) {
    synchronized (this.poolSizeMonitor) {
      if (queueCapacity <= 0 || queueCapacity > 20000)
        throw new IllegalArgumentException(String.format(
            "Illegal value for queueCapacity : %d, its's should between 0 to 20000. ",
            queueCapacity));
      this.factoryBuilder.queueCapacity = queueCapacity;
    }
  }

  protected ExecutorService initializeExecutor(ThreadFactory threadFactory,
      RejectedExecutionHandler rejectedExecutionHandler) {
    initializeExecutor0(threadFactory, rejectedExecutionHandler);
    return this.threadPoolExecutor;
  }

  private void initializeExecutor0(ThreadFactory threadFactory,
      RejectedExecutionHandler rejectedExecutionHandler) {

    BlockingQueue<Runnable> queue = createQueue(this.factoryBuilder.queueCapacity);
    SummerThreadPoolExecutor executor =
        new SummerThreadPoolExecutor(this.factoryBuilder.corePoolSize,
            this.factoryBuilder.maxPoolSize, this.factoryBuilder.keepAliveSeconds,
            TimeUnit.SECONDS, queue, factoryBuilder.build(), rejectedExecutionHandler);
    if (this.factoryBuilder.allowCoreThreadTimeOut) {
      executor.allowCoreThreadTimeOut(true);
    }

    this.threadPoolExecutor = executor;
  }

  /**
   * Create the BlockingQueue to use for the ThreadPoolExecutor.
   * <p>
   * A LinkedBlockingQueue instance will be created for a positive capacity value; a
   * SynchronousQueue else.
   * 
   * @param queueCapacity the specified queue capacity
   * @return the BlockingQueue instance
   * @see java.util.concurrent.LinkedBlockingQueue
   * @see java.util.concurrent.SynchronousQueue
   */
  protected BlockingQueue<Runnable> createQueue(int queueCapacity) {
    if (queueCapacity > 0) {
      return new LinkedBlockingQueue<Runnable>(queueCapacity);
    } else {
      return new SynchronousQueue<Runnable>();
    }
  }

  /**
   * Return the underlying ThreadPoolExecutor for native access.
   * 
   * @return the underlying ThreadPoolExecutor (never <code>null</code>)
   * @throws IllegalStateException if the ThreadPoolTaskExecutor hasn't been initialized yet
   */
  public ThreadPoolExecutor getThreadPoolExecutor() throws IllegalStateException {
    Assert.state(this.threadPoolExecutor != null, "ThreadPoolTaskExecutor not initialized");
    return this.threadPoolExecutor;
  }

  /**
   * Return the current pool size.
   * 
   * @see java.util.concurrent.ThreadPoolExecutor#getPoolSize()
   */
  public int getPoolSize() {
    return getThreadPoolExecutor().getPoolSize();
  }

  /**
   * Return the number of currently active threads.
   * 
   * @see java.util.concurrent.ThreadPoolExecutor#getActiveCount()
   */
  public int getActiveCount() {
    return getThreadPoolExecutor().getActiveCount();
  }

  public void execute(SummerTask task) {
    Executor executor = getThreadPoolExecutor();
    try {
      executor.execute(task);
    } catch (RejectedExecutionException ex) {
      handlerRejectedException(ex, executor, task);
    }
  }

  public Future<?> submit(SummerTask task) {
    ExecutorService executor = getThreadPoolExecutor();
    try {
      return executor.submit(task);
    } catch (RejectedExecutionException ex) {
      handlerRejectedException(ex, executor, task);
      return null;
    }
  }

  @Deprecated
  public <T> Future<T> submit(Callable<T> task) {
    ExecutorService executor = getThreadPoolExecutor();
    try {
      return executor.submit(task);
    } catch (RejectedExecutionException ex) {
      handlerRejectedException(ex, executor, task);
      return null;
    }
  }

  private void handlerRejectedException(RejectedExecutionException ex, Object executor, Object task)
      throws TaskRejectedException {
    throw new TaskRejectedException(String.format("Executor [%s] did not accept task: %s",
        new Object[] {executor, task}), ex);
  }

  /**
   * This task executor prefers short-lived work units.
   */
  public boolean prefersShortLivedTasks() {
    return true;
  }

  /**
   * SummerTask默认私有实现类
   * 
   * @author zhaodan
   * 
   */
  private class SummerTaskWrapper extends SummerTask {

    public SummerTaskWrapper(Runnable task, long validaty) {
      super(validaty);
      if (null == task) throw new IllegalArgumentException("Task can't be null.");
      this._task = task;
    }

    private Runnable _task;

    @Override
    public void run() {
      if (null != this._task) this._task.run();
    }

    @Override
    public void onException(Throwable t) {
      LoggerUtil.error("summer task execute exception",
          new SummerTaskExecuteException(Thread.currentThread(), t));
    }

  }

  /**
   * 此方法已废弃 建议使用SummerTask替代Runnable
   * 
   * @see SummerTaskExecutor#execute(SummerTask, long)
   **/
  @Override
  @Deprecated
  public void execute(Runnable task, long startTimeout) {
    SummerTaskWrapper taskWrapper = new SummerTaskWrapper(task, startTimeout);
    this.execute(taskWrapper);
  }

  /**
   * 此方法已废弃 建议使用SummerTask替代Runnable
   * 
   * @see SummerTaskExecutor#submit(SummerTask)
   **/
  @Override
  @Deprecated
  public Future<?> submit(Runnable task) {
    return this.submit(task, 0);
  }

  /**
   * 此方法已废弃 建议使用SummerTask替代Runnable
   * 
   * @see SummerTaskExecutor#execute(SummerTask)
   **/
  @Override
  @Deprecated
  public void execute(Runnable task) {
    this.execute(task, -1);
  }

  private Future<?> submit(Runnable task, long startTimeout) {
    SummerTaskWrapper taskWrapper = new SummerTaskWrapper(task, startTimeout);
    return this.submit(taskWrapper);
  }

}
