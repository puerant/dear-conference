package com.huiwutong.conference.service.payment.impl;

import com.huiwutong.conference.common.config.PaymentProperties;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.service.dto.payment.NotifyResult;
import com.huiwutong.conference.service.dto.payment.PayRequest;
import com.huiwutong.conference.service.dto.payment.PaymentResponse;
import com.huiwutong.conference.service.dto.payment.RefundRequest;
import com.huiwutong.conference.service.payment.PaymentStrategy;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.core.notification.RequestParam;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import com.wechat.pay.java.service.payments.nativepay.model.Amount;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayRequest;
import com.wechat.pay.java.service.payments.nativepay.model.PrepayResponse;
import com.wechat.pay.java.service.payments.model.Transaction;
import com.wechat.pay.java.service.refund.model.AmountReq;
import com.wechat.pay.java.service.refund.model.CreateRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 微信支付（Native Pay v3）策略实现
 */
@Slf4j
@Component
public class WechatPayStrategy implements PaymentStrategy, InitializingBean {

    private final PaymentProperties properties;
    private final ResourceLoader resourceLoader;

    private RSAAutoCertificateConfig wechatConfig;
    private NativePayService nativePayService;
    private com.wechat.pay.java.service.refund.RefundService refundService;
    private boolean initialized = false;

    public WechatPayStrategy(PaymentProperties properties, ResourceLoader resourceLoader) {
        this.properties = properties;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void afterPropertiesSet() {
        PaymentProperties.Wechat cfg = properties.getWechat();
        if (!cfg.isEnabled()) return;
        try {
            String privateKey = StreamUtils.copyToString(
                    resourceLoader.getResource(cfg.getPrivateKeyPath()).getInputStream(),
                    StandardCharsets.UTF_8);

            wechatConfig = new RSAAutoCertificateConfig.Builder()
                    .merchantId(cfg.getMchId())
                    .privateKey(privateKey)
                    .merchantSerialNumber(cfg.getCertSerialNo())
                    .apiV3Key(cfg.getApiV3Key())
                    .build();

            nativePayService = new NativePayService.Builder().config(wechatConfig).build();
            refundService = new com.wechat.pay.java.service.refund.RefundService.Builder().config(wechatConfig).build();
            initialized = true;
            log.info("微信支付初始化成功");
        } catch (Exception e) {
            log.error("微信支付初始化失败：{}", e.getMessage(), e);
        }
    }

    @Override
    public String getProviderCode() { return "wechat"; }

    @Override
    public boolean isEnabled() { return properties.getWechat().isEnabled() && initialized; }

    @Override
    public PaymentResponse createPayment(PayRequest req) {
        PaymentProperties.Wechat cfg = properties.getWechat();

        PrepayRequest prepay = new PrepayRequest();
        Amount amount = new Amount();
        // 微信支付金额单位：分（整数）
        amount.setTotal(req.getAmount().multiply(BigDecimal.valueOf(100)).intValue());
        amount.setCurrency("CNY");
        prepay.setAmount(amount);
        prepay.setAppid(cfg.getAppId());
        prepay.setMchid(cfg.getMchId());
        prepay.setDescription("会议报名-" + req.getDescription());
        prepay.setNotifyUrl(cfg.getNotifyUrl());
        prepay.setOutTradeNo(req.getPaymentNo());
        prepay.setTimeExpire(req.getExpireAt()
                .atZone(ZoneId.of("Asia/Shanghai"))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

        PrepayResponse response = nativePayService.prepay(prepay);

        PaymentResponse result = new PaymentResponse();
        result.setMethod("wechat");
        result.setQrCode(response.getCodeUrl());
        result.setAmount(req.getAmount());
        return result;
    }

    @Override
    public NotifyResult parseNotify(HttpServletRequest request, String rawBody) {
        try {
            RequestParam requestParam = new RequestParam.Builder()
                    .serialNumber(request.getHeader("Wechatpay-Serial"))
                    .nonce(request.getHeader("Wechatpay-Nonce"))
                    .signature(request.getHeader("Wechatpay-Signature"))
                    .timestamp(request.getHeader("Wechatpay-Timestamp"))
                    .body(rawBody)
                    .build();

            NotificationParser parser = new NotificationParser((NotificationConfig) wechatConfig);
            Transaction transaction = parser.parse(requestParam, Transaction.class);

            if (transaction.getTradeState() != Transaction.TradeStateEnum.SUCCESS) {
                return null;
            }

            NotifyResult result = new NotifyResult();
            result.setPaymentNo(transaction.getOutTradeNo());
            result.setProviderTradeNo(transaction.getTransactionId());
            result.setSuccess(true);
            result.setAmount(BigDecimal.valueOf(transaction.getAmount().getTotal())
                    .divide(BigDecimal.valueOf(100)));
            result.setRawData(rawBody);
            return result;
        } catch (Exception e) {
            log.error("微信回调解析失败：{}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void refund(RefundRequest req) {
        CreateRequest refundReq = new CreateRequest();
        refundReq.setOutTradeNo(req.getPaymentNo());
        refundReq.setOutRefundNo(req.getRefundNo());

        AmountReq amountReq = new AmountReq();
        amountReq.setRefund(req.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
        amountReq.setTotal(req.getAmount().multiply(BigDecimal.valueOf(100)).longValue());
        amountReq.setCurrency("CNY");
        refundReq.setAmount(amountReq);
        refundReq.setReason(req.getReason());

        refundService.create(refundReq);
        log.info("微信退款提交成功，退款单号：{}", req.getRefundNo());
    }

    @Override
    public String buildSuccessResponse() {
        return "{\"code\":\"SUCCESS\",\"message\":\"成功\"}";
    }

    @Override
    public String buildFailResponse() {
        return "{\"code\":\"FAIL\",\"message\":\"签名验证失败\"}";
    }
}
