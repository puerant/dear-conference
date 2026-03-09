package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 团体订单成员实体
 */
@Data
@TableName("group_members")
public class GroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("sequence_no")
    private Integer sequenceNo;

    @TableField("member_name")
    private String memberName;

    @TableField("member_email")
    private String memberEmail;

    @TableField("status")
    private Integer status;

    @TableField("filled_at")
    private LocalDateTime filledAt;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
