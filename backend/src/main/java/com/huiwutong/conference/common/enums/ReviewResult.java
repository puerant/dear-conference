package com.huiwutong.conference.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 审稿结果枚举
 */
public enum ReviewResult {

    ACCEPT(1, "accept", "录用"),
    REJECT(2, "reject", "拒绝"),
    REVISE(3, "revise", "需修改");

    @EnumValue
    private final Integer value;

    @JsonValue
    private final String code;

    private final String label;

    ReviewResult(Integer value, String code, String label) {
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

    public static ReviewResult ofValue(Integer value) {
        for (ReviewResult r : values()) {
            if (r.value.equals(value)) {
                return r;
            }
        }
        return null;
    }
}
