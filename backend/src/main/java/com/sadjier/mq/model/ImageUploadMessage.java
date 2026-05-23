package com.sadjier.mq.model;

import com.sadjier.enums.UploadImageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/// <summary>图片上传异步队列消息</summary>
@Setter
@Getter
@AllArgsConstructor
public class ImageUploadMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /// <summary>业务ID(UUID)</summary>
    /// <remarks>用于前端轮询业务完成状态</remarks>
    private String businessId;
    /// <summary>图片类型</summary>
    private UploadImageType imageType;
    /// <summary>关联ID（用户ID或商品ID）</summary>
    private Long targetId;
    /// <summary>图片文件字节数据</summary>
    private byte[] fileData;
    /// <summary>消息时间戳</summary>
    private Long timestamp;

    public ImageUploadMessage() {
        this.timestamp = System.currentTimeMillis();
    }
}
