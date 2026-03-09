package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 邮箱配置实体
 */
@Data
@TableName("email_config")
public class EmailConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("config_name")
    private String configName;

    @TableField("host")
    private String host;

    @TableField("port")
    private Integer port;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("from_name")
    private String fromName;

    @TableField("from_email")
    private String fromEmail;

    @TableField("ssl_enabled")
    private Integer sslEnabled;

    @TableField("is_enabled")
    private Integer isEnabled;

    @TableField("is_default")
    private Integer isDefault;

    @TableField("priority")
    private Integer priority;

    @TableField("remark")
    private String remark;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
