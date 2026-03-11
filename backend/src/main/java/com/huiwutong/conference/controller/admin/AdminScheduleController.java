package com.huiwutong.conference.controller.admin;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.ScheduleService;
import com.huiwutong.conference.service.dto.conference.ScheduleDto;
import com.huiwutong.conference.service.dto.conference.ScheduleItemDto;
import com.huiwutong.conference.service.dto.conference.ScheduleVenueDto;
import com.huiwutong.conference.service.dto.conference.ScheduleVenueVo;
import com.huiwutong.conference.service.dto.conference.ScheduleVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminScheduleController {

    private final ScheduleService scheduleService;

    @GetMapping("/schedule")
    @RequireRole("admin")
    public Result<List<ScheduleVo>> getScheduleList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Long venueId) {
        List<ScheduleVo> list = scheduleService.getScheduleListForAdmin(startDate, endDate, venueId);
        return Result.success(list);
    }

    @GetMapping("/schedule/{id}")
    @RequireRole("admin")
    public Result<ScheduleVo> getScheduleById(@PathVariable Long id) {
        ScheduleVo vo = scheduleService.getById(id);
        return Result.success(vo);
    }

    @PostMapping("/schedule")
    @RequireRole("admin")
    public Result<Long> createSchedule(@Valid @RequestBody ScheduleDto dto) {
        Long id = scheduleService.create(dto);
        return Result.success(id);
    }

    @PutMapping("/schedule/{id}")
    @RequireRole("admin")
    public Result<Void> updateSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleDto dto) {
        scheduleService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/schedule/{id}")
    @RequireRole("admin")
    public Result<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.delete(id);
        return Result.success();
    }

    @PostMapping("/schedule/{id}/items")
    @RequireRole("admin")
    public Result<Long> addScheduleItem(@PathVariable Long id, @Valid @RequestBody ScheduleItemDto dto) {
        dto.setScheduleId(id);
        Long itemId = scheduleService.addScheduleItem(id, dto);
        return Result.success(itemId);
    }

    @PutMapping("/schedule-item/{id}")
    @RequireRole("admin")
    public Result<Void> updateScheduleItem(@PathVariable Long id, @Valid @RequestBody ScheduleItemDto dto) {
        scheduleService.updateScheduleItem(id, dto);
        return Result.success();
    }

    @DeleteMapping("/schedule-item/{id}")
    @RequireRole("admin")
    public Result<Void> deleteScheduleItem(@PathVariable Long id) {
        scheduleService.deleteScheduleItem(id);
        return Result.success();
    }

    @GetMapping("/schedule-venues")
    @RequireRole("admin")
    public Result<List<ScheduleVenueVo>> getVenueList() {
        return Result.success(scheduleService.getVenueList());
    }

    @PostMapping("/schedule-venues")
    @RequireRole("admin")
    public Result<Long> createVenue(@Valid @RequestBody ScheduleVenueDto dto) {
        return Result.success(scheduleService.createVenue(dto));
    }

    @PutMapping("/schedule-venues/{id}")
    @RequireRole("admin")
    public Result<Void> updateVenue(@PathVariable Long id, @Valid @RequestBody ScheduleVenueDto dto) {
        scheduleService.updateVenue(id, dto);
        return Result.success();
    }

    @DeleteMapping("/schedule-venues/{id}")
    @RequireRole("admin")
    public Result<Void> deleteVenue(@PathVariable Long id) {
        scheduleService.deleteVenue(id);
        return Result.success();
    }
}
