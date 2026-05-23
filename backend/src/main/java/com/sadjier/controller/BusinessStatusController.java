package com.sadjier.controller;

import com.sadjier.annotations.RequireRole;
import com.sadjier.common.Result;
import com.sadjier.service.BusinessStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/// <summary>业务状态控制器</summary>
@RestController
@RequestMapping("/api/business")
@Tag(name = "业务状态")
public class BusinessStatusController {
    /// <summary>业务状态服务</summary>
    @Autowired
    private BusinessStatusService business_status_service;

    /// <summary>查询业务是否完成</summary>
    @GetMapping("/completed/{business_id}")
    @Operation(summary = "查询业务是否完成")
    @RequireRole(all = true)
    public Result<Boolean> checkBusinessCompleted(@PathVariable("business_id") String business_id) {
        return business_status_service.checkBusinessCompleted(business_id);
    }
}
