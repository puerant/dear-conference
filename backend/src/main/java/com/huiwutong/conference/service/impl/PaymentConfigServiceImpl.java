package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.common.util.AesUtil;
import com.huiwutong.conference.entity.PaymentConfig;
import com.huiwutong.conference.mapper.PaymentConfigMapper;
import com.huiwutong.conference.service.PaymentConfigService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付配置服务实现
 */
@Service
@RequiredArgsConstructor
public class PaymentConfigServiceImpl implements PaymentConfigService {

    private final PaymentConfigMapper paymentConfigMapper;
    private final AesUtil aesUtil;

    @Override
    public Page<PaymentConfig> getList(int page, int pageSize) {
        return paymentConfigMapper.selectPage(new Page<>(page, pageSize), null);
    }

    @Override
    public PaymentConfig getByPaymentType(Integer paymentType) {
        return paymentConfigMapper.selectOne(
            new LambdaQueryWrapper<PaymentConfig>()
                .eq(PaymentConfig::getPaymentType, paymentType)
        );
    }

    @Override
    public PaymentConfig getEnabledConfig(Integer paymentType) {
        PaymentConfig config = paymentConfigMapper.selectOne(
            new LambdaQueryWrapper<PaymentConfig>()
                .eq(PaymentConfig::getPaymentType, paymentType)
                .eq(PaymentConfig::getIsEnabled, 1)
        );
        if (config != null) {
            config.setAppSecret("******");
        }
        return config;
    }

    @Override
    @Transactional
    public void saveOrUpdate(PaymentConfig config) {
        PaymentConfig existing = getByPaymentType(config.getPaymentType());

        // 密码加密处理
        if (StringUtils.isNotBlank(config.getAppSecret()) && !config.getAppSecret().equals("******")) {
            config.setAppSecret(aesUtil.encrypt(config.getAppSecret()));
        } else if (existing != null) {
            config.setAppSecret(existing.getAppSecret());
        }

        if (existing == null) {
            paymentConfigMapper.insert(config);
        } else {
            config.setId(existing.getId());
            paymentConfigMapper.updateById(config);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        paymentConfigMapper.deleteById(id);
    }
}
