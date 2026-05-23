package com.sadjier.state;

import com.sadjier.enums.OrderStatusEnum;

import java.util.List;

/// <summary>订单状态抽象基类</summary>
abstract class OrderState {
    /// <summary>获取当前状态枚举</summary>
    abstract OrderStatusEnum getStatus();
    /// <summary>获取允许转换的目标状态列表</summary>
    abstract List<OrderStatusEnum> getAllowedTransitions();
    /// <summary>判断是否可以转换到目标状态</summary>
    boolean canTransitTo(OrderStatusEnum target) {
        return getAllowedTransitions().contains(target);
    }
}
