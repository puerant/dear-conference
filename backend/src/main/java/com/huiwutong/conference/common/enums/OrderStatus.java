package com.huiwutong.conference.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 订单状态枚举
 */
public enum OrderStatus {

    UNPAID(1, "unpaid", "未支付"),
    PAID(2, "paid", "已支付"),
    CANCELLED(3, "cancelled", "已取消"),
    REFUNDED(4, "refunded", "已退款");

    @EnumValue
    private final Integer value;

    @JsonValue
    private final String code;

    private final String label;

    OrderStatus(Integer value, String code, String label) {
        this.value = value;
        this.code = code;
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static OrderStatus ofValue(Integer value) {
        for (OrderStatus s : values()) {
            if (s.value.equals(value)) {
                return s;
            }
        }
        return null;
    }
}
