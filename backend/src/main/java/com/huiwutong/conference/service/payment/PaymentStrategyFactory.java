package com.huiwutong.conference.service.payment;

import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 支付策略工厂：只注册 enabled=true 的渠道
 */
@Component
public class PaymentStrategyFactory {

    private final Map<String, PaymentStrategy> strategies;

    public PaymentStrategyFactory(List<PaymentStrategy> allStrategies) {
        this.strategies = allStrategies.stream()
                .filter(PaymentStrategy::isEnabled)
                .collect(Collectors.toMap(PaymentStrategy::getProviderCode, Function.identity()));
    }

    public PaymentStrategy get(String providerCode) {
        PaymentStrategy strategy = strategies.get(providerCode);
        if (strategy == null) {
            throw new BusinessException(ErrorCode.PAYMENT_PROVIDER_NOT_FOUND);
        }
        return strategy;
    }

    public boolean isSupported(String providerCode) {
        return strategies.containsKey(providerCode);
    }
}
