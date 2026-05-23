package com.sadjier.state;

import com.sadjier.enums.OrderStatusEnum;

import java.util.List;

/// <summary>未支付状态</summary>
/// <remarks>可转为已支付或已取消</remarks>
class UnpaidState extends OrderState {
    @Override
    OrderStatusEnum getStatus() {
        return OrderStatusEnum.UNPAID;
    }
    @Override
    List<OrderStatusEnum> getAllowedTransitions() {
        return List.of(OrderStatusEnum.PAID, OrderStatusEnum.CANCELLED);
    }
}
