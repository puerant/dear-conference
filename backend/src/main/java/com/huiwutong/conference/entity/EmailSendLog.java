package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 邮件发送日志
 */
@Data
@TableName("email_send_log")
public class EmailSendLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("scene_code")
    private String sceneCode;

    @TableField("template_id")
    private Long templateId;

    @TableField("email_config_id")
    private Long emailConfigId;

    /** 1=业务发送，2=测试发送 */
    @TableField("send_type")
    private Integer sendType;

    @TableField("to_email")
    private String toEmail;

    @TableField("from_email")
    private String fromEmail;

    @TableField("subject")
    private String subject;

    @TableField("variables_json")
    private String variablesJson;

    /** 1=成功，0=失败 */
    @TableField("status")
    private Integer status;

    @TableField("error_message")
    private String errorMessage;

    @TableField("triggered_by")
    private Long triggeredBy;

    @TableField("sent_at")
    private LocalDateTime sentAt;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

