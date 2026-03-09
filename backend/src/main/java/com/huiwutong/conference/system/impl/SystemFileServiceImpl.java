package com.huiwutong.conference.system.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.SystemFile;
import com.huiwutong.conference.mapper.SystemFileMapper;
import com.huiwutong.conference.service.FileRoutingService;
import com.huiwutong.conference.storage.FileStorageService;
import com.huiwutong.conference.system.SystemFileService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * 系统文件服务实现
 */
@Service
@RequiredArgsConstructor
public class SystemFileServiceImpl implements SystemFileService {

    private final SystemFileMapper fileMapper;
    private final FileStorageService fileStorageService;
    private final FileRoutingService fileRoutingService;

    // 文件大小限制（字节）
    private static final long MAX_IMAGE_SIZE = 5L * 1024 * 1024;        // 5MB
    private static final long MAX_DOCUMENT_SIZE = 20L * 1024 * 1024;   // 20MB
    private static final long MAX_PRESENTATION_SIZE = 30L * 1024 * 1024; // 30MB

    // 支持的文件类型
    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> DOCUMENT_EXTENSIONS = Set.of("pdf", "doc", "docx");
    private static final Set<String> PRESENTATION_EXTENSIONS = Set.of("ppt", "pptx");

    @Override
    public SystemFile uploadFile(MultipartFile file, String fileCategory, Long categoryId) {
        return uploadFile(file, fileCategory, categoryId, null, null, 1);
    }

    @Override
    public SystemFile uploadFile(MultipartFile file, String fileCategory, Long categoryId,
                                 Long providerId, Long bucketId, Integer visibility) {
        // 校验文件
        validateFile(file);

        // 计算文件MD5
        String md5;
        try {
            md5 = DigestUtils.md5Hex(file.getInputStream());
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_STORAGE_FAILED, "MD5计算失败: " + e.getMessage());
        }

        // 检查是否已存在相同文件（去重）
        SystemFile existingFile = fileMapper.selectByMd5(md5);
        if (existingFile != null) {
            return existingFile; // 返回已有文件
        }

        String fileType = determineFileType(file.getOriginalFilename());
        FileRoutingService.RouteTarget routeTarget = fileRoutingService.resolve(fileCategory, fileType, providerId, bucketId);

        // 存储文件
        String fileUrl;
        String filePath;
        try {
            fileUrl = fileStorageService.store(file);
            filePath = fileUrl;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FILE_STORAGE_FAILED, e.getMessage());
        }

        // 保存文件记录
        SystemFile systemFile = new SystemFile();
        systemFile.setFileName(file.getOriginalFilename());
        systemFile.setFilePath(filePath);
        systemFile.setFileUrl(fileUrl);
        systemFile.setFileType(fileType);
        systemFile.setFileExtension(getFileExtension(file.getOriginalFilename()));
        systemFile.setMimeType(file.getContentType());
        systemFile.setFileSize(file.getSize());
        systemFile.setFileCategory(fileCategory);
        systemFile.setCategoryId(categoryId);
        systemFile.setStorageType(fileStorageService.getStorageType());
        systemFile.setProviderId(routeTarget.provider().getId());
        systemFile.setBucketId(routeTarget.bucket().getId());
        systemFile.setObjectKey(filePath);
        systemFile.setStoragePath(filePath);
        systemFile.setVisibility(visibility == null ? 1 : visibility);
        systemFile.setVersionNo(1);
        systemFile.setIsMigrated(0);
        systemFile.setMd5(md5);
        systemFile.setIsDeleted(0);

        fileMapper.insert(systemFile);
        return systemFile;
    }

    @Override
    public void deleteFile(Long id) {
        SystemFile file = fileMapper.selectById(id);
        if (file == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        // 软删除
        file.setIsDeleted(1);
        fileMapper.updateById(file);

        // TODO: 定时任务清理物理文件
    }

    @Override
    public void restoreFile(Long id) {
        SystemFile file = fileMapper.selectById(id);
        if (file == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }

        file.setIsDeleted(0);
        fileMapper.updateById(file);
    }

    @Override
    public void batchDeleteFiles(Long[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }

        for (Long id : ids) {
            deleteFile(id);
        }
    }

    @Override
    public Page<SystemFile> getFileList(Page<SystemFile> page, String fileType, String fileCategory,
                                                Long categoryId, String startDate, String endDate, String keyword) {
        LambdaQueryWrapper<SystemFile> wrapper = new LambdaQueryWrapper<>();

        if (fileType != null && !fileType.isEmpty()) {
            wrapper.eq(SystemFile::getFileType, fileType);
        }
        if (fileCategory != null && !fileCategory.isEmpty()) {
            wrapper.eq(SystemFile::getFileCategory, fileCategory);
        }
        if (categoryId != null) {
            wrapper.eq(SystemFile::getCategoryId, categoryId);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(SystemFile::getCreatedAt, startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(SystemFile::getCreatedAt, endDate);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(SystemFile::getFileName, keyword);
        }

        wrapper.eq(SystemFile::getIsDeleted, 0);
        wrapper.orderByDesc(SystemFile::getCreatedAt);

        return fileMapper.selectPage(page, wrapper);
    }

    @Override
    public String getPreviewUrl(Long id) {
        SystemFile file = fileMapper.selectById(id);
        if (file == null || (file.getIsDeleted() != null && file.getIsDeleted() == 1)) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        if (file.getVisibility() != null && file.getVisibility() == 2) {
            return file.getFileUrl();
        }
        if (file.getFileUrl() == null) {
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
        long expires = System.currentTimeMillis() + 10 * 60 * 1000L;
        String separator = file.getFileUrl().contains("?") ? "&" : "?";
        return file.getFileUrl() + separator + "expires=" + expires;
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY);
        }

        String filename = file.getOriginalFilename();
        String extension = getFileExtension(filename).toLowerCase();

        // 验证文件类型
        String fileType = determineFileType(filename);
        long maxSize = getMaxFileSizeByType(fileType);
        if (file.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.FILE_TOO_LARGE);
        }

        // 验证扩展名
        Set<String> allowedExtensions = getAllowedExtensions(fileType);
        if (!allowedExtensions.contains(extension)) {
            throw new BusinessException(ErrorCode.FILE_TYPE_NOT_ALLOWED);
        }
    }

    private String determineFileType(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        if (IMAGE_EXTENSIONS.contains(extension)) {
            return "image";
        } else if (DOCUMENT_EXTENSIONS.contains(extension)) {
            return "document";
        } else if (PRESENTATION_EXTENSIONS.contains(extension)) {
            return "presentation";
        }
        return "unknown";
    }

    private long getMaxFileSizeByType(String fileType) {
        return switch (fileType) {
            case "image" -> MAX_IMAGE_SIZE;
            case "document" -> MAX_DOCUMENT_SIZE;
            case "presentation" -> MAX_PRESENTATION_SIZE;
            default -> MAX_IMAGE_SIZE;
        };
    }

    private Set<String> getAllowedExtensions(String fileType) {
        return switch (fileType) {
            case "image" -> IMAGE_EXTENSIONS;
            case "document" -> DOCUMENT_EXTENSIONS;
            case "presentation" -> PRESENTATION_EXTENSIONS;
            default -> Set.of();
        };
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }
}
