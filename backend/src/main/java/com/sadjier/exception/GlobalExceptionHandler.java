package com.sadjier.exception;

import com.sadjier.common.Result;
import com.sadjier.constant.ResultMsgConstant;
import com.sadjier.enums.ResultStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/// <summary>全局异常处理器</summary>
/// <remarks>捕获所有接口异常，比如@NotBlank，返回统一的 Result 格式</remarks>
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /// <summary>捕获 @RequestBody JSON参数校验异常</summary>
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidException(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数校验失败";
        log.warn("参数校验失败：{}", message);
        return Result.result(ResultStatusEnum.DATA_INVALID,message);
    }
    /// <summary>捕获 表单参数校验异常</summary>
    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数绑定失败";
        log.warn("参数绑定失败：{}", message);
        return Result.result(ResultStatusEnum.DATA_INVALID,message);
    }
    /// <summary>捕获其他所有未知异常</summary>
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.result(ResultStatusEnum.SERVER_BUSY,ResultMsgConstant.SERVER_BUSY);
    }
}