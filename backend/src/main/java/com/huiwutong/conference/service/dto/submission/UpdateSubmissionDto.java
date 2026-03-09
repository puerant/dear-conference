package com.huiwutong.conference.service.dto.submission;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改投稿 DTO（仅限 pending 状态）
 */
@Data
public class UpdateSubmissionDto {

    @NotBlank(message = "投稿标题不能为空")
    @Size(max = 200, message = "标题不能超过200个字符")
    private String title;

    @JsonProperty("abstract")
    private String abstract0;

    private String category;

    private String fileUrl;
}
