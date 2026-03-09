package com.huiwutong.conference.controller.admin;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.enums.SubmissionStatus;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.Submission;
import com.huiwutong.conference.service.ReviewService;
import com.huiwutong.conference.service.SubmissionService;
import com.huiwutong.conference.service.dto.submission.SubmissionQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理端 - 投稿管理控制器
 */
@RestController
@RequestMapping("/api/admin/submissions")
@RequiredArgsConstructor
public class AdminSubmissionController {

    private final SubmissionService submissionService;
    private final ReviewService reviewService;

    @GetMapping
    @RequireRole("admin")
    public Result<PageResult<Submission>> list(SubmissionQueryDto query) {
        return Result.success(submissionService.listAll(query));
    }

    @GetMapping("/{id}")
    @RequireRole("admin")
    public Result<Submission> detail(@PathVariable Long id) {
        return Result.success(submissionService.getById(id));
    }

    /**
     * 更新投稿状态（接受字符串 code，如 "reviewing"）
     */
    @PutMapping("/{id}/status")
    @RequireRole("admin")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String statusCode = body.get("status");
        SubmissionStatus status = SubmissionStatus.ofCode(statusCode);
        if (status == null) {
            throw new BusinessException(ErrorCode.SUBMISSION_STATUS_ERROR);
        }
        submissionService.updateStatus(id, status.getValue());
        return Result.success();
    }

    /**
     * 分配审稿人：将投稿状态置为 REVIEWING，并在 reviews 表为每位审稿人创建 result=null 的占位记录，
     * 使审稿人可通过 GET /reviews/my 发现待审稿投稿。
     */
    @PutMapping("/{id}/assign")
    @RequireRole("admin")
    public Result<Void> assignReviewer(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        submissionService.updateStatus(id, SubmissionStatus.REVIEWING.getValue());

        @SuppressWarnings("unchecked")
        List<Integer> rawIds = (List<Integer>) body.get("reviewerIds");
        List<Long> reviewerIds = rawIds != null
                ? rawIds.stream().map(Integer::longValue).collect(Collectors.toList())
                : Collections.emptyList();
        if (!reviewerIds.isEmpty()) {
            reviewService.assign(id, reviewerIds);
        }

        return Result.success();
    }
}
