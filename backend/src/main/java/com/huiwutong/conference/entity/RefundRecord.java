package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款记录实体
 */
@Data
@TableName("refund_records")
public class RefundRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("refund_no")
    private String refundNo;

    @TableField("payment_no")
    private String paymentNo;

    @TableField("order_id")
    private Long orderId;

    @TableField("provider")
    private String provider;

    /** 第三方退款流水号 */
    @TableField("provider_refund_no")
    private String providerRefundNo;

    @TableField("amount")
    private BigDecimal amount;

    /** 1=处理中 2=退款成功 3=退款失败 */
    @TableField("status")
    private Integer status;

    @TableField("reason")
    private String reason;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField("refunded_at")
    private LocalDateTime refundedAt;
}
