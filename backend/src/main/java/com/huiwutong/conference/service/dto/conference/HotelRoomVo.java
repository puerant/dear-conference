package com.huiwutong.conference.service.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 酒店房型 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelRoomVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long hotelId;
    private String roomType;
    private BigDecimal price;
    private Integer stock;
    private String description;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
