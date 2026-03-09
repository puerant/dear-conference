package com.huiwutong.conference.service.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 按日期分组的日程 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleGroupVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期 (yyyy-MM-dd)
     */
    private String date;

    /**
     * 该日期下的日程列表
     */
    private List<ScheduleVo> schedules;
}
