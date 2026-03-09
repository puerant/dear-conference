package com.huiwutong.conference.task;

import com.huiwutong.conference.mapper.CredentialMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 凭证定时过期任务
 * 每天凌晨 1:00 执行，将超过 expire_at 的 VALID 凭证批量置为 EXPIRED
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CredentialExpireTask {

    private final CredentialMapper credentialMapper;

    @Scheduled(cron = "0 0 1 * * ?")
    public void expireOutdatedCredentials() {
        int count = credentialMapper.expireOutdated(LocalDateTime.now());
        if (count > 0) {
            log.info("[CredentialExpireTask] 本次共过期凭证 {} 张", count);
        }
    }
}
