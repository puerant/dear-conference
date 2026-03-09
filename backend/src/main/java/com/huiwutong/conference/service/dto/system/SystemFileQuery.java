package com.huiwutong.conference.service.dto.system;

import lombok.Data;

import java.time.LocalDate;

/**
 * 系统文件查询参数
 */
@Data
public class SystemFileQuery {

    private Integer page = 1;

    private Integer pageSize = 20;

    private String fileType;

    private String fileCategory;

    private Long categoryId;

    private LocalDate startDate;

    private LocalDate endDate;

    private String keyword;
}
