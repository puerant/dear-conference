package com.huiwutong.conference.service;

import com.huiwutong.conference.entity.User;
import com.huiwutong.conference.service.dto.auth.UpdateUserDto;

/**
 * 用户服务接口
 */
public interface UserService {

    User getById(Long id);

    User getByEmail(String email);

    void updateProfile(Long userId, UpdateUserDto dto);
}
