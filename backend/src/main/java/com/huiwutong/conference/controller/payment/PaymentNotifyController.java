package com.huiwutong.conference.controller.payment;

import com.huiwutong.conference.service.PaymentService;
import com.huiwutong.conference.service.dto.payment.NotifyResult;
import com.huiwutong.conference.service.payment.PaymentStrategy;
import com.huiwutong.conference.service.payment.PaymentStrategyFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

/**
 * 支付回调接收控制器（不需要 JWT 鉴权，但各渠道内部验签）
 */
@Slf4j
@RestController
@RequestMapping("/api/payment/notify")
@RequiredArgsConstructor
public class PaymentNotifyController {

    private final PaymentStrategyFactory strategyFactory;
    private final PaymentService paymentService;

    /**
     * 微信支付回调（JSON 报文 + 微信签名头）
     */
    @PostMapping("/wechat")
    public ResponseEntity<String> wechatNotify(HttpServletRequest request) {
        PaymentStrategy strategy = strategyFactory.get("wechat");
        String rawBody = readBody(request);
        NotifyResult result = strategy.parseNotify(request, rawBody);

        if (result == null || !result.isSuccess()) {
            log.warn("微信回调验证失败");
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(strategy.buildFailResponse());
        }

        try {
            paymentService.handleNotify(result.getPaymentNo(), result.getProviderTradeNo(), rawBody);
        } catch (Exception e) {
            log.error("处理微信回调业务异常：{}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(strategy.buildFailResponse());
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(strategy.buildSuccessResponse());
    }

    /**
     * 支付宝回调（form-urlencoded 表单参数 + RSA 签名）
     */
    @PostMapping(value = "/alipay", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> alipayNotify(HttpServletRequest request) {
        PaymentStrategy strategy = strategyFactory.get("alipay");
        // 支付宝参数通过 getParameterMap() 读取，rawBody 仅用于留存
        String rawBody = request.getQueryString() != null ? request.getQueryString() : "";
        NotifyResult result = strategy.parseNotify(request, rawBody);

        if (result == null || !result.isSuccess()) {
            log.warn("支付宝回调验证失败");
            return ResponseEntity.ok(strategy.buildFailResponse());
        }

        try {
            paymentService.handleNotify(result.getPaymentNo(), result.getProviderTradeNo(), rawBody);
        } catch (Exception e) {
            log.error("处理支付宝回调业务异常：{}", e.getMessage(), e);
            return ResponseEntity.ok(strategy.buildFailResponse());
        }

        // 支付宝要求返回纯文本 "success"
        return ResponseEntity.ok(strategy.buildSuccessResponse());
    }

    private String readBody(HttpServletRequest request) {
        try {
            return StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("读取请求体失败：{}", e.getMessage());
            return "";
        }
    }
}
