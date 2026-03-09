package com.huiwutong.conference.service.impl;

import com.huiwutong.conference.common.constant.RedisKey;
import com.huiwutong.conference.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 邮箱验证码服务实现
 */
@Service
@RequiredArgsConstructor
public class VerificationCodeServiceImpl implements VerificationCodeService {

    private static final long CODE_TTL_MINUTES = 15;
    private static final long RATE_LIMIT_SECONDS = 60;

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public String generateAndStore(String email) {
        String code = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
        stringRedisTemplate.opsForValue().set(
                RedisKey.VERIFY_CODE_PREFIX + email, code, CODE_TTL_MINUTES, TimeUnit.MINUTES);
        stringRedisTemplate.opsForValue().set(
                RedisKey.VERIFY_RATE_LIMIT_PREFIX + email, "1", RATE_LIMIT_SECONDS, TimeUnit.SECONDS);
        return code;
    }

    @Override
    public boolean verify(String email, String code) {
        String stored = stringRedisTemplate.opsForValue().get(RedisKey.VERIFY_CODE_PREFIX + email);
        if (stored != null && stored.equals(code)) {
            stringRedisTemplate.delete(RedisKey.VERIFY_CODE_PREFIX + email);
            return true;
        }
        return false;
    }

    @Override
    public boolean isRateLimited(String email) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(RedisKey.VERIFY_RATE_LIMIT_PREFIX + email));
    }
}
