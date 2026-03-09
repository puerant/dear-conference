package com.huiwutong.conference.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.SystemFile;
import com.huiwutong.conference.system.SystemFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 系统文件管理Controller
 */
@RestController
@RequestMapping("/api/admin/system/file")
@RequiredArgsConstructor
public class SystemFileController {

    private final SystemFileService systemFileService;

    /**
     * 文件上传
     */
    @PostMapping("/upload")
    @RequireRole({"admin"})
    public Result<SystemFile> uploadFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam(value = "category", required = false) String fileCategory,
                                      @RequestParam(value = "categoryId", required = false) Long categoryId) {
        SystemFile systemFile = systemFileService.uploadFile(file, fileCategory, categoryId);

        // 转换大小显示
        String fileSizeDisplay = formatFileSize(systemFile.getFileSize());

        systemFile.setFileName(null); // 不返回原文件名
        return Result.success(systemFile);
    }

    /**
     * 文件列表查询
     */
    @GetMapping
    @RequireRole({"admin"})
    public Result<PageResult<SystemFile>> getFileList(
            @RequestParam(value = "fileType", required = false) String fileType,
            @RequestParam(value = "fileCategory", required = false) String fileCategory,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {

        Page<SystemFile> pageInfo = new Page<>(page, pageSize);
        Page<SystemFile> result = systemFileService.getFileList(
                pageInfo, fileType, fileCategory, categoryId, startDate, endDate, keyword
        );

        PageResult<SystemFile> pageResult = PageResult.of(result);

        return Result.success(pageResult);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/{id}")
    @RequireRole({"admin"})
    public Result<Void> deleteFile(@PathVariable Long id) {
        systemFileService.deleteFile(id);
        return Result.success();
    }

    /**
     * 恢复已删除的文件
     */
    @PostMapping("/{id}/restore")
    @RequireRole({"admin"})
    public Result<Void> restoreFile(@PathVariable Long id) {
        systemFileService.restoreFile(id);
        return Result.success();
    }

    /**
     * 批量删除文件
     */
    @PostMapping("/batch-delete")
    @RequireRole({"admin"})
    public Result<Void> batchDeleteFiles(@RequestBody Object body) {
        Long[] ids = parseIds(body);
        systemFileService.batchDeleteFiles(ids);
        return Result.success();
    }

    @GetMapping("/{id}/preview")
    @RequireRole({"admin"})
    public Result<String> getFilePreviewUrl(@PathVariable Long id) {
        return Result.success(systemFileService.getPreviewUrl(id));
    }

    /**
     * 格式化文件大小
     */
    private String formatFileSize(Long size) {
        if (size == null) return "0 B";

        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", size / 1024.0 / 1024.0);
        } else {
            return String.format("%.2f GB", size / 1024.0 / 1024.0 / 1024.0);
        }
    }

    /**
     * 兼容两种请求体：
     * 1) [1,2,3]
     * 2) {"ids":[1,2,3]}
     */
    @SuppressWarnings("unchecked")
    private Long[] parseIds(Object body) {
        if (body == null) {
            return new Long[0];
        }
        if (body instanceof List<?> list) {
            return list.stream().map(v -> Long.valueOf(String.valueOf(v))).toArray(Long[]::new);
        }
        if (body instanceof Map<?, ?> map) {
            Object idsObj = map.get("ids");
            if (idsObj instanceof List<?> list) {
                return list.stream().map(v -> Long.valueOf(String.valueOf(v))).toArray(Long[]::new);
            }
        }
        return new Long[0];
    }
}
