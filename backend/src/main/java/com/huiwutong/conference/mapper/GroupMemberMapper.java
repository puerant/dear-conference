package com.huiwutong.conference.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huiwutong.conference.entity.GroupMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 团体成员 Mapper
 */
@Mapper
public interface GroupMemberMapper extends BaseMapper<GroupMember> {

    /**
     * 查询并锁定第一个待填写的成员槽（SELECT FOR UPDATE 防并发抢占）
     */
    @Select("SELECT * FROM group_members WHERE order_id = #{orderId} AND status = 1 " +
            "ORDER BY sequence_no ASC LIMIT 1 FOR UPDATE")
    GroupMember findFirstPendingSlotForUpdate(@Param("orderId") Long orderId);
}
