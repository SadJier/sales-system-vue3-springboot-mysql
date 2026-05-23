package com.sadjier.service.Impl;

import com.sadjier.common.Result;
import com.sadjier.constant.ResultMsgConstant;
import com.sadjier.dao.StoreStatsRepository;
import com.sadjier.enums.ResultStatusEnum;
import com.sadjier.enums.UserRolesEnum;
import com.sadjier.model.entity.StoreStats;
import com.sadjier.model.vo.store.ProductSaleVO;
import com.sadjier.model.vo.store.StoreStatsVO;
import com.sadjier.service.StoreService;
import com.sadjier.util.CommonUtil;
import com.sadjier.util.JwtUtil;
import com.sadjier.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/// <summary>店铺服务实现</summary>
@Service
@Slf4j
public class StoreServiceImpl implements StoreService {
    /// <summary>店铺统计仓储</summary>
    @Autowired
    private StoreStatsRepository store_stats_repo;

    /// <summary>获取店铺统计</summary>
    public Result<StoreStatsVO> getStoreStats(Long merchant_id) {
        var claims = JwtUtil.parseToken(CommonUtil.getToken());
        var role = JwtUtil.getUserRole(claims);
        var user_id = JwtUtil.getUserId(claims);
        //数据检验
        if (merchant_id == null) {
            return Result.result(ResultStatusEnum.DATA_MISSING, ResultMsgConstant.STORE_STATS_ID_REQUIRED);
        }
        if(role == UserRolesEnum.MERCHANT && !Objects.equals(merchant_id, user_id)){
            return Result.result(ResultStatusEnum.DATA_NO_PERMISSION, ResultMsgConstant.STORE_STATS_ONLY_OWN);
        }
        //直接查表返回
        StoreStats stats = store_stats_repo.findByMerchantId(merchant_id);
        if (stats == null) {
            return Result.result(ResultStatusEnum.NO_DATA, ResultMsgConstant.STORE_STATS_MERCHANT_NOT_FOUND);
        }
        //转换为VO
        StoreStatsVO vo = new StoreStatsVO();
        vo.setTotalOrders(stats.getTotalOrders());
        vo.setUnpaidOrders(stats.getUnpaidOrders());
        vo.setPaidOrders(stats.getPaidOrders());
        vo.setCompletedOrders(stats.getCompletedOrders());
        vo.setShippedOrders(stats.getShippedOrders());
        vo.setCancelledOrders(stats.getCancelledOrders());
        vo.setTotalRevenue(stats.getTotalRevenue());
        //反序列化商品销售统计
        if (stats.getProductSalesJson() != null) {
            vo.setProductSales(JsonUtil.fromJsonList(stats.getProductSalesJson(), ProductSaleVO.class));
        }
        return Result.success(vo);
    }
}
