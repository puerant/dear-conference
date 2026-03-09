-- ========================================
-- 邮箱多配置 + 邮件发送日志迁移脚本
-- 数据库名: mclc
-- 创建时间: 2026-03-04
-- ========================================

USE `mclc`;

-- 1) email_config 增量字段
ALTER TABLE `email_config`
  ADD COLUMN IF NOT EXISTS `config_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '配置名称' AFTER `id`,
  ADD COLUMN IF NOT EXISTS `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认：1=默认，0=非默认' AFTER `is_enabled`,
  ADD COLUMN IF NOT EXISTS `priority` INT NOT NULL DEFAULT 100 COMMENT '优先级（越小越高）' AFTER `is_default`,
  ADD COLUMN IF NOT EXISTS `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注' AFTER `priority`;

-- 初始化配置名称（若为空则回填）
UPDATE `email_config`
SET `config_name` = IFNULL(NULLIF(`config_name`, ''), `from_email`);

-- 初始化默认账号：优先当前已启用账号（再按 id 最小）
UPDATE `email_config` SET `is_default` = 0;
UPDATE `email_config`
SET `is_default` = 1
WHERE `id` = (
  SELECT t.id FROM (
    SELECT `id`
    FROM `email_config`
    WHERE `is_enabled` = 1
    ORDER BY `id` ASC
    LIMIT 1
  ) t
);

CREATE INDEX `idx_email_config_enabled_default` ON `email_config` (`is_enabled`, `is_default`, `priority`);

-- 2) 邮件发送日志表
CREATE TABLE IF NOT EXISTS `email_send_log` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scene_code`      VARCHAR(64)   NOT NULL COMMENT '场景码，如 VERIFICATION_CODE',
  `template_id`     BIGINT        DEFAULT NULL COMMENT '模板ID',
  `email_config_id` BIGINT        DEFAULT NULL COMMENT '发送使用的邮箱配置ID',
  `send_type`       TINYINT       NOT NULL COMMENT '发送类型：1=业务发送，2=测试发送',
  `to_email`        VARCHAR(200)  NOT NULL COMMENT '收件人邮箱',
  `from_email`      VARCHAR(200)  DEFAULT NULL COMMENT '发件人邮箱',
  `subject`         VARCHAR(300)  DEFAULT NULL COMMENT '发送主题（渲染后）',
  `variables_json`  JSON          DEFAULT NULL COMMENT '变量快照',
  `status`          TINYINT       NOT NULL COMMENT '发送状态：1=成功，0=失败',
  `error_message`   VARCHAR(1000) DEFAULT NULL COMMENT '失败原因',
  `triggered_by`    BIGINT        DEFAULT NULL COMMENT '触发人（管理员ID，业务触发时可空）',
  `sent_at`         DATETIME      DEFAULT NULL COMMENT '发送时间',
  `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_scene_code` (`scene_code`),
  KEY `idx_to_email` (`to_email`),
  KEY `idx_status` (`status`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件发送日志表';

