package com.allsafe.queue.manager;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.UUID;

import com.allsafe.queue.enums.CallBackStatus;
import com.allsafe.queue.enums.Operation;
import com.allsafe.queue.message.DefaultMessage;
import com.allsafe.queue.model.ContainerParamModel;
import com.allsafe.queue.util.LoggerUtil;
import com.allsafe.queue.util.QueueMessage;


/**
 * @name AbstractMessageQueueManager 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 抽象消息队列管理者:定义消息管理基本流程
 * @version 1.0
 */
public abstract class AbstractMessageQueueManager implements IMQClientManager {


  /** 队列实例 */
  Queue<DefaultMessage> _msQueue;

  /** 队列大小限制 */
  Integer _limit;

  /** 容器相关参数 */
  ContainerParamModel _queueContainerModel;

  /**
   * 构造函数
   * 
   * @param msQueue
   * @param queueContainerModel
   */
  public AbstractMessageQueueManager(Queue<DefaultMessage> msQueue,
      ContainerParamModel queueContainerModel) {
    this._msQueue = msQueue;
    this._limit = queueContainerModel.getQueueMaxCapacity();
    this._queueContainerModel = queueContainerModel;
  }

  /**
   * 消息加入队列
   * 
   * @param message
   */
  public void addMessage(DefaultMessage message) {
    // TODO 队列容量达到最大值，们默认为10w，写数据库。
    if (_msQueue.size() > _limit) {
      System.out.println("写入DB");
    } else {
      _msQueue.add(message);
    }
  }

  /**
   * 构造依赖队列
   */
  public abstract Queue<UUID> initDependencyQueue();

  /**
   * 从队列中获取一个消息元素 先将该消息元素从队列中移除，然后将返回
   */
  public DefaultMessage getAndRemoveQueueMessage() {
    return _msQueue.poll();
  }

  @Override
  public void callBack(CallBackStatus status, DefaultMessage message) {
    LoggerUtil.messageInfo(new QueueMessage(status, message));
    Operation operation = status.getOperation();
    if (null == operation) return;
    // RESET操作将重置消息
    switch (operation) {
      case RESET:
        addMessage(message);
        break;
      default:
        break;
    }
  }


  /**
   * @name DependencyQueue 
   * CopyRright (c) 2018 by AllSafe Technology
   *
   * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
   * @date 2018年8月2日
   * @description 依赖关系映射
   * @version 1.0
   */
  public static class DependencyQueue implements Queue<UUID> {

    private boolean shutdown = false;

    public void shutDown() {
      this.shutdown = true;
    }

    public boolean isShutDown() {
      return shutdown;
    }

    public DependencyQueue(Queue<UUID> messageQueue) {
      this._mq = messageQueue;
    }

    private Queue<UUID> _mq;

    @Override
    public int size() {
      if (null == _mq) return 0;
      return _mq.size();
    }

    @Override
    public boolean isEmpty() {
      if (null == _mq) return true;
      return _mq.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
      if (null == _mq) return false;
      return _mq.contains(o);
    }

    @Override
    public Iterator<UUID> iterator() {
      if (null == _mq) return null;
      return _mq.iterator();
    }

    @Override
    public Object[] toArray() {
      if (null == _mq) return null;
      return _mq.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
      if (null == _mq) return null;
      return _mq.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
      if (null == _mq) return false;
      return _mq.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
      if (null == _mq) return false;
      return _mq.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends UUID> c) {
      if (null == _mq) return false;
      return _mq.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      if (null == _mq) return false;
      return _mq.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      if (null == _mq) return false;
      return _mq.retainAll(c);
    }

    @Override
    public void clear() {
      if (null == _mq) return;
      _mq.clear();
    }

    @Override
    public boolean add(UUID e) {
      if (null == _mq) return false;
      return _mq.add(e);
    }

    @Override
    public boolean offer(UUID e) {
      if (null == _mq) return false;
      return _mq.offer(e);
    }

    @Override
    public UUID remove() {
      if (null == _mq) return null;
      return _mq.remove();
    }

    @Override
    public UUID poll() {
      if (null == _mq) return null;
      return _mq.poll();
    }

    @Override
    public UUID element() {
      if (null == _mq) return null;
      return _mq.element();
    }

    @Override
    public UUID peek() {
      if (null == _mq) return null;
      return _mq.peek();
    }
  }

}
