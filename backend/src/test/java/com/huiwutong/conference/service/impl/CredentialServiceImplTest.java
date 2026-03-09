package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.enums.CredentialStatus;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.Credential;
import com.huiwutong.conference.entity.Order;
import com.huiwutong.conference.mapper.CredentialMapper;
import com.huiwutong.conference.mapper.OrderMapper;
import com.huiwutong.conference.mapper.TicketMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CredentialServiceImpl 单元测试
 * 覆盖：userId 鉴权、座位分配、有效期校验
 */
@ExtendWith(MockitoExtension.class)
class CredentialServiceImplTest {

    @Mock
    private CredentialMapper credentialMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private CredentialServiceImpl credentialService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(credentialService, "expireDays", 365);
    }

    // ─── 功能一：userId 鉴权 ───────────────────────────────────────

    @Test
    void getByOrderId_orderNotFound_throwsOrderNotFound() {
        when(orderMapper.selectById(1L)).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> credentialService.getByOrderId(1L, 1L));
        assertEquals(ErrorCode.ORDER_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void getByOrderId_orderBelongsToDifferentUser_throwsAccessDenied() {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(999L);
        when(orderMapper.selectById(1L)).thenReturn(order);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> credentialService.getByOrderId(1L, 1L));
        assertEquals(ErrorCode.CREDENTIAL_ACCESS_DENIED.getCode(), ex.getCode());
    }

    @Test
    void getByOrderId_credentialNotFound_throwsCredentialNotFound() {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        when(orderMapper.selectById(1L)).thenReturn(order);
        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> credentialService.getByOrderId(1L, 1L));
        assertEquals(ErrorCode.CREDENTIAL_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    void getByOrderId_success_returnsCredential() {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        Credential credential = new Credential();
        credential.setCredentialNo("CRED123");

        when(orderMapper.selectById(1L)).thenReturn(order);
        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(credential);

        Credential result = credentialService.getByOrderId(1L, 1L);
        assertEquals("CRED123", result.getCredentialNo());
    }

    // ─── 功能二：qrCode 存 credentialNo 明文 ──────────────────────

    @Test
    void generateCredential_qrCodeEqualsCredentialNo() {
        Order order = new Order();
        order.setId(1L);
        order.setTicketId(10L);
        when(orderMapper.selectById(1L)).thenReturn(order);
        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(credentialMapper.countByTicketId(10L)).thenReturn(0);

        Credential result = credentialService.generateCredential(1L);

        assertNotNull(result.getQrCode());
        assertEquals(result.getCredentialNo(), result.getQrCode());
    }

    @Test
    void generateCredentialForMember_qrCodeEqualsCredentialNo() {
        Order order = new Order();
        order.setId(1L);
        order.setTicketId(10L);
        when(orderMapper.selectById(1L)).thenReturn(order);
        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(credentialMapper.countByTicketId(10L)).thenReturn(0);

        Credential result = credentialService.generateCredentialForMember(1L, 5L);

        assertEquals(result.getCredentialNo(), result.getQrCode());
    }

    // ─── 功能三：座位分配 ─────────────────────────────────────────

    @Test
    void generateCredential_firstSeat_isSN0001() {
        Order order = new Order();
        order.setId(1L);
        order.setTicketId(10L);
        when(orderMapper.selectById(1L)).thenReturn(order);
        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(credentialMapper.countByTicketId(10L)).thenReturn(0);

        Credential result = credentialService.generateCredential(1L);

        assertEquals("SN-0001", result.getSeatInfo());
    }

    @Test
    void generateCredential_fifthSeat_isSN0005() {
        Order order = new Order();
        order.setId(1L);
        order.setTicketId(10L);
        when(orderMapper.selectById(1L)).thenReturn(order);
        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(credentialMapper.countByTicketId(10L)).thenReturn(4);

        Credential result = credentialService.generateCredential(1L);

        assertEquals("SN-0005", result.getSeatInfo());
    }

    @Test
    void generateCredentialForMember_seatAssigned() {
        Order order = new Order();
        order.setId(1L);
        order.setTicketId(10L);
        when(orderMapper.selectById(1L)).thenReturn(order);
        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(credentialMapper.countByTicketId(10L)).thenReturn(2);

        Credential result = credentialService.generateCredentialForMember(1L, 5L);

        assertEquals("SN-0003", result.getSeatInfo());
        assertEquals(5L, result.getGroupMemberId());
    }

    // ─── 功能四：有效期 ───────────────────────────────────────────

    @Test
    void generateCredential_expireAtIsSetToFuture() {
        Order order = new Order();
        order.setId(1L);
        order.setTicketId(10L);
        when(orderMapper.selectById(1L)).thenReturn(order);
        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
        when(credentialMapper.countByTicketId(10L)).thenReturn(0);

        Credential result = credentialService.generateCredential(1L);

        assertNotNull(result.getExpireAt());
        assertTrue(result.getExpireAt().isAfter(LocalDateTime.now().plusDays(364)));
    }

    @Test
    void useCredential_whenTimeExpired_throwsTimeExpired_andStatusBecomesExpired() {
        Credential cred = new Credential();
        cred.setId(1L);
        cred.setStatus(CredentialStatus.VALID);
        cred.setExpireAt(LocalDateTime.now().minusDays(1));

        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(cred);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> credentialService.useCredential("CRED-EXPIRED"));
        assertEquals(ErrorCode.CREDENTIAL_TIME_EXPIRED.getCode(), ex.getCode());

        ArgumentCaptor<Credential> captor = ArgumentCaptor.forClass(Credential.class);
        verify(credentialMapper).updateById(captor.capture());
        assertEquals(CredentialStatus.EXPIRED, captor.getValue().getStatus());
    }

    @Test
    void useCredential_whenNotExpired_succeedsAndStatusBecomesUsed() {
        Credential cred = new Credential();
        cred.setId(1L);
        cred.setStatus(CredentialStatus.VALID);
        cred.setExpireAt(LocalDateTime.now().plusDays(100));

        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(cred);

        assertDoesNotThrow(() -> credentialService.useCredential("CRED-VALID"));

        ArgumentCaptor<Credential> captor = ArgumentCaptor.forClass(Credential.class);
        verify(credentialMapper).updateById(captor.capture());
        assertEquals(CredentialStatus.USED, captor.getValue().getStatus());
        assertNotNull(captor.getValue().getUsedAt());
    }

    @Test
    void useCredential_whenStatusUsed_throwsAlreadyUsed() {
        Credential cred = new Credential();
        cred.setStatus(CredentialStatus.USED);
        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(cred);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> credentialService.useCredential("CRED-USED"));
        assertEquals(ErrorCode.CREDENTIAL_ALREADY_USED.getCode(), ex.getCode());
    }

    @Test
    void useCredential_whenStatusExpired_throwsExpired() {
        Credential cred = new Credential();
        cred.setStatus(CredentialStatus.EXPIRED);
        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(cred);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> credentialService.useCredential("CRED-EXP"));
        assertEquals(ErrorCode.CREDENTIAL_EXPIRED.getCode(), ex.getCode());
    }

    // ─── 幂等性 ──────────────────────────────────────────────────

    @Test
    void generateCredential_whenAlreadyExists_returnsExistingWithoutInsert() {
        Credential existing = new Credential();
        existing.setCredentialNo("CRED-EXIST");
        when(credentialMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(existing);

        Credential result = credentialService.generateCredential(1L);

        assertEquals("CRED-EXIST", result.getCredentialNo());
        verify(credentialMapper, never()).insert(any());
    }
}
