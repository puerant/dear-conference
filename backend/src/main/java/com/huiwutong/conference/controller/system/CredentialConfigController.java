package com.huiwutong.conference.controller.system;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.CredentialConfig;
import com.huiwutong.conference.service.CredentialConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class CredentialConfigController {

    private final CredentialConfigService credentialConfigService;

    @PostMapping("/credential-config")
    @RequireRole({"admin"})
    public Result<Void> saveConfig(@Valid @RequestBody CredentialConfig config) {
        credentialConfigService.saveOrUpdate(config);
        return Result.success();
    }
}
