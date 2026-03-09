package com.huiwutong.conference.controller.file;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.FileMigrationTask;
import com.huiwutong.conference.service.FileMigrationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/file/migration/tasks")
@RequiredArgsConstructor
public class FileMigrationController {

    private final FileMigrationService fileMigrationService;

    @PostMapping
    @RequireRole({"admin"})
    public Result<Long> create(@RequestBody CreateTaskRequest request) {
        return Result.success(fileMigrationService.createTask(
            request.getSourceProviderId(), request.getTargetProviderId(), null
        ));
    }

    @GetMapping
    @RequireRole({"admin"})
    public Result<List<FileMigrationTask>> list() {
        return Result.success(fileMigrationService.list());
    }

    @GetMapping("/{id}")
    @RequireRole({"admin"})
    public Result<FileMigrationTask> detail(@PathVariable Long id) {
        return Result.success(fileMigrationService.getById(id));
    }

    @PostMapping("/{id}/retry")
    @RequireRole({"admin"})
    public Result<Void> retry(@PathVariable Long id) {
        fileMigrationService.retry(id);
        return Result.success();
    }

    @Data
    static class CreateTaskRequest {
        private Long sourceProviderId;
        private Long targetProviderId;
    }
}

