package com.sadjier.config;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/// <summary>安全配置</summary>
@Configuration
public class SecurityConfig {
    /// <summary>密码加密器</summary>
    @Bean
    public PasswordEncoder passwordEncoder() {
        /// <summary>加密器实例</summary>
        PasswordEncoder password_encoder = new BCryptPasswordEncoder();
        return password_encoder;
    }
}

