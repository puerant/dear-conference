package com.huiwutong.conference.service.dto.conference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * 专家 DTO
 */
@Data
public class ExpertDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键（更新时必填）
     */
    private Long id;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(max = 100, message = "姓名长度不能超过100字符")
    private String name;

    /**
     * 职称/头衔
     */
    @Size(max = 100, message = "职称长度不能超过100字符")
    private String title;

    /**
     * 所属机构
     */
    @Size(max = 200, message = "机构名称长度不能超过200字符")
    private String organization;

    /**
     * 简介
     */
    private String bio;

    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 是否主旨演讲嘉宾
     */
    private Integer isKeynote;

    /**
     * 排序
     */
    private Integer sortOrder;
}
