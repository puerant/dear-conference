package com.huiwutong.conference.service.dto.conference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 日程 DTO
 */
@Data
public class ScheduleDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键（更新时必填）
     */
    private Long id;

    /**
     * 日期
     */
    @NotNull(message = "日期不能为空")
    private LocalDate date;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

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
     * 场地
     */
    @Size(max = 200, message = "场地长度不能超过200字符")
    private String venue;

    /**
     * 排序
     */
    private Integer sortOrder;
}
