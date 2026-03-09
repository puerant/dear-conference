package com.huiwutong.conference.service.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 日程项 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleItemVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long scheduleId;
    private LocalTime time;
    private String title;
    private String description;
    private Long speakerId;
    private Integer sortOrder;
    private LocalDateTime createdAt;

    /**
     * 演讲人信息
     */
    private ExpertSimpleVo speaker;
}
