package com.huiwutong.conference.service.dto.order;

import lombok.Data;

/**
 * 订单查询 DTO
 */
@Data
public class OrderQueryDto {

    private Integer page = 1;
    private Integer pageSize = 10;
    private String status;
}
