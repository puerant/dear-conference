package com.huiwutong.conference.service;

import com.huiwutong.conference.entity.CredentialConfig;

/**
 * 凭证配置服务
 */
public interface CredentialConfigService {

    /**
     * 获取凭证配置
     */
    CredentialConfig getConfig();

    /**
     * 保存或更新配置
     */
    void saveOrUpdate(CredentialConfig config);
}
