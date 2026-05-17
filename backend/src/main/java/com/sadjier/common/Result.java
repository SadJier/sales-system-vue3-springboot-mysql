package com.sadjier.common;

import com.sadjier.enums.ResultStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/// <summary>后端返回结果</summary>
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Result<T> {
    /// <summary>操作结果状态码</summary>
    /// <remarks>具体与ResultStatusEnum的枚举对应</remarks>
    @Schema(description = "操作结果状态码，见响应状态状态码")
    private Integer code;
    /// <summary>操作结果信息</summary>
    @Schema(description = "操作结果直接说明，无论成功失败都会携带")
    private String msg;
    /// <summary>后端返回的数据</summary>
    @Schema(description = "后端返回的数据，不包含操作结果的直接说明")
    private T data;

    /// <summary>返回默认成功数据</summary>
    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = ResultStatusEnum.SUCCESS.getCode();
        result.msg = ResultStatusEnum.SUCCESS.getMsg();
        result.data = null;
        return result;
    }
    /// <summary>返回成功数据并指定data内容</summary>
    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = ResultStatusEnum.SUCCESS.getCode();
        result.msg = ResultStatusEnum.SUCCESS.getMsg();
        return result;
    }
    /// <summary>返回成功数据并指定成功信息</summary>
    public static <T> Result<T> success(String msg) {
        Result<T> result = new Result<T>();
        result.data = null;
        result.code = ResultStatusEnum.SUCCESS.getCode();
        result.msg = msg;
        return result;
    }
    /// <summary>返回成功数据并指定data以及msg信息</summary>
    public static <T> Result<T> success(T object,String msg) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = ResultStatusEnum.SUCCESS.getCode();
        result.msg = msg;
        return result;
    }
    /// <summary>创建返回结果并以状态指定状态码和操作信息</summary>
    /// <remarks>适用于需要返回特定操作结果类型但不包含data的情况</remarks>
    public static <T> Result<T> result(ResultStatusEnum status) {
        Result<T> result = new Result<T>();
        result.code = status.getCode();
        result.msg = status.getMsg();
        result.data = null;
        return result;
    }
    /// <summary>创建返回结果并以状态指定状态码和操作结果信息</summary>
    /// <remarks>适用于需要返回特定状态码和信息但不包含data的情况</remarks>
    public static <T> Result<T> result(ResultStatusEnum status, String msg) {
        Result<T> result = new Result<T>();
        result.code = status.getCode();
        result.msg = msg;
        result.data = null;
        return result;
    }
}
