package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.huiwutong.conference.common.enums.UserRole;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("users")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("email")
    private String email;

    @TableField("password")
    private String password;

    @TableField("name")
    private String name;

    @TableField("role")
    private UserRole role;

    @TableField("status")
    private Integer status;

    @TableField("email_verified")
    private Integer emailVerified;

    @TableField("avatar")
    private String avatar;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
