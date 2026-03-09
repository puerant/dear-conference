package com.huiwutong.conference.controller.conference;

import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.ExpertService;
import com.huiwutong.conference.service.dto.conference.ExpertListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端 - 专家控制器
 */
@RestController
@RequestMapping("/api/conference")
@RequiredArgsConstructor
public class ExpertController {

    private final ExpertService expertService;

    /**
     * 获取专家列表
     */
    @GetMapping("/experts")
    public Result<ExpertListVo> getExpertList(
            @RequestParam(required = false) Boolean isKeynote) {
        ExpertListVo vo = expertService.getExpertListForPortal(isKeynote);
        return Result.success(vo);
    }
}
