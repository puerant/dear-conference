package com.huiwutong.conference.service;

import com.huiwutong.conference.entity.OrderTaskConfig;

/**
 * 订单定时任务配置服务
 */
public interface OrderTaskConfigService {

    /**
     * 获取配置（带缓存）
     */
    OrderTaskConfig getConfig();

    /**
     * 保存或更新配置
     */
    void saveOrUpdate(OrderTaskConfig config);
}
