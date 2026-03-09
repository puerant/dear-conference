package com.huiwutong.conference.service.dto.payment;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付回调解析结果（策略层返回）
 */
@Data
public class NotifyResult {

    /** 内部支付单号（对应 payment_records.payment_no） */
    private String paymentNo;
    /** 第三方交易流水号 */
    private String providerTradeNo;
    /** 是否支付成功 */
    private boolean success;
    /** 回调中的支付金额（CNY），用于金额校验 */
    private BigDecimal amount;
    /** 回调原始报文（留存） */
    private String rawData;
}
