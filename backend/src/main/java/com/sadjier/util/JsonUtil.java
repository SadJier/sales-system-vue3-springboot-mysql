package com.sadjier.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /// <summary>将对象转换为json字符串</summary>
    @SneakyThrows // 省略异常捕获
    public static String toJson(Object obj) {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }
}
