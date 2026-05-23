package com.sadjier.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/// <summary>RabbitMQ配置类</summary>
/// <remarks>定义交换机、队列、绑定及消息序列化</remarks>
@Configuration
public class RabbitMQConfig {
    // 死信交换机参数键名
    private static final String ARG_KEY_DL_EXCHANGE = "x-dead-letter-exchange";
    // 死信路由键参数键名
    private static final String ARG_KEY_DL_ROUTING_KEY = "x-dead-letter-routing-key";
    // 交换机名称
    /// <summary>业务交换机</summary>
    public static final String BUSINESS_EXCHANGE = "business.exchange";
    /// <summary>死信交换机</summary>
    public static final String DLX_EXCHANGE = "dlx.exchange";
    // 队列名称
    /// <summary>图片上传队列</summary>
    public static final String IMAGE_UPLOAD_QUEUE = "image.upload.queue";
    /// <summary>店铺统计更新队列</summary>
    public static final String STORE_STATS_UPDATE_QUEUE = "store.stats.update.queue";
    /// <summary>死信队列</summary>
    public static final String DLX_QUEUE = "dlx.queue";
    // 路由键名称
    /// <summary>图片上传路由键</summary>
    public static final String IMAGE_UPLOAD_ROUTING_KEY = "image.upload";
    /// <summary>店铺统计更新路由键</summary>
    public static final String STORE_STATS_UPDATE_ROUTING_KEY = "store.stats.update";
    /// <summary>死信路由键</summary>
    public static final String DLX_ROUTING_KEY = "dlx";
    // 交换机声明
    /// <summary>业务Topic交换机</summary>
    @Bean
    public TopicExchange businessExchange() {
        return ExchangeBuilder.topicExchange(BUSINESS_EXCHANGE).durable(true).build();
    }
    /// <summary>死信交换机</summary>
    @Bean
    public DirectExchange dlxExchange() {
        return ExchangeBuilder.directExchange(DLX_EXCHANGE).durable(true).build();
    }
    // 队列声明
    /// <summary>图片上传队列</summary>
    @Bean
    public Queue imageUploadQueue() {
        return QueueBuilder.durable(IMAGE_UPLOAD_QUEUE)
                .withArgument(ARG_KEY_DL_EXCHANGE, DLX_EXCHANGE)
                .withArgument(ARG_KEY_DL_ROUTING_KEY, DLX_ROUTING_KEY)
                .build();
    }
    /// <summary>店铺统计更新队列</summary>
    @Bean
    public Queue storeStatsUpdateQueue() {
        return QueueBuilder.durable(STORE_STATS_UPDATE_QUEUE)
                .withArgument(ARG_KEY_DL_EXCHANGE, DLX_EXCHANGE)
                .withArgument(ARG_KEY_DL_ROUTING_KEY, DLX_ROUTING_KEY)
                .build();
    }
    /// <summary>死信队列</summary>
    @Bean
    public Queue dlxQueue() {
        return QueueBuilder.durable(DLX_QUEUE).build();
    }
    // 绑定声明
    /// <summary>图片上传队列绑定到业务交换机</summary>
    @Bean
    public Binding imageUploadBinding(Queue imageUploadQueue, TopicExchange businessExchange) {
        return BindingBuilder.bind(imageUploadQueue).to(businessExchange).with(IMAGE_UPLOAD_ROUTING_KEY);
    }
    /// <summary>店铺统计更新队列绑定到业务交换机</summary>
    @Bean
    public Binding storeStatsUpdateBinding(Queue storeStatsUpdateQueue, TopicExchange businessExchange) {
        return BindingBuilder.bind(storeStatsUpdateQueue).to(businessExchange).with(STORE_STATS_UPDATE_ROUTING_KEY);
    }
    /// <summary>死信队列绑定到死信交换机</summary>
    @Bean
    public Binding dlxBinding(Queue dlxQueue, DirectExchange dlxExchange) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with(DLX_ROUTING_KEY);
    }
    // 消息序列化
    /// <summary>JSON消息转换器</summary>
    @Bean
    @Scope
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
    /// <summary>配置发送消息的json转换器</summary>
    /// <remarks>添加消息发送确认和路由失败回调</remarks>
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connection_factory, MessageConverter json_message_converter) {
        RabbitTemplate template = new RabbitTemplate(connection_factory);
        template.setMessageConverter(json_message_converter);
        // 消息确认回调
        template.setConfirmCallback((correlation_data, ack, cause) -> {
            if (!ack) {
                System.err.println("消息发送失败: " + cause);
            }
        });
        // 消息返回回调（消息无法路由时触发）
        template.setReturnsCallback(returned -> {
            System.err.println("消息路由失败: " + returned.getMessage());
        });
        return template;
    }
    /// <summary>配置@RabbitListener方法json转换器</summary>
    /// <remarks>配置消费限流和手动签收</remarks>
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connection_factory, MessageConverter json_message_converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connection_factory);
        factory.setMessageConverter(json_message_converter);
        factory.setPrefetchCount(1);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}
