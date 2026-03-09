package com.huiwutong.conference.service.dto.conference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 专家列表 VO（分主旨演讲嘉宾和普通嘉宾）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpertListVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主旨演讲嘉宾列表
     */
    private List<ExpertVo> keynoteSpeakers;

    /**
     * 普通嘉宾列表
     */
    private List<ExpertVo> speakers;
}
