package com.huiwutong.conference.service;

import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.entity.Submission;
import com.huiwutong.conference.service.dto.submission.CreateSubmissionDto;
import com.huiwutong.conference.service.dto.submission.SubmissionQueryDto;
import com.huiwutong.conference.service.dto.submission.UpdateSubmissionDto;

/**
 * 投稿服务接口
 */
public interface SubmissionService {

    Submission create(Long userId, CreateSubmissionDto dto);

    Submission update(Long id, Long userId, UpdateSubmissionDto dto);

    Submission getById(Long id);

    PageResult<Submission> listByUser(Long userId, SubmissionQueryDto query);

    PageResult<Submission> listAll(SubmissionQueryDto query);

    void delete(Long id, Long userId);

    void updateStatus(Long id, Integer status);
}
