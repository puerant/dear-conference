package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.enums.OrderStatus;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.entity.GroupMember;
import com.huiwutong.conference.entity.Order;
import com.huiwutong.conference.entity.Ticket;
import com.huiwutong.conference.entity.User;
import com.huiwutong.conference.mapper.GroupMemberMapper;
import com.huiwutong.conference.mapper.OrderMapper;
import com.huiwutong.conference.mapper.TicketMapper;
import com.huiwutong.conference.mapper.UserMapper;
import com.huiwutong.conference.service.CredentialService;
import com.huiwutong.conference.service.OrderService;
import com.huiwutong.conference.service.dto.order.CreateOrderDto;
import com.huiwutong.conference.service.dto.order.OrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final TicketMapper ticketMapper;
    private final UserMapper userMapper;
    private final GroupMemberMapper groupMemberMapper;
    @Lazy
    private final CredentialService credentialService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order create(Long userId, CreateOrderDto dto) {
        Ticket ticket = ticketMapper.selectById(dto.getTicketId());
        if (ticket == null) throw new BusinessException(ErrorCode.TICKET_NOT_FOUND);
        if (ticket.getStatus() == 0) throw new BusinessException(ErrorCode.TICKET_DISABLED);

        int ticketType = ticket.getTicketType() != null ? ticket.getTicketType() : 1;
        int orderType = dto.getOrderType() != null ? dto.getOrderType() : 1;

        if (ticketType != orderType) throw new BusinessException(ErrorCode.TICKET_TYPE_MISMATCH);

        if (ticketType == 1) {
            // ── 个人票：固定1张，姓名/邮箱从用户信息自动填充 ─────────────────
            // 显式拒绝 quantity > 1，避免前端误传被静默忽略
            if (dto.getQuantity() != null && dto.getQuantity() > 1) {
                throw new BusinessException(ErrorCode.INDIVIDUAL_TICKET_SINGLE_ONLY);
            }
            User user = userMapper.selectById(userId);
            int updated = ticketMapper.decreaseStock(ticket.getId(), 1);
            if (updated == 0) throw new BusinessException(ErrorCode.TICKET_NOT_AVAILABLE);

            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setTicketId(dto.getTicketId());
            order.setQuantity(1);
            order.setTotalAmount(ticket.getPrice());
            order.setStatus(OrderStatus.UNPAID);
            order.setOrderType(1);
            order.setAttendeeName(user != null ? user.getName() : "");
            order.setAttendeeEmail(user != null ? user.getEmail() : "");
            orderMapper.insert(order);
            return order;
        } else {
            // ── 团体票：校验人数范围，批量创建成员槽 ────────────────────────
            int quantity = dto.getQuantity() != null ? dto.getQuantity() : 0;
            if (ticket.getMinPersons() != null && quantity < ticket.getMinPersons()) {
                throw new BusinessException(ErrorCode.GROUP_QUANTITY_INVALID);
            }
            if (ticket.getMaxPersons() != null && quantity > ticket.getMaxPersons()) {
                throw new BusinessException(ErrorCode.GROUP_QUANTITY_INVALID);
            }
            if (quantity < 1) throw new BusinessException(ErrorCode.GROUP_QUANTITY_INVALID);

            if (!StringUtils.hasText(dto.getGroupName())) {
                throw new BusinessException(ErrorCode.PARAM_ERROR);
            }

            // 联系人信息从当前登录用户自动填充，无需前端传入
            User user = userMapper.selectById(userId);

            int updated = ticketMapper.decreaseStock(ticket.getId(), quantity);
            if (updated == 0) throw new BusinessException(ErrorCode.TICKET_NOT_AVAILABLE);

            Order order = new Order();
            order.setOrderNo(generateOrderNo());
            order.setUserId(userId);
            order.setTicketId(dto.getTicketId());
            order.setQuantity(quantity);
            order.setTotalAmount(ticket.getPrice().multiply(BigDecimal.valueOf(quantity)));
            order.setStatus(OrderStatus.UNPAID);
            order.setOrderType(2);
            order.setGroupName(dto.getGroupName());
            order.setAttendeeName(user != null ? user.getName() : "");
            order.setAttendeeEmail(user != null ? user.getEmail() : "");
            orderMapper.insert(order);

            // 批量创建空白成员槽
            List<GroupMember> slots = new ArrayList<>();
            for (int i = 1; i <= quantity; i++) {
                GroupMember slot = new GroupMember();
                slot.setOrderId(order.getId());
                slot.setSequenceNo(i);
                slot.setStatus(1);
                slots.add(slot);
            }
            for (GroupMember slot : slots) {
                groupMemberMapper.insert(slot);
            }
            return order;
        }
    }

    @Override
    public Order getById(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        return order;
    }

    @Override
    public Order getByOrderNo(String orderNo) {
        return orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getOrderNo, orderNo));
    }

    @Override
    public PageResult<Order> listByUser(Long userId, OrderQueryDto query) {
        Page<Order> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreatedAt);
        applyStatusFilter(wrapper, query.getStatus());
        orderMapper.selectPage(page, wrapper);
        fillTicketNames(page.getRecords());
        return PageResult.of(page);
    }

    @Override
    public PageResult<Order> listAll(OrderQueryDto query) {
        Page<Order> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .orderByDesc(Order::getCreatedAt);
        applyStatusFilter(wrapper, query.getStatus());
        orderMapper.selectPage(page, wrapper);
        fillTicketNames(page.getRecords());
        return PageResult.of(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmPaid(Long orderId) {
        Order order = getById(orderId);
        if (order.getStatus() != OrderStatus.UNPAID) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        orderMapper.updateById(order);
        // 个人票立即生成凭证；团体票凭证在每位成员填写信息时单独生成
        if (!Integer.valueOf(2).equals(order.getOrderType())) {
            credentialService.generateCredential(orderId);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id, Long userId) {
        Order order = getById(id);
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_ACCESS_DENIED);
        }
        if (order.getStatus() != OrderStatus.UNPAID) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(OrderStatus.CANCELLED);
        orderMapper.updateById(order);

        // 恢复库存
        ticketMapper.increaseStock(order.getTicketId(), order.getQuantity());

        // 团体订单：删除所有成员槽（尚未支付，成员不会有凭证）
        if (Integer.valueOf(2).equals(order.getOrderType())) {
            groupMemberMapper.delete(new LambdaQueryWrapper<GroupMember>()
                    .eq(GroupMember::getOrderId, order.getId()));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long id) {
        Order order = getById(id);
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(OrderStatus.REFUNDED);
        orderMapper.updateById(order);

        // 将关联凭证置为过期（个人票按 orderId，团体票成员凭证也以 orderId 关联）
        credentialService.expireByOrderId(order.getId());

        // 恢复库存
        ticketMapper.increaseStock(order.getTicketId(), order.getQuantity());

        // 团体订单：清空成员槽数据（凭证已过期，槽位数据无需保留）
        if (Integer.valueOf(2).equals(order.getOrderType())) {
            groupMemberMapper.delete(new LambdaQueryWrapper<GroupMember>()
                    .eq(GroupMember::getOrderId, order.getId()));
        }
    }

    private void applyStatusFilter(LambdaQueryWrapper<Order> wrapper, String status) {
        if (StringUtils.hasText(status)) {
            OrderStatus orderStatus = null;
            try {
                orderStatus = OrderStatus.ofValue(Integer.parseInt(status));
            } catch (NumberFormatException e) {
                for (OrderStatus s : OrderStatus.values()) {
                    if (s.getCode().equals(status)) {
                        orderStatus = s;
                        break;
                    }
                }
            }
            if (orderStatus != null) {
                wrapper.eq(Order::getStatus, orderStatus);
            }
        }
    }

    private void fillTicketNames(java.util.List<Order> orders) {
        if (orders == null || orders.isEmpty()) return;
        java.util.Set<Long> ticketIds = new java.util.HashSet<>();
        for (Order o : orders) ticketIds.add(o.getTicketId());
        java.util.List<Ticket> tickets = ticketMapper.selectBatchIds(ticketIds);
        java.util.Map<Long, String> nameMap = new java.util.HashMap<>();
        for (Ticket t : tickets) nameMap.put(t.getId(), t.getName());
        for (Order o : orders) o.setTicketName(nameMap.get(o.getTicketId()));
    }

    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }
}
