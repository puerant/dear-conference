-- ========================================
-- 数据库和基础表初始化脚本
-- 数据库名: mclc
-- 文档版本: v1.0
-- 最后更新: 2026-03-03
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `mclc` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `mclc`;

-- ========================================
-- 1. 字典表 (dictionary)
-- ========================================
CREATE TABLE IF NOT EXISTS `dictionary` (
                                            `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                            `dict_code` VARCHAR(50) NOT NULL COMMENT '字典编码，唯一',
                                            `dict_name` VARCHAR(100) NOT NULL COMMENT '字典名称',
                                            `description` VARCHAR(255) COMMENT '字典描述',
                                            `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1-启用,0-禁用',
                                            `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
                                            `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                            `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                            PRIMARY KEY (`id`),
                                            UNIQUE KEY `uk_dict_code` (`dict_code`),
                                            KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典表';

-- ========================================
-- 2. 字典项表 (dictionary_item)
-- ========================================
CREATE TABLE IF NOT EXISTS `dictionary_item` (
                                                 `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                                 `dict_id` BIGINT NOT NULL COMMENT '字典ID',
                                                 `item_code` VARCHAR(50) NOT NULL COMMENT '字典项编码',
                                                 `item_name` VARCHAR(100) NOT NULL COMMENT '字典项名称',
                                                 `item_value` VARCHAR(50) NOT NULL COMMENT '字典项值',
                                                 `description` VARCHAR(255) COMMENT '字典项描述',
                                                 `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1-启用,0-禁用',
                                                 `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
                                                 `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                                 `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                                 PRIMARY KEY (`id`),
                                                 KEY `idx_dict_id` (`dict_id`),
                                                 KEY `idx_item_code` (`item_code`),
                                                 KEY `idx_status` (`status`),
                                                 UNIQUE KEY `uk_dict_code_item_code` (`dict_id`, `item_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典项表';

-- ========================================
-- 3. 用户表 (users)
-- ========================================
CREATE TABLE IF NOT EXISTS `users` (
                                       `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                       `email` VARCHAR(100) NOT NULL COMMENT '邮箱',
                                       `password` VARCHAR(255) NOT NULL COMMENT '密码(加密)',
                                       `name` VARCHAR(50) NOT NULL COMMENT '姓名',
                                       `role` TINYINT NOT NULL DEFAULT 3 COMMENT '角色:1-speaker,2-reviewer,3-attendee,4-admin',
                                       `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1-active,0-disabled',
                                       `avatar` VARCHAR(255) COMMENT '头像',
                                       `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uk_email` (`email`),
                                       KEY `idx_role` (`role`),
                                       KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ========================================
-- 4. 投稿表 (submissions)
-- ========================================
CREATE TABLE IF NOT EXISTS `submissions` (
                                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                             `user_id` BIGINT NOT NULL COMMENT '讲者ID',
                                             `title` VARCHAR(200) NOT NULL COMMENT '投稿标题',
                                             `abstract` TEXT COMMENT '摘要',
                                             `category` VARCHAR(50) COMMENT '投稿分类',
                                             `file_url` VARCHAR(255) COMMENT '附件URL',
                                             `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1-pending,2-reviewing,3-accepted,4-rejected',
                                             `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                             PRIMARY KEY (`id`),
                                             KEY `idx_user_id` (`user_id`),
                                             KEY `idx_status` (`status`),
                                             KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='投稿表';

-- ========================================
-- 5. 审稿记录表 (reviews)
-- ========================================
CREATE TABLE IF NOT EXISTS `reviews` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `submission_id` BIGINT NOT NULL COMMENT '投稿ID',
                                        `reviewer_id` BIGINT NOT NULL COMMENT '审稿人ID',
                                        `comment` TEXT COMMENT '审稿意见',
                                        `result` TINYINT COMMENT '审稿结果:1-accept,2-reject,3-revise',
                                        `score` INT COMMENT '评分(0-100)',
                                        `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_submission_id` (`submission_id`),
                                        KEY `idx_reviewer_id` (`reviewer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审稿记录表';

-- ========================================
-- 6. 票务表 (tickets)
-- ========================================
CREATE TABLE IF NOT EXISTS `tickets` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `name` VARCHAR(100) NOT NULL COMMENT '票务名称',
                                        `price` DECIMAL(10, 2) NOT NULL COMMENT '价格',
                                        `description` TEXT COMMENT '描述',
                                        `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
                                        `sold_count` INT NOT NULL DEFAULT 0 COMMENT '已售数量',
                                        `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1-active,0-disabled',
                                        `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='票务表';

-- ========================================
-- 7. 订单表 (orders)
-- ========================================
CREATE TABLE IF NOT EXISTS `orders` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                        `order_no` VARCHAR(32) NOT NULL COMMENT '订单号',
                                        `user_id` BIGINT NOT NULL COMMENT '用户ID',
                                        `ticket_id` BIGINT NOT NULL COMMENT '票务ID',
                                        `quantity` INT NOT NULL COMMENT '数量',
                                        `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '总金额',
                                        `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1-unpaid,2-paid,3-cancelled,4-refunded',
                                        `attendee_name` VARCHAR(50) COMMENT '参会人姓名',
                                        `attendee_email` VARCHAR(100) COMMENT '参会人邮箱',
                                        `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                        `paid_at` DATETIME COMMENT '支付时间',
                                        PRIMARY KEY (`id`),
                                        UNIQUE KEY `uk_order_no` (`order_no`),
                                        KEY `idx_user_id` (`user_id`),
                                        KEY `idx_ticket_id` (`ticket_id`),
                                        KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- ========================================
-- 8. 入会凭证表 (credentials)
-- ========================================
CREATE TABLE IF NOT EXISTS `credentials` (
                                             `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                             `order_id` BIGINT NOT NULL COMMENT '订单ID',
                                             `credential_no` VARCHAR(32) NOT NULL COMMENT '凭证号',
                                             `qr_code` VARCHAR(500) COMMENT '二维码数据',
                                             `seat_info` VARCHAR(50) COMMENT '座位信息',
                                             `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态:1-valid,2-used,3-expired',
                                             `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                             `used_at` DATETIME COMMENT '使用时间',
                                             PRIMARY KEY (`id`),
                                             UNIQUE KEY `uk_credential_no` (`credential_no`),
                                             KEY `idx_order_id` (`order_id`),
                                             KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='入会凭证表';

-- ========================================
-- 9. 支付记录表 (payment_records)
-- ========================================
CREATE TABLE IF NOT EXISTS `payment_records` (
                                                 `id`                BIGINT          NOT NULL AUTO_INCREMENT                          COMMENT '主键ID',
                                                 `payment_no`        VARCHAR(64)     NOT NULL                                         COMMENT '内部支付单号（PAY+时间戳+随机数）',
                                                 `order_id`          BIGINT          NOT NULL                                         COMMENT '关联订单ID',
                                                 `order_no`          VARCHAR(64)     NOT NULL                                         COMMENT '订单号（冗余，方便对账）',
                                                 `provider`          VARCHAR(20)     NOT NULL                                         COMMENT '支付渠道:wechat/alipay/unionpay/paypal',
                                                 `provider_trade_no` VARCHAR(128)                                                     COMMENT '第三方交易流水号（回调后填入）',
                                                 `amount`            DECIMAL(10, 2)  NOT NULL                                         COMMENT '支付金额（CNY）',
                                                 `currency`          VARCHAR(10)     NOT NULL    DEFAULT 'CNY'                        COMMENT '实际支付货币',
                                                 `exchange_rate`     DECIMAL(10, 4)                                                   COMMENT 'CNY→目标货币汇率（PayPal使用）',
                                                 `foreign_amount`    DECIMAL(10, 2)                                                   COMMENT '外币金额（PayPal，单位USD）',
                                                 `status`            TINYINT         NOT NULL    DEFAULT 1                            COMMENT '状态:1-pending,2-success,3-failed,4-closed,5-refunded',
                                                 `pay_url`           TEXT                                                             COMMENT '支付跳转链接（Alipay/UnionPay/PayPal）',
                                                 `qr_code`           TEXT                                                             COMMENT '二维码内容（WeChat Native Pay code_url）',
                                                 `expire_at`         DATETIME                                                         COMMENT '支付超时时间（默认15分钟）',
                                                 `notify_raw`        MEDIUMTEXT                                                       COMMENT '回调原始报文（留存备查）',
                                                 `created_at`        DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP            COMMENT '创建时间',
                                                 `paid_at`           DATETIME                                                         COMMENT '支付成功时间',
                                                 PRIMARY KEY (`id`),
                                                 UNIQUE KEY `uk_payment_no` (`payment_no`),
                                                 KEY `idx_order_id` (`order_id`),
                                                 KEY `idx_provider_trade_no` (`provider_trade_no`),
                                                 KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付记录表';

-- ========================================
-- 10. 退款记录表 (refund_records)
-- ========================================
CREATE TABLE IF NOT EXISTS `refund_records` (
                                                `id`                 BIGINT          NOT NULL AUTO_INCREMENT                          COMMENT '主键ID',
                                                `refund_no`          VARCHAR(64)     NOT NULL                                         COMMENT '内部退款单号（REF+时间戳）',
                                                `payment_no`         VARCHAR(64)     NOT NULL                                         COMMENT '关联支付单号',
                                                `order_id`           BIGINT          NOT NULL                                         COMMENT '关联订单ID',
                                                `provider`           VARCHAR(20)     NOT NULL                                         COMMENT '支付渠道:wechat/alipay/unionpay/paypal',
                                                `provider_refund_no` VARCHAR(128)                                                     COMMENT '第三方退款流水号',
                                                `amount`             DECIMAL(10, 2)  NOT NULL                                         COMMENT '退款金额（CNY）',
                                                `status`             TINYINT         NOT NULL    DEFAULT 1                            COMMENT '状态:1-processing,2-success,3-failed',
                                                `reason`             VARCHAR(255)                                                     COMMENT '退款原因',
                                                `created_at`         DATETIME        NOT NULL    DEFAULT CURRENT_TIMESTAMP            COMMENT '申请时间',
                                                `refunded_at`        DATETIME                                                         COMMENT '退款到账时间',
                                                PRIMARY KEY (`id`),
                                                UNIQUE KEY `uk_refund_no` (`refund_no`),
                                                KEY `idx_payment_no` (`payment_no`),
                                                KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='退款记录表';

-- ========================================
-- 插入字典数据
-- ========================================

-- 用户角色字典 (user_role)
INSERT INTO `dictionary` (`dict_code`, `dict_name`, `description`, `sort_order`) VALUES
                                                                                     ('user_role', '用户角色', '系统用户角色类型', 1),
                                                                                     ('user_status', '用户状态', '用户账号状态', 2),
                                                                                     ('submission_status', '投稿状态', '投稿审核状态', 3),
                                                                                     ('review_result', '审稿结果', '审稿人评审结果', 4),
                                                                                     ('ticket_status', '票务状态', '票务上架状态', 5),
                                                                                     ('order_status', '订单状态', '订单支付状态', 6),
                                                                                     ('credential_status', '凭证状态', '入会凭证状态', 7),
                                                                                     ('common_status', '通用状态', '通用启用/禁用状态', 8);

-- 用户角色字典项 (user_role)
INSERT INTO `dictionary_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
                                                                                                    (1, 'speaker', '投稿讲者', '1', 1),
                                                                                                    (1, 'reviewer', '审稿人', '2', 2),
                                                                                                    (1, 'attendee', '参会者', '3', 3),
                                                                                                    (1, 'admin', '管理员', '4', 4);

-- 用户状态字典项 (user_status)
INSERT INTO `dictionary_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
                                                                                                    (2, 'active', '激活', '1', 1),
                                                                                                    (2, 'disabled', '禁用', '0', 2);

-- 投稿状态字典项 (submission_status)
INSERT INTO `dictionary_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
                                                                                                    (3, 'pending', '待审稿', '1', 1),
                                                                                                    (3, 'reviewing', '审稿中', '2', 2),
                                                                                                    (3, 'accepted', '已录用', '3', 3),
                                                                                                    (3, 'rejected', '已拒绝', '4', 4);

-- 审稿结果字典项 (review_result)
INSERT INTO `dictionary_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
                                                                                                    (4, 'accept', '录用', '1', 1),
                                                                                                    (4, 'reject', '拒绝', '2', 2),
                                                                                                    (4, 'revise', '需修改', '3', 3);

-- 票务状态字典项 (ticket_status)
INSERT INTO `dictionary_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
                                                                                                    (5, 'active', '上架', '1', 1),
                                                                                                    (5, 'disabled', '下架', '0', 2);

-- 订单状态字典项 (order_status)
INSERT INTO `dictionary_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
                                                                                                    (6, 'unpaid', '未支付', '1', 1),
                                                                                                    (6, 'paid', '已支付', '2', 2),
                                                                                                    (6, 'cancelled', '已取消', '3', 3),
                                                                                                    (6, 'refunded', '已退款', '4', 4);

-- 凭证状态字典项 (credential_status)
INSERT INTO `dictionary_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
                                                                                                    (7, 'valid', '有效', '1', 1),
                                                                                                    (7, 'used', '已使用', '2', 2),
                                                                                                    (7, 'expired', '已过期', '3', 3);

-- 通用状态字典项 (common_status)
INSERT INTO `dictionary_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `sort_order`) VALUES
                                                                                                    (8, 'enabled', '启用', '1', 1),
                                                                                                    (8, 'disabled', '禁用', '0', 2);
