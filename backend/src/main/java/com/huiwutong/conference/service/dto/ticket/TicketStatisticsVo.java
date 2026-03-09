package com.huiwutong.conference.service.dto.ticket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 票务统计 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatisticsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总票务数
     */
    private Integer totalCount;

    /**
     * 上架票务数
     */
    private Integer activeCount;

    /**
     * 总库存
     */
    private Integer totalStock;

    /**
     * 已售数量
     */
    private Integer totalSold;

    /**
     * 总销售额
     */
    private BigDecimal totalSales;
}
