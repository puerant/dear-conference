package com.huiwutong.conference.service;

import com.huiwutong.conference.service.dto.order.*;

/**
 * 团体订单服务接口
 */
public interface GroupOrderService {

    /**
     * 获取团体订单详情（含成员列表）
     * @param orderId 订单ID
     * @param userId  当前登录用户ID（权限校验）
     */
    GroupOrderVo getGroupDetail(Long orderId, Long userId);

    /**
     * 获取/生成邀请链接，返回前端路径（如 /group/invite/{token}），不含域名
     * @param orderId 订单ID
     * @param userId  当前登录用户ID（权限校验）
     */
    String getOrGenerateInviteUrl(Long orderId, Long userId);

    /**
     * 负责人添加成员
     */
    GroupMemberVo addMember(Long orderId, Long userId, AddMemberDto dto);

    /**
     * 负责人修改成员信息
     */
    GroupMemberVo updateMember(Long orderId, Long memberId, Long userId, AddMemberDto dto);

    /**
     * 负责人清除成员信息（重置为空白槽）
     */
    void clearMember(Long orderId, Long memberId, Long userId);

    /**
     * 通过邀请链接查询团体信息（公开，不含成员详情）
     */
    GroupInviteInfoVo getGroupInfoByToken(String token);

    /**
     * 成员通过邀请链接自助填写信息
     */
    void fillMemberByToken(String token, FillMemberInfoDto dto);

    /**
     * 负责人为已填写成员重新生成入会凭证（旧凭证自动失效）
     */
    void regenerateCredential(Long orderId, Long memberId, Long userId);
}
