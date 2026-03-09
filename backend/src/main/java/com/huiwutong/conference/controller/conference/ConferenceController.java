package com.huiwutong.conference.controller.conference;

import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.ConferenceService;
import com.huiwutong.conference.service.dto.conference.ConferenceInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端 - 会议信息控制器
 */
@RestController
@RequestMapping("/api/conference")
@RequiredArgsConstructor
public class ConferenceController {

    private final ConferenceService conferenceService;

    /**
     * 获取会议信息
     */
    @GetMapping("/info")
    public Result<ConferenceInfoVo> getConferenceInfo() {
        ConferenceInfoVo vo = conferenceService.getPublishedConferenceInfo();
        return Result.success(vo);
    }
}
