package com.sadjier.service.Impl;

import com.sadjier.common.Result;
import com.sadjier.constant.ResultMsgConstant;
import com.sadjier.dao.OrdersRepository;
import com.sadjier.dao.ProductRepository;
import com.sadjier.dao.SysUserRepository;
import com.sadjier.enums.OrderStatusEnum;
import com.sadjier.enums.ResultStatusEnum;
import com.sadjier.enums.UserRolesEnum;
import com.sadjier.model.dto.order.OrderCreateDTO;
import com.sadjier.model.dto.order.OrderGetPageDTO;
import com.sadjier.model.dto.order.OrderUpdateDTO;
import com.sadjier.model.entity.Orders;
import com.sadjier.model.entity.Product;
import com.sadjier.model.vo.order.OrderGetPageVO;
import com.sadjier.model.vo.order.OrderListItemVO;
import com.sadjier.mq.producer.BusinessMessageProducer;
import com.sadjier.service.OrderService;
import com.sadjier.state.OrderStateMachine;
import com.sadjier.util.CommonUtil;
import com.sadjier.util.JwtUtil;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/// <summary>订单服务实现</summary>
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    /// <summary>订单仓储</summary>
    @Autowired
    private OrdersRepository order_repo;
    /// <summary>商品仓储</summary>
    @Autowired
    private ProductRepository product_repo;
    /// <summary>用户仓储</summary>
    @Autowired
    private SysUserRepository user_repo;
    /// <summary>业务消息生产者</summary>
    @Autowired
    private BusinessMessageProducer business_message_producer;

    /// <summary>分页查询订单</summary>
    public Result<OrderGetPageVO> getOrderPage(OrderGetPageDTO dto) {
        int page_index = dto.getPageIndex() <= 0 ? 0 : dto.getPageIndex() - 1;
        int page_size = dto.getPageSize() <= 0 ? 10 : dto.getPageSize();
        PageRequest page_request = PageRequest.of(page_index, page_size);

        Specification<Orders> order_specification = (root, query, criteria_builder) -> {
            List<Predicate> predicate_list = new ArrayList<>();
            String token = CommonUtil.getToken();
            if (StringUtils.hasText(token)) {
                var claim = JwtUtil.parseToken(token);
                var user_id = JwtUtil.getUserId(claim);
                var role = JwtUtil.getUserRole(claim);
                if (role == UserRolesEnum.MERCHANT) {
                    Predicate merchant_predicate = criteria_builder.equal(root.get("merchant").get("userId"), user_id);
                    predicate_list.add(merchant_predicate);
                }
            }
            return criteria_builder.and(predicate_list.toArray(new Predicate[0]));
        };

        Page<Orders> order_page = order_repo.findAll(order_specification, page_request);

        List<OrderListItemVO> items = order_page.getContent().stream().map(OrderListItemVO::new).toList();

        OrderGetPageVO vo = new OrderGetPageVO();
        vo.setTotal(order_page.getTotalElements());
        vo.setItems(items);
        return Result.success(vo);
    }
    /// <summary>新增订单</summary>
    public Result<String> addOrder(OrderCreateDTO order_create) {
        var claims = JwtUtil.parseToken(CommonUtil.getToken());
        Product product = product_repo.findByProductId(order_create.getProductId());
        if (product == null) {
            return Result.result(ResultStatusEnum.NO_DATA, ResultMsgConstant.ORDER_PRODUCT_NOT_FOUND);
        }

        if (product.getStock() < order_create.getQuantity()) {
            return Result.result(ResultStatusEnum.ERROR, ResultMsgConstant.ORDER_PRODUCT_NO_STOCK);
        }
        var user = user_repo.findByUserId(JwtUtil.getUserId(claims));
        if(user == null){
            return Result.result(ResultStatusEnum.DATA_INVALID,ResultMsgConstant.TOKEN_LOCAL_INVALID);
        }
        BigDecimal total_amount = product.getSalePrice().multiply(BigDecimal.valueOf(order_create.getQuantity()));
        //新增订单（初始状态为未支付，不扣减库存）
        Orders order = new Orders();
        order.setMerchant(user);
        order.setBuyerName(order_create.getBuyerName());
        order.setBuyerPhone(order_create.getBuyerPhone());
        order.setProduct(product);
        order.setQuantity(order_create.getQuantity());
        order.setTotalAmount(total_amount);
        order.setStatus(OrderStatusEnum.UNPAID);
        order.setRemark(order_create.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order_repo.save(order);
        //发送店铺统计更新消息
        business_message_producer.sendStoreStatsUpdateMessage(user.getUserId());

        return Result.success(ResultMsgConstant.ORDER_ADD_SUCCESS);
    }
    /// <summary>更新订单</summary>
    @Transactional(rollbackFor = Exception.class)
    public Result<String> updateOrder(OrderUpdateDTO order_update) {
        Orders order = order_repo.findByOrderId(order_update.getOrderId());
        if (order == null) {
            return Result.result(ResultStatusEnum.NO_DATA, ResultMsgConstant.ORDER_NOT_FOUND);
        }
        //权限验证
        var claims = JwtUtil.parseToken(CommonUtil.getToken());
        var user_id = JwtUtil.getUserId(claims);
        var role = JwtUtil.getUserRole(claims);
        if (role == UserRolesEnum.MERCHANT && !Objects.equals(order.getMerchant().getUserId(), user_id)) {
            return Result.result(ResultStatusEnum.DATA_NO_PERMISSION, ResultMsgConstant.ORDER_UPDATE_ONLY_OWN);
        }
        //状态转换
        if (StringUtils.hasText(order_update.getStatus())) {
            OrderStatusEnum target_status;
            try {
                target_status = OrderStatusEnum.valueOf(order_update.getStatus());
            } catch (IllegalArgumentException e) {
                return Result.result(ResultStatusEnum.DATA_INVALID, ResultMsgConstant.ORDER_STATUS_INVALID);
            }
            //状态机校验
            OrderStateMachine state_machine = new OrderStateMachine(order.getStatus());
            boolean could_transit = state_machine.canTransitTo(target_status);
            if (!could_transit) {
                return Result.result(ResultStatusEnum.DATA_INVALID, ResultMsgConstant.ORDER_STATUS_INVALID);
            }
            //支付时扣减库存
            if (target_status == OrderStatusEnum.PAID && order.getStatus() == OrderStatusEnum.UNPAID) {
                Product product = product_repo.findByProductId(order.getProduct().getProductId());
                if (product.getStock() < order.getQuantity()) {
                    return Result.result(ResultStatusEnum.ERROR, ResultMsgConstant.ORDER_PRODUCT_NO_STOCK);
                }
                product.setStock(product.getStock() - order.getQuantity());
                product_repo.save(product);
            }
            //取消已支付订单时恢复库存
            if (target_status == OrderStatusEnum.CANCELLED && order.getStatus() == OrderStatusEnum.PAID) {
                Product product = product_repo.findByProductId(order.getProduct().getProductId());
                product.setStock(product.getStock() + order.getQuantity());
                product_repo.save(product);
            }
            order.setStatus(target_status);
            //发送店铺统计更新消息
            business_message_producer.sendStoreStatsUpdateMessage(order.getMerchant().getUserId());
        }
        if (order_update.getRemark() != null) {
            order.setRemark(order_update.getRemark());
        }
        order_repo.save(order);
        return Result.success(ResultMsgConstant.ORDER_UPDATE_SUCCESS);
    }
    /// <summary>删除订单</summary>
    public Result<String> deleteOrder(Long order_id) {
        Orders order = order_repo.findByOrderId(order_id);
        if (order == null) {
            return Result.result(ResultStatusEnum.NO_DATA, ResultMsgConstant.ORDER_NOT_FOUND);
        }

        var claims = JwtUtil.parseToken(CommonUtil.getToken());
        var user_id = JwtUtil.getUserId(claims);
        var role = JwtUtil.getUserRole(claims);
        if (role == UserRolesEnum.MERCHANT && !Objects.equals(order.getMerchant().getUserId(), user_id)) {
            return Result.result(ResultStatusEnum.DATA_NO_PERMISSION, ResultMsgConstant.ORDER_DELETE_ONLY_OWN);
        }

        order_repo.deleteById(order_id);
        return Result.success(ResultMsgConstant.ORDER_DELETE_SUCCESS);
    }
    /// <summary>获取订单可转换的状态列表</summary>
    public Result<List<OrderStatusEnum>> getOrderTransitions(Long order_id) {
        Orders order = order_repo.findByOrderId(order_id);
        if (order == null) {
            return Result.result(ResultStatusEnum.NO_DATA, ResultMsgConstant.ORDER_NOT_FOUND);
        }
        //商家只能查询自己订单的可转换状态
        var claims = JwtUtil.parseToken(CommonUtil.getToken());
        var user_id = JwtUtil.getUserId(claims);
        var role = JwtUtil.getUserRole(claims);
        if (role == UserRolesEnum.MERCHANT && !Objects.equals(order.getMerchant().getUserId(), user_id)) {
            return Result.result(ResultStatusEnum.DATA_NO_PERMISSION, ResultMsgConstant.ORDER_UPDATE_ONLY_OWN);
        }
        OrderStateMachine state_machine = new OrderStateMachine(order.getStatus());
        return Result.success(state_machine.getAllowedTransitions());
    }
}
