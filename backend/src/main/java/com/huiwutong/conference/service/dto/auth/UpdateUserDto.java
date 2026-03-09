package com.huiwutong.conference.service.dto.auth;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 更新用户信息 DTO
 */
@Data
public class UpdateUserDto {

    @Size(max = 50, message = "姓名不能超过50个字符")
    private String name;

    private String avatar;

    @Size(min = 6, max = 20, message = "密码长度为6-20位")
    private String newPassword;

    private String oldPassword;
}
