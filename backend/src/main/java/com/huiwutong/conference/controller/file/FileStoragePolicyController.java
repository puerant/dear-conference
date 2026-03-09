package com.huiwutong.conference.controller.file;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.FileStoragePolicy;
import com.huiwutong.conference.service.FileStoragePolicyService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/file/policies")
@RequiredArgsConstructor
public class FileStoragePolicyController {

    private final FileStoragePolicyService fileStoragePolicyService;

    @GetMapping
    @RequireRole({"admin"})
    public Result<List<FileStoragePolicy>> list() {
        return Result.success(fileStoragePolicyService.list());
    }

    @GetMapping("/{id}")
    @RequireRole({"admin"})
    public Result<FileStoragePolicy> get(@PathVariable Long id) {
        return Result.success(fileStoragePolicyService.getById(id));
    }

    @PostMapping
    @RequireRole({"admin"})
    public Result<Long> create(@RequestBody FileStoragePolicy policy) {
        return Result.success(fileStoragePolicyService.create(policy));
    }

    @PutMapping("/{id}")
    @RequireRole({"admin"})
    public Result<Void> update(@PathVariable Long id, @RequestBody FileStoragePolicy policy) {
        fileStoragePolicyService.update(id, policy);
        return Result.success();
    }

    @PutMapping("/{id}/enable")
    @RequireRole({"admin"})
    public Result<Void> updateEnabled(@PathVariable Long id, @RequestBody EnableRequest request) {
        fileStoragePolicyService.updateEnabled(id, request.getEnabled());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequireRole({"admin"})
    public Result<Void> delete(@PathVariable Long id) {
        fileStoragePolicyService.delete(id);
        return Result.success();
    }

    @Data
    static class EnableRequest {
        private Integer enabled;
    }
}

