package com.huiwutong.conference.controller.payment;

import com.huiwutong.conference.common.annotation.RequireLogin;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.PaymentService;
import com.huiwutong.conference.service.dto.payment.PaymentStatusVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 支付状态查询控制器
 */
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 查询支付状态（前端轮询）
     */
    @GetMapping("/{paymentNo}/status")
    @RequireLogin
    public Result<PaymentStatusVo> getStatus(@PathVariable String paymentNo) {
        return Result.success(paymentService.getStatus(paymentNo));
    }
}
