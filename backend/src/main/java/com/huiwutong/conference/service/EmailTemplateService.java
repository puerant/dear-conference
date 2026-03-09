package com.huiwutong.conference.service;

import com.huiwutong.conference.entity.EmailTemplate;
import com.huiwutong.conference.service.dto.system.EmailPreviewVo;

import java.util.List;
import java.util.Map;

/**
 * 邮件模板服务
 */
public interface EmailTemplateService {

    /** 获取所有场景模板列表（不含 body 字段） */
    List<EmailTemplate> listAll();

    /** 按场景码获取模板（含 body） */
    EmailTemplate getBySceneCode(String sceneCode);

    /** 保存/更新模板 */
    void saveOrUpdate(String sceneCode, EmailTemplate template);

    /**
     * 渲染并发送邮件
     * @param sceneCode 场景码
     * @param to        收件人地址
     * @param variables 占位符变量 Map
     */
    void send(String sceneCode, String to, Map<String, String> variables);

    /**
     * 渲染并发送邮件（扩展：发送类型与触发人）
     * @param sceneCode   场景码
     * @param to          收件人地址
     * @param variables   占位符变量 Map
     * @param sendType    1=业务发送, 2=测试发送
     * @param triggeredBy 触发人ID（业务发送可为 null）
     */
    void send(String sceneCode, String to, Map<String, String> variables, Integer sendType, Long triggeredBy);

    /**
     * 渲染模板（不发送，返回预览内容）
     * @param sceneCode 场景码
     * @param variables 测试用变量
     */
    EmailPreviewVo preview(String sceneCode, Map<String, String> variables);
}
