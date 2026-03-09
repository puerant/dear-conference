package com.huiwutong.conference.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.ConferenceInfo;
import com.huiwutong.conference.mapper.ConferenceInfoMapper;
import com.huiwutong.conference.service.ConferenceService;
import com.huiwutong.conference.service.dto.conference.ConferenceInfoDto;
import com.huiwutong.conference.service.dto.conference.ConferenceInfoVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

/**
 * 会议信息服务实现
 */
@Service
@RequiredArgsConstructor
public class ConferenceServiceImpl implements ConferenceService {

    private static final String CACHE_KEY = "conference:info";

    private final ConferenceInfoMapper conferenceInfoMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public ConferenceInfoVo getPublishedConferenceInfo() {
        // 先从缓存获取
        ConferenceInfoVo cached = (ConferenceInfoVo) redisTemplate.opsForValue().get(CACHE_KEY);
        if (cached != null) {
            return cached;
        }

        // 查询已发布的会议信息
        LambdaQueryWrapper<ConferenceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ConferenceInfo::getIsPublished, 1);
        wrapper.orderByDesc(ConferenceInfo::getUpdatedAt);
        wrapper.last("LIMIT 1");

        ConferenceInfo conferenceInfo = conferenceInfoMapper.selectOne(wrapper);
        if (conferenceInfo == null) {
            return null;
        }

        ConferenceInfoVo vo = toVo(conferenceInfo);
        // 缓存1小时
        redisTemplate.opsForValue().set(CACHE_KEY, vo, 1, TimeUnit.HOURS);
        return vo;
    }

    @Override
    public ConferenceInfoVo getConferenceInfo() {
        // 管理端获取最新的会议信息（无论是否发布）
        LambdaQueryWrapper<ConferenceInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ConferenceInfo::getUpdatedAt);
        wrapper.last("LIMIT 1");

        ConferenceInfo conferenceInfo = conferenceInfoMapper.selectOne(wrapper);
        if (conferenceInfo == null) {
            return null;
        }
        return toVo(conferenceInfo);
    }

    @Override
    @Transactional
    public void saveOrUpdate(ConferenceInfoDto dto) {
        // 验证日期
        if (dto.getEndDate() != null && dto.getStartDate() != null) {
            if (dto.getEndDate().isBefore(dto.getStartDate())) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "结束日期不能早于开始日期");
            }
        }

        ConferenceInfo conferenceInfo;
        if (dto.getId() != null) {
            // 更新
            conferenceInfo = conferenceInfoMapper.selectById(dto.getId());
            if (conferenceInfo == null) {
                throw new BusinessException(ErrorCode.NOT_FOUND, "会议信息不存在");
            }
            BeanUtil.copyProperties(dto, conferenceInfo, "id");
            conferenceInfoMapper.updateById(conferenceInfo);
        } else {
            // 新增
            conferenceInfo = new ConferenceInfo();
            BeanUtil.copyProperties(dto, conferenceInfo);
            if (conferenceInfo.getIsPublished() == null) {
                conferenceInfo.setIsPublished(0);
            }
            conferenceInfoMapper.insert(conferenceInfo);
        }

        // 清除缓存
        redisTemplate.delete(CACHE_KEY);
    }

    private ConferenceInfoVo toVo(ConferenceInfo entity) {
        return ConferenceInfoVo.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .subtitle(entity.getSubtitle())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .location(entity.getLocation())
                .address(entity.getAddress())
                .bannerUrl(entity.getBannerUrl())
                .contactName(entity.getContactName())
                .contactPhone(entity.getContactPhone())
                .contactEmail(entity.getContactEmail())
                .isPublished(entity.getIsPublished())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
