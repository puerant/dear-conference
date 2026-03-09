package com.huiwutong.conference.controller.admin;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.Ticket;
import com.huiwutong.conference.service.TicketService;
import com.huiwutong.conference.service.dto.ticket.CreateTicketDto;
import com.huiwutong.conference.service.dto.ticket.TicketStatisticsVo;
import com.huiwutong.conference.service.dto.ticket.TicketVo;
import com.huiwutong.conference.service.dto.ticket.UpdateTicketDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端 - 票务管理控制器
 */
@RestController
@RequestMapping("/api/admin/ticket")
@RequiredArgsConstructor
public class AdminTicketController {

    private final TicketService ticketService;

    /**
     * 查询票务列表（分页+状态筛选）
     */
    @GetMapping
    @RequireRole("admin")
    public Result<PageResult<TicketVo>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(ticketService.list(page, pageSize, status));
    }

    /**
     * 创建票务
     */
    @PostMapping
    @RequireRole("admin")
    public Result<Ticket> create(@Valid @RequestBody CreateTicketDto dto) {
        Ticket ticket = new Ticket();
        BeanUtils.copyProperties(dto, ticket);
        return Result.success(ticketService.create(ticket));
    }

    /**
     * 修改票务信息
     */
    @PutMapping("/{id}")
    @RequireRole("admin")
    public Result<Ticket> update(@PathVariable Long id, @RequestBody UpdateTicketDto dto) {
        Ticket ticket = ticketService.getById(id);
        if (dto.getName() != null) {
            ticket.setName(dto.getName());
        }
        if (dto.getPrice() != null) {
            ticket.setPrice(dto.getPrice());
        }
        if (dto.getDescription() != null) {
            ticket.setDescription(dto.getDescription());
        }
        if (dto.getMinPersons() != null) {
            ticket.setMinPersons(dto.getMinPersons());
        }
        if (dto.getMaxPersons() != null) {
            ticket.setMaxPersons(dto.getMaxPersons());
        }
        if (dto.getSortOrder() != null) {
            // sortOrder 暂不实现，数据库无此字段
        }
        ticketService.update(ticket);
        return Result.success(ticket);
    }

    /**
     * 修改票务状态（上架/下架）
     */
    @PutMapping("/{id}/status")
    @RequireRole("admin")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        ticketService.updateStatus(id, body.get("status"));
        return Result.success();
    }

    /**
     * 调整库存
     */
    @PutMapping("/{id}/stock")
    @RequireRole("admin")
    public Result<Void> updateStock(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        ticketService.updateStock(id, body.get("stock"));
        return Result.success();
    }

    /**
     * 删除票务
     */
    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<Void> delete(@PathVariable Long id) {
        ticketService.delete(id);
        return Result.success();
    }

    /**
     * 票务统计
     */
    @GetMapping("/statistics")
    @RequireRole("admin")
    public Result<TicketStatisticsVo> getStatistics() {
        return Result.success(ticketService.getStatistics());
    }
}
