package com.huiwutong.conference.service.dto.order;

import lombok.Data;

import java.util.List;

/**
 * 团体订单详情 VO（含成员列表）
 */
@Data
public class GroupOrderVo {

    private Long orderId;
    private String orderNo;
    private String groupName;
    private String contactName;
    private String contactEmail;
    private Integer totalCount;
    private Integer filledCount;
    private String inviteUrl;
    private List<GroupMemberVo> members;
}
