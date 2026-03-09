package com.huiwutong.conference.service.dto.ticket;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 更新票务 DTO
 */
@Data
public class UpdateTicketDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 票务名称（可选）
     */
    @Size(min = 1, max = 100, message = "票务名称长度必须在1-100字符之间")
    private String name;

    /**
     * 价格（可选）
     */
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    /**
     * 描述（可选）
     */
    @Size(max = 1000, message = "描述长度不能超过1000字符")
    private String description;

    /**
     * 排序值（可选）
     */
    private Integer sortOrder;

    /**
     * 团体票最小购买人数（可选）
     */
    private Integer minPersons;

    /**
     * 团体票最大购买人数（可选，null 表示不限）
     */
    private Integer maxPersons;
}
