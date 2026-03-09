package com.huiwutong.conference.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.Hotel;
import com.huiwutong.conference.entity.HotelRoom;
import com.huiwutong.conference.mapper.HotelMapper;
import com.huiwutong.conference.mapper.HotelRoomMapper;
import com.huiwutong.conference.service.HotelService;
import com.huiwutong.conference.service.dto.conference.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 酒店服务实现
 */
@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelMapper hotelMapper;
    private final HotelRoomMapper hotelRoomMapper;

    @Override
    public HotelListVo getHotelListForPortal(Boolean isRecommendedOnly) {
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();

        if (Boolean.TRUE.equals(isRecommendedOnly)) {
            wrapper.eq(Hotel::getIsRecommended, 1);
        }

        wrapper.orderByDesc(Hotel::getIsRecommended)
                .orderByAsc(Hotel::getSortOrder);

        List<Hotel> hotels = hotelMapper.selectList(wrapper);

        List<HotelVo> recommended = new ArrayList<>();
        List<HotelVo> normal = new ArrayList<>();

        for (Hotel hotel : hotels) {
            HotelVo vo = toVoWithRooms(hotel);
            if (hotel.getIsRecommended() != null && hotel.getIsRecommended() == 1) {
                recommended.add(vo);
            } else {
                normal.add(vo);
            }
        }

        return HotelListVo.builder()
                .recommended(recommended)
                .hotels(normal)
                .build();
    }

    @Override
    public List<HotelVo> getHotelListForAdmin(String keyword, Integer isRecommended) {
        LambdaQueryWrapper<Hotel> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Hotel::getName, keyword)
                    .or().like(Hotel::getAddress, keyword);
        }

        if (isRecommended != null) {
            wrapper.eq(Hotel::getIsRecommended, isRecommended);
        }

        wrapper.orderByDesc(Hotel::getIsRecommended)
                .orderByAsc(Hotel::getSortOrder);

        List<Hotel> hotels = hotelMapper.selectList(wrapper);
        return hotels.stream().map(this::toVoWithRooms).toList();
    }

    @Override
    public HotelVo getById(Long id) {
        Hotel hotel = hotelMapper.selectById(id);
        if (hotel == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "酒店不存在");
        }
        return toVoWithRooms(hotel);
    }

    @Override
    @Transactional
    public Long create(HotelDto dto) {
        // 验证星级
        if (dto.getStarLevel() != null && (dto.getStarLevel() < 1 || dto.getStarLevel() > 5)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "星级必须在1-5之间");
        }

        Hotel hotel = new Hotel();
        BeanUtil.copyProperties(dto, hotel);
        if (hotel.getIsRecommended() == null) {
            hotel.setIsRecommended(0);
        }
        if (hotel.getSortOrder() == null) {
            hotel.setSortOrder(0);
        }
        if (hotel.getStarLevel() == null) {
            hotel.setStarLevel(3);
        }
        hotelMapper.insert(hotel);
        return hotel.getId();
    }

    @Override
    @Transactional
    public void update(Long id, HotelDto dto) {
        Hotel hotel = hotelMapper.selectById(id);
        if (hotel == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "酒店不存在");
        }

        // 验证星级
        if (dto.getStarLevel() != null && (dto.getStarLevel() < 1 || dto.getStarLevel() > 5)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "星级必须在1-5之间");
        }

        BeanUtil.copyProperties(dto, hotel, "id");
        hotelMapper.updateById(hotel);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Hotel hotel = hotelMapper.selectById(id);
        if (hotel == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "酒店不存在");
        }
        // 先删除关联的房型
        LambdaQueryWrapper<HotelRoom> roomWrapper = new LambdaQueryWrapper<>();
        roomWrapper.eq(HotelRoom::getHotelId, id);
        hotelRoomMapper.delete(roomWrapper);
        // 再删除酒店
        hotelMapper.deleteById(id);
    }

    @Override
    @Transactional
    public Long addRoom(Long hotelId, HotelRoomDto dto) {
        Hotel hotel = hotelMapper.selectById(hotelId);
        if (hotel == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "酒店不存在");
        }

        HotelRoom room = new HotelRoom();
        BeanUtil.copyProperties(dto, room);
        room.setHotelId(hotelId);
        if (room.getSortOrder() == null) {
            room.setSortOrder(0);
        }
        if (room.getStock() == null) {
            room.setStock(0);
        }
        hotelRoomMapper.insert(room);
        return room.getId();
    }

    @Override
    @Transactional
    public void updateRoom(Long roomId, HotelRoomDto dto) {
        HotelRoom room = hotelRoomMapper.selectById(roomId);
        if (room == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "房型不存在");
        }
        BeanUtil.copyProperties(dto, room, "id", "hotelId");
        hotelRoomMapper.updateById(room);
    }

    @Override
    @Transactional
    public void deleteRoom(Long roomId) {
        HotelRoom room = hotelRoomMapper.selectById(roomId);
        if (room == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "房型不存在");
        }
        hotelRoomMapper.deleteById(roomId);
    }

    private HotelVo toVoWithRooms(Hotel hotel) {
        HotelVo vo = HotelVo.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .address(hotel.getAddress())
                .starLevel(hotel.getStarLevel())
                .contactPhone(hotel.getContactPhone())
                .description(hotel.getDescription())
                .imageUrl(hotel.getImageUrl())
                .bookingUrl(hotel.getBookingUrl())
                .isRecommended(hotel.getIsRecommended())
                .sortOrder(hotel.getSortOrder())
                .createdAt(hotel.getCreatedAt())
                .updatedAt(hotel.getUpdatedAt())
                .build();

        // 查询房型
        LambdaQueryWrapper<HotelRoom> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HotelRoom::getHotelId, hotel.getId());
        wrapper.orderByAsc(HotelRoom::getSortOrder);

        List<HotelRoom> rooms = hotelRoomMapper.selectList(wrapper);
        vo.setRooms(rooms.stream().map(this::toRoomVo).collect(java.util.stream.Collectors.toList()));

        return vo;
    }

    private HotelRoomVo toRoomVo(HotelRoom room) {
        return HotelRoomVo.builder()
                .id(room.getId())
                .hotelId(room.getHotelId())
                .roomType(room.getRoomType())
                .price(room.getPrice())
                .stock(room.getStock())
                .description(room.getDescription())
                .sortOrder(room.getSortOrder())
                .createdAt(room.getCreatedAt())
                .updatedAt(room.getUpdatedAt())
                .build();
    }
}
