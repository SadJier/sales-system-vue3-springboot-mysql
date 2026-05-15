package com.sadjier.service.Impl;

import com.sadjier.dao.CustomerRepository;
import com.sadjier.model.entity.Customer;
import com.sadjier.common.Result;
import com.sadjier.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/// <summary>客户服务实现</summary>
@Service
public class CustomerServiceImpl implements CustomerService {
    /// <summary>客户仓储</summary>
    @Autowired
    private CustomerRepository customer_repository;

    /// <summary>客户模糊查询</summary>
    public Result searchCustomer(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return Result.error("关键词不能为空");
        }
        /// <summary>客户列表</summary>
        List<Customer> customer_list = customer_repository.findByNameContaining(keyword);
        return Result.success(customer_list);
    }

    /// <summary>新增客户</summary>
    public Result addCustomer(Customer customer) {
        customer.setCreateTime(LocalDateTime.now());
        customer_repository.save(customer);
        return Result.success("新增客户成功");
    }

    /// <summary>更新客户</summary>
    public Result updateCustomer(Long customer_id, Customer customer) {
        /// <summary>客户信息</summary>
        Customer current_customer = customer_repository.findById(customer_id).orElse(null);
        if (current_customer == null) {
            return Result.error("客户不存在");
        }
        if (StringUtils.hasText(customer.getName())) {
            current_customer.setName(customer.getName());
        }
        if (StringUtils.hasText(customer.getPhone())) {
            current_customer.setPhone(customer.getPhone());
        }
        if (StringUtils.hasText(customer.getAddress())) {
            current_customer.setAddress(customer.getAddress());
        }
        customer_repository.save(current_customer);
        return Result.success("更新客户成功");
    }

    /// <summary>删除客户</summary>
    public Result deleteCustomer(Long customer_id) {
        /// <summary>客户信息</summary>
        Customer current_customer = customer_repository.findById(customer_id).orElse(null);
        if (current_customer == null) {
            return Result.error("客户不存在");
        }
        customer_repository.deleteById(customer_id);
        return Result.success("删除客户成功");
    }
}

