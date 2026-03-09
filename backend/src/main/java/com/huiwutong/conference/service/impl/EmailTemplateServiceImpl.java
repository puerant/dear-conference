package com.huiwutong.conference.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.common.util.AesUtil;
import com.huiwutong.conference.entity.EmailConfig;
import com.huiwutong.conference.entity.EmailSendLog;
import com.huiwutong.conference.entity.EmailTemplate;
import com.huiwutong.conference.mapper.EmailConfigMapper;
import com.huiwutong.conference.mapper.EmailTemplateMapper;
import com.huiwutong.conference.service.EmailSendLogService;
import com.huiwutong.conference.service.EmailTemplateService;
import com.huiwutong.conference.service.dto.system.EmailPreviewVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.internet.MimeMessage;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 邮件模板服务实现
 * 发送流程：场景码 -> 查模板 -> 渲染占位符 -> 选账号 -> 发送 -> 记录日志
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final EmailTemplateMapper emailTemplateMapper;
    private final EmailConfigMapper emailConfigMapper;
    private final EmailSendLogService emailSendLogService;
    private final AesUtil aesUtil;

    @Value("${app.email.dev-mode:true}")
    private boolean devMode;

    // ----------------------------------------------------------------
    // CRUD
    // ----------------------------------------------------------------

    @Override
    public List<EmailTemplate> listAll() {
        List<EmailTemplate> templates = emailTemplateMapper.selectList(
            new LambdaQueryWrapper<EmailTemplate>()
                .select(EmailTemplate::getId, EmailTemplate::getSceneCode,
                    EmailTemplate::getSceneName, EmailTemplate::getEmailConfigId,
                    EmailTemplate::getSubject, EmailTemplate::getVariablesDesc,
                    EmailTemplate::getIsEnabled,
                    EmailTemplate::getCreatedAt, EmailTemplate::getUpdatedAt)
                .orderByAsc(EmailTemplate::getId)
        );

        Set<Long> configIds = templates.stream()
            .map(EmailTemplate::getEmailConfigId)
            .filter(id -> id != null && id > 0)
            .collect(Collectors.toSet());

        Map<Long, EmailConfig> configMap = configIds.isEmpty()
            ? Map.of()
            : emailConfigMapper.selectBatchIds(configIds).stream()
                .collect(Collectors.toMap(EmailConfig::getId, Function.identity()));

        templates.forEach(template -> {
            if (template.getEmailConfigId() == null) {
                template.setEmailConfigName("系统默认");
                return;
            }
            EmailConfig config = configMap.get(template.getEmailConfigId());
            if (config == null) {
                template.setEmailConfigName("账号已删除");
                return;
            }
            template.setEmailConfigName(
                StringUtils.isNotBlank(config.getConfigName()) ? config.getConfigName() : config.getFromEmail()
            );
        });
        return templates;
    }

    @Override
    public EmailTemplate getBySceneCode(String sceneCode) {
        return emailTemplateMapper.selectOne(
            new LambdaQueryWrapper<EmailTemplate>()
                .eq(EmailTemplate::getSceneCode, sceneCode)
        );
    }

    @Override
    @Transactional
    public void saveOrUpdate(String sceneCode, EmailTemplate template) {
        EmailTemplate existing = getBySceneCode(sceneCode);
        if (existing == null) {
            throw new BusinessException(ErrorCode.EMAIL_TEMPLATE_NOT_FOUND);
        }

        if (template.getEmailConfigId() != null) {
            EmailConfig bindConfig = emailConfigMapper.selectById(template.getEmailConfigId());
            if (bindConfig == null) {
                throw new BusinessException(ErrorCode.EMAIL_CONFIG_NOT_FOUND);
            }
        }

        template.setId(existing.getId());
        template.setSceneCode(existing.getSceneCode());
        template.setSceneName(existing.getSceneName());
        template.setVariablesDesc(existing.getVariablesDesc());
        emailTemplateMapper.updateById(template);
    }

    // ----------------------------------------------------------------
    // 发送
    // ----------------------------------------------------------------

    @Override
    public void send(String sceneCode, String to, Map<String, String> variables) {
        send(sceneCode, to, variables, 1, null);
    }

    @Override
    public void send(String sceneCode, String to, Map<String, String> variables, Integer sendType, Long triggeredBy) {
        Map<String, String> safeVariables = variables == null ? Map.of() : variables;
        Long logId = null;
        try {
            logId = createDraftLog(sceneCode, to, safeVariables, sendType, triggeredBy);
        } catch (Exception e) {
            log.warn("[EmailTemplate] 写入发送日志草稿失败，跳过日志记录 scene={} to={} err={}", sceneCode, to, e.getMessage());
        }
        try {
            if (StringUtils.isBlank(to)) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "收件人邮箱不能为空");
            }

            EmailTemplate template = getBySceneCode(sceneCode);
            if (template == null) {
                throw new BusinessException(ErrorCode.EMAIL_TEMPLATE_NOT_FOUND);
            }
            if (template.getIsEnabled() == null || template.getIsEnabled() != 1) {
                throw new BusinessException(ErrorCode.EMAIL_TEMPLATE_DISABLED);
            }

            String subject = render(template.getSubject(), safeVariables);
            String body = render(template.getBody(), safeVariables);

            EmailConfig config = resolveConfig(template);
            if (logId != null) {
                updateLogMeta(logId, template, config, subject);
            }

            if (devMode) {
                log.info("[DEV EMAIL] scene={} to={} vars={} config={}", sceneCode, to, safeVariables, config.getId());
                if (logId != null) {
                    emailSendLogService.markSuccess(logId);
                }
                return;
            }

            doSend(config, to, subject, body, sceneCode);
            if (logId != null) {
                emailSendLogService.markSuccess(logId);
            }
        } catch (BusinessException e) {
            if (logId != null) {
                emailSendLogService.markFailed(logId, e.getMessage());
            }
            throw e;
        } catch (Exception e) {
            if (logId != null) {
                emailSendLogService.markFailed(logId, e.getMessage());
            }
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED, e.getMessage());
        }
    }

    // ----------------------------------------------------------------
    // 预览
    // ----------------------------------------------------------------

    @Override
    public EmailPreviewVo preview(String sceneCode, Map<String, String> variables) {
        EmailTemplate template = getBySceneCode(sceneCode);
        if (template == null) {
            throw new BusinessException(ErrorCode.EMAIL_TEMPLATE_NOT_FOUND);
        }

        EmailConfig config = resolveConfigForPreview(template);

        EmailPreviewVo vo = new EmailPreviewVo();
        vo.setSubject(render(template.getSubject(), variables));
        vo.setBody(render(template.getBody(), variables));
        vo.setFromEmail(config != null ? config.getFromEmail() : "（未配置邮箱账号）");
        vo.setFromName(config != null ? config.getFromName() : "");
        return vo;
    }

    // ----------------------------------------------------------------
    // 私有辅助方法
    // ----------------------------------------------------------------

    private Long createDraftLog(String sceneCode, String to, Map<String, String> variables, Integer sendType, Long triggeredBy) {
        EmailSendLog logItem = new EmailSendLog();
        logItem.setSceneCode(sceneCode);
        logItem.setToEmail(to);
        logItem.setSendType(sendType == null ? 1 : sendType);
        logItem.setTriggeredBy(triggeredBy);
        logItem.setVariablesJson(JSONUtil.toJsonStr(variables));
        logItem.setStatus(0);
        return emailSendLogService.createDraft(logItem);
    }

    private void updateLogMeta(Long logId, EmailTemplate template, EmailConfig config, String subject) {
        EmailSendLog meta = new EmailSendLog();
        meta.setTemplateId(template.getId());
        meta.setEmailConfigId(config.getId());
        meta.setFromEmail(config.getFromEmail());
        meta.setSubject(subject);
        emailSendLogService.updateMeta(logId, meta);
    }

    /** 将模板中的 {{key}} 替换为 variables.get(key) */
    private String render(String template, Map<String, String> variables) {
        if (template == null || variables == null || variables.isEmpty()) {
            return template;
        }
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue() == null ? "" : entry.getValue());
        }
        return result;
    }

    /** 按优先级选取邮箱配置：模板绑定 > 默认启用账号 > 启用账号优先级 */
    private EmailConfig resolveConfig(EmailTemplate template) {
        if (template.getEmailConfigId() != null) {
            EmailConfig bindConfig = emailConfigMapper.selectById(template.getEmailConfigId());
            if (bindConfig == null) {
                throw new BusinessException(ErrorCode.EMAIL_CONFIG_NOT_FOUND);
            }
            if (bindConfig.getIsEnabled() == null || bindConfig.getIsEnabled() != 1) {
                throw new BusinessException(ErrorCode.EMAIL_CONFIG_DISABLED);
            }
            return bindConfig;
        }

        EmailConfig defaultConfig = emailConfigMapper.selectOne(
            new LambdaQueryWrapper<EmailConfig>()
                .eq(EmailConfig::getIsEnabled, 1)
                .eq(EmailConfig::getIsDefault, 1)
                .last("LIMIT 1")
        );
        if (defaultConfig != null) {
            return defaultConfig;
        }

        EmailConfig fallback = emailConfigMapper.selectOne(
            new LambdaQueryWrapper<EmailConfig>()
                .eq(EmailConfig::getIsEnabled, 1)
                .orderByAsc(EmailConfig::getPriority, EmailConfig::getId)
                .last("LIMIT 1")
        );
        if (fallback != null) {
            return fallback;
        }
        throw new BusinessException(ErrorCode.EMAIL_TEMPLATE_NO_CONFIG);
    }

    /** 预览场景下，配置缺失时允许返回空，避免页面直接报错 */
    private EmailConfig resolveConfigForPreview(EmailTemplate template) {
        try {
            return resolveConfig(template);
        } catch (Exception ignored) {
            return null;
        }
    }

    /** 构建 JavaMailSender 并发送 HTML 邮件 */
    private void doSend(EmailConfig config, String to, String subject, String body, String sceneCode) {
        try {
            JavaMailSenderImpl mailSender = buildMailSender(config);
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(config.getFromEmail(), config.getFromName());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(mimeMessage);
            log.info("[EmailTemplate] scene={} 邮件发送成功 to={}", sceneCode, to);
        } catch (Exception e) {
            log.error("[EmailTemplate] scene={} 邮件发送失败 to={} error={}", sceneCode, to, e.getMessage(), e);
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED, e.getMessage());
        }
    }

    private JavaMailSenderImpl buildMailSender(EmailConfig config) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(config.getHost());
        sender.setPort(config.getPort());
        sender.setUsername(config.getUsername());
        sender.setPassword(aesUtil.decrypt(config.getPassword()));
        sender.setProtocol("smtp");
        sender.setDefaultEncoding("UTF-8");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2 TLSv1.3");
        props.put("mail.smtp.ssl.trust", "*");
        if (config.getSslEnabled() != null && config.getSslEnabled() == 1) {
            props.put("mail.smtp.ssl.enable", "true");
        }
        sender.setJavaMailProperties(props);
        return sender;
    }
}
