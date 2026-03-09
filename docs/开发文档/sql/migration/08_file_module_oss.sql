-- ========================================
-- 文件模块（多 OSS 供应商）迁移脚本
-- 数据库名: mclc
-- 创建时间: 2026-03-05
-- ========================================

USE `mclc`;

-- ========================================
-- 1) 供应商配置表
-- ========================================
CREATE TABLE IF NOT EXISTS `storage_provider` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `provider_name` VARCHAR(100) NOT NULL COMMENT '配置名称',
  `provider_type` TINYINT NOT NULL COMMENT '供应商类型：1=LOCAL,2=ALI_OSS,3=TENCENT_COS,4=MINIO_S3',
  `endpoint` VARCHAR(255) DEFAULT NULL COMMENT '访问端点',
  `region` VARCHAR(100) DEFAULT NULL COMMENT '区域',
  `access_key` VARCHAR(500) DEFAULT NULL COMMENT 'AccessKey（AES加密）',
  `secret_key` VARCHAR(500) DEFAULT NULL COMMENT 'SecretKey（AES加密）',
  `sts_enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用STS',
  `is_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认供应商',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_provider_name` (`provider_name`),
  KEY `idx_provider_type` (`provider_type`),
  KEY `idx_provider_enabled_default` (`is_enabled`, `is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储供应商配置表';

-- 初始化供应商（本地默认 + OSS占位）
INSERT INTO `storage_provider` (`provider_name`, `provider_type`, `endpoint`, `region`, `is_enabled`, `is_default`, `remark`)
SELECT '本地存储-默认', 1, '/files', 'local', 1, 1, '系统初始化默认本地存储'
WHERE NOT EXISTS (
  SELECT 1 FROM `storage_provider` WHERE `provider_name` = '本地存储-默认'
);

INSERT INTO `storage_provider` (`provider_name`, `provider_type`, `endpoint`, `region`, `is_enabled`, `is_default`, `remark`)
SELECT '阿里云OSS-待配置', 2, NULL, NULL, 0, 0, '占位配置，需管理员完善'
WHERE NOT EXISTS (
  SELECT 1 FROM `storage_provider` WHERE `provider_name` = '阿里云OSS-待配置'
);

INSERT INTO `storage_provider` (`provider_name`, `provider_type`, `endpoint`, `region`, `is_enabled`, `is_default`, `remark`)
SELECT '腾讯云COS-待配置', 3, NULL, NULL, 0, 0, '占位配置，需管理员完善'
WHERE NOT EXISTS (
  SELECT 1 FROM `storage_provider` WHERE `provider_name` = '腾讯云COS-待配置'
);

-- 兜底：保证至少一个默认且启用的供应商
UPDATE `storage_provider` SET `is_default` = 0;
UPDATE `storage_provider`
SET `is_default` = 1
WHERE `id` = (
  SELECT t.id FROM (
    SELECT `id`
    FROM `storage_provider`
    WHERE `is_enabled` = 1
    ORDER BY `provider_type` ASC, `id` ASC
    LIMIT 1
  ) t
);

-- ========================================
-- 2) 存储桶配置表
-- ========================================
CREATE TABLE IF NOT EXISTS `storage_bucket` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `provider_id` BIGINT NOT NULL COMMENT '供应商ID',
  `bucket_name` VARCHAR(128) NOT NULL COMMENT 'Bucket名称',
  `base_path` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '基础目录前缀',
  `domain` VARCHAR(255) DEFAULT NULL COMMENT '访问域名/CDN域名',
  `acl_type` TINYINT NOT NULL DEFAULT 1 COMMENT '访问级别：1=private,2=public-read',
  `is_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否为该供应商默认bucket',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_provider_bucket_base_path` (`provider_id`, `bucket_name`, `base_path`),
  KEY `idx_provider_id` (`provider_id`),
  KEY `idx_bucket_enabled_default` (`is_enabled`, `is_default`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='存储桶配置表';

-- 初始化每个供应商的默认bucket
INSERT INTO `storage_bucket` (`provider_id`, `bucket_name`, `base_path`, `domain`, `acl_type`, `is_enabled`, `is_default`)
SELECT sp.id, 'local-default', 'conference/', '/files', 1, 1, 1
FROM `storage_provider` sp
WHERE sp.`provider_name` = '本地存储-默认'
  AND NOT EXISTS (
    SELECT 1 FROM `storage_bucket` sb WHERE sb.`provider_id` = sp.`id` AND sb.`is_default` = 1
  );

INSERT INTO `storage_bucket` (`provider_id`, `bucket_name`, `base_path`, `domain`, `acl_type`, `is_enabled`, `is_default`)
SELECT sp.id, 'oss-default', 'conference/', NULL, 1, 1, 1
FROM `storage_provider` sp
WHERE sp.`provider_name` = '阿里云OSS-待配置'
  AND NOT EXISTS (
    SELECT 1 FROM `storage_bucket` sb WHERE sb.`provider_id` = sp.`id` AND sb.`is_default` = 1
  );

INSERT INTO `storage_bucket` (`provider_id`, `bucket_name`, `base_path`, `domain`, `acl_type`, `is_enabled`, `is_default`)
SELECT sp.id, 'cos-default', 'conference/', NULL, 1, 1, 1
FROM `storage_provider` sp
WHERE sp.`provider_name` = '腾讯云COS-待配置'
  AND NOT EXISTS (
    SELECT 1 FROM `storage_bucket` sb WHERE sb.`provider_id` = sp.`id` AND sb.`is_default` = 1
  );

-- ========================================
-- 3) 文件存储策略表
-- ========================================
CREATE TABLE IF NOT EXISTS `file_storage_policy` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `policy_name` VARCHAR(100) NOT NULL COMMENT '策略名称',
  `file_category` VARCHAR(64) DEFAULT NULL COMMENT '文件分类',
  `file_type` VARCHAR(32) DEFAULT NULL COMMENT '文件类型：image/document/presentation',
  `provider_id` BIGINT NOT NULL COMMENT '目标供应商ID',
  `bucket_id` BIGINT NOT NULL COMMENT '目标bucket ID',
  `path_rule` VARCHAR(255) NOT NULL DEFAULT 'yyyy/MM/dd/{category}' COMMENT '路径规则',
  `max_size_mb` INT DEFAULT NULL COMMENT '最大文件大小（MB）',
  `allowed_ext` VARCHAR(255) DEFAULT NULL COMMENT '允许扩展名（逗号分隔）',
  `priority` INT NOT NULL DEFAULT 100 COMMENT '优先级（越小越高）',
  `is_enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_policy_name` (`policy_name`),
  KEY `idx_policy_match` (`file_category`, `file_type`, `is_enabled`, `priority`),
  KEY `idx_policy_provider_bucket` (`provider_id`, `bucket_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件存储策略表';

-- 初始化兜底策略（默认落本地）
INSERT INTO `file_storage_policy` (`policy_name`, `file_category`, `file_type`, `provider_id`, `bucket_id`, `path_rule`, `max_size_mb`, `allowed_ext`, `priority`, `is_enabled`, `remark`)
SELECT
  'DEFAULT_LOCAL_FALLBACK',
  NULL,
  NULL,
  sp.id,
  sb.id,
  'yyyy/MM/dd/{category}',
  NULL,
  NULL,
  9999,
  1,
  '系统初始化兜底策略'
FROM `storage_provider` sp
JOIN `storage_bucket` sb ON sb.`provider_id` = sp.`id` AND sb.`is_default` = 1
WHERE sp.`provider_name` = '本地存储-默认'
  AND NOT EXISTS (
    SELECT 1 FROM `file_storage_policy` p WHERE p.`policy_name` = 'DEFAULT_LOCAL_FALLBACK'
  );

-- ========================================
-- 4) system_file 增量字段
-- ========================================
SET @col_exists = (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'system_file'
    AND column_name = 'provider_id'
);
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `system_file` ADD COLUMN `provider_id` BIGINT DEFAULT NULL COMMENT ''存储供应商ID'' AFTER `storage_type`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'system_file'
    AND column_name = 'bucket_id'
);
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `system_file` ADD COLUMN `bucket_id` BIGINT DEFAULT NULL COMMENT ''存储桶ID'' AFTER `provider_id`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'system_file'
    AND column_name = 'object_key'
);
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `system_file` ADD COLUMN `object_key` VARCHAR(500) DEFAULT NULL COMMENT ''对象存储KEY（不含域名）'' AFTER `bucket_id`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'system_file'
    AND column_name = 'storage_path'
);
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `system_file` ADD COLUMN `storage_path` VARCHAR(1000) DEFAULT NULL COMMENT ''完整存储路径（内部）'' AFTER `object_key`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'system_file'
    AND column_name = 'visibility'
);
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `system_file` ADD COLUMN `visibility` TINYINT NOT NULL DEFAULT 1 COMMENT ''可见性：1=private,2=public'' AFTER `storage_path`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'system_file'
    AND column_name = 'version_no'
);
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `system_file` ADD COLUMN `version_no` INT NOT NULL DEFAULT 1 COMMENT ''版本号（预留）'' AFTER `visibility`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'system_file'
    AND column_name = 'is_migrated'
);
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `system_file` ADD COLUMN `is_migrated` TINYINT NOT NULL DEFAULT 0 COMMENT ''是否迁移：0=否,1=是'' AFTER `version_no`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @col_exists = (
  SELECT COUNT(1)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'system_file'
    AND column_name = 'updated_at'
);
SET @sql = IF(@col_exists = 0, 'ALTER TABLE `system_file` ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT ''更新时间'' AFTER `created_at`', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = (
  SELECT COUNT(1)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'system_file'
    AND index_name = 'idx_system_file_provider_id'
);
SET @sql = IF(@idx_exists = 0, 'CREATE INDEX `idx_system_file_provider_id` ON `system_file` (`provider_id`)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = (
  SELECT COUNT(1)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'system_file'
    AND index_name = 'idx_system_file_bucket_id'
);
SET @sql = IF(@idx_exists = 0, 'CREATE INDEX `idx_system_file_bucket_id` ON `system_file` (`bucket_id`)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = (
  SELECT COUNT(1)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'system_file'
    AND index_name = 'idx_system_file_visibility'
);
SET @sql = IF(@idx_exists = 0, 'CREATE INDEX `idx_system_file_visibility` ON `system_file` (`visibility`)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @idx_exists = (
  SELECT COUNT(1)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'system_file'
    AND index_name = 'idx_system_file_is_migrated'
);
SET @sql = IF(@idx_exists = 0, 'CREATE INDEX `idx_system_file_is_migrated` ON `system_file` (`is_migrated`)', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 回填 provider_id / bucket_id（根据既有 storage_type）
UPDATE `system_file` sf
JOIN `storage_provider` sp ON sp.`provider_type` = sf.`storage_type`
SET sf.`provider_id` = sp.`id`
WHERE sf.`provider_id` IS NULL;

UPDATE `system_file` sf
JOIN `storage_bucket` sb ON sb.`provider_id` = sf.`provider_id` AND sb.`is_default` = 1
SET sf.`bucket_id` = sb.`id`
WHERE sf.`provider_id` IS NOT NULL
  AND sf.`bucket_id` IS NULL;

-- 回填 object_key / storage_path（仅对空值回填）
UPDATE `system_file`
SET `object_key` = CASE
    WHEN `object_key` IS NULL OR `object_key` = '' THEN `file_path`
    ELSE `object_key`
  END,
  `storage_path` = CASE
    WHEN `storage_path` IS NULL OR `storage_path` = '' THEN `file_path`
    ELSE `storage_path`
  END;

-- ========================================
-- 5) 文件迁移任务表
-- ========================================
CREATE TABLE IF NOT EXISTS `file_migration_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_no` VARCHAR(64) NOT NULL COMMENT '任务号',
  `source_provider_id` BIGINT NOT NULL COMMENT '源供应商ID',
  `target_provider_id` BIGINT NOT NULL COMMENT '目标供应商ID',
  `file_count` INT NOT NULL DEFAULT 0 COMMENT '总文件数',
  `success_count` INT NOT NULL DEFAULT 0 COMMENT '成功数',
  `fail_count` INT NOT NULL DEFAULT 0 COMMENT '失败数',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1=待执行,2=执行中,3=完成,4=失败',
  `error_message` VARCHAR(1000) DEFAULT NULL COMMENT '错误信息',
  `created_by` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `started_at` DATETIME DEFAULT NULL COMMENT '开始时间',
  `finished_at` DATETIME DEFAULT NULL COMMENT '完成时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_file_migration_task_no` (`task_no`),
  KEY `idx_file_migration_status` (`status`),
  KEY `idx_file_migration_source_target` (`source_provider_id`, `target_provider_id`),
  KEY `idx_file_migration_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件迁移任务表';

-- ========================================
-- 6) 字典补充：file_category
-- ========================================
INSERT INTO `dictionary` (`dict_code`, `dict_name`, `description`, `status`, `sort_order`)
SELECT 'file_category', '文件分类', '文件模块业务分类', 1, 9
WHERE NOT EXISTS (
  SELECT 1 FROM `dictionary` WHERE `dict_code` = 'file_category'
);

INSERT INTO `dictionary_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `description`, `status`, `sort_order`)
SELECT d.`id`, 'paper_attachment', '投稿附件', '8', '投稿模块论文附件', 1, 8
FROM `dictionary` d
WHERE d.`dict_code` = 'file_category'
  AND NOT EXISTS (
    SELECT 1 FROM `dictionary_item` di
    WHERE di.`dict_id` = d.`id` AND di.`item_code` = 'paper_attachment'
  );

INSERT INTO `dictionary_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `description`, `status`, `sort_order`)
SELECT d.`id`, 'review_attachment', '审稿附件', '9', '审稿模块附件', 1, 9
FROM `dictionary` d
WHERE d.`dict_code` = 'file_category'
  AND NOT EXISTS (
    SELECT 1 FROM `dictionary_item` di
    WHERE di.`dict_id` = d.`id` AND di.`item_code` = 'review_attachment'
  );

INSERT INTO `dictionary_item` (`dict_id`, `item_code`, `item_name`, `item_value`, `description`, `status`, `sort_order`)
SELECT d.`id`, 'conference_material', '会议资料', '10', '会议资料与会务文件', 1, 10
FROM `dictionary` d
WHERE d.`dict_code` = 'file_category'
  AND NOT EXISTS (
    SELECT 1 FROM `dictionary_item` di
    WHERE di.`dict_id` = d.`id` AND di.`item_code` = 'conference_material'
  );
