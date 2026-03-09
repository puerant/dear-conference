package com.huiwutong.conference.service.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 票务 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 票务ID
     */
    private Long id;

    /**
     * 票务名称
     */
    private String name;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 描述
     */
    private String description;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 已售数量
     */
    private Integer soldCount;

    /**
     * 可用库存（stock - soldCount）
     */
    private Integer available;

    /**
     * 状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 票种类型：1=个人票 2=团体票
     */
    private Integer ticketType;

    /**
     * 团体票最小购买人数
     */
    private Integer minPersons;

    /**
     * 团体票最大购买人数
     */
    private Integer maxPersons;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
