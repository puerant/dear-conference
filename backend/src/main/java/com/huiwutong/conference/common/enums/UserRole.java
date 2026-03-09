package com.huiwutong.conference.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 用户角色枚举
 */
public enum UserRole {

    SPEAKER(1, "speaker", "投稿讲者"),
    REVIEWER(2, "reviewer", "审稿人"),
    ATTENDEE(3, "attendee", "参会者"),
    ADMIN(4, "admin", "管理员");

    @EnumValue
    private final Integer value;

    @JsonValue
    private final String code;

    private final String label;

    UserRole(Integer value, String code, String label) {
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

    public static UserRole ofValue(Integer value) {
        for (UserRole role : values()) {
            if (role.value.equals(value)) {
                return role;
            }
        }
        return null;
    }

    public static UserRole ofCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }
}
