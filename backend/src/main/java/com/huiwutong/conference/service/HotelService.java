package com.huiwutong.conference.service;

import com.huiwutong.conference.service.dto.conference.*;

import java.util.List;

/**
 * 酒店服务接口
 */
public interface HotelService {

    /**
     * 获取酒店列表（用户端，分推荐/普通）
     */
    HotelListVo getHotelListForPortal(Boolean isRecommendedOnly);

    /**
     * 获取酒店列表（管理端）
     */
    List<HotelVo> getHotelListForAdmin(String keyword, Integer isRecommended);

    /**
     * 获取酒店详情
     */
    HotelVo getById(Long id);

    /**
     * 创建酒店
     */
    Long create(HotelDto dto);

    /**
     * 更新酒店
     */
    void update(Long id, HotelDto dto);

    /**
     * 删除酒店
     */
    void delete(Long id);

    /**
     * 添加房型
     */
    Long addRoom(Long hotelId, HotelRoomDto dto);

    /**
     * 更新房型
     */
    void updateRoom(Long roomId, HotelRoomDto dto);

    /**
     * 删除房型
     */
    void deleteRoom(Long roomId);
}
