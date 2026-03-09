package com.huiwutong.conference.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 凭证状态枚举
 */
public enum CredentialStatus {

    VALID(1, "valid", "有效"),
    USED(2, "used", "已使用"),
    EXPIRED(3, "expired", "已过期");

    @EnumValue
    private final Integer value;

    @JsonValue
    private final String code;

    private final String label;

    CredentialStatus(Integer value, String code, String label) {
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
}
