package com.sadjier.mq.producer;

import com.sadjier.config.RabbitMQConfig;
import com.sadjier.mq.model.ImageUploadMessage;
import com.sadjier.mq.model.StoreStatsUpdateMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/// <summary>业务消息生产者</summary>
@Component
public class BusinessMessageProducer {
    @Autowired
    private RabbitTemplate rabbit_template;
    /// <summary>发送图片上传消息</summary>
    public void sendImageUploadMessage(ImageUploadMessage message) {
        rabbit_template.convertAndSend(
                RabbitMQConfig.BUSINESS_EXCHANGE,
                RabbitMQConfig.IMAGE_UPLOAD_ROUTING_KEY,
                message);
    }
    /// <summary>发送店铺统计更新消息</summary>
    public void sendStoreStatsUpdateMessage(Long merchant_id) {
        StoreStatsUpdateMessage message = new StoreStatsUpdateMessage();
        message.setMerchantId(merchant_id);
        rabbit_template.convertAndSend(
                RabbitMQConfig.BUSINESS_EXCHANGE,
                RabbitMQConfig.STORE_STATS_UPDATE_ROUTING_KEY,
                message);
    }
}
