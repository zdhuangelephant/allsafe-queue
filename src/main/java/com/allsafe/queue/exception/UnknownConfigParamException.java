package com.allsafe.queue.exception;


/**
 * @name UnknownConfigParamException 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 未知配置项异常
 * @version 1.0
 */
public class UnknownConfigParamException extends RuntimeException {

  /** serialVersionUID */
  private static final long serialVersionUID = 2044479454363453857L;

  public UnknownConfigParamException() {
    super();
  }

  public UnknownConfigParamException(String message) {
    super(message);
  }
}
