package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统文件实体
 */
@Data
@TableName("system_file")
public class SystemFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("file_name")
    private String fileName;

    @TableField("file_path")
    private String filePath;

    @TableField("file_url")
    private String fileUrl;

    @TableField("file_type")
    private String fileType;

    @TableField("file_extension")
    private String fileExtension;

    @TableField("mime_type")
    private String mimeType;

    @TableField("file_size")
    private Long fileSize;

    @TableField("file_category")
    private String fileCategory;

    @TableField("category_id")
    private Long categoryId;

    @TableField("upload_user_id")
    private Long uploadUserId;

    @TableField("upload_user_name")
    private String uploadUserName;

    @TableField("storage_type")
    private Integer storageType;

    @TableField("provider_id")
    private Long providerId;

    @TableField("bucket_id")
    private Long bucketId;

    @TableField("object_key")
    private String objectKey;

    @TableField("storage_path")
    private String storagePath;

    @TableField("visibility")
    private Integer visibility;

    @TableField("version_no")
    private Integer versionNo;

    @TableField("is_migrated")
    private Integer isMigrated;

    @TableField("md5")
    private String md5;

    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
