package com.huiwutong.conference.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.common.constant.ErrorCode;
import com.huiwutong.conference.common.exception.BusinessException;
import com.huiwutong.conference.entity.Expert;
import com.huiwutong.conference.mapper.ExpertMapper;
import com.huiwutong.conference.service.ExpertService;
import com.huiwutong.conference.service.dto.conference.ExpertDto;
import com.huiwutong.conference.service.dto.conference.ExpertListVo;
import com.huiwutong.conference.service.dto.conference.ExpertVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 专家服务实现
 */
@Service
@RequiredArgsConstructor
public class ExpertServiceImpl implements ExpertService {

    private final ExpertMapper expertMapper;

    @Override
    public ExpertListVo getExpertListForPortal(Boolean isKeynoteOnly) {
        LambdaQueryWrapper<Expert> wrapper = new LambdaQueryWrapper<>();

        if (Boolean.TRUE.equals(isKeynoteOnly)) {
            wrapper.eq(Expert::getIsKeynote, 1);
        }

        wrapper.orderByDesc(Expert::getIsKeynote)
                .orderByAsc(Expert::getSortOrder);

        List<Expert> experts = expertMapper.selectList(wrapper);

        List<ExpertVo> keynoteSpeakers = new ArrayList<>();
        List<ExpertVo> speakers = new ArrayList<>();

        for (Expert expert : experts) {
            ExpertVo vo = toVo(expert);
            if (expert.getIsKeynote() != null && expert.getIsKeynote() == 1) {
                keynoteSpeakers.add(vo);
            } else {
                speakers.add(vo);
            }
        }

        return ExpertListVo.builder()
                .keynoteSpeakers(keynoteSpeakers)
                .speakers(speakers)
                .build();
    }

    @Override
    public List<ExpertVo> getExpertListForAdmin(String keyword, Integer isKeynote) {
        LambdaQueryWrapper<Expert> wrapper = new LambdaQueryWrapper<>();

        if (StrUtil.isNotBlank(keyword)) {
            wrapper.like(Expert::getName, keyword)
                    .or().like(Expert::getOrganization, keyword)
                    .or().like(Expert::getTitle, keyword);
        }

        if (isKeynote != null) {
            wrapper.eq(Expert::getIsKeynote, isKeynote);
        }

        wrapper.orderByDesc(Expert::getIsKeynote)
                .orderByAsc(Expert::getSortOrder);

        List<Expert> experts = expertMapper.selectList(wrapper);
        return experts.stream().map(this::toVo).toList();
    }

    @Override
    public ExpertVo getById(Long id) {
        Expert expert = expertMapper.selectById(id);
        if (expert == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "专家不存在");
        }
        return toVo(expert);
    }

    @Override
    @Transactional
    public Long create(ExpertDto dto) {
        Expert expert = new Expert();
        BeanUtil.copyProperties(dto, expert);
        if (expert.getIsKeynote() == null) {
            expert.setIsKeynote(0);
        }
        if (expert.getSortOrder() == null) {
            expert.setSortOrder(0);
        }
        expertMapper.insert(expert);
        return expert.getId();
    }

    @Override
    @Transactional
    public void update(Long id, ExpertDto dto) {
        Expert expert = expertMapper.selectById(id);
        if (expert == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "专家不存在");
        }
        BeanUtil.copyProperties(dto, expert, "id");
        expertMapper.updateById(expert);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Expert expert = expertMapper.selectById(id);
        if (expert == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "专家不存在");
        }
        expertMapper.deleteById(id);
    }

    @Override
    @Transactional
    public void updateAvatar(Long id, String avatarUrl) {
        Expert expert = expertMapper.selectById(id);
        if (expert == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "专家不存在");
        }
        expert.setAvatarUrl(avatarUrl);
        expertMapper.updateById(expert);
    }

    private ExpertVo toVo(Expert entity) {
        return ExpertVo.builder()
                .id(entity.getId())
                .name(entity.getName())
                .title(entity.getTitle())
                .organization(entity.getOrganization())
                .bio(entity.getBio())
                .avatarUrl(entity.getAvatarUrl())
                .isKeynote(entity.getIsKeynote())
                .sortOrder(entity.getSortOrder())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
