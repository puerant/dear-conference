package com.huiwutong.conference.service;

import com.huiwutong.conference.entity.EmailConfig;

import java.util.List;

/**
 * 邮箱配置服务
 */
public interface EmailConfigService {

    /**
     * 获取启用的邮箱配置
     */
    EmailConfig getEnabledConfig();

    /**
     * 获取配置（不返回密码）
     */
    EmailConfig getPublicConfig();

    /**
     * 保存或更新配置
     */
    void saveOrUpdate(EmailConfig config);

    /**
     * 发送测试邮件
     */
    void sendTestEmail(String toEmail);

    /**
     * 获取全部邮箱配置（不含密码）
     */
    List<EmailConfig> listPublicConfigs();

    /**
     * 获取指定邮箱配置（不含密码）
     */
    EmailConfig getPublicConfigById(Long id);

    /**
     * 新增邮箱配置
     */
    Long create(EmailConfig config);

    /**
     * 更新邮箱配置
     */
    void update(Long id, EmailConfig config);

    /**
     * 启用/禁用邮箱配置
     */
    void updateEnabled(Long id, Integer enabled);

    /**
     * 设为默认邮箱配置（并取消其他默认）
     */
    void setDefault(Long id);

    /**
     * 删除邮箱配置
     */
    void delete(Long id);

    /**
     * 发送测试邮件（指定配置）
     */
    void sendTestEmail(Long configId, String toEmail);
}
