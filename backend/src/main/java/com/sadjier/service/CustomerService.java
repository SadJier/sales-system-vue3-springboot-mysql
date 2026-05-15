package com.sadjier.service;

import com.sadjier.model.entity.Customer;
import com.sadjier.common.Result;

/// <summary>客户服务接口</summary>
public interface CustomerService {
    /// <summary>客户模糊查询</summary>
    Result searchCustomer(String keyword);
    /// <summary>新增客户</summary>
    Result addCustomer(Customer customer);
    /// <summary>更新客户</summary>
    Result updateCustomer(Long customer_id, Customer customer);
    /// <summary>删除客户</summary>
    Result deleteCustomer(Long customer_id);
}

