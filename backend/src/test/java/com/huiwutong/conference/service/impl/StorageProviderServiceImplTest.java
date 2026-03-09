package com.huiwutong.conference.service.impl;

import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.StorageProvider;
import com.huiwutong.conference.mapper.StorageBucketMapper;
import com.huiwutong.conference.mapper.StorageProviderMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StorageProviderServiceImplTest {

    @Mock
    private StorageProviderMapper storageProviderMapper;
    @Mock
    private StorageBucketMapper storageBucketMapper;

    @InjectMocks
    private StorageProviderServiceImpl storageProviderService;

    @Test
    void testConnection_localProvider_success() {
        StorageProvider provider = new StorageProvider();
        provider.setId(1L);
        provider.setProviderType(1);
        provider.setIsEnabled(1);
        when(storageProviderMapper.selectById(1L)).thenReturn(provider);

        assertDoesNotThrow(() -> storageProviderService.testConnection(1L));
    }

    @Test
    void testConnection_cloudProviderMissingConfig_throwConnectFailed() {
        StorageProvider provider = new StorageProvider();
        provider.setId(2L);
        provider.setProviderType(2);
        provider.setIsEnabled(1);
        provider.setEndpoint(null);
        provider.setAccessKey("ak");
        provider.setSecretKey(null);
        when(storageProviderMapper.selectById(2L)).thenReturn(provider);

        BusinessException ex = assertThrows(BusinessException.class, () -> storageProviderService.testConnection(2L));
        assertEquals(ErrorCode.STORAGE_TEST_CONNECT_FAILED.getCode(), ex.getCode());
    }

    @Test
    void setDefault_whenProviderDisabled_throwProviderDisabled() {
        StorageProvider provider = new StorageProvider();
        provider.setId(3L);
        provider.setIsEnabled(0);
        when(storageProviderMapper.selectById(3L)).thenReturn(provider);

        BusinessException ex = assertThrows(BusinessException.class, () -> storageProviderService.setDefault(3L));
        assertEquals(ErrorCode.STORAGE_PROVIDER_DISABLED.getCode(), ex.getCode());
    }
}
