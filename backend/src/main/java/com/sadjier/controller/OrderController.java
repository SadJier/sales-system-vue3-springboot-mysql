package com.sadjier.controller;

import com.sadjier.annotations.RequireRole;
import com.sadjier.common.Result;
import com.sadjier.enums.UserRolesEnum;
import com.sadjier.model.dto.order.OrderCreateDTO;
import com.sadjier.model.dto.order.OrderGetPageDTO;
import com.sadjier.model.dto.order.OrderUpdateDTO;
import com.sadjier.model.vo.order.OrderGetPageVO;
import com.sadjier.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/// <summary>订单管理控制器</summary>
@RestController
@RequestMapping("/api/orders")
@Tag(name = "订单管理")
public class OrderController {
    /// <summary>订单服务</summary>
    @Autowired
    private OrderService order_service;

    /// <summary>分页查询订单</summary>
    @GetMapping
    @Operation(summary = "分页查询订单")
    @RequireRole(all = true)
    public Result<OrderGetPageVO> getOrderPage(@ModelAttribute OrderGetPageDTO dto) {
        return order_service.getOrderPage(dto);
    }

    /// <summary>新增订单</summary>
    @PostMapping
    @Operation(summary = "新增订单")
    @RequireRole(roles = {UserRolesEnum.MERCHANT})
    public Result<String> addOrder(@Valid @RequestBody OrderCreateDTO order_create) {
        return order_service.addOrder(order_create);
    }

    /// <summary>更新订单</summary>
    @PutMapping("/update")
    @Operation(summary = "更新订单")
    @RequireRole(all = true)
    public Result<String> updateOrder(@Valid @RequestBody OrderUpdateDTO order_update) {
        return order_service.updateOrder(order_update);
    }

    /// <summary>删除订单</summary>
    @DeleteMapping("/delete/{order_id}")
    @Operation(summary = "删除订单")
    @RequireRole(all = true)
    public Result<String> deleteOrder(@PathVariable("order_id") Long order_id) {
        return order_service.deleteOrder(order_id);
    }
}
