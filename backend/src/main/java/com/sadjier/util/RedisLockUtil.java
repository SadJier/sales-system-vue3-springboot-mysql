package com.sadjier.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/// <summary>Redis分布式锁工具类：基于SET NX EX + Lua脚本实现原子加锁\解锁</summary>
@Component
@Slf4j
public class RedisLockUtil implements ApplicationContextAware {
    /// <summary>解锁Lua脚本</summary>
    /// <remarks>仅当锁的值与传入值相等时才删除，保证原子性</remarks>
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";

    private static StringRedisTemplate string_redis_template;

    /// <summary>重写方法给静态变量赋值</summary>
    @Override
    public void setApplicationContext(ApplicationContext app_context) throws BeansException {
        string_redis_template = app_context.getBean(StringRedisTemplate.class);
    }
    /// <summary>尝试获取分布式锁</summary>
    /// <param name="lock_key">锁键</param>
    /// <param name="lock_value">锁值</param>
    /// <param name="expire_time">锁的过期时间</param>
    /// <param name="time_unit">时间单位</param>
    /// <returns>是否成功获取锁</returns>
    public static boolean tryLock(String lock_key, String lock_value, long expire_time, TimeUnit time_unit) {
        Boolean result = string_redis_template.opsForValue()
                .setIfAbsent(lock_key, lock_value, expire_time, time_unit);
        return Boolean.TRUE.equals(result);
    }
    /// <summary>带重试的分布式锁获取</summary>
    /// <param name="lock_key">锁的键</param>
    /// <param name="lock_value">锁值</param>
    /// <param name="expire_time">锁的过期时间</param>
    /// <param name="time_unit">时间单位</param>
    /// <param name="retry_times">重试次数</param>
    /// <param name="retry_interval_ms">重试间隔（毫秒）</param>
    /// <returns>是否成功获取锁</returns>
    public static boolean tryLockWithRetry(String lock_key, String lock_value, long expire_time,
                                           TimeUnit time_unit, int retry_times, long retry_interval_ms) {
        for (int i = 0; i <= retry_times; i++) {
            if (tryLock(lock_key, lock_value, expire_time, time_unit)) {
                return true;
            }
            if (i < retry_times) {
                try {
                    Thread.sleep(retry_interval_ms);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        return false;
    }
    /// <summary>释放分布式锁（Lua脚本保证原子性）</summary>
    /// <param name="lock_key">锁的Redis键</param>
    /// <param name="lock_value">锁的值（必须与加锁时一致）</param>
    /// <returns>是否成功释放锁</returns>
    public static boolean unlock(String lock_key, String lock_value) {
        DefaultRedisScript<Long> redis_script = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long result = string_redis_template.execute(redis_script,
                Collections.singletonList(lock_key), lock_value);
        return Long.valueOf(1L).equals(result);
    }
    /// <summary>生成唯一锁标识（UUID）</summary>
    public static String generateLockValue() {
        return UUID.randomUUID().toString();
    }
}
