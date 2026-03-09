package com.huiwutong.conference.service.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 提交审稿意见 DTO
 */
@Data
public class CreateReviewDto {

    @NotNull(message = "投稿ID不能为空")
    private Long submissionId;

    private String comment;

    @NotNull(message = "审稿结果不能为空")
    private Integer result;

    @Min(value = 0, message = "评分不能低于0")
    @Max(value = 100, message = "评分不能超过100")
    private Integer score;
}
