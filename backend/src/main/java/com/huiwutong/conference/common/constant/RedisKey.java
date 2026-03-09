package com.huiwutong.conference.common.constant;

/**
 * Redis 缓存 key 常量
 */
public class RedisKey {

    private RedisKey() {}

    /** 字典缓存前缀，格式: dict:{dictCode} */
    public static final String DICT_PREFIX = "dict:";

    /** 用户信息缓存前缀，格式: user:{userId} */
    public static final String USER_PREFIX = "user:";

    /** 票务库存缓存前缀，格式: ticket:stock:{ticketId} */
    public static final String TICKET_STOCK_PREFIX = "ticket:stock:";

    /** 邮箱验证码前缀，格式: verify:code:{email}，TTL 15分钟 */
    public static final String VERIFY_CODE_PREFIX = "verify:code:";

    /** 验证码发送频率限制前缀，格式: verify:limit:{email}，TTL 60秒 */
    public static final String VERIFY_RATE_LIMIT_PREFIX = "verify:limit:";

    public static String dictKey(String dictCode) {
        return DICT_PREFIX + dictCode;
    }

    public static String userKey(Long userId) {
        return USER_PREFIX + userId;
    }

    public static String ticketStockKey(Long ticketId) {
        return TICKET_STOCK_PREFIX + ticketId;
    }
}
