package com.huiwutong.conference.service.dto.conference;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 酒店房型 DTO
 */
@Data
public class HotelRoomDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键（更新时必填）
     */
    private Long id;

    /**
     * 酒店ID
     */
    @NotNull(message = "酒店ID不能为空")
    private Long hotelId;

    /**
     * 房型
     */
    @NotBlank(message = "房型不能为空")
    @Size(max = 100, message = "房型长度不能超过100字符")
    private String roomType;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.00", message = "价格不能为负数")
    private BigDecimal price;

    /**
     * 库存
     */
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sortOrder;
}
