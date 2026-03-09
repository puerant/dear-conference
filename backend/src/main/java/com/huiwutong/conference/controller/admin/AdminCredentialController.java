package com.huiwutong.conference.controller.admin;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.Credential;
import com.huiwutong.conference.service.CredentialService;
import com.huiwutong.conference.service.dto.credential.CredentialQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 凭证管理控制器
 */
@RestController
@RequestMapping("/api/admin/credentials")
@RequiredArgsConstructor
public class AdminCredentialController {

    private final CredentialService credentialService;

    @GetMapping
    @RequireRole("admin")
    public Result<PageResult<Credential>> list(CredentialQueryDto query) {
        return Result.success(credentialService.listAll(query));
    }

    @GetMapping("/{credentialNo}")
    @RequireRole("admin")
    public Result<Credential> detail(@PathVariable String credentialNo) {
        return Result.success(credentialService.getByCredentialNo(credentialNo));
    }
}
