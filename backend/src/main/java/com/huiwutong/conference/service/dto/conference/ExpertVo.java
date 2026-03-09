package com.huiwutong.conference.service.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 专家 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String title;
    private String organization;
    private String bio;
    private String avatarUrl;
    private Integer isKeynote;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
