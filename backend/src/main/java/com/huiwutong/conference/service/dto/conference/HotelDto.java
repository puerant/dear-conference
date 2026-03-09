package com.huiwutong.conference.service.dto.conference;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;

/**
 * 酒店 DTO
 */
@Data
public class HotelDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键（更新时必填）
     */
    private Long id;

    /**
     * 酒店名称
     */
    @NotBlank(message = "酒店名称不能为空")
    @Size(max = 200, message = "酒店名称长度不能超过200字符")
    private String name;

    /**
     * 地址
     */
    @Size(max = 500, message = "地址长度不能超过500字符")
    private String address;

    /**
     * 星级（1-5）
     */
    @Min(value = 1, message = "星级最小为1")
    @Max(value = 5, message = "星级最大为5")
    private Integer starLevel;

    /**
     * 联系电话
     */
    @Size(max = 50, message = "联系电话长度不能超过50字符")
    private String contactPhone;

    /**
     * 描述
     */
    private String description;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 预订链接
     */
    private String bookingUrl;

    /**
     * 是否推荐
     */
    private Integer isRecommended;

    /**
     * 排序
     */
    private Integer sortOrder;
}
