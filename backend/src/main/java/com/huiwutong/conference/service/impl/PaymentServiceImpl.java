package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.config.PaymentProperties;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.enums.OrderStatus;
import com.huiwutong.conference.common.enums.PaymentStatus;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.Order;
import com.huiwutong.conference.entity.PaymentRecord;
import com.huiwutong.conference.entity.RefundRecord;
import com.huiwutong.conference.entity.Ticket;
import com.huiwutong.conference.mapper.OrderMapper;
import com.huiwutong.conference.mapper.PaymentRecordMapper;
import com.huiwutong.conference.mapper.RefundRecordMapper;
import com.huiwutong.conference.mapper.TicketMapper;
import com.huiwutong.conference.service.CredentialService;
import com.huiwutong.conference.service.PaymentService;
import com.huiwutong.conference.service.dto.payment.*;
import com.huiwutong.conference.service.payment.PaymentStrategy;
import com.huiwutong.conference.service.payment.PaymentStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付服务实现
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentStrategyFactory strategyFactory;
    private final PaymentRecordMapper paymentRecordMapper;
    private final RefundRecordMapper refundRecordMapper;
    private final OrderMapper orderMapper;
    private final TicketMapper ticketMapper;
    private final CredentialService credentialService;
    private final PaymentProperties properties;

    public PaymentServiceImpl(PaymentStrategyFactory strategyFactory,
                              PaymentRecordMapper paymentRecordMapper,
                              RefundRecordMapper refundRecordMapper,
                              OrderMapper orderMapper,
                              TicketMapper ticketMapper,
                              @Lazy CredentialService credentialService,
                              PaymentProperties properties) {
        this.strategyFactory = strategyFactory;
        this.paymentRecordMapper = paymentRecordMapper;
        this.refundRecordMapper = refundRecordMapper;
        this.orderMapper = orderMapper;
        this.ticketMapper = ticketMapper;
        this.credentialService = credentialService;
        this.properties = properties;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentResponse initiatePayment(Long orderId, Long userId, InitiatePaymentDto dto) {
        // 1. 校验订单
        Order order = orderMapper.selectById(orderId);
        if (order == null) throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        if (!order.getUserId().equals(userId)) throw new BusinessException(ErrorCode.ORDER_ACCESS_DENIED);
        if (order.getStatus() != OrderStatus.UNPAID) throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);

        // 若已存在任意渠道的成功支付记录，禁止再次发起（防止多渠道并发导致重复 SUCCESS 记录）
        boolean alreadySucceeded = paymentRecordMapper.exists(
                new LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getOrderId, orderId)
                        .eq(PaymentRecord::getStatus, PaymentStatus.SUCCESS));
        if (alreadySucceeded) throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);

        // 2. 幂等：同一订单同一渠道的 PENDING 记录未过期则直接返回
        PaymentRecord existing = paymentRecordMapper.selectOne(
                new LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getOrderId, orderId)
                        .eq(PaymentRecord::getProvider, dto.getMethod())
                        .eq(PaymentRecord::getStatus, PaymentStatus.PENDING)
        );
        if (existing != null && existing.getExpireAt().isAfter(LocalDateTime.now())) {
            return toResponse(existing);
        }

        // 3. 获取策略（不支持的渠道直接抛错）
        PaymentStrategy strategy = strategyFactory.get(dto.getMethod());

        // 4. 构建内部支付请求
        String paymentNo = "PAY" + System.currentTimeMillis() + (int) (Math.random() * 1000);
        LocalDateTime expireAt = LocalDateTime.now().plusMinutes(properties.getExpireMinutes());

        Ticket ticket = ticketMapper.selectById(order.getTicketId());
        String description = ticket != null ? ticket.getName() : "会议票务";

        PayRequest payRequest = new PayRequest();
        payRequest.setPaymentNo(paymentNo);
        payRequest.setOrderNo(order.getOrderNo());
        payRequest.setAmount(order.getTotalAmount());
        payRequest.setDescription(description);
        payRequest.setExpireAt(expireAt);

        // 5. 调用渠道 API
        PaymentResponse response = strategy.createPayment(payRequest);

        // 6. 持久化支付记录
        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo(paymentNo);
        record.setOrderId(orderId);
        record.setOrderNo(order.getOrderNo());
        record.setProvider(dto.getMethod());
        record.setAmount(order.getTotalAmount());
        record.setCurrency("CNY");
        record.setStatus(PaymentStatus.PENDING);
        record.setPayUrl(response.getPayUrl());
        record.setQrCode(response.getQrCode());
        record.setExpireAt(expireAt);
        paymentRecordMapper.insert(record);

        response.setPaymentNo(paymentNo);
        response.setAmount(order.getTotalAmount());
        response.setExpireAt(expireAt);
        return response;
    }

    @Override
    public PaymentStatusVo getStatus(String paymentNo) {
        PaymentRecord record = paymentRecordMapper.selectOne(
                new LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getPaymentNo, paymentNo));
        if (record == null) throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        return new PaymentStatusVo(record.getPaymentNo(), record.getStatus().getCode(), record.getPaidAt());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleNotify(String paymentNo, String providerTradeNo, String rawData) {
        // 幂等：只处理 PENDING 状态的记录
        PaymentRecord record = paymentRecordMapper.selectOne(
                new LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getPaymentNo, paymentNo)
                        .eq(PaymentRecord::getStatus, PaymentStatus.PENDING));
        if (record == null) {
            log.info("支付回调已处理过，跳过：paymentNo={}", paymentNo);
            return;
        }

        // 金额校验（防篡改）
        // 注意：rawData 传入只是为了持久化，金额在 NotifyResult 中已校验过

        record.setStatus(PaymentStatus.SUCCESS);
        record.setProviderTradeNo(providerTradeNo);
        record.setPaidAt(LocalDateTime.now());
        record.setNotifyRaw(rawData);
        paymentRecordMapper.updateById(record);

        // 确认订单支付
        confirmOrderPaid(record.getOrderId());
        log.info("支付成功，orderId={}，paymentNo={}", record.getOrderId(), paymentNo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refund(Long orderId) {
        // 查找成功支付记录（取最新一条，防止多渠道并发产生多条 SUCCESS 记录时崩溃）
        PaymentRecord paymentRecord = paymentRecordMapper.selectOne(
                new LambdaQueryWrapper<PaymentRecord>()
                        .eq(PaymentRecord::getOrderId, orderId)
                        .eq(PaymentRecord::getStatus, PaymentStatus.SUCCESS)
                        .orderByDesc(PaymentRecord::getId)
                        .last("LIMIT 1"));

        if (paymentRecord != null) {
            // 调用渠道退款 API
            String refundNo = "REF" + System.currentTimeMillis() + (int) (Math.random() * 1000);
            try {
                PaymentStrategy strategy = strategyFactory.get(paymentRecord.getProvider());
                RefundRequest refundReq = new RefundRequest();
                refundReq.setPaymentNo(paymentRecord.getPaymentNo());
                refundReq.setRefundNo(refundNo);
                refundReq.setProviderTradeNo(paymentRecord.getProviderTradeNo());
                refundReq.setAmount(paymentRecord.getAmount());
                refundReq.setReason("管理员审批退款");
                strategy.refund(refundReq);
            } catch (Exception e) {
                log.error("调用渠道退款 API 失败，orderId={}：{}", orderId, e.getMessage(), e);
                throw new BusinessException(ErrorCode.PAYMENT_PROVIDER_ERROR);
            }

            // 更新支付记录状态
            paymentRecord.setStatus(PaymentStatus.REFUNDED);
            paymentRecordMapper.updateById(paymentRecord);

            // 创建退款记录
            RefundRecord refundRecord = new RefundRecord();
            refundRecord.setRefundNo(refundNo);
            refundRecord.setPaymentNo(paymentRecord.getPaymentNo());
            refundRecord.setOrderId(orderId);
            refundRecord.setProvider(paymentRecord.getProvider());
            refundRecord.setAmount(paymentRecord.getAmount());
            refundRecord.setStatus(1); // 处理中
            refundRecord.setReason("管理员审批退款");
            refundRecordMapper.insert(refundRecord);
        } else {
            log.warn("未找到成功支付记录，以模拟支付方式执行退款，orderId={}", orderId);
        }

        // 更新订单状态、凭证、库存（无论是否有支付记录都执行）
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != OrderStatus.PAID) {
            throw new BusinessException(ErrorCode.ORDER_STATUS_ERROR);
        }
        order.setStatus(OrderStatus.REFUNDED);
        orderMapper.updateById(order);

        credentialService.expireByOrderId(orderId);
        ticketMapper.increaseStock(order.getTicketId(), order.getQuantity());
    }

    // ── 内部方法 ───────────────────────────────────────────────────────────────

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void mockPay(Long orderId) {
        log.info("[DEV] 模拟支付成功，orderId={}", orderId);
        confirmOrderPaid(orderId);
    }

    private void confirmOrderPaid(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getStatus() != OrderStatus.UNPAID) {
            log.warn("订单不存在或状态非 UNPAID，跳过确认支付，orderId={}", orderId);
            return;
        }
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        orderMapper.updateById(order);
        // 个人票立即生成凭证；团体票凭证在每位成员填写信息时单独生成
        if (!Integer.valueOf(2).equals(order.getOrderType())) {
            credentialService.generateCredential(orderId);
        }
    }

    private PaymentResponse toResponse(PaymentRecord record) {
        PaymentResponse resp = new PaymentResponse();
        resp.setPaymentNo(record.getPaymentNo());
        resp.setMethod(record.getProvider());
        resp.setQrCode(record.getQrCode());
        resp.setPayUrl(record.getPayUrl());
        resp.setAmount(record.getAmount());
        resp.setExpireAt(record.getExpireAt());
        return resp;
    }

    private String getTicketName(Long ticketId) {
        Ticket ticket = ticketMapper.selectById(ticketId);
        return ticket != null ? ticket.getName() : "会议票务";
    }
}
