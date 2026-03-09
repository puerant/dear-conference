package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.FileMigrationTask;
import com.huiwutong.conference.entity.StorageBucket;
import com.huiwutong.conference.entity.StorageProvider;
import com.huiwutong.conference.entity.SystemFile;
import com.huiwutong.conference.mapper.FileMigrationTaskMapper;
import com.huiwutong.conference.mapper.SystemFileMapper;
import com.huiwutong.conference.service.StorageBucketService;
import com.huiwutong.conference.service.StorageProviderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileMigrationServiceImplTest {

    @Mock
    private FileMigrationTaskMapper fileMigrationTaskMapper;
    @Mock
    private SystemFileMapper systemFileMapper;
    @Mock
    private StorageProviderService storageProviderService;
    @Mock
    private StorageBucketService storageBucketService;

    @InjectMocks
    private FileMigrationServiceImpl fileMigrationService;

    @Test
    void createTask_success_countsAndStatusCompleted() {
        StorageProvider source = new StorageProvider();
        source.setId(1L);
        source.setIsEnabled(1);
        StorageProvider target = new StorageProvider();
        target.setId(2L);
        target.setIsEnabled(1);
        when(storageProviderService.getById(1L)).thenReturn(source);
        when(storageProviderService.getById(2L)).thenReturn(target);

        doAnswer(invocation -> {
            FileMigrationTask task = invocation.getArgument(0);
            task.setId(100L);
            return 1;
        }).when(fileMigrationTaskMapper).insert(any(FileMigrationTask.class));

        StorageBucket targetBucket = new StorageBucket();
        targetBucket.setId(20L);
        when(storageBucketService.getDefaultEnabledBucketByProvider(2L)).thenReturn(targetBucket);

        SystemFile f1 = new SystemFile();
        f1.setId(1L);
        f1.setProviderId(1L);
        SystemFile f2 = new SystemFile();
        f2.setId(2L);
        f2.setProviderId(1L);
        when(systemFileMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(List.of(f1, f2));

        Long id = fileMigrationService.createTask(1L, 2L, null);
        assertEquals(100L, id);
        verify(systemFileMapper, times(2)).updateById(any(SystemFile.class));
        verify(fileMigrationTaskMapper, atLeast(2)).updateById(any(FileMigrationTask.class));
    }

    @Test
    void createTask_sameSourceAndTarget_throwParamError() {
        BusinessException ex = assertThrows(BusinessException.class,
            () -> fileMigrationService.createTask(1L, 1L, null));
        assertEquals(ErrorCode.PARAM_ERROR.getCode(), ex.getCode());
    }

    @Test
    void retry_taskNotFound_throwNotFound() {
        when(fileMigrationTaskMapper.selectById(999L)).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class, () -> fileMigrationService.retry(999L));
        assertEquals(ErrorCode.FILE_MIGRATION_TASK_NOT_FOUND.getCode(), ex.getCode());
    }
}
