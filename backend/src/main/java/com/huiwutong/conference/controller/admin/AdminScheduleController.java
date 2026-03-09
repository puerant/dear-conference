package com.huiwutong.conference.controller.admin;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.ScheduleService;
import com.huiwutong.conference.service.dto.conference.ScheduleDto;
import com.huiwutong.conference.service.dto.conference.ScheduleGroupVo;
import com.huiwutong.conference.service.dto.conference.ScheduleItemDto;
import com.huiwutong.conference.service.dto.conference.ScheduleVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 - 日程控制器
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 获取日程列表
     */
    @GetMapping("/schedule")
    @RequireRole("admin")
    public Result<List<ScheduleVo>> getScheduleList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<ScheduleVo> list = scheduleService.getScheduleListForAdmin(startDate, endDate);
        return Result.success(list);
    }

    /**
     * 获取日程详情
     */
    @GetMapping("/schedule/{id}")
    @RequireRole("admin")
    public Result<ScheduleVo> getScheduleById(@PathVariable Long id) {
        ScheduleVo vo = scheduleService.getById(id);
        return Result.success(vo);
    }

    /**
     * 创建日程
     */
    @PostMapping("/schedule")
    @RequireRole("admin")
    public Result<Long> createSchedule(@Valid @RequestBody ScheduleDto dto) {
        Long id = scheduleService.create(dto);
        return Result.success(id);
    }

    /**
     * 更新日程
     */
    @PutMapping("/schedule/{id}")
    @RequireRole("admin")
    public Result<Void> updateSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleDto dto) {
        scheduleService.update(id, dto);
        return Result.success();
    }

    /**
     * 删除日程
     */
    @DeleteMapping("/schedule/{id}")
    @RequireRole("admin")
    public Result<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.delete(id);
        return Result.success();
    }

    /**
     * 添加日程项
     */
    @PostMapping("/schedule/{id}/items")
    @RequireRole("admin")
    public Result<Long> addScheduleItem(@PathVariable Long id, @Valid @RequestBody ScheduleItemDto dto) {
        dto.setScheduleId(id);
        Long itemId = scheduleService.addScheduleItem(id, dto);
        return Result.success(itemId);
    }

    /**
     * 更新日程项
     */
    @PutMapping("/schedule-item/{id}")
    @RequireRole("admin")
    public Result<Void> updateScheduleItem(@PathVariable Long id, @Valid @RequestBody ScheduleItemDto dto) {
        scheduleService.updateScheduleItem(id, dto);
        return Result.success();
    }

    /**
     * 删除日程项
     */
    @DeleteMapping("/schedule-item/{id}")
    @RequireRole("admin")
    public Result<Void> deleteScheduleItem(@PathVariable Long id) {
        scheduleService.deleteScheduleItem(id);
        return Result.success();
    }
}
