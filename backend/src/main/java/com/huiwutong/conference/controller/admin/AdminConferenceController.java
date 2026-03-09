package com.huiwutong.conference.controller.admin;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.ConferenceService;
import com.huiwutong.conference.service.dto.conference.ConferenceInfoDto;
import com.huiwutong.conference.service.dto.conference.ConferenceInfoVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 会议信息控制器
 */
@RestController
@RequestMapping("/api/admin/conference")
@RequiredArgsConstructor
public class AdminConferenceController {

    private final ConferenceService conferenceService;

    /**
     * 获取会议信息（管理端）
     */
    @GetMapping
    @RequireRole("admin")
    public Result<ConferenceInfoVo> getConferenceInfo() {
        ConferenceInfoVo vo = conferenceService.getConferenceInfo();
        return Result.success(vo);
    }

    /**
     * 创建/更新会议信息
     */
    @PostMapping
    @RequireRole("admin")
    public Result<Void> saveOrUpdate(@Valid @RequestBody ConferenceInfoDto dto) {
        conferenceService.saveOrUpdate(dto);
        return Result.success();
    }
}
