package com.huiwutong.conference.service.payment.impl;

import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.service.dto.payment.NotifyResult;
import com.huiwutong.conference.service.dto.payment.PayRequest;
import com.huiwutong.conference.service.dto.payment.PaymentResponse;
import com.huiwutong.conference.service.dto.payment.RefundRequest;
import com.huiwutong.conference.service.payment.PaymentStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * 银联支付策略（桩实现，待接入）
 */
@Component
public class UnionPayStrategy implements PaymentStrategy {

    @Override
    public String getProviderCode() { return "unionpay"; }

    @Override
    public boolean isEnabled() { return false; }

    @Override
    public PaymentResponse createPayment(PayRequest request) {
        throw new BusinessException(ErrorCode.PAYMENT_PROVIDER_NOT_FOUND);
    }

    @Override
    public NotifyResult parseNotify(HttpServletRequest request, String rawBody) {
        throw new BusinessException(ErrorCode.PAYMENT_PROVIDER_NOT_FOUND);
    }

    @Override
    public void refund(RefundRequest request) {
        throw new BusinessException(ErrorCode.PAYMENT_PROVIDER_NOT_FOUND);
    }

    @Override
    public String buildSuccessResponse() { return ""; }

    @Override
    public String buildFailResponse() { return ""; }
}
