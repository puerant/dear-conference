-- ============================================================
-- 团体票功能数据库迁移脚本
-- 执行顺序：在 init.sql 之后执行
-- ============================================================

-- 1. tickets 表新增票种类型字段
ALTER TABLE tickets
    ADD COLUMN ticket_type TINYINT      NOT NULL DEFAULT 1   COMMENT '票种类型：1=个人票 2=团体票' AFTER description,
    ADD COLUMN min_persons INT           DEFAULT NULL         COMMENT '团体最小购买人数（个人票为NULL）' AFTER ticket_type,
    ADD COLUMN max_persons INT           DEFAULT NULL         COMMENT '团体最大购买人数（NULL不限）' AFTER min_persons;

-- 2. orders 表新增团体订单字段
ALTER TABLE orders
    ADD COLUMN order_type   TINYINT      NOT NULL DEFAULT 1   COMMENT '订单类型：1=个人 2=团体' AFTER status,
    ADD COLUMN group_name   VARCHAR(100) DEFAULT NULL         COMMENT '团体名称（团体订单）' AFTER order_type,
    ADD COLUMN invite_token VARCHAR(64)  DEFAULT NULL         COMMENT '团体邀请链接Token（支付后生成）' AFTER group_name;

ALTER TABLE orders
    ADD UNIQUE KEY uk_invite_token (invite_token);

-- 3. credentials 表新增关联成员字段
ALTER TABLE credentials
    ADD COLUMN group_member_id BIGINT DEFAULT NULL COMMENT '关联团体成员ID（个人票为NULL）' AFTER order_id;

-- 4. 新增团体成员表
CREATE TABLE IF NOT EXISTS group_members (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id     BIGINT       NOT NULL               COMMENT '关联订单ID',
    sequence_no  INT          NOT NULL               COMMENT '成员序号（1~quantity，订单内唯一）',
    member_name  VARCHAR(50)  DEFAULT NULL           COMMENT '成员姓名',
    member_email VARCHAR(100) DEFAULT NULL           COMMENT '成员邮箱',
    status       TINYINT      NOT NULL DEFAULT 1     COMMENT '1=待填写 2=已填写',
    filled_at    DATETIME     DEFAULT NULL           COMMENT '填写时间',
    created_at   DATETIME     NOT NULL               COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_seq (order_id, sequence_no),
    INDEX idx_order_id (order_id),
    INDEX idx_order_status (order_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团体订单成员信息';
