-- ========================================
-- 凭证表添加过期时间字段
-- 执行时间: 2026-03-03
-- ========================================

USE `mclc`;

ALTER TABLE `credentials`
    ADD COLUMN expire_at DATETIME NULL COMMENT '凭证有效期' AFTER used_at;
