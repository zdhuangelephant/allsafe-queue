package com.allsafe.queue.exception;


/**
 * @name UndefinedMessageException 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 消息未定义异常
 * @version 1.0
 */
public class UndefinedMessageException extends RuntimeException {

  /** serialVersionUID */
  private static final long serialVersionUID = -2687987519681445452L;

  public UndefinedMessageException() {
    super();
  }

  public UndefinedMessageException(String message) {
    super(message);
  }


}
