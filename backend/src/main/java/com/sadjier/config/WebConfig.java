package com.sadjier.config;

import com.sadjier.interceptor.LoginInterceptor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    /// <summary>拦截器对象</summary>
    private final LoginInterceptor loginInterceptor;

    /// <summary>添加拦截器</summary>
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**") // 拦截所有接口
                .excludePathPatterns(
                //放行登录和注册
                "/api/users/login",
                "/api/users/register",
                //放行静态文件访问
                "/api/users/avatars/**",
                "/uploads/avatars/**",
                // 放行接口文档所有资源
                "/doc.html",
                "/webjars/**",
                "/v3/**",
                "/swagger-resources/**");
    }
}