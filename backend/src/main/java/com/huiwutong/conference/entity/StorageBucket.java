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
 * 存储桶配置
 */
@Data
@TableName("storage_bucket")
public class StorageBucket implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("provider_id")
    private Long providerId;

    @TableField("bucket_name")
    private String bucketName;

    @TableField("base_path")
    private String basePath;

    @TableField("domain")
    private String domain;

    @TableField("acl_type")
    private Integer aclType;

    @TableField("is_enabled")
    private Integer isEnabled;

    @TableField("is_default")
    private Integer isDefault;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

