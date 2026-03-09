package com.huiwutong.conference.service;

import com.huiwutong.conference.service.dto.payment.InitiatePaymentDto;
import com.huiwutong.conference.service.dto.payment.PaymentResponse;
import com.huiwutong.conference.service.dto.payment.PaymentStatusVo;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 发起支付，返回支付数据（二维码/跳转链接）
     */
    PaymentResponse initiatePayment(Long orderId, Long userId, InitiatePaymentDto dto);

    /**
     * 查询支付状态（前端轮询）
     */
    PaymentStatusVo getStatus(String paymentNo);

    /**
     * 处理支付成功回调（由 NotifyController 调用）
     */
    void handleNotify(String paymentNo, String providerTradeNo, String rawData);

    /**
     * 发起退款（查找支付记录 → 调用渠道 API → 更新 DB → 更新订单/凭证/库存）
     */
    void refund(Long orderId);

    /**
     * 模拟支付成功（仅开发环境使用，直接将订单标记为已支付并生成凭证）
     */
    void mockPay(Long orderId);
}
