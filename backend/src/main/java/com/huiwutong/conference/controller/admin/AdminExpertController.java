package com.huiwutong.conference.controller.admin;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.ExpertService;
import com.huiwutong.conference.service.dto.conference.ExpertDto;
import com.huiwutong.conference.service.dto.conference.ExpertVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 - 专家控制器
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminExpertController {

    private final ExpertService expertService;

    /**
     * 获取专家列表
     */
    @GetMapping("/experts")
    @RequireRole("admin")
    public Result<List<ExpertVo>> getExpertList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer isKeynote) {
        List<ExpertVo> list = expertService.getExpertListForAdmin(keyword, isKeynote);
        return Result.success(list);
    }

    /**
     * 获取专家详情
     */
    @GetMapping("/expert/{id}")
    @RequireRole("admin")
    public Result<ExpertVo> getExpertById(@PathVariable Long id) {
        ExpertVo vo = expertService.getById(id);
        return Result.success(vo);
    }

    /**
     * 创建专家
     */
    @PostMapping("/expert")
    @RequireRole("admin")
    public Result<Long> createExpert(@Valid @RequestBody ExpertDto dto) {
        Long id = expertService.create(dto);
        return Result.success(id);
    }

    /**
     * 更新专家
     */
    @PutMapping("/expert/{id}")
    @RequireRole("admin")
    public Result<Void> updateExpert(@PathVariable Long id, @Valid @RequestBody ExpertDto dto) {
        expertService.update(id, dto);
        return Result.success();
    }

    /**
     * 删除专家
     */
    @DeleteMapping("/expert/{id}")
    @RequireRole("admin")
    public Result<Void> deleteExpert(@PathVariable Long id) {
        expertService.delete(id);
        return Result.success();
    }

    /**
     * 更新专家头像
     */
    @PostMapping("/expert/{id}/avatar")
    @RequireRole("admin")
    public Result<Void> updateAvatar(@PathVariable Long id, @RequestParam String avatarUrl) {
        expertService.updateAvatar(id, avatarUrl);
        return Result.success();
    }
}
