package com.huiwutong.conference.service.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 酒店 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String address;
    private Integer starLevel;
    private String contactPhone;
    private String description;
    private String imageUrl;
    private String bookingUrl;
    private Integer isRecommended;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * 房型列表
     */
    private List<HotelRoomVo> rooms;
}
