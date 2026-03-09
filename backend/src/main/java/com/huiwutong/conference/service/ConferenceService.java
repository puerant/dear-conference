package com.huiwutong.conference.service;

import com.huiwutong.conference.service.dto.conference.ConferenceInfoDto;
import com.huiwutong.conference.service.dto.conference.ConferenceInfoVo;

/**
 * 会议信息服务接口
 */
public interface ConferenceService {

    /**
     * 获取已发布的会议信息（用户端）
     */
    ConferenceInfoVo getPublishedConferenceInfo();

    /**
     * 获取会议信息（管理端，无论是否发布）
     */
    ConferenceInfoVo getConferenceInfo();

    /**
     * 保存或更新会议信息
     */
    void saveOrUpdate(ConferenceInfoDto dto);
}
