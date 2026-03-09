package com.huiwutong.conference.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 本地文件存储实现
 */
@Service
@ConditionalOnProperty(name = "file.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.storage.path:/data/files}")
    private String basePath;

    @Value("${server.file-access-url:/files}")
    private String fileAccessUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public String store(MultipartFile file) throws IOException {
        String datePath = LocalDate.now().format(DATE_FORMATTER);
        String fullPath = basePath + File.separator + datePath;
        File dir = new File(fullPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : "";
        String filename = UUID.randomUUID().toString() + extension;

        File targetFile = new File(fullPath, filename);
        file.transferTo(targetFile);

        return fileAccessUrl + "/" + datePath + "/" + filename;
    }

    @Override
    public String getFilePath(MultipartFile file) {
        String datePath = LocalDate.now().format(DATE_FORMATTER);
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : "";
        String filename = UUID.randomUUID().toString() + extension;
        return basePath + File.separator + datePath + File.separator + filename;
    }

    @Override
    public int getStorageType() {
        return 1; // 本地存储
    }

    @Override
    public void deleteFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (file.exists()) {
            Files.delete(file.toPath());
        }
    }
}
