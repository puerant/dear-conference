package com.huiwutong.conference.service;

import java.util.Map;

/**
 * 系统配置服务
 */
public interface SystemConfigService {

    /**
     * 获取所有系统配置
     */
    Map<String, Object> getAllConfigs();
}
