package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 支付配置实体
 */
@Data
@TableName("payment_config")
public class PaymentConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("payment_type")
    private Integer paymentType;

    @TableField("app_id")
    private String appId;

    @TableField("app_secret")
    private String appSecret;

    @TableField("merchant_id")
    private String merchantId;

    @TableField("api_url")
    private String apiUrl;

    @TableField("notify_url")
    private String notifyUrl;

    @TableField("is_enabled")
    private Integer isEnabled;

    @TableField("sandbox_mode")
    private Integer sandboxMode;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
