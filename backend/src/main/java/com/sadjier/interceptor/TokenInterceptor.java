package com.sadjier.interceptor;

import com.sadjier.common.Result;
import com.sadjier.enums.ResultStatusEnum;
import com.sadjier.util.JsonUtil;
import com.sadjier.util.JwtUtil;
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
public class TokenInterceptor implements HandlerInterceptor {
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

        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        //没有Token
        if (token == null) {
            response.setStatus(401);
            response.getWriter().write(JsonUtil.toJson(Result.result(ResultStatusEnum.TOKEN_MISSING)));
            log.info("请求缺少Token");
            return false;
        }
        //校验Token（过期/无效都会返回false）
        if (!JwtUtil.validateToken(token)) {
            response.setStatus(401);
            response.getWriter().write(JsonUtil.toJson(Result.result(ResultStatusEnum.TOKEN_INVALID)));
            log.info("该token过期或无效");
            return false;
        }
        //白名单校验
        var claims = JwtUtil.parseToken(token);
        var user_id = JwtUtil.getUserId(claims);
        if(user_id == null){
            response.setStatus(401);
            response.getWriter().write(JsonUtil.toJson(Result.result(ResultStatusEnum.TOKEN_INVALID)));
            return false;
        }
        String access_key = JwtUtil.REDIS_ACCESS_PREFIX + user_id;
        Object stored_token = redisTemplate.opsForValue().get(access_key);
        if(stored_token == null || !stored_token.equals(token)){
            response.setStatus(401);
            response.getWriter().write(JsonUtil.toJson(Result.result(ResultStatusEnum.TOKEN_INVALID)));
            log.info("该token不在有效白名单中");
            return false;
        }

        //校验通过，放行
        return true;
    }
}
