package com.huiwutong.conference.controller.ticket;

import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.Ticket;
import com.huiwutong.conference.service.TicketService;
import com.huiwutong.conference.service.dto.ticket.TicketVo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 票务控制器（公开接口，所有用户可查看）
 */
@RestController
@RequestMapping("/api/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /**
     * 查询票务列表（分页）
     *
     * @param page     页码，默认1
     * @param pageSize 每页数量，默认10
     * @param status   状态筛选（可选）：0-下架，1-上架
     * @return 分页结果
     */
    @GetMapping
    public Result<PageResult<TicketVo>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(ticketService.list(page, pageSize, status));
    }

    /**
     * 查询可购买票务（库存>0且状态=上架）
     *
     * @param page     页码，默认1
     * @param pageSize 每页数量，默认10
     * @return 分页结果
     */
    @GetMapping("/available")
    public Result<PageResult<TicketVo>> listAvailable(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(ticketService.listAvailable(page, pageSize));
    }

    /**
     * 查询票务详情
     *
     * @param id 票务ID
     * @return 票务详情（含可用库存 available）
     */
    @GetMapping("/{id}")
    public Result<TicketVo> getById(@PathVariable Long id) {
        Ticket ticket = ticketService.getById(id);
        TicketVo vo = TicketVo.builder()
                .id(ticket.getId())
                .name(ticket.getName())
                .price(ticket.getPrice())
                .description(ticket.getDescription())
                .stock(ticket.getStock())
                .soldCount(ticket.getSoldCount())
                .available(ticket.getStock() - ticket.getSoldCount())
                .status(ticket.getStatus())
                .createdAt(ticket.getCreatedAt())
                .build();
        return Result.success(vo);
    }
}
