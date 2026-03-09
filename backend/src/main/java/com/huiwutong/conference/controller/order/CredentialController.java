package com.huiwutong.conference.controller.order;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.Credential;
import com.huiwutong.conference.service.CredentialService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 凭证控制器
 */
@RestController
@RequestMapping("/api/credentials")
@RequiredArgsConstructor
public class CredentialController {

    private final CredentialService credentialService;

    /**
     * 获取订单的入会凭证（仅凭证归属人可访问）
     */
    @GetMapping("/order/{orderId}")
    @RequireRole("attendee")
    public Result<Credential> getByOrder(@PathVariable Long orderId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(credentialService.getByOrderId(orderId, userId));
    }

    /**
     * 核销凭证（管理员在入场时使用）
     */
    @PostMapping("/{credentialNo}/use")
    @RequireRole("admin")
    public Result<Void> use(@PathVariable String credentialNo) {
        credentialService.useCredential(credentialNo);
        return Result.success("核销成功", null);
    }
}
