package com.huiwutong.conference.controller.order;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.GroupOrderService;
import com.huiwutong.conference.service.dto.order.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 团体订单管理控制器（负责人操作，需 attendee 身份）
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class GroupOrderController {

    private final GroupOrderService groupOrderService;

    /**
     * 获取团体订单详情（含成员列表）
     */
    @GetMapping("/{id}/group")
    @RequireRole("attendee")
    public Result<GroupOrderVo> getGroupDetail(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(groupOrderService.getGroupDetail(id, userId));
    }

    /**
     * 获取/生成邀请链接（支付后才可生成）
     */
    @GetMapping("/{id}/group/invite")
    @RequireRole("attendee")
    public Result<Map<String, String>> getInviteUrl(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String inviteUrl = groupOrderService.getOrGenerateInviteUrl(id, userId);
        return Result.success(Map.of("inviteUrl", inviteUrl));
    }

    /**
     * 负责人添加成员
     */
    @PostMapping("/{id}/group/members")
    @RequireRole("attendee")
    public Result<GroupMemberVo> addMember(@PathVariable Long id,
                                           @Valid @RequestBody AddMemberDto dto,
                                           HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(groupOrderService.addMember(id, userId, dto));
    }

    /**
     * 负责人修改成员信息
     */
    @PutMapping("/{id}/group/members/{memberId}")
    @RequireRole("attendee")
    public Result<GroupMemberVo> updateMember(@PathVariable Long id,
                                              @PathVariable Long memberId,
                                              @Valid @RequestBody AddMemberDto dto,
                                              HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(groupOrderService.updateMember(id, memberId, userId, dto));
    }

    /**
     * 负责人清除成员信息（重置为空白槽）
     */
    @DeleteMapping("/{id}/group/members/{memberId}")
    @RequireRole("attendee")
    public Result<Void> clearMember(@PathVariable Long id,
                                    @PathVariable Long memberId,
                                    HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        groupOrderService.clearMember(id, memberId, userId);
        return Result.success();
    }

    /**
     * 负责人为成员重新生成凭证（旧凭证自动失效）
     */
    @PostMapping("/{id}/group/members/{memberId}/credential")
    @RequireRole("attendee")
    public Result<Void> regenerateCredential(@PathVariable Long id,
                                             @PathVariable Long memberId,
                                             HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        groupOrderService.regenerateCredential(id, memberId, userId);
        return Result.success();
    }
}
