package com.sadjier.interceptor;

import com.sadjier.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@AllArgsConstructor
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final RedisTemplate<Object, Object> redisTemplate;

    /// <summary>请求前拦截</summary>
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //放行OPTIONS请求（预检请求）
        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        //获取请求头的Token
        String token = request.getHeader("Authorization");

//        log.info("拦截请求:URI({}),方法({}),Token({})", request.getRequestURI(), request.getMethod(), token);
        //没有Token
        if (token == null) {
            response.setStatus(401);
            response.getWriter().write("未登录，请先登录");
            return false;
        }

        //截断黑名单中的token（自行退出登录）
        if(redisTemplate.hasKey(token)){
            response.setStatus(401);
            response.getWriter().write("Token已过期，请重新登录");
            return false;
        }
        //校验Token（过期/无效都会返回false）
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.getWriter().write("Token已过期，请重新登录");
            return false;
        }

//        log.info("请求通过:URI({}),方法({}),Token({})", request.getRequestURI(), request.getMethod(), token);
        //校验通过，放行
        return true;
    }
}