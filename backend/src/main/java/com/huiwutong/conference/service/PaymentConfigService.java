package com.huiwutong.conference.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.entity.PaymentConfig;

/**
 * 支付配置服务
 */
public interface PaymentConfigService {

    /**
     * 获取支付配置列表
     */
    Page<PaymentConfig> getList(int page, int pageSize);

    /**
     * 根据支付类型获取配置
     */
    PaymentConfig getByPaymentType(Integer paymentType);

    /**
     * 获取启用的支付配置（不返回密钥）
     */
    PaymentConfig getEnabledConfig(Integer paymentType);

    /**
     * 保存或更新配置
     */
    void saveOrUpdate(PaymentConfig config);

    /**
     * 删除配置
     */
    void delete(Long id);
}
