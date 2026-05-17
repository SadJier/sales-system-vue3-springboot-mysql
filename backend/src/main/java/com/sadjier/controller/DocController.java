package com.sadjier.controller;

import com.sadjier.common.Result;
import com.sadjier.enums.OrderStatusEnum;
import com.sadjier.enums.ResultStatusEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "接口文档说明")
@RestController
@RequestMapping("/doc")
public class DocController {

    @Operation(summary = "业务状态码辅助接口",
            description = "用于生成ResultStatusEnum文档，无实际作用，相应参数的data字段的可用值即为ResultStatusEnum的描述")
    @GetMapping("/enum/result-status")
    public Result<ResultStatusEnum> getStatuses() {
        return Result.success("文档辅助接口，用于生成返回结果状态码的文档");
    }
    @Operation(summary = "订单状态枚举辅助接口",
            description = "用于生成OrderStatusEnum文档，无实际作用，相应参数的data字段的可用值即为OrderStatusEnum的描述")
    @GetMapping("/enum/order-status")
    public Result<OrderStatusEnum> getOrderStatuses() {
        return Result.success("文档辅助接口，用于生成订单状态枚举的文档");
    }
}