package com.huiwutong.conference.service;

import com.huiwutong.conference.entity.Review;
import com.huiwutong.conference.service.dto.review.CreateReviewDto;

import java.util.List;

/**
 * 审稿服务接口
 */
public interface ReviewService {

    Review create(Long reviewerId, CreateReviewDto dto);

    void assign(Long submissionId, List<Long> reviewerIds);

    List<Review> listBySubmission(Long submissionId);

    List<Review> listByReviewer(Long reviewerId);
}
