package com.huiwutong.conference.service.dto.system;

import lombok.Data;

/**
 * 邮件模板预览响应 VO
 */
@Data
public class EmailPreviewVo {
    /** 渲染后的邮件主题 */
    private String subject;
    /** 渲染后的 HTML 正文 */
    private String body;
    /** 发件人邮箱地址 */
    private String fromEmail;
    /** 发件人名称 */
    private String fromName;
}
