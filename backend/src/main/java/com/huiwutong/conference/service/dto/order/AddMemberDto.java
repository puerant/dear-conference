package com.huiwutong.conference.service.dto.order;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 负责人添加/修改成员 DTO
 */
@Data
public class AddMemberDto {

    @NotBlank(message = "成员姓名不能为空")
    @Size(max = 50, message = "成员姓名不能超过50个字符")
    private String memberName;

    @NotBlank(message = "成员邮箱不能为空")
    @Email(message = "成员邮箱格式不正确")
    private String memberEmail;
}
