package com.huiwutong.conference.controller.file;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.StorageProvider;
import com.huiwutong.conference.service.StorageProviderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/file/providers")
@RequiredArgsConstructor
public class StorageProviderController {

    private final StorageProviderService storageProviderService;

    @GetMapping
    @RequireRole({"admin"})
    public Result<List<StorageProvider>> list() {
        return Result.success(storageProviderService.list());
    }

    @GetMapping("/{id}")
    @RequireRole({"admin"})
    public Result<StorageProvider> get(@PathVariable Long id) {
        return Result.success(storageProviderService.getById(id));
    }

    @PostMapping
    @RequireRole({"admin"})
    public Result<Long> create(@RequestBody StorageProvider provider) {
        return Result.success(storageProviderService.create(provider));
    }

    @PutMapping("/{id}")
    @RequireRole({"admin"})
    public Result<Void> update(@PathVariable Long id, @RequestBody StorageProvider provider) {
        storageProviderService.update(id, provider);
        return Result.success();
    }

    @PutMapping("/{id}/enable")
    @RequireRole({"admin"})
    public Result<Void> updateEnabled(@PathVariable Long id, @RequestBody EnableRequest request) {
        storageProviderService.updateEnabled(id, request.getEnabled());
        return Result.success();
    }

    @PutMapping("/{id}/default")
    @RequireRole({"admin"})
    public Result<Void> setDefault(@PathVariable Long id) {
        storageProviderService.setDefault(id);
        return Result.success();
    }

    @PostMapping("/{id}/test")
    @RequireRole({"admin"})
    public Result<Void> test(@PathVariable Long id) {
        storageProviderService.testConnection(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequireRole({"admin"})
    public Result<Void> delete(@PathVariable Long id) {
        storageProviderService.delete(id);
        return Result.success();
    }

    @Data
    static class EnableRequest {
        private Integer enabled;
    }
}

