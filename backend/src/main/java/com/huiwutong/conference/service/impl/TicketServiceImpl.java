package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.entity.Ticket;
import com.huiwutong.conference.mapper.TicketMapper;
import com.huiwutong.conference.service.TicketService;
import com.huiwutong.conference.service.dto.ticket.TicketStatisticsVo;
import com.huiwutong.conference.service.dto.ticket.TicketVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 票务服务实现
 */
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketMapper ticketMapper;

    @Override
    public List<Ticket> listActive() {
        return ticketMapper.selectList(new LambdaQueryWrapper<Ticket>()
                .eq(Ticket::getStatus, 1)
                .orderByAsc(Ticket::getCreatedAt));
    }

    @Override
    public PageResult<TicketVo> list(Integer page, Integer pageSize, Integer status) {
        Page<Ticket> ticketPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Ticket::getStatus, status);
        }
        wrapper.orderByDesc(Ticket::getCreatedAt);

        Page<Ticket> result = ticketMapper.selectPage(ticketPage, wrapper);
        List<TicketVo> voList = result.getRecords().stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), (long) page, (long) pageSize);
    }

    @Override
    public PageResult<TicketVo> listAvailable(Integer page, Integer pageSize) {
        Page<Ticket> ticketPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ticket::getStatus, 1)
                .apply("stock > sold_count")
                .orderByDesc(Ticket::getCreatedAt);

        Page<Ticket> result = ticketMapper.selectPage(ticketPage, wrapper);
        List<TicketVo> voList = result.getRecords().stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), (long) page, (long) pageSize);
    }

    @Override
    public Ticket getById(Long id) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket == null) {
            throw new BusinessException(ErrorCode.TICKET_NOT_FOUND);
        }
        return ticket;
    }

    @Override
    public Ticket create(Ticket ticket) {
        ticket.setStatus(1);
        ticket.setSoldCount(0);
        ticketMapper.insert(ticket);
        return ticket;
    }

    @Override
    public void update(Ticket ticket) {
        ticketMapper.updateById(ticket);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Ticket ticket = getById(id);
        ticket.setStatus(status);
        ticketMapper.updateById(ticket);
    }

    @Override
    public void updateStock(Long id, Integer stock) {
        Ticket ticket = getById(id);
        if (stock < ticket.getSoldCount()) {
            throw new BusinessException(ErrorCode.STOCK_ADJUST_INVALID);
        }
        ticket.setStock(stock);
        ticketMapper.updateById(ticket);
    }

    @Override
    public void delete(Long id) {
        Ticket ticket = getById(id);
        if (ticket.getSoldCount() > 0) {
            throw new BusinessException(ErrorCode.TICKET_SOLD_CANT_DELETE);
        }
        ticketMapper.deleteById(id);
    }

    @Override
    public TicketStatisticsVo getStatistics() {
        List<Ticket> allTickets = ticketMapper.selectList(null);

        int totalCount = allTickets.size();
        int activeCount = (int) allTickets.stream().filter(t -> t.getStatus() == 1).count();
        int totalStock = allTickets.stream().mapToInt(Ticket::getStock).sum();
        int totalSold = allTickets.stream().mapToInt(Ticket::getSoldCount).sum();
        BigDecimal totalSales = allTickets.stream()
                .map(t -> t.getPrice().multiply(BigDecimal.valueOf(t.getSoldCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return TicketStatisticsVo.builder()
                .totalCount(totalCount)
                .activeCount(activeCount)
                .totalStock(totalStock)
                .totalSold(totalSold)
                .totalSales(totalSales)
                .build();
    }

    /**
     * 转换为 TicketVo
     */
    private TicketVo convertToVo(Ticket ticket) {
        return TicketVo.builder()
                .id(ticket.getId())
                .name(ticket.getName())
                .price(ticket.getPrice())
                .description(ticket.getDescription())
                .stock(ticket.getStock())
                .soldCount(ticket.getSoldCount())
                .available(ticket.getStock() - ticket.getSoldCount())
                .status(ticket.getStatus())
                .ticketType(ticket.getTicketType())
                .minPersons(ticket.getMinPersons())
                .maxPersons(ticket.getMaxPersons())
                .createdAt(ticket.getCreatedAt())
                .build();
    }
}
