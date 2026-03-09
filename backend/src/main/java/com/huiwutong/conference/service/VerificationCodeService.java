package com.huiwutong.conference.service;

/**
 * 邮箱验证码服务
 */
public interface VerificationCodeService {

    /**
     * 生成6位验证码并存入 Redis（TTL 15分钟）
     * @return 生成的验证码
     */
    String generateAndStore(String email);

    /**
     * 验证验证码是否正确，正确则删除 Redis 中的记录
     */
    boolean verify(String email, String code);

    /**
     * 检查发送频率是否超限（60秒内只能发1次）
     */
    boolean isRateLimited(String email);
}
