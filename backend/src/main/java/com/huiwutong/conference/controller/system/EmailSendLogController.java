package com.huiwutong.conference.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.EmailSendLog;
import com.huiwutong.conference.service.EmailSendLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 邮件发送日志管理
 */
@RestController
@RequestMapping("/api/admin/system/email-logs")
@RequiredArgsConstructor
public class EmailSendLogController {

    private final EmailSendLogService emailSendLogService;

    @GetMapping
    @RequireRole({"admin"})
    public Result<PageResult<EmailSendLog>> list(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestParam(defaultValue = "20") Integer pageSize,
        @RequestParam(required = false) String sceneCode,
        @RequestParam(required = false) Integer status,
        @RequestParam(required = false) String toEmail,
        @RequestParam(required = false) Integer sendType,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
        @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime
    ) {
        Page<EmailSendLog> pageData = emailSendLogService.query(
            page, pageSize, sceneCode, status, toEmail, sendType, startTime, endTime
        );
        return Result.success(PageResult.of(pageData));
    }

    @GetMapping("/{id}")
    @RequireRole({"admin"})
    public Result<EmailSendLog> detail(@PathVariable Long id) {
        return Result.success(emailSendLogService.getById(id));
    }
}

