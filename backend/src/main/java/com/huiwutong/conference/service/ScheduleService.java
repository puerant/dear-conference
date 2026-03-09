package com.huiwutong.conference.service;

import com.huiwutong.conference.service.dto.conference.*;

import java.util.List;

/**
 * 日程服务接口
 */
public interface ScheduleService {

    /**
     * 获取日程列表（用户端，按日期分组）
     */
    List<ScheduleGroupVo> getScheduleListForPortal(String startDate, String endDate);

    /**
     * 获取日程列表（管理端）
     */
    List<ScheduleVo> getScheduleListForAdmin(String startDate, String endDate);

    /**
     * 获取日程详情
     */
    ScheduleVo getById(Long id);

    /**
     * 创建日程
     */
    Long create(ScheduleDto dto);

    /**
     * 更新日程
     */
    void update(Long id, ScheduleDto dto);

    /**
     * 删除日程
     */
    void delete(Long id);

    /**
     * 添加日程项
     */
    Long addScheduleItem(Long scheduleId, ScheduleItemDto dto);

    /**
     * 更新日程项
     */
    void updateScheduleItem(Long itemId, ScheduleItemDto dto);

    /**
     * 删除日程项
     */
    void deleteScheduleItem(Long itemId);
}
