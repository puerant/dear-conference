package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 票务实体
 */
@Data
@TableName("tickets")
public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("price")
    private BigDecimal price;

    @TableField("description")
    private String description;

    @TableField("stock")
    private Integer stock;

    @TableField("sold_count")
    private Integer soldCount;

    @TableField("status")
    private Integer status;

    @TableField("ticket_type")
    private Integer ticketType;

    @TableField("min_persons")
    private Integer minPersons;

    @TableField("max_persons")
    private Integer maxPersons;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
