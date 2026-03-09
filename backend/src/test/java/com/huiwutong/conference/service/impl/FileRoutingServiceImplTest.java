package com.huiwutong.conference.service.impl;

import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.FileStoragePolicy;
import com.huiwutong.conference.entity.StorageBucket;
import com.huiwutong.conference.entity.StorageProvider;
import com.huiwutong.conference.service.FileStoragePolicyService;
import com.huiwutong.conference.service.StorageBucketService;
import com.huiwutong.conference.service.StorageProviderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileRoutingServiceImplTest {

    @Mock
    private StorageProviderService storageProviderService;
    @Mock
    private StorageBucketService storageBucketService;
    @Mock
    private FileStoragePolicyService fileStoragePolicyService;

    @InjectMocks
    private FileRoutingServiceImpl fileRoutingService;

    @Test
    void resolve_whenPolicyMatched_returnsPolicyTarget() {
        FileStoragePolicy policy = new FileStoragePolicy();
        policy.setProviderId(2L);
        policy.setBucketId(3L);
        StorageProvider provider = provider(2L, 1);
        StorageBucket bucket = bucket(3L, 2L, 1);

        when(fileStoragePolicyService.matchPolicy("conference_banner", "image")).thenReturn(policy);
        when(storageProviderService.getById(2L)).thenReturn(provider);
        when(storageBucketService.getById(3L)).thenReturn(bucket);

        var target = fileRoutingService.resolve("conference_banner", "image", null, null);
        assertEquals(2L, target.provider().getId());
        assertEquals(3L, target.bucket().getId());
    }

    @Test
    void resolve_whenNoPolicy_useDefaultTarget() {
        StorageProvider defaultProvider = provider(1L, 1);
        StorageBucket defaultBucket = bucket(10L, 1L, 1);
        when(fileStoragePolicyService.matchPolicy("document", "document")).thenReturn(null);
        when(storageProviderService.getDefaultEnabledProvider()).thenReturn(defaultProvider);
        when(storageBucketService.getDefaultEnabledBucketByProvider(1L)).thenReturn(defaultBucket);

        var target = fileRoutingService.resolve("document", "document", null, null);
        assertEquals(1L, target.provider().getId());
        assertEquals(10L, target.bucket().getId());
    }

    @Test
    void resolve_whenExplicitTargetDisabled_throwBucketDisabled() {
        StorageProvider provider = provider(2L, 1);
        StorageBucket bucket = bucket(3L, 2L, 0);
        when(storageProviderService.getById(2L)).thenReturn(provider);
        when(storageBucketService.getById(3L)).thenReturn(bucket);

        BusinessException ex = assertThrows(BusinessException.class,
            () -> fileRoutingService.resolve("conference_banner", "image", 2L, 3L));
        assertEquals(ErrorCode.STORAGE_BUCKET_DISABLED.getCode(), ex.getCode());
    }

    private StorageProvider provider(Long id, Integer enabled) {
        StorageProvider provider = new StorageProvider();
        provider.setId(id);
        provider.setIsEnabled(enabled);
        return provider;
    }

    private StorageBucket bucket(Long id, Long providerId, Integer enabled) {
        StorageBucket bucket = new StorageBucket();
        bucket.setId(id);
        bucket.setProviderId(providerId);
        bucket.setIsEnabled(enabled);
        return bucket;
    }
}

