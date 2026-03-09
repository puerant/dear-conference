-- ========================================
-- 系统模块表初始化脚本
-- 数据库名: mclc
-- 文档版本: v1.0
-- 创建时间: 2026-03-03
-- ========================================

USE `mclc`;

-- ========================================
-- 1. 邮箱配置表 (email_config)
-- ========================================
CREATE TABLE IF NOT EXISTS `email_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `host` VARCHAR(100) NOT NULL COMMENT 'SMTP服务器地址',
  `port` INT NOT NULL COMMENT 'SMTP端口',
  `username` VARCHAR(100) NOT NULL COMMENT 'SMTP用户名',
  `password` VARCHAR(500) NOT NULL COMMENT 'SMTP密码（AES加密）',
  `from_name` VARCHAR(50) NOT NULL COMMENT '发件人名称',
  `from_email` VARCHAR(100) NOT NULL COMMENT '发件人邮箱',
  `ssl_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用SSL',
  `is_enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱配置表';

-- 初始数据
INSERT INTO `email_config` (`host`, `port`, `username`, `password`, `from_name`, `from_email`, `is_enabled`)
VALUES ('smtp.example.com', 465, 'noreply@huiwutong.com', 'encrypted_password', '会务通系统', 'noreply@huiwutong.com', 0);

-- ========================================
-- 2. 凭证配置表 (credential_config)
-- ========================================
CREATE TABLE IF NOT EXISTS `credential_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `template_id` VARCHAR(50) NOT NULL DEFAULT 'default' COMMENT '凭证模板ID',
  `background_image` VARCHAR(500) DEFAULT NULL COMMENT '背景图片URL',
  `watermark_text` VARCHAR(200) DEFAULT NULL COMMENT '水印文字',
  `watermark_opacity` INT NOT NULL DEFAULT 20 COMMENT '水印透明度（0-100）',
  `watermark_x` INT NOT NULL DEFAULT 50 COMMENT '水印X坐标',
  `watermark_y` INT NOT NULL DEFAULT 50 COMMENT '水印Y坐标',
  `expiry_days` INT NOT NULL DEFAULT 30 COMMENT '凭证有效期（天）',
  `qr_code_position` VARCHAR(50) NOT NULL DEFAULT 'bottom_right' COMMENT '二维码位置',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='凭证配置表';

-- 初始数据
INSERT INTO `credential_config` (`template_id`, `expiry_days`) VALUES ('default', 30);

-- ========================================
-- 3. 支付配置表 (payment_config)
-- ========================================
CREATE TABLE IF NOT EXISTS `payment_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `payment_type` TINYINT NOT NULL COMMENT '支付类型（1=微信,2=支付宝）',
  `app_id` VARCHAR(100) NOT NULL COMMENT '应用ID',
  `app_secret` VARCHAR(500) NOT NULL COMMENT '应用密钥（AES加密）',
  `merchant_id` VARCHAR(100) NOT NULL COMMENT '商户号',
  `api_url` VARCHAR(200) NOT NULL COMMENT 'API地址',
  `notify_url` VARCHAR(200) NOT NULL COMMENT '回调地址',
  `is_enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用',
  `sandbox_mode` TINYINT NOT NULL DEFAULT 1 COMMENT '沙盒模式',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_payment_type` (`payment_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付配置表';

-- 初始数据（微信支付）
INSERT INTO `payment_config` (`payment_type`, `app_id`, `app_secret`, `merchant_id`, `api_url`, `notify_url`, `sandbox_mode`)
VALUES (1, 'wx_app_id', 'encrypted_secret', '1234567890', 'https://api.mch.weixin.qq.com/pay/unifiedorder', '/api/payment/callback/wechat', 1);

-- 初始数据（支付宝）
INSERT INTO `payment_config` (`payment_type`, `app_id`, `app_secret`, `merchant_id`, `api_url`, `notify_url`, `sandbox_mode`)
VALUES (2, 'alipay_app_id', 'encrypted_secret', '2088123456789012', 'https://openapi.alipay.com/gateway.do', '/api/payment/callback/alipay', 1);

-- ========================================
-- 4. 订单定时任务配置表 (order_task_config)
-- ========================================
CREATE TABLE IF NOT EXISTS `order_task_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `timeout_minutes` INT NOT NULL DEFAULT 30 COMMENT '订单超时分钟数',
  `check_interval_minutes` INT NOT NULL DEFAULT 5 COMMENT '检查间隔分钟数',
  `cancel_task_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '超时取消任务是否启用',
  `refund_task_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '退款检查任务是否启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单定时任务配置表';

-- 初始数据
INSERT INTO `order_task_config` (`timeout_minutes`, `check_interval_minutes`) VALUES (30, 5);

-- ========================================
-- 5. 系统文件表 (system_file)
-- ========================================
CREATE TABLE IF NOT EXISTS `system_file` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `file_name` VARCHAR(255) NOT NULL COMMENT '原始文件名',
  `file_path` VARCHAR(500) NOT NULL COMMENT '文件存储路径',
  `file_url` VARCHAR(500) DEFAULT NULL COMMENT '文件访问URL',
  `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型（image, document, presentation）',
  `file_extension` VARCHAR(20) NOT NULL COMMENT '文件扩展名（jpg, png, pdf, docx, pptx）',
  `mime_type` VARCHAR(100) DEFAULT NULL COMMENT 'MIME类型',
  `file_size` BIGINT NOT NULL COMMENT '文件大小（字节）',
  `file_category` VARCHAR(50) DEFAULT NULL COMMENT '文件分类（conference_banner, expert_avatar, hotel_image, presentation, document）',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID（关联conference_info、expert、hotel等）',
  `upload_user_id` BIGINT DEFAULT NULL COMMENT '上传用户ID',
  `upload_user_name` VARCHAR(100) DEFAULT NULL COMMENT '上传用户名',
  `storage_type` TINYINT NOT NULL DEFAULT 1 COMMENT '存储类型（1=本地,2=阿里云OSS,3=腾讯云COS）',
  `md5` VARCHAR(32) DEFAULT NULL COMMENT '文件MD5值（去重）',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '是否删除（软删除）',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_file_type` (`file_type`),
  KEY `idx_file_category` (`file_category`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_upload_user_id` (`upload_user_id`),
  KEY `idx_md5` (`md5`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统文件表';
