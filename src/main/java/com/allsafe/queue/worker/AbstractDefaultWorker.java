package com.allsafe.queue.worker;

import java.util.List;

import com.allsafe.queue.base.AbstractMethodWithCallBack;
import com.allsafe.queue.callback.IMQCallBacker;
import com.allsafe.queue.message.DefaultMessage;


/**
 * @name AbstractDefaultWorker 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 所有work的抽象父类
 * @version 1.0
 */
public abstract class AbstractDefaultWorker
    extends AbstractMethodWithCallBack<DefaultMessage, DefaultMessage>
    implements
      IWorker<DefaultMessage, DefaultMessage> {

  public AbstractDefaultWorker() {}

  /** UID */
  private static final long serialVersionUID = -8117021887505560545L;


  @Override
  public void excute(DefaultMessage message, IMQCallBacker<DefaultMessage> callback) {
//	TraceWrapper.getWrapper().getTraceParam().setTraceId(message.getTraceId());
//	TraceWrapper.getWrapper().getTraceParam().setMyId(message.getMyId());
    excute0(message, callback);
  }

  @Override
  public void excute(List<DefaultMessage> message, IMQCallBacker<List<DefaultMessage>> callback) {
    excute0(message, callback);
  }

}
