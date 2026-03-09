package com.huiwutong.conference.controller.file;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.StorageBucket;
import com.huiwutong.conference.service.StorageBucketService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/file/buckets")
@RequiredArgsConstructor
public class StorageBucketController {

    private final StorageBucketService storageBucketService;

    @GetMapping
    @RequireRole({"admin"})
    public Result<List<StorageBucket>> list(@RequestParam(value = "providerId", required = false) Long providerId) {
        return Result.success(storageBucketService.list(providerId));
    }

    @GetMapping("/{id}")
    @RequireRole({"admin"})
    public Result<StorageBucket> get(@PathVariable Long id) {
        return Result.success(storageBucketService.getById(id));
    }

    @PostMapping
    @RequireRole({"admin"})
    public Result<Long> create(@RequestBody StorageBucket bucket) {
        return Result.success(storageBucketService.create(bucket));
    }

    @PutMapping("/{id}")
    @RequireRole({"admin"})
    public Result<Void> update(@PathVariable Long id, @RequestBody StorageBucket bucket) {
        storageBucketService.update(id, bucket);
        return Result.success();
    }

    @PutMapping("/{id}/enable")
    @RequireRole({"admin"})
    public Result<Void> updateEnabled(@PathVariable Long id, @RequestBody EnableRequest request) {
        storageBucketService.updateEnabled(id, request.getEnabled());
        return Result.success();
    }

    @PutMapping("/{id}/default")
    @RequireRole({"admin"})
    public Result<Void> setDefault(@PathVariable Long id) {
        storageBucketService.setDefault(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequireRole({"admin"})
    public Result<Void> delete(@PathVariable Long id) {
        storageBucketService.delete(id);
        return Result.success();
    }

    @Data
    static class EnableRequest {
        private Integer enabled;
    }
}

