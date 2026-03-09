package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 日程项实体
 */
@Data
@TableName("schedule_item")
public class ScheduleItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("schedule_id")
    private Long scheduleId;

    @TableField("time")
    private LocalTime time;

    @TableField("title")
    private String title;

    @TableField("description")
    private String description;

    @TableField("speaker_id")
    private Long speakerId;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
