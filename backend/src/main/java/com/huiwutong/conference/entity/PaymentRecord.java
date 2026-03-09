package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.huiwutong.conference.common.enums.PaymentStatus;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体
 */
@Data
@TableName("payment_records")
public class PaymentRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("payment_no")
    private String paymentNo;

    @TableField("order_id")
    private Long orderId;

    @TableField("order_no")
    private String orderNo;

    /** 支付渠道：wechat / alipay / unionpay / paypal */
    @TableField("provider")
    private String provider;

    /** 第三方交易流水号（回调后填入） */
    @TableField("provider_trade_no")
    private String providerTradeNo;

    /** 支付金额（CNY） */
    @TableField("amount")
    private BigDecimal amount;

    @TableField("currency")
    private String currency;

    /** CNY→目标货币汇率（PayPal 使用） */
    @TableField("exchange_rate")
    private BigDecimal exchangeRate;

    /** 外币金额（PayPal，单位 USD） */
    @TableField("foreign_amount")
    private BigDecimal foreignAmount;

    @TableField("status")
    private PaymentStatus status;

    /** 支付跳转链接（Alipay/UnionPay/PayPal） */
    @TableField("pay_url")
    private String payUrl;

    /** 二维码内容（WeChat Native Pay） */
    @TableField("qr_code")
    private String qrCode;

    /** 支付超时时间 */
    @TableField("expire_at")
    private LocalDateTime expireAt;

    /** 回调原始报文（留存备查） */
    @TableField("notify_raw")
    private String notifyRaw;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField("paid_at")
    private LocalDateTime paidAt;
}
