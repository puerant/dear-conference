package com.huiwutong.conference.service.dto.payment;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 传给 PaymentStrategy 的内部支付请求对象
 */
@Data
public class PayRequest {

    private String paymentNo;
    private String orderNo;
    private BigDecimal amount;
    /** 商品描述（票务名称） */
    private String description;
    private LocalDateTime expireAt;
}
