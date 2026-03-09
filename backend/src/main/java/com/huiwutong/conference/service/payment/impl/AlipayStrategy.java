package com.huiwutong.conference.service.payment.impl;

import com.alipay.v3.ApiClient;
import com.alipay.v3.ApiException;
import com.alipay.v3.Configuration;
import com.alipay.v3.api.AlipayTradeApi;
import com.alipay.v3.model.AlipayTradeRefundModel;
import com.alipay.v3.util.AlipaySignature;
import com.alipay.v3.util.model.AlipayConfig;
import com.huiwutong.conference.common.config.PaymentProperties;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.service.dto.payment.NotifyResult;
import com.huiwutong.conference.service.dto.payment.PayRequest;
import com.huiwutong.conference.service.dto.payment.PaymentResponse;
import com.huiwutong.conference.service.dto.payment.RefundRequest;
import com.huiwutong.conference.service.payment.PaymentStrategy;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 支付宝（PC 网页支付）策略实现 — 使用 alipay-sdk-java-v3
 *
 * 注：alipay.trade.page.pay 是表单跳转 API，不在 v3 REST SDK 内，
 * 通过 AlipaySignature.sign() 手动构造 GET 跳转链接。
 * 退款使用 AlipayTradeApi.refund()（v3 REST API）。
 */
@Slf4j
@Component
public class AlipayStrategy implements PaymentStrategy, InitializingBean {

    private static final String GATEWAY_URL = "https://openapi.alipay.com/gateway.do";
    private static final String CHARSET = "UTF-8";
    private static final String SIGN_TYPE = "RSA2";

    private final PaymentProperties properties;
    private AlipayTradeApi alipayTradeApi;
    private boolean initialized = false;

    public AlipayStrategy(PaymentProperties properties) {
        this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
        PaymentProperties.Alipay cfg = properties.getAlipay();
        if (!cfg.isEnabled()) return;
        try {
            AlipayConfig alipayConfig = new AlipayConfig();
            alipayConfig.setAppId(cfg.getAppId());
            alipayConfig.setPrivateKey(cfg.getPrivateKey());
            alipayConfig.setAlipayPublicKey(cfg.getAlipayPublicKey());

            ApiClient defaultClient = Configuration.getDefaultApiClient();
            defaultClient.setBasePath("https://openapi.alipay.com");
            defaultClient.setAlipayConfig(alipayConfig);

            alipayTradeApi = new AlipayTradeApi();
            initialized = true;
            log.info("支付宝 v3 SDK 初始化成功");
        } catch (Exception e) {
            log.error("支付宝初始化失败：{}", e.getMessage(), e);
        }
    }

    @Override
    public String getProviderCode() { return "alipay"; }

    @Override
    public boolean isEnabled() { return properties.getAlipay().isEnabled() && initialized; }

    @Override
    public PaymentResponse createPayment(PayRequest req) {
        PaymentProperties.Alipay cfg = properties.getAlipay();
        try {
            // alipay.trade.page.pay is a form-redirect API; build the GET URL manually
            Map<String, String> params = new LinkedHashMap<>();
            params.put("app_id", cfg.getAppId());
            params.put("method", "alipay.trade.page.pay");
            params.put("charset", CHARSET);
            params.put("sign_type", SIGN_TYPE);
            params.put("timestamp", LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            params.put("version", "1.0");
            params.put("return_url", cfg.getReturnUrl());
            params.put("notify_url", cfg.getNotifyUrl());
            params.put("biz_content",
                    "{\"out_trade_no\":\"" + req.getPaymentNo()
                    + "\",\"product_code\":\"FAST_INSTANT_TRADE_PAY\""
                    + ",\"total_amount\":\"" + req.getAmount().toPlainString()
                    + "\",\"subject\":\"会议报名-" + req.getDescription() + "\"}");

            String sign = AlipaySignature.sign(params, cfg.getPrivateKey(), CHARSET, SIGN_TYPE);

            StringBuilder url = new StringBuilder(GATEWAY_URL).append('?');
            for (Map.Entry<String, String> e : params.entrySet()) {
                url.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
                   .append('=')
                   .append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                   .append('&');
            }
            url.append("sign=").append(URLEncoder.encode(sign, StandardCharsets.UTF_8));

            PaymentResponse result = new PaymentResponse();
            result.setMethod("alipay");
            result.setPayUrl(url.toString());
            result.setAmount(req.getAmount());
            return result;
        } catch (ApiException e) {
            log.error("支付宝发起支付失败：{}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.PAYMENT_PROVIDER_ERROR);
        }
    }

    @Override
    public NotifyResult parseNotify(HttpServletRequest request, String rawBody) {
        try {
            Map<String, String> params = new HashMap<>();
            for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
                params.put(entry.getKey(), String.join(",", entry.getValue()));
            }

            // v3 SDK: verifyV1 replaces rsaCheckV1
            boolean valid = AlipaySignature.verifyV1(
                    params, properties.getAlipay().getAlipayPublicKey(), CHARSET, SIGN_TYPE);
            if (!valid) {
                log.warn("支付宝回调签名验证失败");
                return null;
            }

            String tradeStatus = params.get("trade_status");
            if (!"TRADE_SUCCESS".equals(tradeStatus) && !"TRADE_FINISHED".equals(tradeStatus)) {
                log.info("支付宝回调状态非成功：{}", tradeStatus);
                return null;
            }

            NotifyResult result = new NotifyResult();
            result.setPaymentNo(params.get("out_trade_no"));
            result.setProviderTradeNo(params.get("trade_no"));
            result.setSuccess(true);
            result.setAmount(new BigDecimal(params.get("total_amount")));
            result.setRawData(rawBody);
            return result;
        } catch (Exception e) {
            log.error("支付宝回调解析失败：{}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public void refund(RefundRequest req) {
        AlipayTradeRefundModel model = new AlipayTradeRefundModel();
        model.setOutTradeNo(req.getPaymentNo());
        model.setRefundAmount(req.getAmount().toPlainString());
        model.setOutRequestNo(req.getRefundNo());
        model.setRefundReason(req.getReason());

        try {
            alipayTradeApi.refund(model);
            log.info("支付宝退款提交成功，退款单号：{}", req.getRefundNo());
        } catch (ApiException e) {
            log.error("支付宝退款失败：{}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.PAYMENT_PROVIDER_ERROR);
        }
    }

    @Override
    public String buildSuccessResponse() { return "success"; }

    @Override
    public String buildFailResponse() { return "fail"; }
}
