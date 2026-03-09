package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.huiwutong.conference.common.enums.OrderStatus;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@TableName("orders")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("user_id")
    private Long userId;

    @TableField("ticket_id")
    private Long ticketId;

    @TableField("quantity")
    private Integer quantity;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("status")
    private OrderStatus status;

    @TableField("order_type")
    private Integer orderType;

    @TableField("group_name")
    private String groupName;

    @TableField("invite_token")
    private String inviteToken;

    @TableField("attendee_name")
    private String attendeeName;

    @TableField("attendee_email")
    private String attendeeEmail;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField("paid_at")
    private LocalDateTime paidAt;

    @TableField(exist = false)
    private String ticketName;
}
