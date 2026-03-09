package com.huiwutong.conference.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.entity.SystemFile;
import org.springframework.web.multipart.MultipartFile;

/**
 * 系统文件服务接口
 */
public interface SystemFileService {

    /**
     * 上传文件
     * @param file 上传的文件
     * @param fileCategory 文件分类
     * @param categoryId 关联ID
     * @return 文件记录
     */
    SystemFile uploadFile(MultipartFile file, String fileCategory, Long categoryId);

    /**
     * 上传文件（支持指定存储目标）
     */
    SystemFile uploadFile(MultipartFile file, String fileCategory, Long categoryId,
                          Long providerId, Long bucketId, Integer visibility);

    /**
     * 删除文件
     * @param id 文件ID
     */
    void deleteFile(Long id);

    /**
     * 恢复已删除的文件
     * @param id 文件ID
     */
    void restoreFile(Long id);

    /**
     * 批量删除文件
     * @param ids 文件ID列表
     */
    void batchDeleteFiles(Long[] ids);

    /**
     * 查询文件列表
     * @param page 分页参数
     * @param fileType 文件类型
     * @param fileCategory 文件分类
     * @param categoryId 关联ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param keyword 关键词
     * @return 分页结果
     */
    Page<SystemFile> getFileList(Page<SystemFile> page, String fileType, String fileCategory,
                                     Long categoryId, String startDate, String endDate, String keyword);

    /**
     * 生成预览URL
     */
    String getPreviewUrl(Long id);
}
