package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.enums.SubmissionStatus;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.entity.Submission;
import com.huiwutong.conference.entity.User;
import com.huiwutong.conference.mapper.SubmissionMapper;
import com.huiwutong.conference.mapper.UserMapper;
import com.huiwutong.conference.service.EmailService;
import com.huiwutong.conference.service.SubmissionService;
import com.huiwutong.conference.service.dto.submission.CreateSubmissionDto;
import com.huiwutong.conference.service.dto.submission.SubmissionQueryDto;
import com.huiwutong.conference.service.dto.submission.UpdateSubmissionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 投稿服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionMapper submissionMapper;
    private final UserMapper userMapper;
    private final EmailService emailService;

    /**
     * 将查询参数中的 status 字符串解析为枚举。
     * 兼容两种格式：整数值（"1"）和枚举 code（"pending"）。
     */
    private SubmissionStatus parseStatus(String status) {
        try {
            return SubmissionStatus.ofValue(Integer.parseInt(status));
        } catch (NumberFormatException e) {
            return SubmissionStatus.ofCode(status);
        }
    }

    @Override
    public Submission create(Long userId, CreateSubmissionDto dto) {
        Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setTitle(dto.getTitle());
        submission.setAbstract0(dto.getAbstract0());
        submission.setCategory(dto.getCategory());
        submission.setFileUrl(dto.getFileUrl());
        submission.setStatus(SubmissionStatus.PENDING);
        submissionMapper.insert(submission);

        // 投稿创建成功后发送确认邮件
        try {
            User speaker = userMapper.selectById(userId);
            if (speaker != null) {
                emailService.sendSubmissionConfirmation(speaker.getEmail(), speaker.getName(), dto.getTitle());
            }
        } catch (Exception e) {
            log.warn("投稿确认邮件发送失败 submissionId={}", submission.getId(), e);
        }

        return submission;
    }

    @Override
    public Submission update(Long id, Long userId, UpdateSubmissionDto dto) {
        Submission submission = getById(id);
        if (!submission.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.SUBMISSION_ACCESS_DENIED);
        }
        if (submission.getStatus() != SubmissionStatus.PENDING) {
            throw new BusinessException(ErrorCode.SUBMISSION_STATUS_ERROR);
        }
        submission.setTitle(dto.getTitle());
        submission.setAbstract0(dto.getAbstract0());
        submission.setCategory(dto.getCategory());
        if (dto.getFileUrl() != null) {
            submission.setFileUrl(dto.getFileUrl());
        }
        submissionMapper.updateById(submission);
        return submission;
    }

    @Override
    public Submission getById(Long id) {
        Submission submission = submissionMapper.selectById(id);
        if (submission == null) {
            throw new BusinessException(ErrorCode.SUBMISSION_NOT_FOUND);
        }
        User user = userMapper.selectById(submission.getUserId());
        if (user != null) {
            submission.setSpeakerName(user.getName());
            submission.setSpeakerEmail(user.getEmail());
        }
        return submission;
    }

    @Override
    public PageResult<Submission> listByUser(Long userId, SubmissionQueryDto query) {
        Page<Submission> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Submission> wrapper = new LambdaQueryWrapper<Submission>()
                .eq(Submission::getUserId, userId)
                .orderByDesc(Submission::getCreatedAt);

        if (StringUtils.hasText(query.getStatus())) {
            SubmissionStatus status = parseStatus(query.getStatus());
            if (status != null) {
                wrapper.eq(Submission::getStatus, status);
            }
        }
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.eq(Submission::getCategory, query.getCategory());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Submission::getTitle, query.getKeyword());
        }

        submissionMapper.selectPage(page, wrapper);
        return PageResult.of(page);
    }

    @Override
    public PageResult<Submission> listAll(SubmissionQueryDto query) {
        Page<Submission> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Submission> wrapper = new LambdaQueryWrapper<Submission>()
                .orderByDesc(Submission::getCreatedAt);

        if (StringUtils.hasText(query.getStatus())) {
            SubmissionStatus status = parseStatus(query.getStatus());
            if (status != null) {
                wrapper.eq(Submission::getStatus, status);
            }
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.like(Submission::getTitle, query.getKeyword());
        }

        submissionMapper.selectPage(page, wrapper);

        // 批量填充讲者姓名和邮箱
        List<Submission> records = page.getRecords();
        if (!records.isEmpty()) {
            List<Long> userIds = records.stream()
                    .map(Submission::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                    .collect(Collectors.toMap(User::getId, u -> u));
            records.forEach(s -> {
                User user = userMap.get(s.getUserId());
                if (user != null) {
                    s.setSpeakerName(user.getName());
                    s.setSpeakerEmail(user.getEmail());
                }
            });
        }

        return PageResult.of(page);
    }

    @Override
    public void delete(Long id, Long userId) {
        Submission submission = getById(id);
        if (!submission.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.SUBMISSION_ACCESS_DENIED);
        }
        if (submission.getStatus() != SubmissionStatus.PENDING) {
            throw new BusinessException(ErrorCode.SUBMISSION_DELETE_DENIED);
        }
        submissionMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Submission submission = getById(id);
        SubmissionStatus newStatus = SubmissionStatus.ofValue(status);
        if (newStatus == null) {
            throw new BusinessException(ErrorCode.SUBMISSION_STATUS_ERROR);
        }
        submission.setStatus(newStatus);
        submissionMapper.updateById(submission);

        // 状态变为已录用或已拒绝时，通知讲者
        if (newStatus == SubmissionStatus.ACCEPTED || newStatus == SubmissionStatus.REJECTED) {
            try {
                String resultLabel = newStatus == SubmissionStatus.ACCEPTED ? "已录用" : "已拒绝";
                emailService.sendSubmissionResult(
                        submission.getSpeakerEmail(),
                        submission.getSpeakerName(),
                        submission.getTitle(),
                        resultLabel);
            } catch (Exception e) {
                log.warn("投稿结果邮件发送失败 submissionId={}", id, e);
            }
        }
    }
}
