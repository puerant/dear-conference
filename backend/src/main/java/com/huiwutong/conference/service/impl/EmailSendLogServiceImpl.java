package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.EmailSendLog;
import com.huiwutong.conference.mapper.EmailSendLogMapper;
import com.huiwutong.conference.service.EmailSendLogService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 邮件发送日志服务实现
 */
@Service
@RequiredArgsConstructor
public class EmailSendLogServiceImpl implements EmailSendLogService {

    private final EmailSendLogMapper emailSendLogMapper;

    @Override
    public Long createDraft(EmailSendLog log) {
        if (log.getStatus() == null) {
            log.setStatus(0);
        }
        emailSendLogMapper.insert(log);
        return log.getId();
    }

    @Override
    public void updateMeta(Long id, EmailSendLog meta) {
        EmailSendLog update = new EmailSendLog();
        update.setId(id);
        update.setTemplateId(meta.getTemplateId());
        update.setEmailConfigId(meta.getEmailConfigId());
        update.setFromEmail(meta.getFromEmail());
        update.setSubject(meta.getSubject());
        emailSendLogMapper.updateById(update);
    }

    @Override
    public void markSuccess(Long id) {
        EmailSendLog update = new EmailSendLog();
        update.setId(id);
        update.setStatus(1);
        update.setErrorMessage(null);
        update.setSentAt(LocalDateTime.now());
        emailSendLogMapper.updateById(update);
    }

    @Override
    public void markFailed(Long id, String errorMessage) {
        EmailSendLog update = new EmailSendLog();
        update.setId(id);
        update.setStatus(0);
        update.setErrorMessage(StringUtils.left(errorMessage, 1000));
        emailSendLogMapper.updateById(update);
    }

    @Override
    public Page<EmailSendLog> query(Integer page, Integer pageSize, String sceneCode, Integer status, String toEmail,
                                    Integer sendType, LocalDateTime startTime, LocalDateTime endTime) {
        Page<EmailSendLog> pageObj = new Page<>(page, pageSize);
        LambdaQueryWrapper<EmailSendLog> wrapper = new LambdaQueryWrapper<EmailSendLog>()
            .orderByDesc(EmailSendLog::getId);

        if (StringUtils.isNotBlank(sceneCode)) {
            wrapper.eq(EmailSendLog::getSceneCode, sceneCode);
        }
        if (status != null) {
            wrapper.eq(EmailSendLog::getStatus, status);
        }
        if (StringUtils.isNotBlank(toEmail)) {
            wrapper.like(EmailSendLog::getToEmail, toEmail);
        }
        if (sendType != null) {
            wrapper.eq(EmailSendLog::getSendType, sendType);
        }
        if (startTime != null) {
            wrapper.ge(EmailSendLog::getCreatedAt, startTime);
        }
        if (endTime != null) {
            wrapper.le(EmailSendLog::getCreatedAt, endTime);
        }

        emailSendLogMapper.selectPage(pageObj, wrapper);
        return pageObj;
    }

    @Override
    public EmailSendLog getById(Long id) {
        EmailSendLog log = emailSendLogMapper.selectById(id);
        if (log == null) {
            throw new BusinessException(ErrorCode.EMAIL_SEND_LOG_NOT_FOUND);
        }
        return log;
    }
}

