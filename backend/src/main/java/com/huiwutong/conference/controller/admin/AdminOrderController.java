package com.huiwutong.conference.controller.admin;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.Order;
import com.huiwutong.conference.service.GroupOrderService;
import com.huiwutong.conference.service.OrderService;
import com.huiwutong.conference.service.PaymentService;
import com.huiwutong.conference.service.dto.order.GroupMemberVo;
import com.huiwutong.conference.service.dto.order.OrderQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端 - 订单管理控制器
 */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final GroupOrderService groupOrderService;

    @GetMapping
    @RequireRole("admin")
    public Result<PageResult<Order>> list(OrderQueryDto query) {
        return Result.success(orderService.listAll(query));
    }

    @GetMapping("/{id}")
    @RequireRole("admin")
    public Result<Order> detail(@PathVariable Long id) {
        return Result.success(orderService.getById(id));
    }

    @PostMapping("/{id}/refund")
    @RequireRole("admin")
    public Result<Void> refund(@PathVariable Long id) {
        // 通过 PaymentService 完成退款（调用渠道 API + 更新订单/凭证/库存）
        paymentService.refund(id);
        return Result.success("退款成功", null);
    }

    /**
     * 管理员查看团体订单的成员列表
     */
    @GetMapping("/{id}/group/members")
    @RequireRole("admin")
    public Result<java.util.List<GroupMemberVo>> getGroupMembers(@PathVariable Long id) {
        // admin 传 null 作为 userId，GroupOrderServiceImpl 需处理 null 时跳过权限校验
        return Result.success(groupOrderService.getGroupDetail(id, null).getMembers());
    }
}
