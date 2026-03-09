package com.huiwutong.conference.service.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 日程 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String title;
    private String description;
    private String venue;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 日程项列表
     */
    private List<ScheduleItemVo> items;
}
