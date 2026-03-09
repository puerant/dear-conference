package com.huiwutong.conference.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.controller.SystemFileController;
import com.huiwutong.conference.controller.file.FileController;
import com.huiwutong.conference.controller.system.SystemFileCompatController;
import com.huiwutong.conference.entity.SystemFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 文件模块接口冒烟测试（不依赖数据库）
 */
class SystemFileControllerSmokeTest {

    private MockMvc mockMvc;
    private SystemFileService systemFileService;

    @BeforeEach
    void setUp() {
        systemFileService = mock(SystemFileService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(
            new FileController(systemFileService),
            new SystemFileController(systemFileService),
            new SystemFileCompatController(systemFileService)
        ).build();
    }

    @Test
    void newApi_list_shouldReturn200() throws Exception {
        Page<SystemFile> page = new Page<>(1, 20);
        page.setTotal(0);
        when(systemFileService.getFileList(any(Page.class), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/admin/file/list"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void oldCompatApi_files_shouldReturn200() throws Exception {
        Page<SystemFile> page = new Page<>(1, 20);
        page.setTotal(0);
        when(systemFileService.getFileList(any(Page.class), any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/admin/system/files"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void oldBatchDelete_acceptObjectBody_shouldReturn200() throws Exception {
        mockMvc.perform(post("/api/admin/system/file/batch-delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ids\":[1,2,3]}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    void newPreviewUrl_shouldReturn200() throws Exception {
        when(systemFileService.getPreviewUrl(1L)).thenReturn("https://example.com/a.jpg?expires=1");
        mockMvc.perform(get("/api/admin/file/1/preview-url"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value(200))
            .andExpect(jsonPath("$.data").value("https://example.com/a.jpg?expires=1"));
    }
}

