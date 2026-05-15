package com.sadjier.controller;

import com.sadjier.model.entity.Customer;
import com.sadjier.common.Result;
import com.sadjier.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/// <summary>客户管理控制器</summary>
@RestController
@RequestMapping("/api/customers")
@Tag(name = "客户管理")
public class CustomerController {
    /// <summary>客户服务</summary>
    @Autowired
    private CustomerService customer_service;

    /// <summary>客户模糊查询</summary>
    @GetMapping
    @Operation(summary = "客户模糊查询")
    public Result searchCustomer(@RequestParam("keyword") String keyword) {
        return customer_service.searchCustomer(keyword);
    }

    /// <summary>新增客户</summary>
    @PostMapping
    @Operation(summary = "新增客户")
    public Result addCustomer(@RequestBody Customer customer) {
        return customer_service.addCustomer(customer);
    }

    /// <summary>更新客户</summary>
    @PutMapping("/{customer_id}")
    @Operation(summary = "更新客户")
    public Result updateCustomer(@PathVariable("customer_id") Long customer_id,
                                 @RequestBody Customer customer) {
        return customer_service.updateCustomer(customer_id, customer);
    }

    /// <summary>删除客户</summary>
    @DeleteMapping("/{customer_id}")
    @Operation(summary = "删除客户")
    public Result deleteCustomer(@PathVariable("customer_id") Long customer_id) {
        return customer_service.deleteCustomer(customer_id);
    }
}

