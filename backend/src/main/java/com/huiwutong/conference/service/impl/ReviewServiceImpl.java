package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.enums.ReviewResult;
import com.huiwutong.conference.common.enums.SubmissionStatus;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.Review;
import com.huiwutong.conference.entity.Submission;
import com.huiwutong.conference.entity.User;
import com.huiwutong.conference.mapper.ReviewMapper;
import com.huiwutong.conference.mapper.SubmissionMapper;
import com.huiwutong.conference.mapper.UserMapper;
import com.huiwutong.conference.service.EmailService;
import com.huiwutong.conference.service.ReviewService;
import com.huiwutong.conference.service.dto.review.CreateReviewDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 审稿服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final SubmissionMapper submissionMapper;
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Review create(Long reviewerId, CreateReviewDto dto) {
        Submission submission = submissionMapper.selectById(dto.getSubmissionId());
        if (submission == null) {
            throw new BusinessException(ErrorCode.SUBMISSION_NOT_FOUND);
        }
        if (submission.getStatus() != SubmissionStatus.REVIEWING) {
            throw new BusinessException(ErrorCode.SUBMISSION_STATUS_ERROR);
        }

        Review existing = reviewMapper.selectOne(new LambdaQueryWrapper<Review>()
                .eq(Review::getSubmissionId, dto.getSubmissionId())
                .eq(Review::getReviewerId, reviewerId));
        if (existing != null && existing.getResult() != null) {
            throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review review;
        if (existing != null) {
            // 更新 assign 时预创建的占位记录
            existing.setComment(dto.getComment());
            existing.setResult(ReviewResult.ofValue(dto.getResult()));
            existing.setScore(dto.getScore());
            reviewMapper.updateById(existing);
            review = existing;
        } else {
            review = new Review();
            review.setSubmissionId(dto.getSubmissionId());
            review.setReviewerId(reviewerId);
            review.setComment(dto.getComment());
            review.setResult(ReviewResult.ofValue(dto.getResult()) != null
                    ? ReviewResult.ofValue(dto.getResult()) : null);
            review.setScore(dto.getScore());
            reviewMapper.insert(review);
        }

        // 更新投稿状态
        if (dto.getResult() != null) {
            ReviewResult result = ReviewResult.ofValue(dto.getResult());
            if (result == ReviewResult.ACCEPT) {
                submission.setStatus(SubmissionStatus.ACCEPTED);
            } else if (result == ReviewResult.REJECT) {
                submission.setStatus(SubmissionStatus.REJECTED);
            }
            submissionMapper.updateById(submission);
        }

        return review;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assign(Long submissionId, List<Long> reviewerIds) {
        Submission submission = submissionMapper.selectById(submissionId);
        String submissionTitle = submission != null ? submission.getTitle() : "";

        for (Long reviewerId : reviewerIds) {
            long existsCount = reviewMapper.selectCount(new LambdaQueryWrapper<Review>()
                    .eq(Review::getSubmissionId, submissionId)
                    .eq(Review::getReviewerId, reviewerId));
            if (existsCount == 0) {
                Review review = new Review();
                review.setSubmissionId(submissionId);
                review.setReviewerId(reviewerId);
                reviewMapper.insert(review);
            }

            // 向审稿人发送分配通知邮件
            try {
                User reviewer = userMapper.selectById(reviewerId);
                if (reviewer != null) {
                    emailService.sendReviewAssignment(reviewer.getEmail(), reviewer.getName(), submissionTitle);
                }
            } catch (Exception e) {
                log.warn("审稿分配邮件发送失败 submissionId={}, reviewerId={}", submissionId, reviewerId, e);
            }
        }
    }

    @Override
    public List<Review> listBySubmission(Long submissionId) {
        return reviewMapper.selectList(new LambdaQueryWrapper<Review>()
                .eq(Review::getSubmissionId, submissionId)
                .orderByDesc(Review::getCreatedAt));
    }

    @Override
    public List<Review> listByReviewer(Long reviewerId) {
        return reviewMapper.selectList(new LambdaQueryWrapper<Review>()
                .eq(Review::getReviewerId, reviewerId)
                .orderByDesc(Review::getCreatedAt));
    }
}
