package com.sadjier.controller;

import com.sadjier.annotations.RequireRole;
import com.sadjier.common.Result;
import com.sadjier.enums.UserRolesEnum;
import com.sadjier.model.vo.store.StoreStatsVO;
import com.sadjier.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/// <summary>店铺管理控制器</summary>
@RestController
@RequestMapping("/api/stores")
@Tag(name = "店铺管理")
public class StoreController {
    /// <summary>店铺服务</summary>
    @Autowired
    private StoreService store_service;

    /// <summary>获取店铺统计</summary>
    @GetMapping("/stats")
    @Operation(summary = "获取店铺统计",description = "传入查询的店铺对应的商家id")
    @RequireRole(all = true)
    public Result<StoreStatsVO> getStoreStats(@RequestParam(value = "merchantId", required = true) Long merchant_id) {
        return store_service.getStoreStats(merchant_id);
    }
}
