package com.huiwutong.conference.service.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 专家简要信息 VO（用于日程项中展示）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertSimpleVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String title;
    private String organization;
    private String avatarUrl;
}
