package com.sadjier.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/// <summary>消息队列日志切面</summary>
/// <remarks>对生产者和消费者方法进行日志打印</remarks>
@Slf4j
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MqLogAspect {
    @Autowired
    private RabbitTemplate rabbit_template;

    /// <summary>生产者方法切点</summary>
    @Pointcut("execution(* com.sadjier.mq.producer..*.*(..))")
    public void producerPointcut() {}
    /// <summary>消费者方法切点</summary>
    @Pointcut("execution(* com.sadjier.mq.consumer..*.*(..))")
    public void consumerPointcut() {}
    /// <summary>生产者日志打印</summary>
    @Around("producerPointcut()")
    public Object aroundProducer(ProceedingJoinPoint join_point) throws Throwable {
        String method_name = join_point.getSignature().getName();
        Object[] args = join_point.getArgs();
        long start = System.currentTimeMillis();
        Object result = null;
        String str_error = null;

        String traceId = MDC.get(WebLogAspect.TRACE_ID_KEY);
        if (traceId == null) {
            MDC.put(WebLogAspect.TRACE_ID_KEY, "无效trace_id");
        }
        // 将trace_id传入消息头进行传递，用于同步trace_id划分请求
        MessagePostProcessor processor = message -> {
            message.getMessageProperties().setHeader(WebLogAspect.TRACE_ID_KEY, traceId);
            return message;
        };
        rabbit_template.setBeforePublishPostProcessors(processor);

        log.info("[MQ生产者] {} 参数: {}", method_name, args);
        try{
            result = join_point.proceed();
        }catch(Throwable e){
            str_error = e.getMessage();
            throw e;
        }finally {
            log.info("[MQ生产者] {} 耗时: {}ms", method_name, System.currentTimeMillis() - start);
            if(str_error != null) log.error(str_error);
        }

        return result;
    }
    /// <summary>消费者日志打印</summary>
    @Around("consumerPointcut()")
    public Object aroundConsumer(ProceedingJoinPoint join_point) throws Throwable {
        String method_name = join_point.getSignature().getName();
        Object[] args = join_point.getArgs();
        long start = System.currentTimeMillis();
        Object result = null;
        String str_error = null;

        String traceId = extractTraceId(args);
        if (traceId != null) {
            MDC.put(WebLogAspect.TRACE_ID_KEY, traceId);
        }
        log.info("[MQ消费者] {} 参数: {}", method_name, args);

        try{
            result = join_point.proceed();
        }catch(Throwable e){
            str_error = e.getMessage();
            throw e;
        }finally {
            log.info("[MQ消费者] {} 耗时: {}ms", method_name, System.currentTimeMillis() - start);
            if(str_error != null) log.error(str_error);
            MDC.remove(WebLogAspect.TRACE_ID_KEY);
        }
        return result;
    }
    // 提取消息中的TraceId
    private String extractTraceId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof org.springframework.amqp.core.Message message) {
                return message.getMessageProperties().getHeader(WebLogAspect.TRACE_ID_KEY);
            }
        }
        return "无效trace_id";
    }
}
