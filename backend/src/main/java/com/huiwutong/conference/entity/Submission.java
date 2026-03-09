package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.huiwutong.conference.common.enums.SubmissionStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 投稿实体
 */
@Data
@TableName("submissions")
public class Submission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("title")
    private String title;

    @JsonProperty("abstract")
    @TableField("`abstract`")
    private String abstract0;

    @TableField("category")
    private String category;

    @TableField("file_url")
    private String fileUrl;

    @TableField("status")
    private SubmissionStatus status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private String speakerName;

    @TableField(exist = false)
    private String speakerEmail;
}
