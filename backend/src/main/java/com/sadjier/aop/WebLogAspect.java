package com.sadjier.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/// <summary>日志打印切面类</summary>
@Slf4j
@Aspect
@Component
public class WebLogAspect {

    /// <summary>切点，匹配类接口</summary>
    @Pointcut("execution(* com.sadjier.controller..*.*(..))")
    public void webLog() {}
    /// <summary>打印日志</summary>
    @Around("webLog()")
    public Object around(ProceedingJoinPoint join_point) throws Throwable {
        // 获取请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes==null){
            log.error("请求对象获取失败");
            return join_point.proceed();
        }
        HttpServletRequest request = attributes.getRequest();
        //打印前缀
        log.info("==================== 请求开始 ====================");
        log.info("请求地址: {}", request.getRequestURL());
        log.info("请求IP: {}", request.getRemoteAddr());
        log.info("请求方法: {}.{}",
                join_point.getSignature().getDeclaringTypeName(),
                join_point.getSignature().getName());
        log.info("请求参数: {}", join_point.getArgs());
        long start = System.currentTimeMillis();
        //执行接口原逻辑
        Object result = join_point.proceed();
        //打印后缀
        log.info("接口耗时: {} ms", System.currentTimeMillis() - start);
        log.info("响应结果: {}", result);
        log.info("==================== 请求结束 ====================");

        return result;
    }
}