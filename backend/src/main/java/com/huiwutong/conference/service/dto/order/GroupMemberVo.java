package com.huiwutong.conference.service.dto.order;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 团体成员 VO
 */
@Data
public class GroupMemberVo {

    private Long id;
    private Integer sequenceNo;
    private String memberName;
    private String memberEmail;
    private Integer status;    // 1=待填写 2=已填写
    private LocalDateTime filledAt;
}
