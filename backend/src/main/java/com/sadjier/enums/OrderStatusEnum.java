package com.sadjier.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/// <summary>订单状态枚举</summary>
@AllArgsConstructor
@Getter
@Schema(description = "订单状态枚举")
public enum OrderStatusEnum {
    /// <summary>未支付</summary>
    @Schema(description = "未支付")
    UNPAID("未支付"),
    /// <summary>已支付</summary>
    @Schema(description = "已支付")
    PAID("已支付"),
    /// <summary>已发货</summary>
    @Schema(description = "已发货")
    SHIPPED("已发货"),
    /// <summary>已取消</summary>
    @Schema(description = "已取消")
    CANCELLED("已取消"),
    /// <summary>已完成</summary>
    @Schema(description = "已完成")
    COMPLETED("已完成");

    @Schema(description = "订单状态枚举信息")
    final String msg;

    @Override
    public String toString() {
        return String.format("{name(%s), msg(%s)}", name(), msg);
    }
}
