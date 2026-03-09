package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 酒店实体
 */
@Data
@TableName("hotel")
public class Hotel implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("address")
    private String address;

    @TableField("star_level")
    private Integer starLevel;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("description")
    private String description;

    @TableField("image_url")
    private String imageUrl;

    @TableField("booking_url")
    private String bookingUrl;

    @TableField("is_recommended")
    private Integer isRecommended;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
