package com.huiwutong.conference.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件存储服务接口
 */
public interface FileStorageService {

    /**
     * 存储文件并返回访问URL
     */
    String store(MultipartFile file) throws IOException;

    /**
     * 获取文件存储路径
     */
    String getFilePath(MultipartFile file);

    /**
     * 获取存储类型（1=本地, 2=阿里云OSS, 3=腾讯云COS）
     */
    int getStorageType();

    /**
     * 删除文件
     */
    void deleteFile(String filePath) throws IOException;
}
