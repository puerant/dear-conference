package com.huiwutong.conference.controller.system;

import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.OrderTaskConfig;
import com.huiwutong.conference.service.OrderTaskConfigService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class OrderTaskConfigController {

    private final OrderTaskConfigService orderTaskConfigService;

    @PostMapping("/order-task-config")
    @RequireRole({"admin"})
    public Result<Void> saveConfig(@Valid @RequestBody OrderTaskConfig config) {
        orderTaskConfigService.saveOrUpdate(config);
        return Result.success();
    }
}
