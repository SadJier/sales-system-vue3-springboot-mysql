package com.sadjier.aop;

import com.sadjier.annotations.DistributedLock;
import com.sadjier.util.RedisLockUtil;
import com.sadjier.util.SpelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/// <summary>分布式锁切面</summary>
/// <remarks>拦截@DistributedLock注解方法，自动加锁\解锁</remarks>
@Slf4j
@Aspect
@Component
public class DistributedLockAspect {
    /// <summary>分布式锁</summary>
    @Around("@annotation(com.sadjier.annotations.DistributedLock)")
    public Object around(ProceedingJoinPoint join_point) throws Throwable {
        MethodSignature signature = (MethodSignature) join_point.getSignature();
        Method method = signature.getMethod();
        DistributedLock lock_annotation = method.getAnnotation(DistributedLock.class);

        // 获取锁键
        String class_name = join_point.getTarget().getClass().getSimpleName();
        String method_name = join_point.getSignature().getName();
        Object[] args = join_point.getArgs();
        var lock_key_spel = lock_annotation.lockKey();
        String str_param = null;
        if(!StringUtils.isEmpty(lock_key_spel)) str_param = SpelUtil.parse(join_point,lock_key_spel);
        String lock_key = "lock:"+class_name+":"+method_name+(StringUtils.isEmpty(str_param)?"":":" + str_param);
        // 生成唯一标识作为锁值，用于区分同方法的不同调用
        String lock_value = RedisLockUtil.generateLockValue();

        // 尝试获取锁
        boolean if_lock = RedisLockUtil.tryLockWithRetry(
                lock_key, lock_value,
                lock_annotation.expireTime(), lock_annotation.timeUnit(),
                lock_annotation.retryTimes(), lock_annotation.retryIntervalMs());

        if (!if_lock) {
            log.warn("获取分布式锁失败，key({})", lock_key);
            throw new RuntimeException("操作繁忙，请稍后重试");
        }

        try {
            return join_point.proceed();
        } finally {
            boolean released = RedisLockUtil.unlock(lock_key, lock_value);
            if (!released) {
                log.warn("释放分布式锁失败（可能已过期），key({})", lock_key);
            }
        }
    }
}
