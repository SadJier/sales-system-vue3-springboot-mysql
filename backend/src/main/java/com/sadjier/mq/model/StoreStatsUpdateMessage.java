package com.sadjier.mq.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/// <summary>店铺统计更新异步队列消息</summary>
@Setter
@Getter
@AllArgsConstructor
public class StoreStatsUpdateMessage implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /// <summary>商家ID</summary>
    private Long merchantId;
    /// <summary>消息时间戳</summary>
    private Long timestamp;

    public StoreStatsUpdateMessage() {
        this.timestamp = System.currentTimeMillis();
    }
}
