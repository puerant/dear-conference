package com.huiwutong.conference.service;

import com.huiwutong.conference.service.dto.conference.ExpertDto;
import com.huiwutong.conference.service.dto.conference.ExpertListVo;
import com.huiwutong.conference.service.dto.conference.ExpertVo;

import java.util.List;

/**
 * 专家服务接口
 */
public interface ExpertService {

    /**
     * 获取专家列表（用户端，分主旨/普通）
     */
    ExpertListVo getExpertListForPortal(Boolean isKeynoteOnly);

    /**
     * 获取专家列表（管理端）
     */
    List<ExpertVo> getExpertListForAdmin(String keyword, Integer isKeynote);

    /**
     * 获取专家详情
     */
    ExpertVo getById(Long id);

    /**
     * 创建专家
     */
    Long create(ExpertDto dto);

    /**
     * 更新专家
     */
    void update(Long id, ExpertDto dto);

    /**
     * 删除专家
     */
    void delete(Long id);

    /**
     * 更新专家头像
     */
    void updateAvatar(Long id, String avatarUrl);
}
