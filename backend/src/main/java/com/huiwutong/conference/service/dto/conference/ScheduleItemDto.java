package com.huiwutong.conference.service.dto.conference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalTime;

/**
 * 日程项 DTO
 */
@Data
public class ScheduleItemDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键（更新时必填）
     */
    private Long id;

    /**
     * 日程ID
     */
    @NotNull(message = "日程ID不能为空")
    private Long scheduleId;

    /**
     * 具体时间
     */
    @NotNull(message = "时间不能为空")
    private LocalTime time;

    /**
     * 标题
     */
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 演讲人ID
     */
    private Long speakerId;

    /**
     * 排序
     */
    private Integer sortOrder;
}
