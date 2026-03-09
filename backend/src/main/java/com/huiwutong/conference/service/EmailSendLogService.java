package com.huiwutong.conference.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.entity.EmailSendLog;

import java.time.LocalDateTime;

/**
 * 邮件发送日志服务
 */
public interface EmailSendLogService {

    Long createDraft(EmailSendLog log);

    void updateMeta(Long id, EmailSendLog meta);

    void markSuccess(Long id);

    void markFailed(Long id, String errorMessage);

    Page<EmailSendLog> query(
        Integer page,
        Integer pageSize,
        String sceneCode,
        Integer status,
        String toEmail,
        Integer sendType,
        LocalDateTime startTime,
        LocalDateTime endTime
    );

    EmailSendLog getById(Long id);
}

