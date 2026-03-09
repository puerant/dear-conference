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

    @Value("${file.storage.path:${file.upload-path:./uploads}}")
    private String basePath;

    @Value("${server.file-access-url:/files}")
    private String fileAccessUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public String store(MultipartFile file) throws IOException {
        String datePath = LocalDate.now().format(DATE_FORMATTER);
        Path dir = Path.of(basePath, datePath).toAbsolutePath().normalize();
        Files.createDirectories(dir);

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : "";
        String filename = UUID.randomUUID().toString() + extension;

        Path targetPath = dir.resolve(filename);
        file.transferTo(targetPath);

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
        return Path.of(basePath, datePath, filename).toString();
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
