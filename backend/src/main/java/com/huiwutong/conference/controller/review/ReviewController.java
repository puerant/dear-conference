package com.huiwutong.conference.controller.review;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.Review;
import com.huiwutong.conference.service.ReviewService;
import com.huiwutong.conference.service.dto.review.CreateReviewDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 审稿控制器
 */
@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 提交审稿意见（审稿人）
     */
    @PostMapping
    @RequireRole("reviewer")
    public Result<Review> create(@Valid @RequestBody CreateReviewDto dto, HttpServletRequest request) {
        Long reviewerId = (Long) request.getAttribute("userId");
        return Result.success(reviewService.create(reviewerId, dto));
    }

    /**
     * 获取投稿的审稿列表
     */
    @GetMapping("/submission/{submissionId}")
    @RequireRole({"reviewer", "admin"})
    public Result<List<Review>> listBySubmission(@PathVariable Long submissionId) {
        return Result.success(reviewService.listBySubmission(submissionId));
    }

    /**
     * 获取我的审稿记录（审稿人）
     */
    @GetMapping("/my")
    @RequireRole("reviewer")
    public Result<List<Review>> listMy(HttpServletRequest request) {
        Long reviewerId = (Long) request.getAttribute("userId");
        return Result.success(reviewService.listByReviewer(reviewerId));
    }
}
