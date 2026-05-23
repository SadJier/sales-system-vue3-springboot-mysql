package com.sadjier.annotations;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/// <summary>分布式锁注解</summary>
/// <remarks>标注在方法上，AOP自动加锁\解锁</remarks>
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DistributedLock {
    /// <summary>SPEL语法匹配参数级事务锁键</summary>
    String lockKey() default "";
    /// <summary>锁的过期时间</summary>
    long expireTime() default 10;
    /// <summary>过期时间单位</summary>
    TimeUnit timeUnit() default TimeUnit.SECONDS;
    /// <summary>获取锁的重试次数</summary>
    int retryTimes() default 3;
    /// <summary>重试间隔（毫秒）</summary>
    long retryIntervalMs() default 200;
}
