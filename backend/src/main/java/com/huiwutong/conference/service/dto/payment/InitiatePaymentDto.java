package com.huiwutong.conference.service.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 发起支付请求 DTO
 */
@Data
public class InitiatePaymentDto {

    @NotBlank(message = "支付方式不能为空")
    @Pattern(regexp = "wechat|alipay|unionpay|paypal", message = "不支持的支付方式")
    private String method;
}
