package com.huiwutong.conference.controller.file;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.SystemFile;
import com.huiwutong.conference.system.SystemFileService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/file")
@RequiredArgsConstructor
public class FileController {

    private final SystemFileService systemFileService;

    @PostMapping("/upload")
    @RequireRole({"admin"})
    public Result<SystemFile> uploadFile(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "category", required = false) String fileCategory,
                                         @RequestParam(value = "categoryId", required = false) Long categoryId,
                                         @RequestParam(value = "providerId", required = false) Long providerId,
                                         @RequestParam(value = "bucketId", required = false) Long bucketId,
                                         @RequestParam(value = "visibility", required = false) Integer visibility) {
        return Result.success(systemFileService.uploadFile(file, fileCategory, categoryId, providerId, bucketId, visibility));
    }

    @GetMapping("/list")
    @RequireRole({"admin"})
    public Result<PageResult<SystemFile>> getFileList(@RequestParam(value = "fileType", required = false) String fileType,
                                                      @RequestParam(value = "fileCategory", required = false) String fileCategory,
                                                      @RequestParam(value = "categoryId", required = false) Long categoryId,
                                                      @RequestParam(value = "startDate", required = false) String startDate,
                                                      @RequestParam(value = "endDate", required = false) String endDate,
                                                      @RequestParam(value = "keyword", required = false) String keyword,
                                                      @RequestParam(value = "page", defaultValue = "1") Integer page,
                                                      @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        Page<SystemFile> pageInfo = new Page<>(page, pageSize);
        return Result.success(PageResult.of(systemFileService.getFileList(
            pageInfo, fileType, fileCategory, categoryId, startDate, endDate, keyword
        )));
    }

    @DeleteMapping("/{id}")
    @RequireRole({"admin"})
    public Result<Void> delete(@PathVariable Long id) {
        systemFileService.deleteFile(id);
        return Result.success();
    }

    @PostMapping("/{id}/restore")
    @RequireRole({"admin"})
    public Result<Void> restore(@PathVariable Long id) {
        systemFileService.restoreFile(id);
        return Result.success();
    }

    @PostMapping("/batch-delete")
    @RequireRole({"admin"})
    public Result<Void> batchDelete(@RequestBody BatchDeleteRequest request) {
        systemFileService.batchDeleteFiles(request.getIds());
        return Result.success();
    }

    @GetMapping("/{id}/preview-url")
    @RequireRole({"admin"})
    public Result<String> previewUrl(@PathVariable Long id) {
        return Result.success(systemFileService.getPreviewUrl(id));
    }

    @Data
    public static class BatchDeleteRequest {
        private Long[] ids;
    }
}

