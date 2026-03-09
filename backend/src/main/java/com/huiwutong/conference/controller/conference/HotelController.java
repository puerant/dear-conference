package com.huiwutong.conference.controller.conference;

import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.HotelService;
import com.huiwutong.conference.service.dto.conference.HotelListVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端 - 酒店控制器
 */
@RestController
@RequestMapping("/api/conference")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    /**
     * 获取酒店列表
     */
    @GetMapping("/hotels")
    public Result<HotelListVo> getHotelList(
            @RequestParam(required = false) Boolean isRecommended) {
        HotelListVo vo = hotelService.getHotelListForPortal(isRecommended);
        return Result.success(vo);
    }
}
