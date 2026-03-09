package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.enums.CredentialStatus;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.entity.Credential;
import com.huiwutong.conference.entity.Order;
import com.huiwutong.conference.entity.Ticket;
import com.huiwutong.conference.mapper.CredentialMapper;
import com.huiwutong.conference.mapper.OrderMapper;
import com.huiwutong.conference.mapper.TicketMapper;
import com.huiwutong.conference.service.CredentialService;
import com.huiwutong.conference.service.dto.credential.CredentialQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 凭证服务实现
 */
@Service
@RequiredArgsConstructor
public class CredentialServiceImpl implements CredentialService {

    private final CredentialMapper credentialMapper;
    private final OrderMapper orderMapper;
    private final TicketMapper ticketMapper;

    @Value("${credential.expire-days:365}")
    private int expireDays;

    // ─────────────────────────────────────────────────────────────
    // 查询
    // ─────────────────────────────────────────────────────────────

    @Override
    public Credential getByOrderId(Long orderId, Long userId) {
        // 先验证订单归属当前用户
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.CREDENTIAL_ACCESS_DENIED);
        }
        Credential credential = credentialMapper.selectOne(new LambdaQueryWrapper<Credential>()
                .eq(Credential::getOrderId, orderId)
                .isNull(Credential::getGroupMemberId));
        if (credential == null) {
            throw new BusinessException(ErrorCode.CREDENTIAL_NOT_FOUND);
        }
        return credential;
    }

    // ─────────────────────────────────────────────────────────────
    // 生成
    // ─────────────────────────────────────────────────────────────

    @Override
    public Credential generateCredential(Long orderId) {
        // 幂等：若已生成（非团体成员凭证）则直接返回
        Credential existing = credentialMapper.selectOne(new LambdaQueryWrapper<Credential>()
                .eq(Credential::getOrderId, orderId)
                .isNull(Credential::getGroupMemberId));
        if (existing != null) {
            return existing;
        }

        String credentialNo = buildCredentialNo();
        Order order = orderMapper.selectById(orderId);
        String seatInfo = assignSeat(order.getTicketId());

        Credential credential = new Credential();
        credential.setOrderId(orderId);
        credential.setCredentialNo(credentialNo);
        credential.setQrCode(credentialNo);
        credential.setSeatInfo(seatInfo);
        credential.setStatus(CredentialStatus.VALID);
        credential.setExpireAt(LocalDateTime.now().plusDays(expireDays));
        credentialMapper.insert(credential);
        return credential;
    }

    @Override
    public Credential generateCredentialForMember(Long orderId, Long groupMemberId) {
        Credential existing = credentialMapper.selectOne(new LambdaQueryWrapper<Credential>()
                .eq(Credential::getGroupMemberId, groupMemberId));
        if (existing != null) {
            return existing;
        }

        Order order = orderMapper.selectById(orderId);
        String credentialNo = buildCredentialNo();
        String seatInfo = assignSeat(order.getTicketId());

        Credential credential = new Credential();
        credential.setOrderId(orderId);
        credential.setGroupMemberId(groupMemberId);
        credential.setCredentialNo(credentialNo);
        credential.setQrCode(credentialNo);
        credential.setSeatInfo(seatInfo);
        credential.setStatus(CredentialStatus.VALID);
        credential.setExpireAt(LocalDateTime.now().plusDays(expireDays));
        credentialMapper.insert(credential);
        return credential;
    }

    // ─────────────────────────────────────────────────────────────
    // 核销
    // ─────────────────────────────────────────────────────────────

    @Override
    public void useCredential(String credentialNo) {
        Credential credential = credentialMapper.selectOne(new LambdaQueryWrapper<Credential>()
                .eq(Credential::getCredentialNo, credentialNo));
        if (credential == null) {
            throw new BusinessException(ErrorCode.CREDENTIAL_NOT_FOUND);
        }
        if (credential.getStatus() == CredentialStatus.USED) {
            throw new BusinessException(ErrorCode.CREDENTIAL_ALREADY_USED);
        }
        if (credential.getStatus() == CredentialStatus.EXPIRED) {
            throw new BusinessException(ErrorCode.CREDENTIAL_EXPIRED);
        }
        // 校验有效期
        if (credential.getExpireAt() != null && LocalDateTime.now().isAfter(credential.getExpireAt())) {
            // 同步将状态更新为 EXPIRED（定时任务未来得及处理的边界情况）
            credential.setStatus(CredentialStatus.EXPIRED);
            credentialMapper.updateById(credential);
            throw new BusinessException(ErrorCode.CREDENTIAL_TIME_EXPIRED);
        }

        credential.setStatus(CredentialStatus.USED);
        credential.setUsedAt(LocalDateTime.now());
        credentialMapper.updateById(credential);
    }

    // ─────────────────────────────────────────────────────────────
    // 失效
    // ─────────────────────────────────────────────────────────────

    @Override
    public void expireByOrderId(Long orderId) {
        Credential credential = credentialMapper.selectOne(new LambdaQueryWrapper<Credential>()
                .eq(Credential::getOrderId, orderId));
        if (credential != null && credential.getStatus() == CredentialStatus.VALID) {
            credential.setStatus(CredentialStatus.EXPIRED);
            credentialMapper.updateById(credential);
        }
    }

    @Override
    public void expireByGroupMemberId(Long groupMemberId) {
        Credential credential = credentialMapper.selectOne(new LambdaQueryWrapper<Credential>()
                .eq(Credential::getGroupMemberId, groupMemberId));
        if (credential != null && credential.getStatus() == CredentialStatus.VALID) {
            credential.setStatus(CredentialStatus.EXPIRED);
            credentialMapper.updateById(credential);
        }
    }

    // ─────────────────────────────────────────────────────────────
    // 管理员查询
    // ─────────────────────────────────────────────────────────────

    @Override
    public PageResult<Credential> listAll(CredentialQueryDto query) {
        Page<Credential> page = new Page<>(query.getPage(), query.getPageSize());
        LambdaQueryWrapper<Credential> wrapper = new LambdaQueryWrapper<Credential>()
                .orderByDesc(Credential::getCreatedAt);

        if (StringUtils.hasText(query.getCredentialNo())) {
            wrapper.like(Credential::getCredentialNo, query.getCredentialNo());
        }
        applyStatusFilter(wrapper, query.getStatus());

        credentialMapper.selectPage(page, wrapper);
        fillOrderInfo(page.getRecords());
        return PageResult.of(page);
    }

    @Override
    public Credential getByCredentialNo(String credentialNo) {
        Credential credential = credentialMapper.selectOne(new LambdaQueryWrapper<Credential>()
                .eq(Credential::getCredentialNo, credentialNo));
        if (credential == null) {
            throw new BusinessException(ErrorCode.CREDENTIAL_NOT_FOUND);
        }
        fillOrderInfo(Collections.singletonList(credential));
        return credential;
    }

    // ─────────────────────────────────────────────────────────────
    // 私有辅助方法
    // ─────────────────────────────────────────────────────────────

    private String buildCredentialNo() {
        return "CRED" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }

    /**
     * 按票种顺序分配座位号
     * 在同一事务内执行 COUNT 后 +1，适合低并发场景
     */
    private String assignSeat(Long ticketId) {
        int seq = credentialMapper.countByTicketId(ticketId) + 1;
        return String.format("SN-%04d", seq);
    }

    private void applyStatusFilter(LambdaQueryWrapper<Credential> wrapper, String status) {
        if (!StringUtils.hasText(status)) return;
        for (CredentialStatus s : CredentialStatus.values()) {
            if (s.getCode().equals(status)) {
                wrapper.eq(Credential::getStatus, s);
                return;
            }
        }
    }

    private void fillOrderInfo(List<Credential> credentials) {
        if (credentials == null || credentials.isEmpty()) return;

        Set<Long> orderIds = new HashSet<>();
        for (Credential c : credentials) orderIds.add(c.getOrderId());

        List<Order> orders = orderMapper.selectBatchIds(orderIds);
        Map<Long, Order> orderMap = new HashMap<>();
        Set<Long> ticketIds = new HashSet<>();
        for (Order o : orders) {
            orderMap.put(o.getId(), o);
            ticketIds.add(o.getTicketId());
        }

        List<Ticket> tickets = ticketMapper.selectBatchIds(ticketIds);
        Map<Long, String> ticketNameMap = new HashMap<>();
        for (Ticket t : tickets) ticketNameMap.put(t.getId(), t.getName());

        for (Credential c : credentials) {
            Order order = orderMap.get(c.getOrderId());
            if (order != null) {
                c.setOrderNo(order.getOrderNo());
                c.setAttendeeName(order.getAttendeeName());
                c.setAttendeeEmail(order.getAttendeeEmail());
                c.setTicketName(ticketNameMap.get(order.getTicketId()));
            }
        }
    }
}
