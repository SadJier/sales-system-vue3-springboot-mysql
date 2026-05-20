package com.sadjier.config;

import com.sadjier.interceptor.PermissionInterceptor;
import com.sadjier.interceptor.TokenInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class WebConfig implements WebMvcConfigurer {
    /// <summary>Token拦截器对象</summary>
    private final TokenInterceptor token_inter;
    /// <summary>Permission拦截器对象</summary>
    private final PermissionInterceptor permission_inter;
    /// <summary>公共放行接口</summary>
    private static final String[] EXCLUDE_PATH = {
            //放行注册、登录和令牌刷新
            "/api/users/login",
            "/api/users/register",
            "/api/users/refresh",
            //放行静态文件访问
            "/api/users/avatars/**",
            "/uploads/avatars/**",
            "/api/products/image/**",
            "/uploads/products/**",
            // 放行接口文档所有资源
            "/favicon.ico",
            "/doc.html",
            "/webjars/**",
            "/v3/**",
            "/swagger-resources/**"};

    /// <summary>添加拦截器</summary>
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(token_inter)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATH).order(1);
        registry.addInterceptor(permission_inter)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATH).order(2);
    }
}