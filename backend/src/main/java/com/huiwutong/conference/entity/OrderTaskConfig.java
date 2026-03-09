package com.huiwutong.conference.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单定时任务配置实体
 */
@Data
@TableName("order_task_config")
public class OrderTaskConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("timeout_minutes")
    private Integer timeoutMinutes;

    @TableField("check_interval_minutes")
    private Integer checkIntervalMinutes;

    @TableField("cancel_task_enabled")
    private Integer cancelTaskEnabled;

    @TableField("refund_task_enabled")
    private Integer refundTaskEnabled;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
