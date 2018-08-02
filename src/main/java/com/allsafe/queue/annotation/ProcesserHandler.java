package com.allsafe.queue.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Size;

import com.allsafe.queue.enums.WeightEnum;


/**
 * @name ProcesserHandler 
 * CopyRright (c) 2018 by AllSafe Technology
 *
 * @author <a href="mailto:hzd82274@gmail.com">zdhuang</a>
 * @date 2018年8月2日
 * @description 声明为一个消息处理者
 * @version 1.0
 */
@Documented
@Constraint(validatedBy = {})
@Target({TYPE})
@Retention(RUNTIME)
@ReportAsSingleViolation
@Size(min = 1)
public @interface ProcesserHandler {
  WeightEnum weight();

  String queueName() default "";
}
