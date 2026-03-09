package com.huiwutong.conference.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.SystemFile;
import com.huiwutong.conference.system.SystemFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件模块兼容接口（历史路径）
 */
@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class SystemFileCompatController {

    private final SystemFileService systemFileService;

    /**
     * 兼容历史接口：GET /api/admin/system/files
     */
    @GetMapping("/files")
    @RequireRole({"admin"})
    public Result<PageResult<SystemFile>> getFileListCompat(
        @RequestParam(value = "fileType", required = false) String fileType,
        @RequestParam(value = "fileCategory", required = false) String fileCategory,
        @RequestParam(value = "categoryId", required = false) Long categoryId,
        @RequestParam(value = "startDate", required = false) String startDate,
        @RequestParam(value = "endDate", required = false) String endDate,
        @RequestParam(value = "keyword", required = false) String keyword,
        @RequestParam(value = "page", defaultValue = "1") Integer page,
        @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize
    ) {
        Page<SystemFile> pageInfo = new Page<>(page, pageSize);
        Page<SystemFile> result = systemFileService.getFileList(
            pageInfo, fileType, fileCategory, categoryId, startDate, endDate, keyword
        );
        return Result.success(PageResult.of(result));
    }
}

