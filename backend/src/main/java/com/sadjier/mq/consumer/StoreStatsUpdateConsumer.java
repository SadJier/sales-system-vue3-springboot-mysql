package com.sadjier.mq.consumer;

import com.rabbitmq.client.Channel;
import com.sadjier.annotations.DistributedLock;
import com.sadjier.config.RabbitMQConfig;
import com.sadjier.dao.OrdersRepository;
import com.sadjier.dao.ProductRepository;
import com.sadjier.dao.StoreStatsRepository;
import com.sadjier.dao.SysUserRepository;
import com.sadjier.enums.OrderStatusEnum;
import com.sadjier.model.entity.Product;
import com.sadjier.model.entity.StoreStats;
import com.sadjier.model.entity.SysUser;
import com.sadjier.model.vo.store.ProductSaleVO;
import com.sadjier.mq.model.StoreStatsUpdateMessage;
import com.sadjier.util.JsonUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/// <summary>店铺统计更新消费者</summary>
/// <remarks>收到消息后全量计算并更新店铺统计表</remarks>
@Component
public class StoreStatsUpdateConsumer {
    /// <summary>订单仓储</summary>
    @Autowired
    private OrdersRepository order_repo;
    /// <summary>用户仓储</summary>
    @Autowired
    private SysUserRepository user_repo;
    /// <summary>商品仓储</summary>
    @Autowired
    private ProductRepository product_repo;
    /// <summary>店铺统计仓储</summary>
    @Autowired
    private StoreStatsRepository store_stats_repo;

    /// <summary>消费店铺统计更新消息</summary>
    @DistributedLock(lockKey = "#store_stats_update_message.merchantId")
    @Transactional(rollbackFor = Exception.class)
    @RabbitListener(queues = RabbitMQConfig.STORE_STATS_UPDATE_QUEUE)
    public void handleStoreStatsUpdateMessage(StoreStatsUpdateMessage store_stats_update_message, Message message, Channel channel) throws Exception {
        long delivery_tag = message.getMessageProperties().getDeliveryTag();
        Long merchant_id = store_stats_update_message.getMerchantId();
        SysUser merchant = user_repo.findByUserId(merchant_id);
        if (merchant == null) {
            channel.basicAck(delivery_tag, false);
            return;
        }
        //全量计算统计数据
        long total_orders = order_repo.countByMerchant(merchant);
        long completed_orders = order_repo.countByMerchantAndStatus(merchant, OrderStatusEnum.COMPLETED);
        long unpaid_orders = order_repo.countByMerchantAndStatus(merchant, OrderStatusEnum.UNPAID);
        long paid_orders = order_repo.countByMerchantAndStatus(merchant, OrderStatusEnum.PAID);
        long shipped_orders = order_repo.countByMerchantAndStatus(merchant, OrderStatusEnum.SHIPPED);
        long cancelled_orders = order_repo.countByMerchantAndStatus(merchant, OrderStatusEnum.CANCELLED);

        BigDecimal total_revenue = order_repo.sumTotalAmountByMerchantAndStatus(merchant, OrderStatusEnum.COMPLETED);

        List<Object[]> sales_data = order_repo.groupProductSalesByMerchant(merchant);
        List<ProductSaleVO> product_sales = new ArrayList<>();
        for (Object[] row : sales_data) {
            ProductSaleVO sale_vo = new ProductSaleVO();
            Long pid = ((Number) row[0]).longValue();
            Product product = product_repo.findByProductId(pid);
            if (product != null){
                sale_vo.setProductId(pid);
                sale_vo.setProductName(product.getName());
                sale_vo.setQuantity(((Number) row[1]).intValue());
                sale_vo.setRevenue((BigDecimal) row[2]);
                product_sales.add(sale_vo);
            }
        }
        //更新或创建店铺统计记录
        StoreStats stats = store_stats_repo.findByMerchantId(merchant_id);
        if (stats == null) {
            stats = new StoreStats();
            stats.setMerchantId(merchant_id);
        }
        stats.setTotalOrders(total_orders);
        stats.setCompletedOrders(completed_orders);
        stats.setUnpaidOrders(unpaid_orders);
        stats.setPaidOrders(paid_orders);
        stats.setShippedOrders(shipped_orders);
        stats.setCancelledOrders(cancelled_orders);
        stats.setTotalRevenue(total_revenue);
        stats.setProductSalesJson(JsonUtil.toJson(product_sales));
        stats.setUpdateTime(LocalDateTime.now());
        store_stats_repo.saveAndFlush(stats);
        channel.basicAck(delivery_tag, false);
    }
}
