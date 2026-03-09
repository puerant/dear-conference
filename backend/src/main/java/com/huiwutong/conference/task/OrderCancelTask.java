package com.huiwutong.conference.task;

import com.huiwutong.conference.entity.Order;
import com.huiwutong.conference.mapper.CredentialMapper;
import com.huiwutong.conference.mapper.GroupMemberMapper;
import com.huiwutong.conference.mapper.OrderMapper;
import com.huiwutong.conference.mapper.TicketMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单超时自动取消任务
 * 每 5 分钟执行一次，将超过 30 分钟未支付的订单自动取消并恢复库存
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelTask {

    private final OrderMapper orderMapper;
    private final TicketMapper ticketMapper;
    private final CredentialMapper credentialMapper;
    private final GroupMemberMapper groupMemberMapper;

    /**
     * 订单支付超时时间（分钟）
     */
    private static final int ORDER_TIMEOUT_MINUTES = 30;

    /**
     * 定时任务 cron 表达式：每 5 分钟执行一次
     * 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 */5 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void cancelTimeoutOrders() {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(ORDER_TIMEOUT_MINUTES);

        // 查找超时未支付的订单
        List<Order> timeoutOrders = orderMapper.findTimeoutUnpaidOrders(timeoutThreshold);

        if (timeoutOrders.isEmpty()) {
            return;
        }

        log.info("[OrderCancelTask] 检测到 {} 笔超时未支付订单", timeoutOrders.size());

        for (Order order : timeoutOrders) {
            try {
                cancelOrder(order);
                log.info("[OrderCancelTask] 订单取消成功，orderId={}", order.getId());
            } catch (Exception e) {
                log.error("[OrderCancelTask] 订单取消失败，orderId={}：{}", order.getId(), e.getMessage(), e);
            }
        }
    }

    /**
     * 取消单个订单
     */
    private void cancelOrder(Order order) {
        // 1. 更新订单状态为已取消
        orderMapper.cancelOrder(order.getId());

        // 2. 恢复库存
        ticketMapper.increaseStock(order.getTicketId(), order.getQuantity());

        // 3. 处理凭证和团体成员
        if (Integer.valueOf(2).equals(order.getOrderType())) {
            // 团体票：删除团体成员记录
            groupMemberMapper.delete(
                    com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery(com.huiwutong.conference.entity.GroupMember.class)
                            .eq(com.huiwutong.conference.entity.GroupMember::getOrderId, order.getId())
            );
        } else {
            // 个人票：将凭证状态置为已过期（理论上不应有凭证，但防御性处理）
            credentialMapper.expireByOrderId(order.getId());
        }
    }
}
