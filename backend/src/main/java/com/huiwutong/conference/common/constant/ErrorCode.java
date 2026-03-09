package com.huiwutong.conference.common.constant;

/**
 * 错误码常量
 */
public enum ErrorCode {

    // 通用错误码
    SUCCESS(200, "操作成功"),
    BUSINESS_ERROR(400, "业务异常"),
    UNAUTHORIZED(401, "未授权，请登录"),
    FORBIDDEN(403, "拒绝访问"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(422, "参数错误"),
    INTERNAL_ERROR(500, "系统内部错误"),

    // 认证相关
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_INVALID(1004, "Token 无效或已过期"),
    USER_DISABLED(1005, "账号已被禁用"),
    EMAIL_NOT_VERIFIED(1006, "邮箱未验证，请先完成邮箱验证"),
    VERIFY_CODE_INVALID(1007, "验证码无效或已过期"),
    VERIFY_CODE_TOO_FREQUENT(1008, "验证码发送过于频繁，请稍后再试"),

    // 投稿相关
    SUBMISSION_NOT_FOUND(2001, "投稿不存在"),
    SUBMISSION_STATUS_ERROR(2002, "投稿状态错误"),
    SUBMISSION_DELETE_DENIED(2003, "当前状态不允许删除"),
    SUBMISSION_ACCESS_DENIED(2004, "无权访问该投稿"),

    // 订单相关
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态错误"),
    TICKET_NOT_AVAILABLE(3003, "票务库存不足"),
    ORDER_AMOUNT_ERROR(3004, "订单金额错误"),
    ORDER_ACCESS_DENIED(3005, "无权访问该订单"),
    TICKET_TYPE_MISMATCH(3008, "票种类型与操作不匹配"),
    INDIVIDUAL_TICKET_SINGLE_ONLY(3009, "个人票每次仅限购买1张"),
    GROUP_QUANTITY_INVALID(3010, "购买人数不符合团体票要求"),
    GROUP_MEMBER_FULL(3011, "团体成员信息已全部填写，无空余槽位"),
    INVITE_TOKEN_INVALID(3012, "邀请链接无效或已过期"),
    GROUP_NOT_PAID(3013, "团体订单尚未支付，暂不可填写成员信息"),

    // 票务相关
    TICKET_NOT_FOUND(4001, "票务不存在"),
    TICKET_DISABLED(4002, "票务已下架"),
    TICKET_NAME_TOO_LONG(4003, "票务名称过长"),
    TICKET_PRICE_INVALID(4004, "票务价格无效"),
    TICKET_STOCK_INSUFFICIENT(4005, "库存不足"),
    TICKET_SOLD_CANT_DELETE(4006, "票务已售出不能删除"),
    STOCK_ADJUST_INVALID(4007, "库存调整无效"),
    TICKET_HAS_PENDING_ORDERS(4008, "票务有未完成订单不能下架"),

    // 审稿相关
    REVIEW_NOT_FOUND(5001, "审稿记录不存在"),
    REVIEW_ALREADY_EXISTS(5002, "已提交过审稿意见"),
    REVIEW_ACCESS_DENIED(5003, "无权进行审稿"),

    // 凭证相关
    CREDENTIAL_NOT_FOUND(6001, "凭证不存在"),
    CREDENTIAL_ALREADY_USED(6002, "凭证已使用"),
    CREDENTIAL_EXPIRED(6003, "凭证已过期"),
    CREDENTIAL_ACCESS_DENIED(6004, "无权访问该凭证"),
    CREDENTIAL_TIME_EXPIRED(6005, "凭证已超过有效期"),

    // 支付相关
    PAYMENT_PROVIDER_NOT_FOUND(7001, "不支持的支付方式"),
    PAYMENT_PROVIDER_ERROR(7002, "支付渠道异常，请稍后重试"),
    PAYMENT_AMOUNT_MISMATCH(7003, "支付金额与订单不符"),

    // 系统模块相关
    EMAIL_CONFIG_NOT_ENABLED(8001, "邮箱配置未启用"),
    EMAIL_SEND_FAILED(8002, "邮件发送失败"),
    FILE_NOT_FOUND(8003, "文件不存在"),
    FILE_EMPTY(8004, "文件不能为空"),
    FILE_TOO_LARGE(8005, "文件大小超过限制"),
    FILE_TYPE_NOT_ALLOWED(8006, "不支持的文件类型"),
    FILE_STORAGE_FAILED(8007, "文件存储失败"),

    // 邮件模板相关
    EMAIL_TEMPLATE_NOT_FOUND(8010, "邮件模板不存在"),
    EMAIL_TEMPLATE_DISABLED(8011, "邮件模板已禁用"),
    EMAIL_TEMPLATE_NO_CONFIG(8012, "模板无可用邮箱账号配置"),
    EMAIL_CONFIG_NOT_FOUND(8013, "邮箱配置不存在"),
    EMAIL_CONFIG_DISABLED(8014, "邮箱配置已禁用"),
    EMAIL_CONFIG_DEFAULT_REQUIRED(8015, "必须保留至少一个默认邮箱配置"),
    EMAIL_SEND_LOG_NOT_FOUND(8016, "邮件发送日志不存在"),

    // 文件模块（多存储）相关
    STORAGE_PROVIDER_NOT_FOUND(8101, "存储供应商不存在"),
    STORAGE_PROVIDER_DISABLED(8102, "存储供应商已禁用"),
    STORAGE_BUCKET_NOT_FOUND(8103, "存储桶不存在"),
    STORAGE_BUCKET_DISABLED(8104, "存储桶已禁用"),
    FILE_STORAGE_TARGET_NOT_FOUND(8105, "未找到可用存储目标"),
    STORAGE_TEST_CONNECT_FAILED(8106, "存储连接测试失败"),
    FILE_MIGRATION_TASK_NOT_FOUND(8107, "迁移任务不存在"),
    FILE_MIGRATION_EXECUTE_FAILED(8108, "迁移执行失败"),

    // 会议模块相关
    CONFERENCE_INFO_NOT_FOUND(8201, "会议信息不存在"),
    CONFERENCE_NOT_PUBLISHED(8202, "会议信息未发布"),
    EXPERT_NOT_FOUND(8203, "专家不存在"),
    SCHEDULE_NOT_FOUND(8204, "日程不存在"),
    SCHEDULE_ITEM_NOT_FOUND(8205, "日程项不存在"),
    HOTEL_NOT_FOUND(8206, "酒店不存在"),
    HOTEL_ROOM_NOT_FOUND(8207, "房型不存在");

    private final Integer code;
    private final String message;

    ErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
