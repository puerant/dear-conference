package com.huiwutong.conference.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.common.util.AesUtil;
import com.huiwutong.conference.entity.EmailConfig;
import com.huiwutong.conference.entity.EmailSendLog;
import com.huiwutong.conference.mapper.EmailConfigMapper;
import com.huiwutong.conference.service.EmailConfigService;
import com.huiwutong.conference.service.EmailSendLogService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Properties;

/**
 * 邮箱配置服务实现
 */
@Service
@RequiredArgsConstructor
public class EmailConfigServiceImpl implements EmailConfigService {

    private final EmailConfigMapper emailConfigMapper;
    private final EmailSendLogService emailSendLogService;
    private final AesUtil aesUtil;

    @Override
    public EmailConfig getEnabledConfig() {
        EmailConfig defaultEnabled = emailConfigMapper.selectOne(
            new LambdaQueryWrapper<EmailConfig>()
                .eq(EmailConfig::getIsEnabled, 1)
                .eq(EmailConfig::getIsDefault, 1)
                .last("LIMIT 1")
        );
        if (defaultEnabled != null) {
            return defaultEnabled;
        }
        return emailConfigMapper.selectOne(
            new LambdaQueryWrapper<EmailConfig>()
                .eq(EmailConfig::getIsEnabled, 1)
                .orderByAsc(EmailConfig::getPriority, EmailConfig::getId)
                .last("LIMIT 1")
        );
    }

    @Override
    public EmailConfig getPublicConfig() {
        EmailConfig config = getEnabledConfig();
        if (config != null) {
            config.setPassword("******");
        }
        return config;
    }

    @Override
    public List<EmailConfig> listPublicConfigs() {
        List<EmailConfig> list = emailConfigMapper.selectList(
            new LambdaQueryWrapper<EmailConfig>()
                .orderByDesc(EmailConfig::getIsDefault)
                .orderByAsc(EmailConfig::getPriority, EmailConfig::getId)
        );
        list.forEach(item -> item.setPassword("******"));
        return list;
    }

    @Override
    public EmailConfig getPublicConfigById(Long id) {
        EmailConfig config = emailConfigMapper.selectById(id);
        if (config == null) {
            throw new BusinessException(ErrorCode.EMAIL_CONFIG_NOT_FOUND);
        }
        config.setPassword("******");
        return config;
    }

    @Override
    @Transactional
    public Long create(EmailConfig config) {
        prepareConfigDefaults(config);
        if (StringUtils.isBlank(config.getPassword())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "密码不能为空");
        }
        config.setPassword(aesUtil.encrypt(config.getPassword()));

        if (config.getIsDefault() != null && config.getIsDefault() == 1) {
            clearDefaultFlag();
            config.setIsEnabled(1);
        }

        emailConfigMapper.insert(config);
        ensureDefaultEnabledConfig(config.getId());
        return config.getId();
    }

    @Override
    @Transactional
    public void update(Long id, EmailConfig config) {
        EmailConfig existing = emailConfigMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.EMAIL_CONFIG_NOT_FOUND);
        }

        prepareConfigDefaults(config);
        config.setId(id);

        if (StringUtils.isBlank(config.getPassword()) || "******".equals(config.getPassword())) {
            config.setPassword(existing.getPassword());
        } else {
            config.setPassword(aesUtil.encrypt(config.getPassword()));
        }

        if (config.getIsDefault() != null && config.getIsDefault() == 1) {
            clearDefaultFlag();
            config.setIsEnabled(1);
        }

        emailConfigMapper.updateById(config);
        ensureDefaultEnabledConfig(id);
    }

    @Override
    @Transactional
    public void updateEnabled(Long id, Integer enabled) {
        EmailConfig existing = emailConfigMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.EMAIL_CONFIG_NOT_FOUND);
        }
        if (enabled == null || (enabled != 0 && enabled != 1)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "启用状态仅支持 0/1");
        }

        if (enabled == 0 && existing.getIsDefault() != null && existing.getIsDefault() == 1) {
            EmailConfig anotherEnabled = emailConfigMapper.selectOne(
                new LambdaQueryWrapper<EmailConfig>()
                    .eq(EmailConfig::getIsEnabled, 1)
                    .ne(EmailConfig::getId, id)
                    .orderByAsc(EmailConfig::getPriority, EmailConfig::getId)
                    .last("LIMIT 1")
            );
            if (anotherEnabled == null) {
                throw new BusinessException(ErrorCode.EMAIL_CONFIG_DEFAULT_REQUIRED);
            }
            setDefault(anotherEnabled.getId());
        }

        EmailConfig update = new EmailConfig();
        update.setId(id);
        update.setIsEnabled(enabled);
        emailConfigMapper.updateById(update);
        ensureDefaultEnabledConfig(id);
    }

    @Override
    @Transactional
    public void setDefault(Long id) {
        EmailConfig existing = emailConfigMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.EMAIL_CONFIG_NOT_FOUND);
        }
        if (existing.getIsEnabled() == null || existing.getIsEnabled() != 1) {
            throw new BusinessException(ErrorCode.EMAIL_CONFIG_DISABLED);
        }

        clearDefaultFlag();
        EmailConfig update = new EmailConfig();
        update.setId(id);
        update.setIsDefault(1);
        emailConfigMapper.updateById(update);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        EmailConfig existing = emailConfigMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.EMAIL_CONFIG_NOT_FOUND);
        }

        if (existing.getIsDefault() != null && existing.getIsDefault() == 1) {
            EmailConfig anotherEnabled = emailConfigMapper.selectOne(
                new LambdaQueryWrapper<EmailConfig>()
                    .eq(EmailConfig::getIsEnabled, 1)
                    .ne(EmailConfig::getId, id)
                    .orderByAsc(EmailConfig::getPriority, EmailConfig::getId)
                    .last("LIMIT 1")
            );
            if (anotherEnabled == null) {
                throw new BusinessException(ErrorCode.EMAIL_CONFIG_DEFAULT_REQUIRED);
            }
        }

        emailConfigMapper.deleteById(id);
        ensureDefaultEnabledConfig(null);
    }

    @Override
    @Transactional
    public void saveOrUpdate(EmailConfig config) {
        if (config.getId() != null) {
            update(config.getId(), config);
            return;
        }
        EmailConfig target = getEnabledConfig();
        if (target != null) {
            update(target.getId(), config);
        } else {
            config.setIsDefault(1);
            if (config.getIsEnabled() == null) {
                config.setIsEnabled(1);
            }
            create(config);
        }
    }

    @Override
    public void sendTestEmail(String toEmail) {
        sendTestEmail(null, toEmail);
    }

    @Override
    public void sendTestEmail(Long configId, String toEmail) {
        if (StringUtils.isBlank(toEmail)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "收件人邮箱不能为空");
        }

        Long logId = null;
        try {
            logId = createTestSendLog(toEmail, configId);
        } catch (Exception ignored) {
            // 迁移脚本尚未执行时允许降级，不影响测试发送主流程
        }
        EmailConfig config;
        try {
            if (configId == null) {
                config = getEnabledConfig();
                if (config == null) {
                    throw new BusinessException(ErrorCode.EMAIL_CONFIG_NOT_ENABLED);
                }
            } else {
                config = emailConfigMapper.selectById(configId);
                if (config == null) {
                    throw new BusinessException(ErrorCode.EMAIL_CONFIG_NOT_FOUND);
                }
                if (config.getIsEnabled() == null || config.getIsEnabled() != 1) {
                    throw new BusinessException(ErrorCode.EMAIL_CONFIG_DISABLED);
                }
            }

            if (logId != null) {
                updateTestLogMeta(logId, config);
            }
            doSend(config, toEmail);
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
            throw e;
        }
    }

    private void doSend(EmailConfig config, String toEmail) {
        try {
            JavaMailSender mailSender = buildMailSender(config);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(config.getFromEmail());
            message.setTo(toEmail);
            message.setSubject("会务通系统 - 邮箱配置测试");
            message.setText("这是一封测试邮件，如果您收到此邮件，说明邮箱配置正确。");

            mailSender.send(message);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED, e.getMessage());
        }
    }

    private void prepareConfigDefaults(EmailConfig config) {
        if (StringUtils.isBlank(config.getConfigName())) {
            config.setConfigName(config.getFromEmail());
        }
        if (config.getIsEnabled() == null) {
            config.setIsEnabled(1);
        }
        if (config.getIsDefault() == null) {
            config.setIsDefault(0);
        }
        if (config.getPriority() == null) {
            config.setPriority(100);
        }
    }

    private void clearDefaultFlag() {
        emailConfigMapper.update(
            null,
            new LambdaUpdateWrapper<EmailConfig>().set(EmailConfig::getIsDefault, 0)
        );
    }

    private Long createTestSendLog(String toEmail, Long configId) {
        EmailSendLog log = new EmailSendLog();
        log.setSceneCode("EMAIL_CONFIG_TEST");
        log.setSendType(2);
        log.setToEmail(toEmail);
        log.setEmailConfigId(configId);
        log.setStatus(0);
        log.setVariablesJson(JSONUtil.toJsonStr(new java.util.HashMap<>()));
        return emailSendLogService.createDraft(log);
    }

    private void updateTestLogMeta(Long logId, EmailConfig config) {
        EmailSendLog meta = new EmailSendLog();
        meta.setEmailConfigId(config.getId());
        meta.setFromEmail(config.getFromEmail());
        meta.setSubject("会务通系统 - 邮箱配置测试");
        emailSendLogService.updateMeta(logId, meta);
    }

    /**
     * 确保始终存在一个默认且启用的邮箱配置
     */
    private void ensureDefaultEnabledConfig(Long fallbackId) {
        EmailConfig defaultEnabled = emailConfigMapper.selectOne(
            new LambdaQueryWrapper<EmailConfig>()
                .eq(EmailConfig::getIsEnabled, 1)
                .eq(EmailConfig::getIsDefault, 1)
                .last("LIMIT 1")
        );
        if (defaultEnabled != null) {
            return;
        }

        EmailConfig candidate = null;
        if (fallbackId != null) {
            EmailConfig fallback = emailConfigMapper.selectById(fallbackId);
            if (fallback != null && fallback.getIsEnabled() != null && fallback.getIsEnabled() == 1) {
                candidate = fallback;
            }
        }
        if (candidate == null) {
            candidate = emailConfigMapper.selectOne(
                new LambdaQueryWrapper<EmailConfig>()
                    .eq(EmailConfig::getIsEnabled, 1)
                    .orderByAsc(EmailConfig::getPriority, EmailConfig::getId)
                    .last("LIMIT 1")
            );
        }
        if (candidate == null) {
            return;
        }

        clearDefaultFlag();
        EmailConfig update = new EmailConfig();
        update.setId(candidate.getId());
        update.setIsDefault(1);
        emailConfigMapper.updateById(update);
    }

    private JavaMailSender buildMailSender(EmailConfig config) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(config.getHost());
        mailSender.setPort(config.getPort());
        mailSender.setUsername(config.getUsername());
        mailSender.setPassword(aesUtil.decrypt(config.getPassword()));
        mailSender.setProtocol("smtp");
        mailSender.setDefaultEncoding("UTF-8");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        if (config.getSslEnabled() != null && config.getSslEnabled() == 1) {
            props.put("mail.smtp.ssl.enable", "true");
        }
        mailSender.setJavaMailProperties(props);

        return mailSender;
    }
}
