package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.huiwutong.conference.common.enums.CredentialStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 入会凭证实体
 */
@Data
@TableName("credentials")
public class Credential implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("order_id")
    private Long orderId;

    @TableField("group_member_id")
    private Long groupMemberId;

    @TableField("credential_no")
    private String credentialNo;

    @TableField("qr_code")
    private String qrCode;

    @TableField("seat_info")
    private String seatInfo;

    @TableField("status")
    private CredentialStatus status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField("used_at")
    private LocalDateTime usedAt;

    @TableField("expire_at")
    private LocalDateTime expireAt;

    // 非数据库字段，用于展示关联信息
    @TableField(exist = false)
    private String orderNo;

    @TableField(exist = false)
    private String attendeeName;

    @TableField(exist = false)
    private String attendeeEmail;

    @TableField(exist = false)
    private String ticketName;
}
