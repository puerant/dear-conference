package com.huiwutong.conference.service.dto.ticket;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 创建票务 DTO
 */
@Data
public class CreateTicketDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 票务名称
     */
    @NotBlank(message = "票务名称不能为空")
    @Size(min = 1, max = 100, message = "票务名称长度必须在1-100字符之间")
    private String name;

    /**
     * 价格
     */
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;

    /**
     * 描述
     */
    @Size(max = 1000, message = "描述长度不能超过1000字符")
    private String description;

    /**
     * 库存数量
     */
    @Min(value = 0, message = "库存数量不能为负数")
    private Integer stock;

    /**
     * 排序值（越小越靠前）
     */
    private Integer sortOrder;

    /**
     * 票种类型：1=个人票 2=团体票，默认 1
     */
    private Integer ticketType;

    /**
     * 团体票最小购买人数（个人票为 null）
     */
    private Integer minPersons;

    /**
     * 团体票最大购买人数（null 表示不限）
     */
    private Integer maxPersons;
}
