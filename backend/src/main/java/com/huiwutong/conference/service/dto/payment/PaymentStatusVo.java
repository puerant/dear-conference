package com.huiwutong.conference.service.dto.payment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 支付状态查询响应 VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentStatusVo {

    private String paymentNo;
    /** pending / success / failed / closed / refunded */
    private String status;
    private LocalDateTime paidAt;
}
