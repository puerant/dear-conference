package com.huiwutong.conference.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 投稿状态枚举
 */
public enum SubmissionStatus {

    PENDING(1, "pending", "待审稿"),
    REVIEWING(2, "reviewing", "审稿中"),
    ACCEPTED(3, "accepted", "已录用"),
    REJECTED(4, "rejected", "已拒绝");

    @EnumValue
    private final Integer value;

    @JsonValue
    private final String code;

    private final String label;

    SubmissionStatus(Integer value, String code, String label) {
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

    public static SubmissionStatus ofValue(Integer value) {
        for (SubmissionStatus s : values()) {
            if (s.value.equals(value)) {
                return s;
            }
        }
        return null;
    }

    public static SubmissionStatus ofCode(String code) {
        for (SubmissionStatus s : values()) {
            if (s.code.equals(code)) {
                return s;
            }
        }
        return null;
    }
}
