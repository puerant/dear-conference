package com.huiwutong.conference.controller.admin;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.HotelService;
import com.huiwutong.conference.service.dto.conference.HotelDto;
import com.huiwutong.conference.service.dto.conference.HotelRoomDto;
import com.huiwutong.conference.service.dto.conference.HotelVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理端 - 酒店控制器
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminHotelController {

    private final HotelService hotelService;

    /**
     * 获取酒店列表
     */
    @GetMapping("/hotels")
    @RequireRole("admin")
    public Result<List<HotelVo>> getHotelList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer isRecommended) {
        List<HotelVo> list = hotelService.getHotelListForAdmin(keyword, isRecommended);
        return Result.success(list);
    }

    /**
     * 获取酒店详情
     */
    @GetMapping("/hotel/{id}")
    @RequireRole("admin")
    public Result<HotelVo> getHotelById(@PathVariable Long id) {
        HotelVo vo = hotelService.getById(id);
        return Result.success(vo);
    }

    /**
     * 创建酒店
     */
    @PostMapping("/hotel")
    @RequireRole("admin")
    public Result<Long> createHotel(@Valid @RequestBody HotelDto dto) {
        Long id = hotelService.create(dto);
        return Result.success(id);
    }

    /**
     * 更新酒店
     */
    @PutMapping("/hotel/{id}")
    @RequireRole("admin")
    public Result<Void> updateHotel(@PathVariable Long id, @Valid @RequestBody HotelDto dto) {
        hotelService.update(id, dto);
        return Result.success();
    }

    /**
     * 删除酒店
     */
    @DeleteMapping("/hotel/{id}")
    @RequireRole("admin")
    public Result<Void> deleteHotel(@PathVariable Long id) {
        hotelService.delete(id);
        return Result.success();
    }

    /**
     * 添加房型
     */
    @PostMapping("/hotel/{id}/rooms")
    @RequireRole("admin")
    public Result<Long> addRoom(@PathVariable Long id, @Valid @RequestBody HotelRoomDto dto) {
        dto.setHotelId(id);
        Long roomId = hotelService.addRoom(id, dto);
        return Result.success(roomId);
    }

    /**
     * 更新房型
     */
    @PutMapping("/hotel/room/{id}")
    @RequireRole("admin")
    public Result<Void> updateRoom(@PathVariable Long id, @Valid @RequestBody HotelRoomDto dto) {
        hotelService.updateRoom(id, dto);
        return Result.success();
    }

    /**
     * 删除房型
     */
    @DeleteMapping("/hotel/room/{id}")
    @RequireRole("admin")
    public Result<Void> deleteRoom(@PathVariable Long id) {
        hotelService.deleteRoom(id);
        return Result.success();
    }
}
