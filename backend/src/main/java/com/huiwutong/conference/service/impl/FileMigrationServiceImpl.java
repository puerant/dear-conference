package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.FileMigrationTask;
import com.huiwutong.conference.entity.StorageBucket;
import com.huiwutong.conference.entity.SystemFile;
import com.huiwutong.conference.mapper.FileMigrationTaskMapper;
import com.huiwutong.conference.mapper.SystemFileMapper;
import com.huiwutong.conference.service.FileMigrationService;
import com.huiwutong.conference.service.StorageBucketService;
import com.huiwutong.conference.service.StorageProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class FileMigrationServiceImpl implements FileMigrationService {

    private final FileMigrationTaskMapper fileMigrationTaskMapper;
    private final SystemFileMapper systemFileMapper;
    private final StorageProviderService storageProviderService;
    private final StorageBucketService storageBucketService;

    @Override
    @Transactional
    public Long createTask(Long sourceProviderId, Long targetProviderId, Long createdBy) {
        if (sourceProviderId == null || targetProviderId == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "sourceProviderId/targetProviderId 不能为空");
        }
        if (sourceProviderId.equals(targetProviderId)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "源与目标供应商不能相同");
        }

        storageProviderService.getById(sourceProviderId);
        storageProviderService.getById(targetProviderId);

        FileMigrationTask task = new FileMigrationTask();
        task.setTaskNo(buildTaskNo());
        task.setSourceProviderId(sourceProviderId);
        task.setTargetProviderId(targetProviderId);
        task.setStatus(1);
        task.setCreatedBy(createdBy);
        task.setFileCount(0);
        task.setSuccessCount(0);
        task.setFailCount(0);
        fileMigrationTaskMapper.insert(task);

        execute(task);
        return task.getId();
    }

    @Override
    public List<FileMigrationTask> list() {
        return fileMigrationTaskMapper.selectList(
            new LambdaQueryWrapper<FileMigrationTask>()
                .orderByDesc(FileMigrationTask::getCreatedAt, FileMigrationTask::getId)
        );
    }

    @Override
    public FileMigrationTask getById(Long id) {
        FileMigrationTask task = fileMigrationTaskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(ErrorCode.FILE_MIGRATION_TASK_NOT_FOUND);
        }
        return task;
    }

    @Override
    @Transactional
    public void retry(Long id) {
        FileMigrationTask task = fileMigrationTaskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(ErrorCode.FILE_MIGRATION_TASK_NOT_FOUND);
        }
        execute(task);
    }

    private void execute(FileMigrationTask task) {
        task.setStatus(2);
        task.setStartedAt(LocalDateTime.now());
        task.setFinishedAt(null);
        task.setErrorMessage(null);
        task.setFileCount(0);
        task.setSuccessCount(0);
        task.setFailCount(0);
        fileMigrationTaskMapper.updateById(task);

        int success = 0;
        int fail = 0;
        StringBuilder error = new StringBuilder();

        try {
            StorageBucket targetBucket = storageBucketService.getDefaultEnabledBucketByProvider(task.getTargetProviderId());
            List<SystemFile> sourceFiles = systemFileMapper.selectList(
                new LambdaQueryWrapper<SystemFile>()
                    .eq(SystemFile::getProviderId, task.getSourceProviderId())
                    .eq(SystemFile::getIsDeleted, 0)
            );

            task.setFileCount(sourceFiles.size());
            for (SystemFile file : sourceFiles) {
                try {
                    file.setProviderId(task.getTargetProviderId());
                    file.setBucketId(targetBucket.getId());
                    file.setIsMigrated(1);
                    systemFileMapper.updateById(file);
                    success++;
                } catch (Exception e) {
                    fail++;
                    if (error.length() < 900) {
                        error.append("fileId=").append(file.getId()).append(":").append(e.getMessage()).append("; ");
                    }
                }
            }

            task.setSuccessCount(success);
            task.setFailCount(fail);
            task.setStatus(fail > 0 ? 4 : 3);
            task.setErrorMessage(error.length() == 0 ? null : error.toString());
            task.setFinishedAt(LocalDateTime.now());
            fileMigrationTaskMapper.updateById(task);
        } catch (BusinessException e) {
            task.setStatus(4);
            task.setErrorMessage(e.getMessage());
            task.setFinishedAt(LocalDateTime.now());
            fileMigrationTaskMapper.updateById(task);
            throw e;
        } catch (Exception e) {
            task.setStatus(4);
            task.setErrorMessage(e.getMessage());
            task.setFinishedAt(LocalDateTime.now());
            fileMigrationTaskMapper.updateById(task);
            throw new BusinessException(ErrorCode.FILE_MIGRATION_EXECUTE_FAILED, e.getMessage());
        }
    }

    private String buildTaskNo() {
        int rnd = ThreadLocalRandom.current().nextInt(1000, 10000);
        return "FM" + System.currentTimeMillis() + rnd;
    }
}

