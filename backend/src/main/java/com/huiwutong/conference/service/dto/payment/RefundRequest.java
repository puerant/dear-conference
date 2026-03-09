package com.huiwutong.conference.service.dto.payment;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 传给 PaymentStrategy 的退款请求对象
 */
@Data
public class RefundRequest {

    private String paymentNo;
    private String refundNo;
    /** 第三方原始交易号（部分渠道需要） */
    private String providerTradeNo;
    private BigDecimal amount;
    private String reason;
}
