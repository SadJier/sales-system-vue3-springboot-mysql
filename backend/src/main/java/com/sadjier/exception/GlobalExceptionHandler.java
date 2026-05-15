package com.sadjier.exception;

import com.sadjier.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/// <summary>全局异常处理器</summary>
/// <remarks>捕获所有接口异常，返回统一的 Result 格式</remarks>
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /// <summary>捕获 @RequestBody JSON参数校验异常</summary>
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidException(MethodArgumentNotValidException e) {
        // 获取第一个校验错误信息
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError.getDefaultMessage();
        log.warn("参数校验失败：{}", message);
        // 返回你自定义的Result
        return Result.error(message);
    }
    /// <summary>捕获 表单参数校验异常</summary>
    @ExceptionHandler(BindException.class)
    public Result handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError.getDefaultMessage();
        log.warn("参数绑定失败：{}", message);
        return Result.error(message);
    }
    /// <summary>捕获其他所有未知异常</summary>
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统异常", e);
        return Result.error("服务器繁忙，请稍后再试");
    }
}