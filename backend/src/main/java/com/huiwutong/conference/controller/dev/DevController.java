package com.huiwutong.conference.controller.dev;

import com.huiwutong.conference.common.annotation.RequireLogin;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 开发调试接口（仅 dev profile 加载，生产环境不注册）
 */
@Profile("dev")
@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
public class DevController {

    private final PaymentService paymentService;

    /**
     * 模拟支付成功
     * <p>跳过真实支付渠道，直接将订单标记为已支付并生成凭证，方便本地测试购票流程。</p>
     */
    @PostMapping("/orders/{orderId}/mock-pay")
    @RequireLogin
    public Result<Void> mockPay(@PathVariable Long orderId, HttpServletRequest request) {
        paymentService.mockPay(orderId);
        return Result.success();
    }
}
