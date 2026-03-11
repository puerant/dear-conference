package com.huiwutong.conference.service;

import com.huiwutong.conference.service.dto.conference.ScheduleDto;
import com.huiwutong.conference.service.dto.conference.ScheduleGroupVo;
import com.huiwutong.conference.service.dto.conference.ScheduleItemDto;
import com.huiwutong.conference.service.dto.conference.ScheduleVenueDto;
import com.huiwutong.conference.service.dto.conference.ScheduleVenueVo;
import com.huiwutong.conference.service.dto.conference.ScheduleVo;

import java.util.List;

/**
 * 日程服务接口
 */
public interface ScheduleService {

    List<ScheduleGroupVo> getScheduleListForPortal(String startDate, String endDate);

    List<ScheduleVo> getScheduleListForAdmin(String startDate, String endDate, Long venueId);

    ScheduleVo getById(Long id);

    Long create(ScheduleDto dto);

    void update(Long id, ScheduleDto dto);

    void delete(Long id);

    Long addScheduleItem(Long scheduleId, ScheduleItemDto dto);

    void updateScheduleItem(Long itemId, ScheduleItemDto dto);

    void deleteScheduleItem(Long itemId);

    List<ScheduleVenueVo> getVenueList();

    Long createVenue(ScheduleVenueDto dto);

    void updateVenue(Long venueId, ScheduleVenueDto dto);

    void deleteVenue(Long venueId);
}
