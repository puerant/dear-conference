package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.enums.OrderStatus;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.GroupMember;
import com.huiwutong.conference.entity.Order;
import com.huiwutong.conference.entity.Ticket;
import com.huiwutong.conference.mapper.GroupMemberMapper;
import com.huiwutong.conference.mapper.OrderMapper;
import com.huiwutong.conference.mapper.TicketMapper;
import com.huiwutong.conference.service.CredentialService;
import com.huiwutong.conference.service.GroupOrderService;
import com.huiwutong.conference.service.dto.order.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 团体订单服务实现
 */
@Service
@RequiredArgsConstructor
public class GroupOrderServiceImpl implements GroupOrderService {

    private final OrderMapper orderMapper;
    private final GroupMemberMapper groupMemberMapper;
    private final TicketMapper ticketMapper;
    private final CredentialService credentialService;

    @Override
    public GroupOrderVo getGroupDetail(Long orderId, Long userId) {
        Order order = getOrderAndVerifyOwner(orderId, userId);
        if (!Integer.valueOf(2).equals(order.getOrderType())) {
            throw new BusinessException(ErrorCode.TICKET_TYPE_MISMATCH);
        }

        List<GroupMember> members = groupMemberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>()
                        .eq(GroupMember::getOrderId, orderId)
                        .orderByAsc(GroupMember::getSequenceNo));

        long filledCount = members.stream().filter(m -> Integer.valueOf(2).equals(m.getStatus())).count();

        GroupOrderVo vo = new GroupOrderVo();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setGroupName(order.getGroupName());
        vo.setContactName(order.getAttendeeName());
        vo.setContactEmail(order.getAttendeeEmail());
        vo.setTotalCount(order.getQuantity());
        vo.setFilledCount((int) filledCount);
        vo.setInviteUrl(order.getInviteToken() != null ? "/group/invite/" + order.getInviteToken() : null);
        vo.setMembers(members.stream().map(this::toVo).collect(Collectors.toList()));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getOrGenerateInviteUrl(Long orderId, Long userId) {
        Order order = getOrderAndVerifyOwner(orderId, userId);
        if (!Integer.valueOf(2).equals(order.getOrderType())) {
            throw new BusinessException(ErrorCode.TICKET_TYPE_MISMATCH);
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.GROUP_NOT_PAID);
        }
        if (order.getInviteToken() == null) {
            order.setInviteToken(UUID.randomUUID().toString().replace("-", ""));
            orderMapper.updateById(order);
        }
        return "/group/invite/" + order.getInviteToken();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GroupMemberVo addMember(Long orderId, Long userId, AddMemberDto dto) {
        Order order = getOrderAndVerifyOwner(orderId, userId);
        if (!Integer.valueOf(2).equals(order.getOrderType())) {
            throw new BusinessException(ErrorCode.TICKET_TYPE_MISMATCH);
        }
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.GROUP_NOT_PAID);
        }

        GroupMember slot = groupMemberMapper.findFirstPendingSlotForUpdate(orderId);
        if (slot == null) {
            throw new BusinessException(ErrorCode.GROUP_MEMBER_FULL);
        }

        slot.setMemberName(dto.getMemberName());
        slot.setMemberEmail(dto.getMemberEmail());
        slot.setStatus(2);
        slot.setFilledAt(LocalDateTime.now());
        groupMemberMapper.updateById(slot);

        credentialService.generateCredentialForMember(orderId, slot.getId());
        return toVo(slot);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GroupMemberVo updateMember(Long orderId, Long memberId, Long userId, AddMemberDto dto) {
        getOrderAndVerifyOwner(orderId, userId);
        GroupMember member = getMemberAndVerifyBelongsToOrder(memberId, orderId);

        member.setMemberName(dto.getMemberName());
        member.setMemberEmail(dto.getMemberEmail());
        member.setFilledAt(LocalDateTime.now());
        if (!Integer.valueOf(2).equals(member.getStatus())) {
            member.setStatus(2);
        }
        groupMemberMapper.updateById(member);
        return toVo(member);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearMember(Long orderId, Long memberId, Long userId) {
        getOrderAndVerifyOwner(orderId, userId);
        GroupMember member = getMemberAndVerifyBelongsToOrder(memberId, orderId);

        member.setMemberName(null);
        member.setMemberEmail(null);
        member.setStatus(1);
        member.setFilledAt(null);
        groupMemberMapper.updateById(member);

        credentialService.expireByGroupMemberId(memberId);
    }

    @Override
    public GroupInviteInfoVo getGroupInfoByToken(String token) {
        Order order = getOrderByToken(token);
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.GROUP_NOT_PAID);
        }

        List<GroupMember> members = groupMemberMapper.selectList(
                new LambdaQueryWrapper<GroupMember>().eq(GroupMember::getOrderId, order.getId()));
        long filledCount = members.stream().filter(m -> Integer.valueOf(2).equals(m.getStatus())).count();

        Ticket ticket = ticketMapper.selectById(order.getTicketId());

        GroupInviteInfoVo vo = new GroupInviteInfoVo();
        vo.setGroupName(order.getGroupName());
        vo.setContactName(order.getAttendeeName());
        vo.setTicketName(ticket != null ? ticket.getName() : "");
        vo.setTotalCount(order.getQuantity());
        vo.setFilledCount((int) filledCount);
        vo.setRemainSlots(order.getQuantity() - (int) filledCount);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fillMemberByToken(String token, FillMemberInfoDto dto) {
        Order order = getOrderByToken(token);
        if (order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.GROUP_NOT_PAID);
        }

        GroupMember slot = groupMemberMapper.findFirstPendingSlotForUpdate(order.getId());
        if (slot == null) {
            throw new BusinessException(ErrorCode.GROUP_MEMBER_FULL);
        }

        slot.setMemberName(dto.getMemberName());
        slot.setMemberEmail(dto.getMemberEmail());
        slot.setStatus(2);
        slot.setFilledAt(LocalDateTime.now());
        groupMemberMapper.updateById(slot);

        credentialService.generateCredentialForMember(order.getId(), slot.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void regenerateCredential(Long orderId, Long memberId, Long userId) {
        getOrderAndVerifyOwner(orderId, userId);
        GroupMember member = getMemberAndVerifyBelongsToOrder(memberId, orderId);
        if (!Integer.valueOf(2).equals(member.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR);
        }
        credentialService.expireByGroupMemberId(memberId);
        credentialService.generateCredentialForMember(orderId, memberId);
    }

    private Order getOrderAndVerifyOwner(Long orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        // userId == null means admin bypass (no ownership check)
        if (userId != null && !order.getUserId().equals(userId)) throw new BusinessException(ErrorCode.ORDER_ACCESS_DENIED);
        return order;
    }

    private GroupMember getMemberAndVerifyBelongsToOrder(Long memberId, Long orderId) {
        GroupMember member = groupMemberMapper.selectById(memberId);
        if (member == null || !member.getOrderId().equals(orderId)) {
            throw new BusinessException(ErrorCode.NOT_FOUND);
        }
        return member;
    }

    private Order getOrderByToken(String token) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getInviteToken, token));
        if (order == null) throw new BusinessException(ErrorCode.INVITE_TOKEN_INVALID);
        return order;
    }

    private GroupMemberVo toVo(GroupMember m) {
        GroupMemberVo vo = new GroupMemberVo();
        vo.setId(m.getId());
        vo.setSequenceNo(m.getSequenceNo());
        vo.setMemberName(m.getMemberName());
        vo.setMemberEmail(m.getMemberEmail());
        vo.setStatus(m.getStatus());
        vo.setFilledAt(m.getFilledAt());
        return vo;
    }
}
