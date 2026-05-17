package com.sadjier.config;
import com.sadjier.dao.CategoryRepository;
import com.sadjier.dao.SysUserRepository;
import com.sadjier.enums.UserRolesEnum;
import com.sadjier.model.entity.Category;
import com.sadjier.model.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/// <summary>项目启动后的数据库数据初始化</summary>
@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private CategoryRepository category_repo;
    @Autowired
    private SysUserRepository sys_user_repo;
    /// <summary>密码加密器</summary>
    @Autowired
    private PasswordEncoder password_encoder;

    /// <summary>启动完毕后自动执行的代码</summary>
    @Override
    public void run(String... args) throws Exception {
        //管理员初始化
        if (!sys_user_repo.existsById(1L)) {
            SysUser admin = new SysUser(1L,"admin",password_encoder.encode("admin123")
                    ,"ACTIVE", UserRolesEnum.ADMIN, LocalDateTime.now(),LocalDateTime.now());
            sys_user_repo.save(admin);
            System.out.println("初始化默认管理员");
        }
        //初始化“未分类”分类
        if (!category_repo.existsById(1L)) {
            Category category = new Category();
            category.setName("未分类");
            category_repo.save(category);
            System.out.println("初始化默认分类：未分类");
        }
    }
}