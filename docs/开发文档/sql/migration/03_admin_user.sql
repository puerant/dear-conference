-- ========================================
-- 默认管理员账户初始化脚本
-- 数据库名: mclc
-- 创建时间: 2026-02-25
-- ========================================
--
-- 默认管理员账户信息:
--   邮箱:   admin@huiwutong.com
--   密码:   Admin@2026
--   角色:   管理员 (role=4)
--
-- 如需修改密码，使用 BCrypt 强度10 重新生成哈希，
-- 然后替换下方 INSERT 中的 password 字段值。
-- ========================================

USE `mclc`;

INSERT INTO `users` (`email`, `password`, `name`, `role`, `status`, `avatar`)
VALUES (
    'admin@huiwutong.com',
    '$2a$10$0LNT96zm4PIOBtMfwaolruBt8iNLVmWircnlkianojI6cFacmmuNW',
    '超级管理员',
    4,
    1,
    NULL
)
ON DUPLICATE KEY UPDATE
    `password` = VALUES(`password`),
    `name`     = VALUES(`name`),
    `role`     = VALUES(`role`),
    `status`   = VALUES(`status`),
    `updated_at` = CURRENT_TIMESTAMP;
