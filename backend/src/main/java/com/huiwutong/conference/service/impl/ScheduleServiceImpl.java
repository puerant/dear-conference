package com.huiwutong.conference.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.Expert;
import com.huiwutong.conference.entity.Schedule;
import com.huiwutong.conference.entity.ScheduleItem;
import com.huiwutong.conference.mapper.ExpertMapper;
import com.huiwutong.conference.mapper.ScheduleItemMapper;
import com.huiwutong.conference.mapper.ScheduleMapper;
import com.huiwutong.conference.service.ScheduleService;
import com.huiwutong.conference.service.dto.conference.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 日程服务实现
 */
@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final ScheduleItemMapper scheduleItemMapper;
    private final ExpertMapper expertMapper;

    @Override
    public List<ScheduleGroupVo> getScheduleListForPortal(String startDate, String endDate) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Schedule::getDate, Schedule::getSortOrder);

        if (StrUtil.isNotBlank(startDate)) {
            wrapper.ge(Schedule::getDate, LocalDate.parse(startDate));
        }
        if (StrUtil.isNotBlank(endDate)) {
            wrapper.le(Schedule::getDate, LocalDate.parse(endDate));
        }

        List<Schedule> schedules = scheduleMapper.selectList(wrapper);

        // 按日期分组
        Map<LocalDate, List<ScheduleVo>> grouped = schedules.stream()
                .collect(Collectors.groupingBy(
                        Schedule::getDate,
                        LinkedHashMap::new,
                        Collectors.mapping(this::toVoWithItems, Collectors.toList())
                ));

        // 转换为响应格式
        return grouped.entrySet().stream()
                .map(entry -> ScheduleGroupVo.builder()
                        .date(entry.getKey().toString())
                        .schedules(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleVo> getScheduleListForAdmin(String startDate, String endDate) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Schedule::getDate, Schedule::getSortOrder);

        if (StrUtil.isNotBlank(startDate)) {
            wrapper.ge(Schedule::getDate, LocalDate.parse(startDate));
        }
        if (StrUtil.isNotBlank(endDate)) {
            wrapper.le(Schedule::getDate, LocalDate.parse(endDate));
        }

        List<Schedule> schedules = scheduleMapper.selectList(wrapper);
        return schedules.stream().map(this::toVoWithItems).collect(Collectors.toList());
    }

    @Override
    public ScheduleVo getById(Long id) {
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "日程不存在");
        }
        return toVoWithItems(schedule);
    }

    @Override
    @Transactional
    public Long create(ScheduleDto dto) {
        Schedule schedule = new Schedule();
        BeanUtil.copyProperties(dto, schedule);
        if (schedule.getSortOrder() == null) {
            schedule.setSortOrder(0);
        }
        scheduleMapper.insert(schedule);
        return schedule.getId();
    }

    @Override
    @Transactional
    public void update(Long id, ScheduleDto dto) {
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "日程不存在");
        }
        BeanUtil.copyProperties(dto, schedule, "id");
        scheduleMapper.updateById(schedule);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "日程不存在");
        }
        // 先删除关联的日程项
        LambdaQueryWrapper<ScheduleItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(ScheduleItem::getScheduleId, id);
        scheduleItemMapper.delete(itemWrapper);
        // 再删除日程
        scheduleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public Long addScheduleItem(Long scheduleId, ScheduleItemDto dto) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "日程不存在");
        }

        ScheduleItem item = new ScheduleItem();
        BeanUtil.copyProperties(dto, item);
        item.setScheduleId(scheduleId);
        if (item.getSortOrder() == null) {
            item.setSortOrder(0);
        }
        scheduleItemMapper.insert(item);
        return item.getId();
    }

    @Override
    @Transactional
    public void updateScheduleItem(Long itemId, ScheduleItemDto dto) {
        ScheduleItem item = scheduleItemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "日程项不存在");
        }
        BeanUtil.copyProperties(dto, item, "id", "scheduleId");
        scheduleItemMapper.updateById(item);
    }

    @Override
    @Transactional
    public void deleteScheduleItem(Long itemId) {
        ScheduleItem item = scheduleItemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "日程项不存在");
        }
        scheduleItemMapper.deleteById(itemId);
    }

    private ScheduleVo toVoWithItems(Schedule schedule) {
        ScheduleVo vo = ScheduleVo.builder()
                .id(schedule.getId())
                .date(schedule.getDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .venue(schedule.getVenue())
                .sortOrder(schedule.getSortOrder())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();

        // 查询日程项
        LambdaQueryWrapper<ScheduleItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleItem::getScheduleId, schedule.getId());
        wrapper.orderByAsc(ScheduleItem::getSortOrder, ScheduleItem::getTime);

        List<ScheduleItem> items = scheduleItemMapper.selectList(wrapper);
        vo.setItems(items.stream().map(this::toItemVo).collect(Collectors.toList()));

        return vo;
    }

    private ScheduleItemVo toItemVo(ScheduleItem item) {
        ScheduleItemVo vo = ScheduleItemVo.builder()
                .id(item.getId())
                .scheduleId(item.getScheduleId())
                .time(item.getTime())
                .title(item.getTitle())
                .description(item.getDescription())
                .speakerId(item.getSpeakerId())
                .sortOrder(item.getSortOrder())
                .createdAt(item.getCreatedAt())
                .build();

        // 查询演讲人信息
        if (item.getSpeakerId() != null) {
            Expert expert = expertMapper.selectById(item.getSpeakerId());
            if (expert != null) {
                vo.setSpeaker(ExpertSimpleVo.builder()
                        .id(expert.getId())
                        .name(expert.getName())
                        .title(expert.getTitle())
                        .organization(expert.getOrganization())
                        .avatarUrl(expert.getAvatarUrl())
                        .build());
            }
        }

        return vo;
    }
}
