package com.huiwutong.conference.service.impl;

import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.FileStoragePolicy;
import com.huiwutong.conference.entity.StorageBucket;
import com.huiwutong.conference.entity.StorageProvider;
import com.huiwutong.conference.service.FileRoutingService;
import com.huiwutong.conference.service.FileStoragePolicyService;
import com.huiwutong.conference.service.StorageBucketService;
import com.huiwutong.conference.service.StorageProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileRoutingServiceImpl implements FileRoutingService {

    private final StorageProviderService storageProviderService;
    private final StorageBucketService storageBucketService;
    private final FileStoragePolicyService fileStoragePolicyService;

    @Override
    public RouteTarget resolve(String fileCategory, String fileType, Long providerId, Long bucketId) {
        if (providerId != null && bucketId != null) {
            StorageProvider provider = storageProviderService.getById(providerId);
            StorageBucket bucket = storageBucketService.getById(bucketId);
            validateAvailable(provider, bucket);
            return new RouteTarget(provider, bucket);
        }

        FileStoragePolicy policy = fileStoragePolicyService.matchPolicy(fileCategory, fileType);
        if (policy != null) {
            StorageProvider policyProvider = storageProviderService.getById(policy.getProviderId());
            StorageBucket policyBucket = storageBucketService.getById(policy.getBucketId());
            if (isAvailable(policyProvider, policyBucket)) {
                return new RouteTarget(policyProvider, policyBucket);
            }
        }

        StorageProvider defaultProvider = storageProviderService.getDefaultEnabledProvider();
        StorageBucket defaultBucket = storageBucketService.getDefaultEnabledBucketByProvider(defaultProvider.getId());
        validateAvailable(defaultProvider, defaultBucket);
        return new RouteTarget(defaultProvider, defaultBucket);
    }

    private boolean isAvailable(StorageProvider provider, StorageBucket bucket) {
        return provider.getIsEnabled() != null && provider.getIsEnabled() == 1
            && bucket.getIsEnabled() != null && bucket.getIsEnabled() == 1
            && bucket.getProviderId().equals(provider.getId());
    }

    private void validateAvailable(StorageProvider provider, StorageBucket bucket) {
        if (provider == null) {
            throw new BusinessException(ErrorCode.STORAGE_PROVIDER_NOT_FOUND);
        }
        if (provider.getIsEnabled() == null || provider.getIsEnabled() != 1) {
            throw new BusinessException(ErrorCode.STORAGE_PROVIDER_DISABLED);
        }
        if (bucket == null) {
            throw new BusinessException(ErrorCode.STORAGE_BUCKET_NOT_FOUND);
        }
        if (!bucket.getProviderId().equals(provider.getId())) {
            throw new BusinessException(ErrorCode.FILE_STORAGE_TARGET_NOT_FOUND);
        }
        if (bucket.getIsEnabled() == null || bucket.getIsEnabled() != 1) {
            throw new BusinessException(ErrorCode.STORAGE_BUCKET_DISABLED);
        }
    }
}

