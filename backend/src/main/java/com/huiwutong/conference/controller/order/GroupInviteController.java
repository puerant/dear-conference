package com.huiwutong.conference.controller.order;

import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.GroupOrderService;
import com.huiwutong.conference.service.dto.order.FillMemberInfoDto;
import com.huiwutong.conference.service.dto.order.GroupInviteInfoVo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 团体邀请链接公开控制器（无需 JWT 鉴权）
 */
@RestController
@RequestMapping("/api/group/invite")
@RequiredArgsConstructor
public class GroupInviteController {

    private final GroupOrderService groupOrderService;

    /**
     * 通过邀请 Token 获取团体基本信息（公开，成员可查看）
     */
    @GetMapping("/{token}")
    public Result<GroupInviteInfoVo> getGroupInfo(@PathVariable String token) {
        return Result.success(groupOrderService.getGroupInfoByToken(token));
    }

    /**
     * 成员通过邀请链接自助填写个人信息
     */
    @PostMapping("/{token}/fill")
    public Result<Void> fillMemberInfo(@PathVariable String token,
                                       @Valid @RequestBody FillMemberInfoDto dto) {
        groupOrderService.fillMemberByToken(token, dto);
        return Result.success("信息已提交，入会凭证将通过邮件发送至您的邮箱", null);
    }
}
