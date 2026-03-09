package com.huiwutong.conference.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huiwutong.conference.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单 Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 查询超时未支付的订单（超过 30 分钟未支付）
     */
    @Select("SELECT id, ticket_id, quantity, order_type FROM orders " +
            "WHERE status = 1 AND created_at < #{timeoutThreshold}")
    List<Order> findTimeoutUnpaidOrders(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);

    /**
     * 批量取消订单（更新状态）
     */
    @Update("UPDATE orders SET status = 3 WHERE id IN " +
            "(SELECT tmp.id FROM (SELECT id FROM orders WHERE id = #{id}) AS tmp)")
    int cancelOrder(@Param("id") Long id);
}
