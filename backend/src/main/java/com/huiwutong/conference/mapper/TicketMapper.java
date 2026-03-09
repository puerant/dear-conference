package com.huiwutong.conference.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huiwutong.conference.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 票务 Mapper
 */
@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {

    /**
     * 扣减库存（乐观锁防超卖）
     */
    @Update("UPDATE tickets SET stock = stock - #{quantity}, sold_count = sold_count + #{quantity} " +
            "WHERE id = #{id} AND stock >= #{quantity}")
    int decreaseStock(@Param("id") Long id, @Param("quantity") int quantity);

    /**
     * 恢复库存（取消 / 退款时回滚）
     */
    @Update("UPDATE tickets SET stock = stock + #{quantity}, sold_count = sold_count - #{quantity} " +
            "WHERE id = #{id}")
    int increaseStock(@Param("id") Long id, @Param("quantity") int quantity);
}
