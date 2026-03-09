package com.huiwutong.conference.controller.auth;

import com.huiwutong.conference.common.annotation.RequireLogin;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.common.vo.Result;
import com.huiwutong.conference.service.AuthService;
import com.huiwutong.conference.service.dto.auth.LoginDto;
import com.huiwutong.conference.service.dto.auth.RegisterDto;
import com.huiwutong.conference.service.dto.auth.SendCodeDto;
import com.huiwutong.conference.service.dto.auth.VerifyEmailDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<AuthService.LoginVo> login(@Valid @RequestBody LoginDto dto) {
        return Result.success(authService.login(dto));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDto dto) {
        authService.register(dto);
        return Result.success("注册成功，验证码已发送到您的邮箱", null);
    }

    @PostMapping("/send-code")
    public Result<Void> sendCode(@Valid @RequestBody SendCodeDto dto) {
        authService.sendVerificationCode(dto.getEmail());
        return Result.success("验证码已发送", null);
    }

    @PostMapping("/verify-email")
    public Result<AuthService.LoginVo> verifyEmail(@Valid @RequestBody VerifyEmailDto dto) {
        return Result.success(authService.verifyEmail(dto.getEmail(), dto.getCode()));
    }

    /**
     * 刷新 Token —— 不加 @RequireLogin，因为 token 可能已过期
     * 过期 24h 内仍可换新 token，超过则需重新登录
     */
    @PostMapping("/refresh")
    public Result<AuthService.LoginVo> refresh(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }
        return Result.success(authService.refresh(authHeader.substring(7)));
    }

    @PostMapping("/logout")
    @RequireLogin
    public Result<Void> logout() {
        authService.logout();
        return Result.success();
    }
}
