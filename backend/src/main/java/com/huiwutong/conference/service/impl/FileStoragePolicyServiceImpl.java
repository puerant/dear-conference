package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.FileStoragePolicy;
import com.huiwutong.conference.entity.StorageBucket;
import com.huiwutong.conference.entity.StorageProvider;
import com.huiwutong.conference.mapper.FileStoragePolicyMapper;
import com.huiwutong.conference.mapper.StorageBucketMapper;
import com.huiwutong.conference.mapper.StorageProviderMapper;
import com.huiwutong.conference.service.FileStoragePolicyService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStoragePolicyServiceImpl implements FileStoragePolicyService {

    private final FileStoragePolicyMapper fileStoragePolicyMapper;
    private final StorageProviderMapper storageProviderMapper;
    private final StorageBucketMapper storageBucketMapper;

    @Override
    public List<FileStoragePolicy> list() {
        return fileStoragePolicyMapper.selectList(
            new LambdaQueryWrapper<FileStoragePolicy>()
                .orderByAsc(FileStoragePolicy::getPriority, FileStoragePolicy::getId)
        );
    }

    @Override
    public FileStoragePolicy getById(Long id) {
        FileStoragePolicy policy = fileStoragePolicyMapper.selectById(id);
        if (policy == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "存储策略不存在");
        }
        return policy;
    }

    @Override
    public Long create(FileStoragePolicy policy) {
        normalize(policy);
        validateTarget(policy.getProviderId(), policy.getBucketId());
        fileStoragePolicyMapper.insert(policy);
        return policy.getId();
    }

    @Override
    public void update(Long id, FileStoragePolicy policy) {
        FileStoragePolicy existing = fileStoragePolicyMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "存储策略不存在");
        }
        normalize(policy);
        validateTarget(policy.getProviderId(), policy.getBucketId());
        policy.setId(id);
        fileStoragePolicyMapper.updateById(policy);
    }

    @Override
    public void updateEnabled(Long id, Integer enabled) {
        FileStoragePolicy existing = fileStoragePolicyMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "存储策略不存在");
        }
        if (enabled == null || (enabled != 0 && enabled != 1)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "启用状态仅支持 0/1");
        }
        FileStoragePolicy update = new FileStoragePolicy();
        update.setId(id);
        update.setIsEnabled(enabled);
        fileStoragePolicyMapper.updateById(update);
    }

    @Override
    public void delete(Long id) {
        FileStoragePolicy existing = fileStoragePolicyMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "存储策略不存在");
        }
        fileStoragePolicyMapper.deleteById(id);
    }

    @Override
    public FileStoragePolicy matchPolicy(String fileCategory, String fileType) {
        List<FileStoragePolicy> candidates = fileStoragePolicyMapper.selectList(
            new LambdaQueryWrapper<FileStoragePolicy>()
                .eq(FileStoragePolicy::getIsEnabled, 1)
                .orderByAsc(FileStoragePolicy::getPriority, FileStoragePolicy::getId)
        );
        for (FileStoragePolicy policy : candidates) {
            boolean categoryMatch = StringUtils.isBlank(policy.getFileCategory()) || StringUtils.equals(policy.getFileCategory(), fileCategory);
            boolean typeMatch = StringUtils.isBlank(policy.getFileType()) || StringUtils.equals(policy.getFileType(), fileType);
            if (categoryMatch && typeMatch) {
                return policy;
            }
        }
        return null;
    }

    private void normalize(FileStoragePolicy policy) {
        if (policy.getPriority() == null) {
            policy.setPriority(100);
        }
        if (policy.getIsEnabled() == null) {
            policy.setIsEnabled(1);
        }
        if (policy.getPathRule() == null) {
            policy.setPathRule("yyyy/MM/dd/{category}");
        }
    }

    private void validateTarget(Long providerId, Long bucketId) {
        StorageProvider provider = storageProviderMapper.selectById(providerId);
        if (provider == null) {
            throw new BusinessException(ErrorCode.STORAGE_PROVIDER_NOT_FOUND);
        }
        StorageBucket bucket = storageBucketMapper.selectById(bucketId);
        if (bucket == null || !bucket.getProviderId().equals(providerId)) {
            throw new BusinessException(ErrorCode.STORAGE_BUCKET_NOT_FOUND);
        }
    }
}

