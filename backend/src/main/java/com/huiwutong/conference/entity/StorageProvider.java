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
 * 存储供应商配置
 */
@Data
@TableName("storage_provider")
public class StorageProvider implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("provider_name")
    private String providerName;

    @TableField("provider_type")
    private Integer providerType;

    @TableField("endpoint")
    private String endpoint;

    @TableField("region")
    private String region;

    @TableField("access_key")
    private String accessKey;

    @TableField("secret_key")
    private String secretKey;

    @TableField("sts_enabled")
    private Integer stsEnabled;

    @TableField("is_enabled")
    private Integer isEnabled;

    @TableField("is_default")
    private Integer isDefault;

    @TableField("remark")
    private String remark;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

