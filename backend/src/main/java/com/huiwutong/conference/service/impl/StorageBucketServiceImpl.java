package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.StorageBucket;
import com.huiwutong.conference.entity.StorageProvider;
import com.huiwutong.conference.mapper.StorageBucketMapper;
import com.huiwutong.conference.mapper.StorageProviderMapper;
import com.huiwutong.conference.service.StorageBucketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageBucketServiceImpl implements StorageBucketService {

    private final StorageBucketMapper storageBucketMapper;
    private final StorageProviderMapper storageProviderMapper;

    @Override
    public List<StorageBucket> list(Long providerId) {
        LambdaQueryWrapper<StorageBucket> wrapper = new LambdaQueryWrapper<>();
        if (providerId != null) {
            wrapper.eq(StorageBucket::getProviderId, providerId);
        }
        wrapper.orderByDesc(StorageBucket::getIsDefault).orderByAsc(StorageBucket::getId);
        return storageBucketMapper.selectList(wrapper);
    }

    @Override
    public StorageBucket getById(Long id) {
        StorageBucket bucket = storageBucketMapper.selectById(id);
        if (bucket == null) {
            throw new BusinessException(ErrorCode.STORAGE_BUCKET_NOT_FOUND);
        }
        return bucket;
    }

    @Override
    @Transactional
    public Long create(StorageBucket bucket) {
        validateProvider(bucket.getProviderId());
        normalize(bucket);
        if (bucket.getIsDefault() != null && bucket.getIsDefault() == 1) {
            clearDefaultByProvider(bucket.getProviderId());
        }
        storageBucketMapper.insert(bucket);
        ensureDefaultEnabledBucket(bucket.getProviderId(), bucket.getId());
        return bucket.getId();
    }

    @Override
    @Transactional
    public void update(Long id, StorageBucket bucket) {
        StorageBucket existing = storageBucketMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.STORAGE_BUCKET_NOT_FOUND);
        }
        validateProvider(bucket.getProviderId());
        normalize(bucket);
        bucket.setId(id);
        if (bucket.getIsDefault() != null && bucket.getIsDefault() == 1) {
            clearDefaultByProvider(bucket.getProviderId());
        }
        storageBucketMapper.updateById(bucket);
        ensureDefaultEnabledBucket(bucket.getProviderId(), id);
    }

    @Override
    @Transactional
    public void updateEnabled(Long id, Integer enabled) {
        StorageBucket bucket = storageBucketMapper.selectById(id);
        if (bucket == null) {
            throw new BusinessException(ErrorCode.STORAGE_BUCKET_NOT_FOUND);
        }
        if (enabled == null || (enabled != 0 && enabled != 1)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "启用状态仅支持 0/1");
        }
        if (enabled == 0 && bucket.getIsDefault() != null && bucket.getIsDefault() == 1) {
            StorageBucket another = storageBucketMapper.selectOne(
                new LambdaQueryWrapper<StorageBucket>()
                    .eq(StorageBucket::getProviderId, bucket.getProviderId())
                    .eq(StorageBucket::getIsEnabled, 1)
                    .ne(StorageBucket::getId, id)
                    .orderByAsc(StorageBucket::getId)
                    .last("LIMIT 1")
            );
            if (another == null) {
                throw new BusinessException(ErrorCode.FILE_STORAGE_TARGET_NOT_FOUND, "至少保留一个可用默认桶");
            }
            setDefault(another.getId());
        }
        StorageBucket update = new StorageBucket();
        update.setId(id);
        update.setIsEnabled(enabled);
        storageBucketMapper.updateById(update);
        ensureDefaultEnabledBucket(bucket.getProviderId(), id);
    }

    @Override
    @Transactional
    public void setDefault(Long id) {
        StorageBucket bucket = storageBucketMapper.selectById(id);
        if (bucket == null) {
            throw new BusinessException(ErrorCode.STORAGE_BUCKET_NOT_FOUND);
        }
        if (bucket.getIsEnabled() == null || bucket.getIsEnabled() != 1) {
            throw new BusinessException(ErrorCode.STORAGE_BUCKET_DISABLED);
        }
        clearDefaultByProvider(bucket.getProviderId());
        StorageBucket update = new StorageBucket();
        update.setId(id);
        update.setIsDefault(1);
        storageBucketMapper.updateById(update);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StorageBucket bucket = storageBucketMapper.selectById(id);
        if (bucket == null) {
            throw new BusinessException(ErrorCode.STORAGE_BUCKET_NOT_FOUND);
        }
        storageBucketMapper.deleteById(id);
        ensureDefaultEnabledBucket(bucket.getProviderId(), null);
    }

    @Override
    public StorageBucket getDefaultEnabledBucketByProvider(Long providerId) {
        StorageBucket bucket = storageBucketMapper.selectOne(
            new LambdaQueryWrapper<StorageBucket>()
                .eq(StorageBucket::getProviderId, providerId)
                .eq(StorageBucket::getIsEnabled, 1)
                .eq(StorageBucket::getIsDefault, 1)
                .last("LIMIT 1")
        );
        if (bucket != null) {
            return bucket;
        }
        bucket = storageBucketMapper.selectOne(
            new LambdaQueryWrapper<StorageBucket>()
                .eq(StorageBucket::getProviderId, providerId)
                .eq(StorageBucket::getIsEnabled, 1)
                .orderByAsc(StorageBucket::getId)
                .last("LIMIT 1")
        );
        if (bucket == null) {
            throw new BusinessException(ErrorCode.FILE_STORAGE_TARGET_NOT_FOUND);
        }
        return bucket;
    }

    private void normalize(StorageBucket bucket) {
        if (bucket.getAclType() == null) {
            bucket.setAclType(1);
        }
        if (bucket.getIsEnabled() == null) {
            bucket.setIsEnabled(1);
        }
        if (bucket.getIsDefault() == null) {
            bucket.setIsDefault(0);
        }
        if (bucket.getBasePath() == null) {
            bucket.setBasePath("");
        }
    }

    private void clearDefaultByProvider(Long providerId) {
        storageBucketMapper.update(
            null,
            new LambdaUpdateWrapper<StorageBucket>()
                .eq(StorageBucket::getProviderId, providerId)
                .set(StorageBucket::getIsDefault, 0)
        );
    }

    private void ensureDefaultEnabledBucket(Long providerId, Long fallbackId) {
        StorageBucket defaultEnabled = storageBucketMapper.selectOne(
            new LambdaQueryWrapper<StorageBucket>()
                .eq(StorageBucket::getProviderId, providerId)
                .eq(StorageBucket::getIsEnabled, 1)
                .eq(StorageBucket::getIsDefault, 1)
                .last("LIMIT 1")
        );
        if (defaultEnabled != null) {
            return;
        }
        StorageBucket candidate = null;
        if (fallbackId != null) {
            StorageBucket fallback = storageBucketMapper.selectById(fallbackId);
            if (fallback != null && fallback.getIsEnabled() != null && fallback.getIsEnabled() == 1) {
                candidate = fallback;
            }
        }
        if (candidate == null) {
            candidate = storageBucketMapper.selectOne(
                new LambdaQueryWrapper<StorageBucket>()
                    .eq(StorageBucket::getProviderId, providerId)
                    .eq(StorageBucket::getIsEnabled, 1)
                    .orderByAsc(StorageBucket::getId)
                    .last("LIMIT 1")
            );
        }
        if (candidate == null) {
            return;
        }
        clearDefaultByProvider(providerId);
        StorageBucket update = new StorageBucket();
        update.setId(candidate.getId());
        update.setIsDefault(1);
        storageBucketMapper.updateById(update);
    }

    private void validateProvider(Long providerId) {
        StorageProvider provider = storageProviderMapper.selectById(providerId);
        if (provider == null) {
            throw new BusinessException(ErrorCode.STORAGE_PROVIDER_NOT_FOUND);
        }
    }
}

