package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)//注解会保存在class文件中,Jvm会保留注释
public @interface AutoFill {
    //String value() default "";  普通情况
    //利用枚举情况 有默认则default,没有可以不写。
    OperationType value();
}
