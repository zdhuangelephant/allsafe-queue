package com.allsafe.queue.base;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.allsafe.queue.callback.IMQCallBacker;
import com.allsafe.queue.util.LoggerUtil;


/**
 * @name AbstractMethodWithCallBack 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 部分通用主干方法实现(带回调)
 * @version 1.0
 * @param <M>
 * @param <C>
 */
public abstract class AbstractMethodWithCallBack<M, C> {
  public boolean check() {
    return true;
  }

  public void excute0(M message, IMQCallBacker<C> callback) {
    try {
      if (check()) {
        domain(message, callback);
      }
    } catch (Exception e) {
      onExceptionCallBack(message, e, callback);
    }
  }

  public void excute0(List<M> message, IMQCallBacker<List<C>> callback) {
    try {
      if (check()) {
        domain(message, callback);
      }
    } catch (Exception e) {
      onExceptionCallBack(message, e, callback);
    }
  }

  public abstract void domain(M message, IMQCallBacker<C> callback) throws Exception;

  public abstract void domain(List<M> message, IMQCallBacker<List<C>> callback) throws Exception;

  protected void onExceptionCallBack(List<M> message, Throwable t, IMQCallBacker<List<C>> callback) {
    LoggerUtil.error(
        String.format("批量消息处理异常.Message : %s", JSON.toJSON(message.toString())), t);
    onException(t, message, callback);
  }

  protected void onExceptionCallBack(M message, Throwable t, IMQCallBacker<C> callback) {
    LoggerUtil.error(String.format("消息处理异常.Message : %s", JSON.toJSON(message.toString())),
        t);
    onException(t, message, callback);
  }

  public abstract void onException(Throwable t, List<M> message, IMQCallBacker<List<C>> callback);

  public abstract void onException(Throwable t, M message, IMQCallBacker<C> callback);
}
