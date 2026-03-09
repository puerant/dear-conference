package com.huiwutong.conference.controller.auth;

import com.huiwutong.conference.common.annotation.RequireLogin;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.entity.User;
import com.huiwutong.conference.service.UserService;
import com.huiwutong.conference.service.dto.auth.UpdateUserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @RequireLogin
    public Result<User> getProfile(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        user.setPassword(null);
        return Result.success(user);
    }

    @PutMapping("/profile")
    @RequireLogin
    public Result<Void> updateProfile(@Valid @RequestBody UpdateUserDto dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, dto);
        return Result.success();
    }
}
