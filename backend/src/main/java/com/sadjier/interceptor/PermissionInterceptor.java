package com.sadjier.interceptor;

import com.sadjier.annotations.RequireRole;
import com.sadjier.common.Result;
import com.sadjier.enums.ResultStatusEnum;
import com.sadjier.enums.UserRolesEnum;
import com.sadjier.util.JsonUtil;
import com.sadjier.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;

@Component
public class PermissionInterceptor implements HandlerInterceptor {
    /// <summary>校验请求权限</summary>
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod handler_method)) {
            return true;
        }
        //判断为Controller的api才做后续权限检测
        Class<?> bean_class = handler_method.getBeanType();
        boolean is_controller = bean_class.isAnnotationPresent(RestController.class)
                || bean_class.isAnnotationPresent(Controller.class);
        if (!is_controller) {
            return true;
        }
        //获取身份并检验权限
        RequireRole require_role = handler_method.getMethodAnnotation(RequireRole.class);
        if(require_role == null){
            return true;
        }
        // all=true时对所有身份开放
        if(require_role.all()) return true;
        // 获取身份
        var claims = JwtUtil.getNowClaims();
        UserRolesEnum role = JwtUtil.getUserRole(claims);
        UserRolesEnum[] roles = require_role.roles();

        if(Arrays.stream(roles).toList().contains(role)) return true;

        //权限不足，返回错误响应
        response.setContentType("application/json;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonUtil.toJson(Result.result(ResultStatusEnum.DATA_NO_PERMISSION)));
        return false;
    }
}
