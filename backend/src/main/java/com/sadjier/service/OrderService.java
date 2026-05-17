package com.sadjier.service;

import com.sadjier.common.Result;
import com.sadjier.model.dto.order.OrderCreateDTO;
import com.sadjier.model.dto.order.OrderGetPageDTO;
import com.sadjier.model.dto.order.OrderUpdateDTO;
import com.sadjier.model.vo.order.OrderGetPageVO;

/// <summary>订单服务接口</summary>
public interface OrderService {
    /// <summary>分页查询订单</summary>
    Result<OrderGetPageVO> getOrderPage(OrderGetPageDTO dto);
    /// <summary>新增订单</summary>
    Result<String> addOrder(OrderCreateDTO order_create);
    /// <summary>更新订单</summary>
    Result<String> updateOrder(OrderUpdateDTO order_update);
    /// <summary>删除订单</summary>
    Result<String> deleteOrder(Long order_id);
}
