package com.huiwutong.conference.service.dto.order;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 成员通过邀请链接自助填写 DTO
 */
@Data
public class FillMemberInfoDto {

    @NotBlank(message = "姓名不能为空")
    @Size(max = 50, message = "姓名不能超过50个字符")
    private String memberName;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String memberEmail;
}
