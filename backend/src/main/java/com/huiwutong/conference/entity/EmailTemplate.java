package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 邮件模板实体
 */
@Data
@TableName("email_template")
public class EmailTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("scene_code")
    private String sceneCode;

    @TableField("scene_name")
    private String sceneName;

    /** 绑定的邮箱配置 ID，null 时使用系统默认启用账号 */
    @TableField("email_config_id")
    private Long emailConfigId;

    @TableField("subject")
    private String subject;

    @TableField("body")
    private String body;

    /** 可用变量说明 JSON 字符串，如 [{"name":"code","desc":"验证码"}] */
    @TableField("variables_desc")
    private String variablesDesc;

    @TableField("is_enabled")
    private Integer isEnabled;

    @TableField(exist = false)
    private String emailConfigName;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
