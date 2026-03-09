package com.huiwutong.conference.controller.order;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.Order;
import com.huiwutong.conference.service.OrderService;
import com.huiwutong.conference.service.PaymentService;
import com.huiwutong.conference.service.dto.order.CreateOrderDto;
import com.huiwutong.conference.service.dto.order.OrderQueryDto;
import com.huiwutong.conference.service.dto.payment.InitiatePaymentDto;
import com.huiwutong.conference.service.dto.payment.PaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器（参会者使用）
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    /**
     * 创建订单
     */
    @PostMapping
    @RequireRole("attendee")
    public Result<Order> create(@Valid @RequestBody CreateOrderDto dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.create(userId, dto));
    }

    /**
     * 获取我的订单列表
     */
    @GetMapping("/my")
    @RequireRole("attendee")
    public Result<PageResult<Order>> listMy(OrderQueryDto query, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(orderService.listByUser(userId, query));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    @RequireRole("attendee")
    public Result<Order> getDetail(@PathVariable Long id, HttpServletRequest request) {
        Order order = orderService.getById(id);
        Long userId = (Long) request.getAttribute("userId");
        if (!order.getUserId().equals(userId)) {
            return Result.error(403, "无权访问该订单");
        }
        return Result.success(order);
    }

    /**
     * 发起支付（选择支付方式，返回二维码/跳转链接）
     */
    @PostMapping("/{id}/pay")
    @RequireRole("attendee")
    public Result<PaymentResponse> pay(@PathVariable Long id,
                                       @Valid @RequestBody InitiatePaymentDto dto,
                                       HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(paymentService.initiatePayment(id, userId, dto));
    }

    /**
     * 取消订单
     */
    @PostMapping("/{id}/cancel")
    @RequireRole("attendee")
    public Result<Void> cancel(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        orderService.cancel(id, userId);
        return Result.success();
    }
}
