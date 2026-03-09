package com.huiwutong.conference.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huiwutong.conference.entity.CredentialConfig;
import com.huiwutong.conference.mapper.CredentialConfigMapper;
import com.huiwutong.conference.service.CredentialConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 凭证配置服务实现
 */
@Service
@RequiredArgsConstructor
public class CredentialConfigServiceImpl implements CredentialConfigService {

    private final CredentialConfigMapper credentialConfigMapper;

    @Override
    public CredentialConfig getConfig() {
        return credentialConfigMapper.selectOne(
            new LambdaQueryWrapper<CredentialConfig>()
                .last("LIMIT 1")
        );
    }

    @Override
    @Transactional
    public void saveOrUpdate(CredentialConfig config) {
        CredentialConfig existing = credentialConfigMapper.selectOne(
            new LambdaQueryWrapper<CredentialConfig>()
                .last("LIMIT 1")
        );

        if (existing == null) {
            credentialConfigMapper.insert(config);
        } else {
            config.setId(existing.getId());
            credentialConfigMapper.updateById(config);
        }
    }
}
