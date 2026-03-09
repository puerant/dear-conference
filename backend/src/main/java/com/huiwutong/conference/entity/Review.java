package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.huiwutong.conference.common.enums.ReviewResult;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审稿记录实体
 */
@Data
@TableName("reviews")
public class Review implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("submission_id")
    private Long submissionId;

    @TableField("reviewer_id")
    private Long reviewerId;

    @TableField("comment")
    private String comment;

    @TableField("result")
    private ReviewResult result;

    @TableField("score")
    private Integer score;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
