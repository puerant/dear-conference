package com.huiwutong.conference.service;

import com.huiwutong.conference.service.dto.auth.LoginDto;
import com.huiwutong.conference.service.dto.auth.RegisterDto;

/**
 * 认证服务接口
 */
public interface AuthService {

    LoginVo login(LoginDto dto);

    /** 注册：创建账号并发送验证码，不返回 token（须先验证邮箱） */
    void register(RegisterDto dto);

    /** 重新发送邮箱验证码 */
    void sendVerificationCode(String email);

    /** 验证邮箱验证码，通过后返回登录凭证 */
    LoginVo verifyEmail(String email, String code);

    /** 使用旧 token（允许已过期）换取新 token，宽限期 24h */
    LoginVo refresh(String token);

    void logout();

    record LoginVo(String token, UserInfoVo userInfo) {}

    record UserInfoVo(Long id, String email, String name, String role, String avatar) {}
}

