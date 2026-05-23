package com.sadjier.mq.consumer;

import com.rabbitmq.client.Channel;
import com.sadjier.config.RabbitMQConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/// <summary>死信消费者</summary>
/// <remarks>消费处理失败进入死信队列的消息</remarks>
@Component
public class DlxConsumer {
    /// <summary>消费死信队列消息</summary>
    @RabbitListener(queues = RabbitMQConfig.DLX_QUEUE)
    public void handleDlxMessage(Object dlx_message, Message message, Channel channel) throws Exception {
        long delivery_tag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(delivery_tag, false);
    }
}
