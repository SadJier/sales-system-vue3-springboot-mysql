package com.sadjier.common;

import com.sadjier.model.vo.SysUserVO;
import com.sadjier.model.vo.UserLoginVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/// <summary>后端返回结果</summary>
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Result<T> {
    /// <summary>编码</summary>
    /// <remarks>1成功，0为失败</remarks>
    @Schema(description = "编码(1成功，0为失败)")
    private Integer code;
    /// <summary>错误信息</summary>
    @Schema(description = "错误信息")
    private String msg;
    /// <summary>后端返回的数据</summary>
    @Schema(description = "后端返回的数据")
    private T data;

    /// <summary>返回默认成功数据</summary>
    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.code = 1;
        result.msg = "success";
        result.data = null;
        return result;
    }
    /// <summary>返回成功数据并指定data内容</summary>
    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 1;
        result.msg = "success";
        return result;
    }
    /// <summary>返回错误数据结果并描述错误内容</summary>
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<T>();
        result.code = 0;
        result.msg = msg;
        return result;
    }
}
