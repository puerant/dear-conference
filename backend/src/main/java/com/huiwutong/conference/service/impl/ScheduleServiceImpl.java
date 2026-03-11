package com.huiwutong.conference.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.Expert;
import com.huiwutong.conference.entity.Schedule;
import com.huiwutong.conference.entity.ScheduleItem;
import com.huiwutong.conference.entity.ScheduleVenue;
import com.huiwutong.conference.mapper.ExpertMapper;
import com.huiwutong.conference.mapper.ScheduleItemMapper;
import com.huiwutong.conference.mapper.ScheduleMapper;
import com.huiwutong.conference.mapper.ScheduleVenueMapper;
import com.huiwutong.conference.service.ScheduleService;
import com.huiwutong.conference.service.dto.conference.ExpertSimpleVo;
import com.huiwutong.conference.service.dto.conference.ScheduleDto;
import com.huiwutong.conference.service.dto.conference.ScheduleGroupVo;
import com.huiwutong.conference.service.dto.conference.ScheduleItemDto;
import com.huiwutong.conference.service.dto.conference.ScheduleItemVo;
import com.huiwutong.conference.service.dto.conference.ScheduleVenueDto;
import com.huiwutong.conference.service.dto.conference.ScheduleVenueVo;
import com.huiwutong.conference.service.dto.conference.ScheduleVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final ScheduleItemMapper scheduleItemMapper;
    private final ScheduleVenueMapper scheduleVenueMapper;
    private final ExpertMapper expertMapper;

    @Override
    public List<ScheduleGroupVo> getScheduleListForPortal(String startDate, String endDate) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Schedule::getDate, Schedule::getVenueId, Schedule::getSortOrder, Schedule::getStartTime);
        if (StrUtil.isNotBlank(startDate)) {
            wrapper.ge(Schedule::getDate, LocalDate.parse(startDate));
        }
        if (StrUtil.isNotBlank(endDate)) {
            wrapper.le(Schedule::getDate, LocalDate.parse(endDate));
        }
        List<Schedule> schedules = scheduleMapper.selectList(wrapper);

        Map<LocalDate, List<ScheduleVo>> grouped = schedules.stream()
                .collect(Collectors.groupingBy(
                        Schedule::getDate,
                        LinkedHashMap::new,
                        Collectors.mapping(this::toVoWithItems, Collectors.toList())
                ));

        return grouped.entrySet().stream()
                .map(entry -> ScheduleGroupVo.builder()
                        .date(entry.getKey().toString())
                        .schedules(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleVo> getScheduleListForAdmin(String startDate, String endDate, Long venueId) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Schedule::getDate, Schedule::getVenueId, Schedule::getStartTime, Schedule::getSortOrder);
        if (StrUtil.isNotBlank(startDate)) {
            wrapper.ge(Schedule::getDate, LocalDate.parse(startDate));
        }
        if (StrUtil.isNotBlank(endDate)) {
            wrapper.le(Schedule::getDate, LocalDate.parse(endDate));
        }
        if (venueId != null) {
            wrapper.eq(Schedule::getVenueId, venueId);
        }
        return scheduleMapper.selectList(wrapper).stream().map(this::toVoWithItems).collect(Collectors.toList());
    }

    @Override
    public ScheduleVo getById(Long id) {
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND);
        }
        return toVoWithItems(schedule);
    }

    @Override
    @Transactional
    public Long create(ScheduleDto dto) {
        validateScheduleTimeRange(dto.getStartTime(), dto.getEndTime());
        ScheduleVenue venue = getRequiredVenue(dto.getVenueId());
        validateVenueTimeConflict(null, dto.getDate(), dto.getVenueId(), dto.getStartTime(), dto.getEndTime());

        Schedule schedule = new Schedule();
        BeanUtil.copyProperties(dto, schedule);
        schedule.setVenueId(venue.getId());
        schedule.setVenue(venue.getName());
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
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND);
        }
        validateScheduleTimeRange(dto.getStartTime(), dto.getEndTime());
        ScheduleVenue venue = getRequiredVenue(dto.getVenueId());
        validateVenueTimeConflict(id, dto.getDate(), dto.getVenueId(), dto.getStartTime(), dto.getEndTime());

        BeanUtil.copyProperties(dto, schedule, "id", "venue");
        schedule.setVenueId(venue.getId());
        schedule.setVenue(venue.getName());
        scheduleMapper.updateById(schedule);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Schedule schedule = scheduleMapper.selectById(id);
        if (schedule == null) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND);
        }
        LambdaQueryWrapper<ScheduleItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.eq(ScheduleItem::getScheduleId, id);
        scheduleItemMapper.delete(itemWrapper);
        scheduleMapper.deleteById(id);
    }

    @Override
    @Transactional
    public Long addScheduleItem(Long scheduleId, ScheduleItemDto dto) {
        Schedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND);
        }

        LocalTime start = normalizeItemStart(dto);
        LocalTime end = normalizeItemEnd(dto);
        validateItemTimeRange(start, end);
        validateItemWithinScheduleRange(schedule, start, end);
        validateItemConflictInSchedule(scheduleId, null, start, end);

        ScheduleItem item = new ScheduleItem();
        BeanUtil.copyProperties(dto, item);
        item.setScheduleId(scheduleId);
        item.setStartTime(start);
        item.setEndTime(end);
        item.setTime(start);
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
            throw new BusinessException(ErrorCode.SCHEDULE_ITEM_NOT_FOUND);
        }
        Schedule schedule = scheduleMapper.selectById(item.getScheduleId());
        if (schedule == null) {
            throw new BusinessException(ErrorCode.SCHEDULE_NOT_FOUND);
        }

        LocalTime start = normalizeItemStart(dto);
        LocalTime end = normalizeItemEnd(dto);
        validateItemTimeRange(start, end);
        validateItemWithinScheduleRange(schedule, start, end);
        validateItemConflictInSchedule(item.getScheduleId(), itemId, start, end);

        BeanUtil.copyProperties(dto, item, "id", "scheduleId", "startTime", "endTime", "time");
        item.setStartTime(start);
        item.setEndTime(end);
        item.setTime(start);
        scheduleItemMapper.updateById(item);
    }

    @Override
    @Transactional
    public void deleteScheduleItem(Long itemId) {
        ScheduleItem item = scheduleItemMapper.selectById(itemId);
        if (item == null) {
            throw new BusinessException(ErrorCode.SCHEDULE_ITEM_NOT_FOUND);
        }
        scheduleItemMapper.deleteById(itemId);
    }

    @Override
    public List<ScheduleVenueVo> getVenueList() {
        LambdaQueryWrapper<ScheduleVenue> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ScheduleVenue::getSortOrder, ScheduleVenue::getId);
        return scheduleVenueMapper.selectList(wrapper).stream().map(this::toVenueVo).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Long createVenue(ScheduleVenueDto dto) {
        String name = dto.getName() == null ? null : dto.getName().trim();
        checkVenueNameDuplicate(null, name);

        ScheduleVenue venue = new ScheduleVenue();
        BeanUtil.copyProperties(dto, venue);
        venue.setName(name);
        if (venue.getSortOrder() == null) {
            venue.setSortOrder(0);
        }
        scheduleVenueMapper.insert(venue);
        return venue.getId();
    }

    @Override
    @Transactional
    public void updateVenue(Long venueId, ScheduleVenueDto dto) {
        ScheduleVenue existing = scheduleVenueMapper.selectById(venueId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "会场不存在");
        }
        String name = dto.getName() == null ? null : dto.getName().trim();
        checkVenueNameDuplicate(venueId, name);

        BeanUtil.copyProperties(dto, existing, "id");
        existing.setName(name);
        if (existing.getSortOrder() == null) {
            existing.setSortOrder(0);
        }
        scheduleVenueMapper.updateById(existing);

        // 同步更新历史冗余会场名称
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getVenueId, venueId);
        List<Schedule> schedules = scheduleMapper.selectList(wrapper);
        for (Schedule schedule : schedules) {
            schedule.setVenue(name);
            scheduleMapper.updateById(schedule);
        }
    }

    @Override
    @Transactional
    public void deleteVenue(Long venueId) {
        ScheduleVenue existing = scheduleVenueMapper.selectById(venueId);
        if (existing == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "会场不存在");
        }

        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getVenueId, venueId);
        Long usedCount = scheduleMapper.selectCount(wrapper);
        if (usedCount != null && usedCount > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "该会场下已有日程，无法删除");
        }

        scheduleVenueMapper.deleteById(venueId);
    }

    private void validateScheduleTimeRange(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "日程开始和结束时间不能为空");
        }
        if (!startTime.isBefore(endTime)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "日程结束时间必须晚于开始时间");
        }
    }

    private void validateVenueTimeConflict(Long currentScheduleId, LocalDate date, Long venueId, LocalTime start, LocalTime end) {
        LambdaQueryWrapper<Schedule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Schedule::getDate, date).eq(Schedule::getVenueId, venueId);
        if (currentScheduleId != null) {
            wrapper.ne(Schedule::getId, currentScheduleId);
        }
        List<Schedule> sameVenueSchedules = scheduleMapper.selectList(wrapper);
        boolean conflict = sameVenueSchedules.stream().anyMatch(existing ->
                existing.getStartTime() != null
                        && existing.getEndTime() != null
                        && isTimeOverlap(start, end, existing.getStartTime(), existing.getEndTime())
        );
        if (conflict) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "同一会场存在时间冲突，请调整日程时间");
        }
    }

    private LocalTime normalizeItemStart(ScheduleItemDto dto) {
        return dto.getStartTime() != null ? dto.getStartTime() : dto.getTime();
    }

    private LocalTime normalizeItemEnd(ScheduleItemDto dto) {
        if (dto.getEndTime() != null) {
            return dto.getEndTime();
        }
        return dto.getTime();
    }

    private void validateItemTimeRange(LocalTime start, LocalTime end) {
        if (start == null || end == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "日程项开始和结束时间不能为空");
        }
        if (!start.isBefore(end)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "日程项结束时间必须晚于开始时间");
        }
    }

    private void validateItemWithinScheduleRange(Schedule schedule, LocalTime itemStart, LocalTime itemEnd) {
        LocalTime scheduleStart = schedule.getStartTime();
        LocalTime scheduleEnd = schedule.getEndTime();
        if (scheduleStart == null || scheduleEnd == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "所属日程未配置完整时间范围");
        }
        if (itemStart.isBefore(scheduleStart) || itemEnd.isAfter(scheduleEnd)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "日程项时间范围不能超出所属日程");
        }
    }

    private void validateItemConflictInSchedule(Long scheduleId, Long currentItemId, LocalTime start, LocalTime end) {
        LambdaQueryWrapper<ScheduleItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleItem::getScheduleId, scheduleId);
        if (currentItemId != null) {
            wrapper.ne(ScheduleItem::getId, currentItemId);
        }
        List<ScheduleItem> existingItems = scheduleItemMapper.selectList(wrapper);
        boolean conflict = existingItems.stream().anyMatch(existing -> {
            LocalTime existStart = existing.getStartTime() != null ? existing.getStartTime() : existing.getTime();
            LocalTime existEnd = existing.getEndTime() != null ? existing.getEndTime() : existing.getTime();
            if (existStart == null || existEnd == null || !existStart.isBefore(existEnd)) {
                return false;
            }
            return isTimeOverlap(start, end, existStart, existEnd);
        });
        if (conflict) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "同一日程下的日程项时间存在重叠");
        }
    }

    private void checkVenueNameDuplicate(Long currentVenueId, String name) {
        LambdaQueryWrapper<ScheduleVenue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleVenue::getName, name);
        if (currentVenueId != null) {
            wrapper.ne(ScheduleVenue::getId, currentVenueId);
        }
        Long count = scheduleVenueMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "会场名称已存在");
        }
    }

    private ScheduleVenue getRequiredVenue(Long venueId) {
        ScheduleVenue venue = scheduleVenueMapper.selectById(venueId);
        if (venue == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "会场不存在，请先创建会场");
        }
        return venue;
    }

    private boolean isTimeOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private ScheduleVo toVoWithItems(Schedule schedule) {
        ScheduleVo vo = ScheduleVo.builder()
                .id(schedule.getId())
                .date(schedule.getDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .title(schedule.getTitle())
                .description(schedule.getDescription())
                .venueId(schedule.getVenueId())
                .venue(schedule.getVenue())
                .sortOrder(schedule.getSortOrder())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();

        LambdaQueryWrapper<ScheduleItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleItem::getScheduleId, schedule.getId());
        wrapper.orderByAsc(ScheduleItem::getSortOrder, ScheduleItem::getStartTime, ScheduleItem::getTime);
        List<ScheduleItem> items = scheduleItemMapper.selectList(wrapper);
        vo.setItems(items.stream().map(this::toItemVo).collect(Collectors.toList()));

        return vo;
    }

    private ScheduleItemVo toItemVo(ScheduleItem item) {
        LocalTime start = item.getStartTime() != null ? item.getStartTime() : item.getTime();
        LocalTime end = item.getEndTime() != null ? item.getEndTime() : item.getTime();
        ScheduleItemVo vo = ScheduleItemVo.builder()
                .id(item.getId())
                .scheduleId(item.getScheduleId())
                .startTime(start)
                .endTime(end)
                .time(start)
                .title(item.getTitle())
                .description(item.getDescription())
                .speakerId(item.getSpeakerId())
                .sortOrder(item.getSortOrder())
                .createdAt(item.getCreatedAt())
                .build();

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

    private ScheduleVenueVo toVenueVo(ScheduleVenue venue) {
        return ScheduleVenueVo.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                .description(venue.getDescription())
                .sortOrder(venue.getSortOrder())
                .createdAt(venue.getCreatedAt())
                .updatedAt(venue.getUpdatedAt())
                .build();
    }
}
