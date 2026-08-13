package com.cheny.aop;


import com.cheny.domain.entity.OperateLog;
import com.cheny.mapper.OperateLogMapper;
import com.cheny.utils.CurrentHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect//用 @Aspect 标记，写「方法执行前 / 后 / 抛异常时」要做的通用代码，不用修改原有业务代码，就给业务方法附加功能。
@Component
public class OperationLogAspect {

    @Autowired
    private OperateLogMapper operateLogMapper;

    //AOP 适合做：日志记录、耗时统计、方法级别的权限控制（比如角色校验），而不是登录 token 校验
    //基于AOP实现所有增删改功能的操作日志
    @Around("@annotation(com.cheny.anno.Log)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable{
            long startTime = System.currentTimeMillis();
            //获取方法签名(方法名+参数类型列表等)
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();//获取这个方法的完整元数据
        //执行目标方法
        Object result = joinPoint.proceed();//去跑加了自定义注解的方法，执行完毕会接收返回值

        //计算耗时
        long endTime = System.currentTimeMillis();
        long costTime = endTime - startTime;

        //构建日志
        OperateLog operateLog = new OperateLog();
        operateLog.setOperateEmpId(CurrentHolder.getCurrentId());
        operateLog.setCostTime(costTime);
        operateLog.setReturnValue(result!=null? result.toString():"void");

        operateLogMapper.insert(operateLog);

        return result;

    }

}
