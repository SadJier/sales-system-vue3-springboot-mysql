package com.sadjier.dao;

import com.sadjier.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/// <summary>客户数据访问层接口</summary>
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    /// <summary>根据姓名模糊查询客户</summary>
    java.util.List<Customer> findByNameContaining(String name);
}

