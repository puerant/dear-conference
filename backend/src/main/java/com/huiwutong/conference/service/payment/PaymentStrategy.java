package com.huiwutong.conference.service.payment;

import com.huiwutong.conference.service.dto.payment.NotifyResult;
import com.huiwutong.conference.service.dto.payment.PayRequest;
import com.huiwutong.conference.service.dto.payment.PaymentResponse;
import com.huiwutong.conference.service.dto.payment.RefundRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 支付渠道策略接口
 */
public interface PaymentStrategy {

    /** 渠道标识：wechat / alipay / unionpay / paypal */
    String getProviderCode();

    /** 该渠道是否已启用（配置中 enabled=true 且 SDK 初始化完毕） */
    boolean isEnabled();

    /**
     * 发起支付，返回支付数据（二维码内容或跳转链接）
     */
    PaymentResponse createPayment(PayRequest request);

    /**
     * 解析并验证回调，返回 NotifyResult；签名不合法或解析失败返回 null
     */
    NotifyResult parseNotify(HttpServletRequest request, String rawBody);

    /**
     * 调用渠道退款 API
     */
    void refund(RefundRequest request);

    /**
     * 构建回调成功应答（各渠道格式不同）
     */
    String buildSuccessResponse();

    /**
     * 构建回调失败应答
     */
    String buildFailResponse();
}
