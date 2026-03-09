package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.StorageProvider;
import com.huiwutong.conference.mapper.StorageBucketMapper;
import com.huiwutong.conference.mapper.StorageProviderMapper;
import com.huiwutong.conference.service.StorageProviderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StorageProviderServiceImpl implements StorageProviderService {

    private final StorageProviderMapper storageProviderMapper;
    private final StorageBucketMapper storageBucketMapper;

    @Override
    public List<StorageProvider> list() {
        List<StorageProvider> providers = storageProviderMapper.selectList(
            new LambdaQueryWrapper<StorageProvider>()
                .orderByDesc(StorageProvider::getIsDefault)
                .orderByAsc(StorageProvider::getId)
        );
        providers.forEach(this::maskSecret);
        return providers;
    }

    @Override
    public StorageProvider getById(Long id) {
        StorageProvider provider = storageProviderMapper.selectById(id);
        if (provider == null) {
            throw new BusinessException(ErrorCode.STORAGE_PROVIDER_NOT_FOUND);
        }
        maskSecret(provider);
        return provider;
    }

    @Override
    @Transactional
    public Long create(StorageProvider provider) {
        normalize(provider);
        if (provider.getIsDefault() != null && provider.getIsDefault() == 1) {
            clearDefault();
            provider.setIsEnabled(1);
        }
        storageProviderMapper.insert(provider);
        ensureDefaultEnabledProvider(provider.getId());
        return provider.getId();
    }

    @Override
    @Transactional
    public void update(Long id, StorageProvider provider) {
        StorageProvider existing = storageProviderMapper.selectById(id);
        if (existing == null) {
            throw new BusinessException(ErrorCode.STORAGE_PROVIDER_NOT_FOUND);
        }
        normalize(provider);
        provider.setId(id);

        if (StringUtils.isBlank(provider.getSecretKey()) || "******".equals(provider.getSecretKey())) {
            provider.setSecretKey(existing.getSecretKey());
        }
        if (StringUtils.isBlank(provider.getAccessKey()) || "******".equals(provider.getAccessKey())) {
            provider.setAccessKey(existing.getAccessKey());
        }

        if (provider.getIsDefault() != null && provider.getIsDefault() == 1) {
            clearDefault();
            provider.setIsEnabled(1);
        }
        storageProviderMapper.updateById(provider);
        ensureDefaultEnabledProvider(id);
    }

    @Override
    @Transactional
    public void updateEnabled(Long id, Integer enabled) {
        StorageProvider provider = storageProviderMapper.selectById(id);
        if (provider == null) {
            throw new BusinessException(ErrorCode.STORAGE_PROVIDER_NOT_FOUND);
        }
        if (enabled == null || (enabled != 0 && enabled != 1)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "启用状态仅支持 0/1");
        }

        if (enabled == 0 && provider.getIsDefault() != null && provider.getIsDefault() == 1) {
            StorageProvider another = storageProviderMapper.selectOne(
                new LambdaQueryWrapper<StorageProvider>()
                    .eq(StorageProvider::getIsEnabled, 1)
                    .ne(StorageProvider::getId, id)
                    .orderByAsc(StorageProvider::getId)
                    .last("LIMIT 1")
            );
            if (another == null) {
                throw new BusinessException(ErrorCode.FILE_STORAGE_TARGET_NOT_FOUND);
            }
            setDefault(another.getId());
        }

        StorageProvider update = new StorageProvider();
        update.setId(id);
        update.setIsEnabled(enabled);
        storageProviderMapper.updateById(update);
        ensureDefaultEnabledProvider(id);
    }

    @Override
    @Transactional
    public void setDefault(Long id) {
        StorageProvider provider = storageProviderMapper.selectById(id);
        if (provider == null) {
            throw new BusinessException(ErrorCode.STORAGE_PROVIDER_NOT_FOUND);
        }
        if (provider.getIsEnabled() == null || provider.getIsEnabled() != 1) {
            throw new BusinessException(ErrorCode.STORAGE_PROVIDER_DISABLED);
        }
        clearDefault();
        StorageProvider update = new StorageProvider();
        update.setId(id);
        update.setIsDefault(1);
        storageProviderMapper.updateById(update);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        StorageProvider provider = storageProviderMapper.selectById(id);
        if (provider == null) {
            throw new BusinessException(ErrorCode.STORAGE_PROVIDER_NOT_FOUND);
        }
        long bucketCount = storageBucketMapper.selectCount(
            new LambdaQueryWrapper<com.huiwutong.conference.entity.StorageBucket>()
                .eq(com.huiwutong.conference.entity.StorageBucket::getProviderId, id)
        );
        if (bucketCount > 0) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "该供应商已绑定存储桶，无法删除");
        }
        storageProviderMapper.deleteById(id);
        ensureDefaultEnabledProvider(null);
    }

    @Override
    public void testConnection(Long id) {
        StorageProvider provider = storageProviderMapper.selectById(id);
        if (provider == null) {
            throw new BusinessException(ErrorCode.STORAGE_PROVIDER_NOT_FOUND);
        }
        if (provider.getIsEnabled() == null || provider.getIsEnabled() != 1) {
            throw new BusinessException(ErrorCode.STORAGE_PROVIDER_DISABLED);
        }

        Integer type = provider.getProviderType();
        if (type != null && type == 1) {
            return;
        }
        if (StringUtils.isAnyBlank(provider.getEndpoint(), provider.getAccessKey(), provider.getSecretKey())) {
            throw new BusinessException(ErrorCode.STORAGE_TEST_CONNECT_FAILED, "云存储配置不完整");
        }
    }

    @Override
    public StorageProvider getDefaultEnabledProvider() {
        StorageProvider provider = storageProviderMapper.selectOne(
            new LambdaQueryWrapper<StorageProvider>()
                .eq(StorageProvider::getIsEnabled, 1)
                .eq(StorageProvider::getIsDefault, 1)
                .last("LIMIT 1")
        );
        if (provider != null) {
            return provider;
        }
        provider = storageProviderMapper.selectOne(
            new LambdaQueryWrapper<StorageProvider>()
                .eq(StorageProvider::getIsEnabled, 1)
                .orderByAsc(StorageProvider::getId)
                .last("LIMIT 1")
        );
        if (provider == null) {
            throw new BusinessException(ErrorCode.FILE_STORAGE_TARGET_NOT_FOUND);
        }
        return provider;
    }

    private void normalize(StorageProvider provider) {
        if (provider.getIsEnabled() == null) {
            provider.setIsEnabled(1);
        }
        if (provider.getIsDefault() == null) {
            provider.setIsDefault(0);
        }
        if (provider.getStsEnabled() == null) {
            provider.setStsEnabled(0);
        }
    }

    private void clearDefault() {
        storageProviderMapper.update(
            null,
            new LambdaUpdateWrapper<StorageProvider>()
                .set(StorageProvider::getIsDefault, 0)
        );
    }

    private void ensureDefaultEnabledProvider(Long fallbackId) {
        StorageProvider defaultEnabled = storageProviderMapper.selectOne(
            new LambdaQueryWrapper<StorageProvider>()
                .eq(StorageProvider::getIsEnabled, 1)
                .eq(StorageProvider::getIsDefault, 1)
                .last("LIMIT 1")
        );
        if (defaultEnabled != null) {
            return;
        }
        StorageProvider candidate = null;
        if (fallbackId != null) {
            StorageProvider fallback = storageProviderMapper.selectById(fallbackId);
            if (fallback != null && fallback.getIsEnabled() != null && fallback.getIsEnabled() == 1) {
                candidate = fallback;
            }
        }
        if (candidate == null) {
            candidate = storageProviderMapper.selectOne(
                new LambdaQueryWrapper<StorageProvider>()
                    .eq(StorageProvider::getIsEnabled, 1)
                    .orderByAsc(StorageProvider::getId)
                    .last("LIMIT 1")
            );
        }
        if (candidate == null) {
            return;
        }
        clearDefault();
        StorageProvider update = new StorageProvider();
        update.setId(candidate.getId());
        update.setIsDefault(1);
        storageProviderMapper.updateById(update);
    }

    private void maskSecret(StorageProvider provider) {
        if (provider != null) {
            if (StringUtils.isNotBlank(provider.getAccessKey())) {
                provider.setAccessKey("******");
            }
            if (StringUtils.isNotBlank(provider.getSecretKey())) {
                provider.setSecretKey("******");
            }
        }
    }
}

