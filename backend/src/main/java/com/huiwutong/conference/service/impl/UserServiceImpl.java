package com.huiwutong.conference.service.impl;

import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.User;
import com.huiwutong.conference.mapper.UserMapper;
import com.huiwutong.conference.service.UserService;
import com.huiwutong.conference.service.dto.auth.UpdateUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 用户服务实现
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    @Override
    public User getByEmail(String email) {
        return userMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<User>()
                        .eq(User::getEmail, email));
    }

    @Override
    public void updateProfile(Long userId, UpdateUserDto dto) {
        User user = getById(userId);

        if (StringUtils.hasText(dto.getName())) {
            user.setName(dto.getName());
        }
        if (StringUtils.hasText(dto.getAvatar())) {
            user.setAvatar(dto.getAvatar());
        }
        if (StringUtils.hasText(dto.getNewPassword())) {
            if (!StringUtils.hasText(dto.getOldPassword())) {
                throw new BusinessException("修改密码需要提供旧密码");
            }
            if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
                throw new BusinessException(ErrorCode.PASSWORD_ERROR);
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        userMapper.updateById(user);
    }
}
