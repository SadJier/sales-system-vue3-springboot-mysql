package com.sadjier.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/// <summary>Redis配置类</summary>
/// <remarks>定义json序列化方式</remarks>
@Configuration
public class RedisConfig {
    /// <summary>自定义redis的json序列化</summary>
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connection_factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connection_factory);
        ObjectMapper object_mapper = new ObjectMapper();
        object_mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        object_mapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL);
        // Value使用json序列化
        Jackson2JsonRedisSerializer<Object> jackson_serializer = new Jackson2JsonRedisSerializer<>(object_mapper, Object.class);
        // Key使用String序列化
        StringRedisSerializer string_serializer = new StringRedisSerializer();
        template.setKeySerializer(string_serializer);
        template.setHashKeySerializer(string_serializer);
        template.setValueSerializer(jackson_serializer);
        template.setHashValueSerializer(jackson_serializer);
        template.afterPropertiesSet();
        return template;
    }
}
