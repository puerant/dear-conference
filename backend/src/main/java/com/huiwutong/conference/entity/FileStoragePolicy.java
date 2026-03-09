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
 * 文件存储策略
 */
@Data
@TableName("file_storage_policy")
public class FileStoragePolicy implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("policy_name")
    private String policyName;

    @TableField("file_category")
    private String fileCategory;

    @TableField("file_type")
    private String fileType;

    @TableField("provider_id")
    private Long providerId;

    @TableField("bucket_id")
    private Long bucketId;

    @TableField("path_rule")
    private String pathRule;

    @TableField("max_size_mb")
    private Integer maxSizeMb;

    @TableField("allowed_ext")
    private String allowedExt;

    @TableField("priority")
    private Integer priority;

    @TableField("is_enabled")
    private Integer isEnabled;

    @TableField("remark")
    private String remark;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

