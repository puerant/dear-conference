package com.huiwutong.conference.service.dto.conference;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class ScheduleVenueDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotBlank(message = "会场名称不能为空")
    @Size(max = 200, message = "会场名称长度不能超过200个字符")
    private String name;

    @Size(max = 500, message = "会场地址长度不能超过500个字符")
    private String address;

    private String description;

    private Integer sortOrder;
}
