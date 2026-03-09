package com.huiwutong.conference.controller.system;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.PaymentConfig;
import com.huiwutong.conference.service.PaymentConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class PaymentConfigController {

    private final PaymentConfigService paymentConfigService;

    @PostMapping("/payment-config")
    @RequireRole({"admin"})
    public Result<Void> saveConfig(@Valid @RequestBody PaymentConfig config) {
        paymentConfigService.saveOrUpdate(config);
        return Result.success();
    }

    @DeleteMapping("/payment-config/{id}")
    @RequireRole({"admin"})
    public Result<Void> deleteConfig(@PathVariable Long id) {
        paymentConfigService.delete(id);
        return Result.success();
    }
}
