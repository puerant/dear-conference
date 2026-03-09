package com.huiwutong.conference.service.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会议信息 VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConferenceInfoVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String title;
    private String subtitle;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private String location;
    private String address;
    private String bannerUrl;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
    private Integer isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
