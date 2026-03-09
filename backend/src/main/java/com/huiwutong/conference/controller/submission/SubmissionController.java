package com.huiwutong.conference.controller.submission;

import com.huiwutong.conference.common.annotation.RequireLogin;
import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.Submission;
import com.huiwutong.conference.service.SubmissionService;
import com.huiwutong.conference.service.dto.submission.CreateSubmissionDto;
import com.huiwutong.conference.service.dto.submission.SubmissionQueryDto;
import com.huiwutong.conference.service.dto.submission.UpdateSubmissionDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 投稿控制器（讲者/审稿人使用）
 */
@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

    private final SubmissionService submissionService;

    /**
     * 创建投稿（讲者）
     */
    @PostMapping
    @RequireRole("speaker")
    public Result<Submission> create(@Valid @RequestBody CreateSubmissionDto dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(submissionService.create(userId, dto));
    }

    /**
     * 获取我的投稿列表（讲者）
     */
    @GetMapping("/my")
    @RequireRole("speaker")
    public Result<PageResult<Submission>> listMy(SubmissionQueryDto query, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(submissionService.listByUser(userId, query));
    }

    /**
     * 获取所有投稿（审稿人）
     */
    @GetMapping
    @RequireRole({"reviewer", "admin"})
    public Result<PageResult<Submission>> listAll(SubmissionQueryDto query) {
        return Result.success(submissionService.listAll(query));
    }

    /**
     * 获取投稿详情
     */
    @GetMapping("/{id}")
    @RequireLogin
    public Result<Submission> getDetail(@PathVariable Long id) {
        return Result.success(submissionService.getById(id));
    }

    /**
     * 修改投稿（讲者，仅限 pending 状态）
     */
    @PutMapping("/{id}")
    @RequireRole("speaker")
    public Result<Submission> update(@PathVariable Long id, @Valid @RequestBody UpdateSubmissionDto dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(submissionService.update(id, userId, dto));
    }

    /**
     * 删除投稿（讲者，仅限 pending 状态）
     */
    @DeleteMapping("/{id}")
    @RequireRole("speaker")
    public Result<Void> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        submissionService.delete(id, userId);
        return Result.success();
    }
}
