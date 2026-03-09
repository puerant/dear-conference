package com.huiwutong.conference.service.dto.system;

import lombok.Data;

import java.util.Map;

/** 预览/测试发送邮件模板请求体 */
@Data
public class EmailTemplateActionDto {
    /** 测试发送时必填：收件人地址 */
    private String toEmail;
    /** 占位符变量 Map，key 与模板中 {{key}} 对应 */
    private Map<String, String> variables;
}
