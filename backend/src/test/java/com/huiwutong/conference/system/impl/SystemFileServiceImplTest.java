package com.huiwutong.conference.system.impl;

import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.StorageBucket;
import com.huiwutong.conference.entity.StorageProvider;
import com.huiwutong.conference.entity.SystemFile;
import com.huiwutong.conference.mapper.SystemFileMapper;
import com.huiwutong.conference.service.FileRoutingService;
import com.huiwutong.conference.storage.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemFileServiceImplTest {

    @Mock
    private SystemFileMapper fileMapper;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private FileRoutingService fileRoutingService;

    @InjectMocks
    private SystemFileServiceImpl systemFileService;

    @Test
    void uploadFile_whenSameMd5Exists_returnsExisting() {
        MockMultipartFile file = new MockMultipartFile("file", "a.jpg", "image/jpeg", "abc".getBytes());
        SystemFile existing = new SystemFile();
        existing.setId(10L);
        when(fileMapper.selectByMd5(any())).thenReturn(existing);

        SystemFile result = systemFileService.uploadFile(file, "conference_banner", null, null, null, 1);
        assertEquals(10L, result.getId());
        verify(fileMapper, never()).insert(any());
    }

    @Test
    void uploadFile_whenNoProviderSpecified_routesAndSaveMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "a.jpg", "image/jpeg", "abc".getBytes());
        when(fileMapper.selectByMd5(any())).thenReturn(null);
        when(fileStorageService.store(any())).thenReturn("/files/20260305/x.jpg");
        when(fileStorageService.getStorageType()).thenReturn(1);

        StorageProvider provider = new StorageProvider();
        provider.setId(2L);
        StorageBucket bucket = new StorageBucket();
        bucket.setId(3L);
        var target = new FileRoutingService.RouteTarget(provider, bucket);
        when(fileRoutingService.resolve(eq("conference_banner"), eq("image"), isNull(), isNull())).thenReturn(target);

        SystemFile result = systemFileService.uploadFile(file, "conference_banner", 1L, null, null, 2);
        assertNotNull(result);

        ArgumentCaptor<SystemFile> captor = ArgumentCaptor.forClass(SystemFile.class);
        verify(fileMapper).insert(captor.capture());
        SystemFile saved = captor.getValue();
        assertEquals(2L, saved.getProviderId());
        assertEquals(3L, saved.getBucketId());
        assertEquals(2, saved.getVisibility());
    }

    @Test
    void getPreviewUrl_private_returnsSignedLikeUrl() {
        SystemFile file = new SystemFile();
        file.setId(1L);
        file.setIsDeleted(0);
        file.setVisibility(1);
        file.setFileUrl("https://example.com/a.jpg");
        when(fileMapper.selectById(1L)).thenReturn(file);

        String url = systemFileService.getPreviewUrl(1L);
        assertTrue(url.contains("expires="));
    }

    @Test
    void getPreviewUrl_public_returnsRawUrl() {
        SystemFile file = new SystemFile();
        file.setId(1L);
        file.setIsDeleted(0);
        file.setVisibility(2);
        file.setFileUrl("https://example.com/a.jpg");
        when(fileMapper.selectById(1L)).thenReturn(file);

        assertEquals("https://example.com/a.jpg", systemFileService.getPreviewUrl(1L));
    }

    @Test
    void getPreviewUrl_notFound_throwFileNotFound() {
        when(fileMapper.selectById(99L)).thenReturn(null);
        BusinessException ex = assertThrows(BusinessException.class, () -> systemFileService.getPreviewUrl(99L));
        assertEquals(ErrorCode.FILE_NOT_FOUND.getCode(), ex.getCode());
    }
}
