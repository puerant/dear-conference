package com.huiwutong.conference.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 支付配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    /** 支付超时时间（分钟），超时后自动关单 */
    private int expireMinutes = 15;

    private Wechat wechat = new Wechat();
    private Alipay alipay = new Alipay();
    private UnionPay unionpay = new UnionPay();
    private PayPal paypal = new PayPal();

    @Data
    public static class Wechat {
        private boolean enabled = false;
        private String appId;
        private String mchId;
        private String apiV3Key;
        private String privateKeyPath = "classpath:cert/apiclient_key.pem";
        private String certSerialNo;
        private String notifyUrl;
    }

    @Data
    public static class Alipay {
        private boolean enabled = false;
        private String appId;
        private String gateway = "https://openapi.alipay.com/gateway.do";
        private String privateKey;
        private String alipayPublicKey;
        private String returnUrl;
        private String notifyUrl;
    }

    @Data
    public static class UnionPay {
        private boolean enabled = false;
    }

    @Data
    public static class PayPal {
        private boolean enabled = false;
        private double exchangeRate = 7.20;
    }
}
