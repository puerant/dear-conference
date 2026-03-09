package com.huiwutong.conference.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huiwutong.conference.common.annotation.RequireRole;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.enums.UserRole;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.common.vo.PageResult;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.User;
import com.huiwutong.conference.mapper.UserMapper;
import com.huiwutong.conference.service.dto.admin.AdminCreateUserDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 管理端 - 用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    @RequireRole("admin")
    public Result<PageResult<User>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {

        Page<User> pageObj = new Page<>(page, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreatedAt);

        if (role != null && !role.isBlank()) {
            UserRole userRole = UserRole.ofCode(role);
            if (userRole != null) {
                wrapper.eq(User::getRole, userRole);
            }
        }
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(User::getName, keyword).or().like(User::getEmail, keyword));
        }

        userMapper.selectPage(pageObj, wrapper);
        // 清除密码字段
        pageObj.getRecords().forEach(u -> u.setPassword(null));
        return Result.success(PageResult.of(pageObj));
    }

    @PostMapping
    @RequireRole("admin")
    public Result<Void> create(@Valid @RequestBody AdminCreateUserDto dto) {
        // 检查邮箱是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getEmail, dto.getEmail()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        UserRole userRole = UserRole.ofCode(dto.getRole());
        if (userRole == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "角色值不合法");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setRole(userRole);
        user.setStatus(1);
        user.setEmailVerified(1);
        userMapper.insert(user);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    @RequireRole("admin")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        user.setStatus(status);
        userMapper.updateById(user);
        return Result.success();
    }

    @PutMapping("/{id}/role")
    @RequireRole("admin")
    public Result<Void> updateRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String roleCode = body.get("role");
        UserRole userRole = UserRole.ofCode(roleCode);
        if (userRole == null) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "角色值不合法");
        }

        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        user.setRole(userRole);
        userMapper.updateById(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @RequireRole("admin")
    public Result<Void> delete(@PathVariable Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        userMapper.deleteById(id);
        return Result.success();
    }
}
