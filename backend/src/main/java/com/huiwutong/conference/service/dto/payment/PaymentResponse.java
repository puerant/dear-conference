package com.huiwutong.conference.service.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 发起支付响应 VO（返回给前端）
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

    private String paymentNo;
    private String method;
    private String status = "pending";

    /** 微信 Native Pay 二维码内容，前端生成图片 */
    private String qrCode;

    /** Alipay/UnionPay/PayPal 跳转链接 */
    private String payUrl;

    private BigDecimal amount;
    private String currency = "CNY";

    /** PayPal 专用：外币金额（USD） */
    private BigDecimal foreignAmount;

    private LocalDateTime expireAt;
}
