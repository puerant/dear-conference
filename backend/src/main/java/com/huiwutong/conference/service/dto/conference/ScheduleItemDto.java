package com.huiwutong.conference.service.dto.conference;

import jakarta.validation.constraints.NotBlank;
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

    private Long id;

    /**
     * 由路径参数回填
     */
    private Long scheduleId;

    /**
     * 时间段开始
     */
    private LocalTime startTime;

    /**
     * 时间段结束
     */
    private LocalTime endTime;

    /**
     * 兼容旧数据/旧前端，未传 startTime/endTime 时可退化读取此值
     */
    private LocalTime time;

    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    private String description;

    private Long speakerId;

    private Integer sortOrder;
}
