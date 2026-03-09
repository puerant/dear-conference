package com.huiwutong.conference.service.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 酒店列表 VO（分推荐和普通）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 推荐酒店列表
     */
    private List<HotelVo> recommended;

    /**
     * 普通酒店列表
     */
    private List<HotelVo> hotels;
}
