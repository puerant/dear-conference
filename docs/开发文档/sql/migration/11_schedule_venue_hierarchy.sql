-- =====================================================
-- 日程模块增强：会场实体化 + 日程归属会场
-- 创建日期: 2026-03-10
-- =====================================================

-- 1) 创建会场表
CREATE TABLE IF NOT EXISTS `schedule_venue` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(200) NOT NULL COMMENT '会场名称',
  `address` VARCHAR(500) DEFAULT NULL COMMENT '会场地址',
  `description` TEXT DEFAULT NULL COMMENT '会场描述',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_schedule_venue_name` (`name`),
  KEY `idx_schedule_venue_sort` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程会场表';

-- 2) 为 schedule 增加 venue_id
ALTER TABLE `schedule`
  ADD COLUMN `venue_id` BIGINT NULL COMMENT '会场ID' AFTER `description`;

-- 3) 将历史 venue 文本迁移到会场表
INSERT INTO `schedule_venue` (`name`, `sort_order`)
SELECT DISTINCT
  TRIM(`venue`) AS `name`,
  0 AS `sort_order`
FROM `schedule`
WHERE `venue` IS NOT NULL
  AND TRIM(`venue`) <> ''
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 4) 回填 schedule.venue_id
UPDATE `schedule` s
JOIN `schedule_venue` v ON v.`name` = s.`venue`
SET s.`venue_id` = v.`id`
WHERE s.`venue_id` IS NULL
  AND s.`venue` IS NOT NULL
  AND TRIM(s.`venue`) <> '';

-- 5) 兜底：仍为空时挂到“未命名会场”
INSERT INTO `schedule_venue` (`name`, `sort_order`)
VALUES ('未命名会场', 9999)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

UPDATE `schedule` s
JOIN `schedule_venue` v ON v.`name` = '未命名会场'
SET s.`venue_id` = v.`id`
WHERE s.`venue_id` IS NULL;

-- 6) 添加约束与索引
ALTER TABLE `schedule`
  MODIFY COLUMN `venue_id` BIGINT NOT NULL COMMENT '会场ID',
  ADD INDEX `idx_schedule_venue_date_time` (`venue_id`, `date`, `start_time`, `end_time`),
  ADD CONSTRAINT `fk_schedule_venue` FOREIGN KEY (`venue_id`) REFERENCES `schedule_venue` (`id`);

-- =====================================================
-- 迁移完成
-- =====================================================
