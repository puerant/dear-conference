package com.huiwutong.conference.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 支付状态枚举
 */
public enum PaymentStatus {

    PENDING(1, "pending", "待支付"),
    SUCCESS(2, "success", "支付成功"),
    FAILED(3, "failed", "支付失败"),
    CLOSED(4, "closed", "已关闭"),
    REFUNDED(5, "refunded", "已退款");

    @EnumValue
    private final Integer value;

    @JsonValue
    private final String code;

    private final String label;

    PaymentStatus(Integer value, String code, String label) {
        this.value = value;
        this.code = code;
        this.label = label;
    }

    public Integer getValue() { return value; }
    public String getCode() { return code; }
    public String getLabel() { return label; }
}
