package com.huiwutong.conference.common.enums;

/**
 * 支付渠道枚举
 */
public enum PaymentProvider {

    WECHAT("wechat", "微信支付"),
    ALIPAY("alipay", "支付宝"),
    UNIONPAY("unionpay", "银联"),
    PAYPAL("paypal", "PayPal");

    private final String code;
    private final String label;

    PaymentProvider(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }
}
