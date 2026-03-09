package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.enums.UserRole;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.common.util.JwtUtil;
import com.huiwutong.conference.entity.User;
import com.huiwutong.conference.mapper.UserMapper;
import com.huiwutong.conference.service.AuthService;
import com.huiwutong.conference.service.EmailService;
import com.huiwutong.conference.service.VerificationCodeService;
import com.huiwutong.conference.service.dto.auth.LoginDto;
import com.huiwutong.conference.service.dto.auth.RegisterDto;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 认证服务实现
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VerificationCodeService verificationCodeService;
    private final EmailService emailService;

    @Override
    public LoginVo login(LoginDto dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getEmail, dto.getEmail()));

        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }
        if (user.getEmailVerified() == null || user.getEmailVerified() == 0) {
            throw new BusinessException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().getCode());
        UserInfoVo userInfoVo = new UserInfoVo(user.getId(), user.getEmail(), user.getName(),
                user.getRole().getCode(), user.getAvatar());
        return new LoginVo(token, userInfoVo);
    }

    @Override
    public void register(RegisterDto dto) {
        long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getEmail, dto.getEmail()));
        if (count > 0) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setRole(UserRole.ofCode(dto.getRole()));
        user.setStatus(1);
        user.setEmailVerified(0);
        userMapper.insert(user);

        String code = verificationCodeService.generateAndStore(dto.getEmail());
        emailService.sendVerificationCode(dto.getEmail(), code);
    }

    @Override
    public void sendVerificationCode(String email) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (verificationCodeService.isRateLimited(email)) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_TOO_FREQUENT);
        }
        String code = verificationCodeService.generateAndStore(email);
        emailService.sendVerificationCode(email, code);
    }

    @Override
    public LoginVo verifyEmail(String email, String code) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (!verificationCodeService.verify(email, code)) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_INVALID);
        }

        userMapper.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, user.getId())
                .set(User::getEmailVerified, 1));

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().getCode());
        UserInfoVo userInfoVo = new UserInfoVo(user.getId(), user.getEmail(), user.getName(),
                user.getRole().getCode(), user.getAvatar());
        return new LoginVo(token, userInfoVo);
    }

    @Override
    public void logout() {
        // JWT 无状态，客户端清除 token 即可
    }

    @Override
    public LoginVo refresh(String token) {
        Claims claims;
        try {
            claims = jwtUtil.getClaimsAllowExpired(token);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        // 宽限期：仅允许过期 24h 以内的 token 刷新
        Date expiredAt = claims.getExpiration();
        long graceMs = 24L * 60 * 60 * 1000;
        if (new Date().after(new Date(expiredAt.getTime() + graceMs))) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        Long userId = Long.valueOf(claims.get("userId").toString());
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        if (user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.USER_DISABLED);
        }

        String newToken = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().getCode());
        UserInfoVo userInfoVo = new UserInfoVo(user.getId(), user.getEmail(), user.getName(),
                user.getRole().getCode(), user.getAvatar());
        return new LoginVo(newToken, userInfoVo);
    }
}
