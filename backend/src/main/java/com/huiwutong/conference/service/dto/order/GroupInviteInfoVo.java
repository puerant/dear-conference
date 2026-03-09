package com.huiwutong.conference.service.dto.order;

import lombok.Data;

/**
 * 邀请链接返回的团体基本信息 VO（公开，不含成员详情）
 */
@Data
public class GroupInviteInfoVo {

    private String groupName;
    private String contactName;
    private String ticketName;
    private Integer totalCount;
    private Integer filledCount;
    private Integer remainSlots;
}
