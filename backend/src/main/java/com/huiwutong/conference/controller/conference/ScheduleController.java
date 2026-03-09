package com.huiwutong.conference.controller.conference;

import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.ScheduleService;
import com.huiwutong.conference.service.dto.conference.ScheduleGroupVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端 - 日程控制器
 */
@RestController
@RequestMapping("/api/conference")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    /**
     * 获取日程列表
     */
    @GetMapping("/schedule")
    public Result<List<ScheduleGroupVo>> getScheduleList(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<ScheduleGroupVo> list = scheduleService.getScheduleListForPortal(startDate, endDate);
        return Result.success(list);
    }
}
