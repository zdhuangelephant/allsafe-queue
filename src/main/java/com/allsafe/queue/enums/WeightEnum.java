package com.allsafe.queue.enums;


/**
 * @name WeightEnum 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 权重枚举值
 * @version 1.0
 */
public enum WeightEnum {
  ONE(1), TWO(2), THREE(3), FOUR(4), FIVE(5);
  private Integer code;
  public Integer getCode() {
    return code;
  }
  WeightEnum(int code) {
    this.code = code;
  }
}
