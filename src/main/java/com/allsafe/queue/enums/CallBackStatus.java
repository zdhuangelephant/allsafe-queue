package com.allsafe.queue.enums;


/**
 * @name CallBackStatus 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 回调状态
 * @version 1.0
 */
public enum CallBackStatus {
  /** INIT 初始化 */
  INIT(null),
  /** SUCCESS 执行成功 */
  SUCCESS(null),
  /** FAIL 执行失败 */
  FAIL(Operation.RESET),
  /** RESET 需要重置 */
  RESET(Operation.RESET),
  /** NULLMESSAGE 空消息 */
  NULLMESSAGE(null),
  /** MISSINFO 信息缺失 */
  MISSINFO(null),
  /** CHECKFAIL 检验未通过 */
  CHECKFAIL(null),
  /** KILLEDWORKER work已失效 */
  KILLEDWORKER(null),
  /** EXCEPTION 发生异常 */
  EXCEPTION(null);

  CallBackStatus(Operation operation) {
    this.operation = operation;
  }

  private Operation operation;

  public Operation getOperation() {
    return operation;
  }

  public void setOperation(Operation operation) {
    this.operation = operation;
  }
}
