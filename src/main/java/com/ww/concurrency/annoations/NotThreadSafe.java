package com.ww.concurrency.annoations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
//注解：标识线程不安全的类
//注解作用的目标为类
@Target(ElementType.TYPE)
//注解存在的范围:source是在编译之前，runtime是可以运行时获取，一般用于反射
@Retention(RetentionPolicy.SOURCE)
public @interface NotThreadSafe {
    String value() default "";
}
