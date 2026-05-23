package com.sadjier.state;

import com.sadjier.enums.OrderStatusEnum;

import java.util.List;

/// <summary>已支付状态</summary>
/// <remarks>可转为已完成或已取消</remarks>
class PaidState extends OrderState {
    @Override
    OrderStatusEnum getStatus() {
        return OrderStatusEnum.PAID;
    }
    @Override
    List<OrderStatusEnum> getAllowedTransitions() {
        return List.of(OrderStatusEnum.SHIPPED, OrderStatusEnum.CANCELLED);
    }
}
