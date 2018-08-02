package com.allsafe.queue.util;

import com.alibaba.fastjson.JSON;


/**
 * @name MessageEntity 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description <p>异步消息记录日志实体类</p>
 * @version 1.0
 * @param <T>
 */
public class MessageEntity<T>{
  
  private T pojo;
  
  private String tag;
  
  private String messageName;
  
  private String result;
  
  private Exception errmsg;

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public Exception getErrmsg() {
    return errmsg;
  }

  public void setErrmsg(Exception errmsg) {
    this.errmsg = errmsg;
  }

  public T getPojo() {
    return pojo;
  }

  public void setPojo(T pojo) {
    this.pojo = pojo;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public String getMessageName() {
    return messageName;
  }

  public void setMessageName(String messageName) {
    this.messageName = messageName;
  }

  public MessageEntity(T pojo, String tag, String messageName) {
    this.pojo = pojo;
    this.tag = tag;
    this.messageName = messageName;
  }
  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }
}