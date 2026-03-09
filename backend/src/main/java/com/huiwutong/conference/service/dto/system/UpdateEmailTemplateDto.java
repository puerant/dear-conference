package com.huiwutong.conference.service.dto.system;

import lombok.Data;

import java.util.Map;

/** 更新邮件模板请求体 */
@Data
public class UpdateEmailTemplateDto {
    private Long emailConfigId;
    private String subject;
    private String body;
    private Integer isEnabled;
}
