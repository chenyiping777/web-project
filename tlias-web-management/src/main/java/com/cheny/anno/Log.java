package com.cheny.anno;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)//限制注解只能标在方法上
@Retention(RetentionPolicy.RUNTIME)
//这个注解会一直保留到程序运行时期，JVM运行时可以通过反射读取到这个注解

//这是一个自定义注解
public @interface Log {
   //自定义注解本身不包含任何逻辑，
   // 真正的功能逻辑是靠AOP去识别这个标签后执行的，这里就是单纯自己做了个标记
}
