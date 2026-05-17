package com.sadjier.dao;

import com.sadjier.model.entity.Product;
import com.sadjier.model.entity.SysUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/// <summary>系统用户数据访问层接口</summary>
@Repository
public interface SysUserRepository extends JpaRepository<SysUser, Long> , JpaSpecificationExecutor<SysUser> {
    /// <summary>根据用户id查询用户</summary>
    SysUser findByUserId(Long userId);
    /// <summary>根据用户名查询用户</summary>
    SysUser findByUserName(String user_name);
    /// <summary>根据用户名模糊查询用户</summary>
    java.util.List<SysUser> findByUserNameContaining(String user_name);
}

