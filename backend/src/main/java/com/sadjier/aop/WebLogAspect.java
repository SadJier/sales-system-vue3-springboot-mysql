package com.sadjier.aop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sadjier.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/// <summary>日志打印切面类</summary>
@Slf4j
@Aspect
@Component
public class WebLogAspect {
    public static String TRACE_ID_KEY = "trace_id";

    /// <summary>切点，匹配类接口</summary>
    @Pointcut("execution(* com.sadjier.controller..*.*(..))")
    public void webLog() {}
    /// <summary>打印日志</summary>
    @Around("webLog()")
    public Object around(ProceedingJoinPoint join_point) throws Throwable {
        // 获取请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes==null){
            log.error("请求对象获取失败");
            return join_point.proceed();
        }
        HttpServletRequest request = attributes.getRequest();

        MDC.put(TRACE_ID_KEY, UUID.randomUUID().toString().replace("-",""));
        long start = System.currentTimeMillis();
        Object result = null;
        String info_error = null;
        ObjectMapper object_mapper = new ObjectMapper();
        StringBuilder log_msg_start = new StringBuilder();
        String line = System.lineSeparator();

        log_msg_start.append(line).append("==================== 请求开始 ====================").append(line)
                .append("请求地址: ").append(request.getRequestURL()).append(line)
                .append("请求IP: ").append(request.getRemoteAddr()).append(line)
                .append("请求方法: ").append(join_point.getSignature().getName()).append(line);
        log.info(log_msg_start.toString());
        try{
            result = join_point.proceed();
        }catch (Throwable throwable) {
            info_error = throwable.getMessage();
            throw throwable;
        }finally {
            String str_response = "";
            if (info_error == null) {
                try {
                    if (result != null && Result.class.getName().equals(result.getClass().getName())) {
                        int code = (int) result.getClass().getMethod("getCode").invoke(result);
                        String msg = (String) result.getClass().getMethod("getMsg").invoke(result);
                        Object data = result.getClass().getMethod("getData").invoke(result);

                        String dataStr;
                        if (data instanceof org.springframework.core.io.Resource
                                || data instanceof java.io.InputStream
                                || data instanceof java.io.File) {
                            dataStr = "【文件/流资源】" + data.getClass().getSimpleName();
                        } else {
                            dataStr = object_mapper.writeValueAsString(data);
                        }

                        str_response = String.format("Result{code=%d, msg='%s', data=%s}", code, msg, dataStr);
                    } else {
                        if(result instanceof org.springframework.core.io.Resource
                                || result instanceof java.io.InputStream
                                || result instanceof java.io.File)
                        {
                            str_response = "【文件/流资源】" + result.getClass().getSimpleName();
                        }else{
                            str_response = object_mapper.writeValueAsString(result);
                        }
                    }
                } catch (Exception e) {
                    str_response = "【日志序列化失败】";
                }
            }

            String log_msg_end = "接口耗时: " + (System.currentTimeMillis() - start) + " ms" + line
                    + (info_error == null ? "响应结果: " + str_response : "请求异常: " + info_error) + line
                    + "==================== 请求结束 ====================";
            log.info(log_msg_end);
            MDC.clear();
        }

        return result;
    }
}