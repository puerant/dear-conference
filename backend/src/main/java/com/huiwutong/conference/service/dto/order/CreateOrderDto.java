package com.huiwutong.conference.service.dto.order;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 创建订单 DTO
 * orderType=1 个人票：attendeeName/attendeeEmail 服务端从登录用户自动填充
 * orderType=2 团体票：contactName/contactEmail 同样从登录用户自动填充，前端只需提供 groupName 和 quantity
 */
@Data
public class CreateOrderDto {

    @NotNull(message = "票务ID不能为空")
    private Long ticketId;

    @NotNull(message = "订单类型不能为空")
    private Integer orderType;

    // 团体票必填，个人票忽略（服务端强制为1）
    @Min(value = 1, message = "购买数量最少1")
    private Integer quantity;

    // 团体票必填
    @Size(max = 100, message = "团体名称不能超过100个字符")
    private String groupName;
}
