package com.sadjier.mq.consumer;

import com.rabbitmq.client.Channel;
import com.sadjier.annotations.DistributedLock;
import com.sadjier.config.RabbitMQConfig;
import com.sadjier.constant.CommonMsgConstant;
import com.sadjier.enums.UploadImageType;
import com.sadjier.mq.model.ImageUploadMessage;
import com.sadjier.util.CommonUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/// <summary>图片上传消费者</summary>
/// <remarks>异步处理图片上传，完成后通过Redis标记业务完成</remarks>
@Component
public class ImageUploadConsumer {
    /// <summary>Redis</summary>
    @Autowired
    private RedisTemplate<Object, Object> redis_template;

    /// <summary>消费图片上传消息</summary>
    @DistributedLock(lockKey = "#image_upload_message.imageType.name() + ':' + #image_upload_message.targetId")
    @Transactional
    @RabbitListener(queues = RabbitMQConfig.IMAGE_UPLOAD_QUEUE)
    public void handleImageUploadMessage(ImageUploadMessage image_upload_message, Message message, Channel channel) throws Exception {
        long delivery_tag = message.getMessageProperties().getDeliveryTag();
        boolean success = false;
        //根据图片类型执行上传
        if (image_upload_message.getImageType() == UploadImageType.AVATAR) {
            success = CommonUtil.uploadUserAvatarFromBytes(image_upload_message.getTargetId(), image_upload_message.getFileData());
        } else if (image_upload_message.getImageType() == UploadImageType.PRODUCT) {
            success = CommonUtil.uploadProductImageFromBytes(image_upload_message.getTargetId(), image_upload_message.getFileData());
        }
        //redis标记业务完成
        if (success) {
            String key = CommonMsgConstant.PRE_BUSINESS_COMPLETED + image_upload_message.getBusinessId();
            redis_template.opsForValue().set(key, "1", 10, TimeUnit.MINUTES);
        }
        channel.basicAck(delivery_tag, false);
    }
}
