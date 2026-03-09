package com.huiwutong.conference.controller.system;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.EmailConfig;
import com.huiwutong.conference.service.EmailConfigService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class EmailConfigController {

    private final EmailConfigService emailConfigService;

    @PostMapping("/email-config")
    @RequireRole({"admin"})
    public Result<Void> saveConfig(@Valid @RequestBody EmailConfig config) {
        emailConfigService.saveOrUpdate(config);
        return Result.success();
    }

    @PostMapping("/email/test")
    @RequireRole({"admin"})
    public Result<Void> testEmail(@RequestBody TestEmailRequest request) {
        emailConfigService.sendTestEmail(request.getToEmail());
        return Result.success();
    }

    @GetMapping("/email-configs")
    @RequireRole({"admin"})
    public Result<List<EmailConfig>> list() {
        return Result.success(emailConfigService.listPublicConfigs());
    }

    @GetMapping("/email-configs/{id}")
    @RequireRole({"admin"})
    public Result<EmailConfig> detail(@PathVariable Long id) {
        return Result.success(emailConfigService.getPublicConfigById(id));
    }

    @PostMapping("/email-configs")
    @RequireRole({"admin"})
    public Result<Long> create(@Valid @RequestBody EmailConfig config) {
        return Result.success(emailConfigService.create(config));
    }

    @PutMapping("/email-configs/{id}")
    @RequireRole({"admin"})
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody EmailConfig config) {
        emailConfigService.update(id, config);
        return Result.success();
    }

    @PutMapping("/email-configs/{id}/enable")
    @RequireRole({"admin"})
    public Result<Void> updateEnabled(@PathVariable Long id, @RequestBody EnableRequest request) {
        emailConfigService.updateEnabled(id, request.getEnabled());
        return Result.success();
    }

    @PutMapping("/email-configs/{id}/default")
    @RequireRole({"admin"})
    public Result<Void> setDefault(@PathVariable Long id) {
        emailConfigService.setDefault(id);
        return Result.success();
    }

    @DeleteMapping("/email-configs/{id}")
    @RequireRole({"admin"})
    public Result<Void> delete(@PathVariable Long id) {
        emailConfigService.delete(id);
        return Result.success();
    }

    @PostMapping("/email-configs/{id}/test")
    @RequireRole({"admin"})
    public Result<Void> testEmailByConfig(@PathVariable Long id, @RequestBody TestEmailRequest request) {
        emailConfigService.sendTestEmail(id, request.getToEmail());
        return Result.success();
    }

    @Data
    static class TestEmailRequest {
        private String toEmail;
    }

    @Data
    static class EnableRequest {
        private Integer enabled;
    }
}
