package com.huiwutong.conference.service.dto.submission;

import lombok.Data;

/**
 * 投稿查询 DTO
 */
@Data
public class SubmissionQueryDto {

    private Integer page = 1;
    private Integer pageSize = 10;
    private String status;
    private String category;
    private String keyword;
}
