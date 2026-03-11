-- =====================================================
-- 日程模块增强：会场隔离 + 日程项时间段
-- 创建日期: 2026-03-10
-- =====================================================

-- 1) 为 schedule_item 增加时间段字段（兼容旧字段 time）
ALTER TABLE `schedule_item`
  ADD COLUMN `start_time` TIME NULL COMMENT '开始时间' AFTER `schedule_id`,
  ADD COLUMN `end_time` TIME NULL COMMENT '结束时间' AFTER `start_time`;

-- 2) 将历史单点时间迁移为时间段（start=end=time）
UPDATE `schedule_item`
SET `start_time` = `time`,
    `end_time` = `time`
WHERE `start_time` IS NULL
  AND `end_time` IS NULL
  AND `time` IS NOT NULL;

-- 3) 增加检索索引
ALTER TABLE `schedule`
  ADD INDEX `idx_schedule_date_venue_time` (`date`, `venue`, `start_time`, `end_time`);

ALTER TABLE `schedule_item`
  ADD INDEX `idx_schedule_item_time` (`schedule_id`, `start_time`, `end_time`);

-- =====================================================
-- 迁移完成
-- =====================================================
