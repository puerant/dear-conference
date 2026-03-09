package com.huiwutong.conference.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huiwutong.conference.entity.Credential;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

/**
 * 凭证 Mapper
 */
@Mapper
public interface CredentialMapper extends BaseMapper<Credential> {

    /**
     * 统计某票种已发出的凭证数量（用于座位顺序分配）
     */
    @Select("SELECT COUNT(*) FROM credentials c JOIN orders o ON c.order_id = o.id WHERE o.ticket_id = #{ticketId}")
    int countByTicketId(@Param("ticketId") Long ticketId);

    /**
     * 批量将超过有效期的 VALID 凭证置为 EXPIRED（由定时任务调用）
     */
    @Update("UPDATE credentials SET status = 3 WHERE status = 1 AND expire_at IS NOT NULL AND expire_at < #{now}")
    int expireOutdated(@Param("now") LocalDateTime now);

    /**
     * 将指定订单的凭证置为 EXPIRED（订单取消时调用）
     */
    @Update("UPDATE credentials SET status = 3 WHERE order_id = #{orderId}")
    int expireByOrderId(@Param("orderId") Long orderId);
}
