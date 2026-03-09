package com.huiwutong.conference.service.dto.conference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * 会议信息 DTO
 */
@Data
public class ConferenceInfoDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键（更新时必填）
     */
    private Long id;

    /**
     * 会议标题
     */
    @NotBlank(message = "会议标题不能为空")
    @Size(max = 200, message = "会议标题长度不能超过200字符")
    private String title;

    /**
     * 副标题
     */
    @Size(max = 200, message = "副标题长度不能超过200字符")
    private String subtitle;

    /**
     * 会议描述
     */
    private String description;

    /**
     * 开始日期
     */
    @NotNull(message = "开始日期不能为空")
    private LocalDate startDate;

    /**
     * 结束日期
     */
    @NotNull(message = "结束日期不能为空")
    private LocalDate endDate;

    /**
     * 会议地点
     */
    @Size(max = 200, message = "会议地点长度不能超过200字符")
    private String location;

    /**
     * 详细地址
     */
    @Size(max = 500, message = "详细地址长度不能超过500字符")
    private String address;

    /**
     * 横幅图片URL
     */
    private String bannerUrl;

    /**
     * 联系人
     */
    @Size(max = 100, message = "联系人长度不能超过100字符")
    private String contactName;

    /**
     * 联系电话
     */
    @Size(max = 50, message = "联系电话长度不能超过50字符")
    private String contactPhone;

    /**
     * 联系邮箱
     */
    @Size(max = 100, message = "联系邮箱长度不能超过100字符")
    private String contactEmail;

    /**
     * 是否发布
     */
    private Integer isPublished;
}
