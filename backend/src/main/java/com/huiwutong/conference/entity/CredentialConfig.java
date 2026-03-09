package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 凭证配置实体
 */
@Data
@TableName("credential_config")
public class CredentialConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("template_id")
    private String templateId;

    @TableField("background_image")
    private String backgroundImage;

    @TableField("watermark_text")
    private String watermarkText;

    @TableField("watermark_opacity")
    private Integer watermarkOpacity;

    @TableField("watermark_x")
    private Integer watermarkX;

    @TableField("watermark_y")
    private Integer watermarkY;

    @TableField("expiry_days")
    private Integer expiryDays;

    @TableField("qr_code_position")
    private String qrCodePosition;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
