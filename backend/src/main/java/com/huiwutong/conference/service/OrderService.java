package com.huiwutong.conference.service;

import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.entity.Order;
import com.huiwutong.conference.service.dto.order.CreateOrderDto;
import com.huiwutong.conference.service.dto.order.OrderQueryDto;

/**
 * 订单服务接口
 */
public interface OrderService {

    Order create(Long userId, CreateOrderDto dto);

    Order getById(Long id);

    Order getByOrderNo(String orderNo);

    PageResult<Order> listByUser(Long userId, OrderQueryDto query);

    PageResult<Order> listAll(OrderQueryDto query);

    /**
     * 确认订单支付成功（由 PaymentService 在回调中调用）
     */
    void confirmPaid(Long orderId);

    void cancel(Long id, Long userId);

    void refund(Long id);
}
