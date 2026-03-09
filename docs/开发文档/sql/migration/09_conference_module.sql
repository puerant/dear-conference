-- =====================================================
-- 会议配置模块数据库迁移脚本
-- 创建日期: 2026-03-09
-- 说明: 创建会议信息、日程、专家、酒店相关表
-- =====================================================

-- 1. 会议信息表
CREATE TABLE IF NOT EXISTS `conference_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` VARCHAR(200) NOT NULL COMMENT '会议标题',
  `subtitle` VARCHAR(200) DEFAULT NULL COMMENT '副标题',
  `description` TEXT DEFAULT NULL COMMENT '会议描述',
  `start_date` DATE NOT NULL COMMENT '开始日期',
  `end_date` DATE NOT NULL COMMENT '结束日期',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '会议地点',
  `address` VARCHAR(500) DEFAULT NULL COMMENT '详细地址',
  `banner_url` VARCHAR(500) DEFAULT NULL COMMENT '横幅图片URL',
  `contact_name` VARCHAR(100) DEFAULT NULL COMMENT '联系人',
  `contact_phone` VARCHAR(50) DEFAULT NULL COMMENT '联系电话',
  `contact_email` VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',
  `is_published` TINYINT NOT NULL DEFAULT 0 COMMENT '是否发布 (0=未发布, 1=已发布)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会议信息表';

-- 2. 专家表
CREATE TABLE IF NOT EXISTS `expert` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(100) NOT NULL COMMENT '姓名',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '职称/头衔',
  `organization` VARCHAR(200) DEFAULT NULL COMMENT '所属机构',
  `bio` TEXT DEFAULT NULL COMMENT '简介',
  `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `is_keynote` TINYINT NOT NULL DEFAULT 0 COMMENT '是否主旨演讲嘉宾 (0=否, 1=是)',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_is_keynote` (`is_keynote`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专家表';

-- 3. 日程表
CREATE TABLE IF NOT EXISTS `schedule` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `date` DATE NOT NULL COMMENT '日期',
  `start_time` TIME DEFAULT NULL COMMENT '开始时间',
  `end_time` TIME DEFAULT NULL COMMENT '结束时间',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `description` TEXT DEFAULT NULL COMMENT '描述',
  `venue` VARCHAR(200) DEFAULT NULL COMMENT '场地',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_date` (`date`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程表';

-- 4. 日程项表
CREATE TABLE IF NOT EXISTS `schedule_item` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `schedule_id` BIGINT NOT NULL COMMENT '日程ID',
  `time` TIME NOT NULL COMMENT '具体时间',
  `title` VARCHAR(200) NOT NULL COMMENT '标题',
  `description` TEXT DEFAULT NULL COMMENT '描述',
  `speaker_id` BIGINT DEFAULT NULL COMMENT '演讲人ID（关联expert表）',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_schedule_id` (`schedule_id`),
  KEY `idx_speaker_id` (`speaker_id`),
  CONSTRAINT `fk_schedule_item_schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_schedule_item_speaker` FOREIGN KEY (`speaker_id`) REFERENCES `expert` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日程项表';

-- 5. 酒店表
CREATE TABLE IF NOT EXISTS `hotel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` VARCHAR(200) NOT NULL COMMENT '酒店名称',
  `address` VARCHAR(500) DEFAULT NULL COMMENT '地址',
  `star_level` INT NOT NULL DEFAULT 3 COMMENT '星级（1-5）',
  `contact_phone` VARCHAR(50) DEFAULT NULL COMMENT '联系电话',
  `description` TEXT DEFAULT NULL COMMENT '描述',
  `image_url` VARCHAR(500) DEFAULT NULL COMMENT '图片URL',
  `booking_url` VARCHAR(500) DEFAULT NULL COMMENT '预订链接',
  `is_recommended` TINYINT NOT NULL DEFAULT 0 COMMENT '是否推荐 (0=否, 1=是)',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_is_recommended` (`is_recommended`),
  KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店表';

-- 6. 酒店房型表
CREATE TABLE IF NOT EXISTS `hotel_room` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `hotel_id` BIGINT NOT NULL COMMENT '酒店ID',
  `room_type` VARCHAR(100) NOT NULL COMMENT '房型',
  `price` DECIMAL(10,2) NOT NULL COMMENT '价格',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存',
  `description` TEXT DEFAULT NULL COMMENT '描述',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_hotel_id` (`hotel_id`),
  CONSTRAINT `fk_hotel_room_hotel` FOREIGN KEY (`hotel_id`) REFERENCES `hotel` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='酒店房型表';

-- =====================================================
-- 初始数据
-- =====================================================

-- 插入默认会议信息
INSERT INTO `conference_info` (`title`, `subtitle`, `description`, `start_date`, `end_date`, `location`, `address`, `contact_name`, `contact_phone`, `contact_email`, `is_published`)
VALUES ('2026 学术会议', '探索前沿技术，共话未来发展', '本次会议将汇聚全球顶尖学者，共同探讨人工智能、大数据、云计算等前沿技术领域的发展趋势。', '2026-06-15', '2026-06-17', '北京国际会议中心', '北京市朝阳区北辰东路8号', '会务组', '400-123-4567', 'info@huiwutong.com', 1);

-- 插入示例专家数据
INSERT INTO `expert` (`name`, `title`, `organization`, `bio`, `is_keynote`, `sort_order`) VALUES
('张明', '教授', '清华大学', '人工智能领域专家，长期从事机器学习和深度学习研究，发表顶级论文100余篇。', 1, 1),
('李华', '研究员', '中国科学院', '大数据与云计算专家，主持多项国家重点研发项目。', 1, 2),
('王芳', '副教授', '北京大学', '自然语言处理专家，专注于大语言模型研究。', 0, 1),
('陈伟', '高级工程师', '阿里巴巴', '分布式系统专家，负责大规模数据处理平台架构设计。', 0, 2);

-- 插入示例日程数据
INSERT INTO `schedule` (`date`, `start_time`, `end_time`, `title`, `description`, `venue`, `sort_order`) VALUES
('2026-06-15', '09:00:00', '09:30:00', '开幕式', '会议开幕致辞，介绍会议议程和注意事项', '主会场A', 1),
('2026-06-15', '10:00:00', '12:00:00', '人工智能主题演讲', '人工智能领域最新研究成果分享', '主会场A', 2),
('2026-06-15', '14:00:00', '17:00:00', '分论坛：机器学习', '机器学习算法与应用专题讨论', '分会场B1', 3),
('2026-06-16', '09:00:00', '12:00:00', '大数据主题演讲', '大数据技术与行业应用', '主会场A', 4),
('2026-06-16', '14:00:00', '17:00:00', '分论坛：云计算', '云计算架构与实践', '分会场B2', 5),
('2026-06-17', '09:00:00', '11:00:00', '圆桌讨论', '前沿技术发展趋势探讨', '主会场A', 6),
('2026-06-17', '11:00:00', '12:00:00', '闭幕式', '会议总结与颁奖', '主会场A', 7);

-- 插入示例日程项数据
INSERT INTO `schedule_item` (`schedule_id`, `time`, `title`, `description`, `speaker_id`, `sort_order`) VALUES
(2, '10:00:00', '大模型的未来发展方向', '探讨大语言模型的技术演进和应用前景', 1, 1),
(2, '11:00:00', '多模态学习研究进展', '多模态大模型的最新研究成果', 3, 2),
(4, '09:00:00', '大规模数据处理架构', '分布式数据处理系统的设计与优化', 2, 1),
(4, '10:30:00', '实时数据流处理', '流式计算在业务场景中的应用', 4, 2);

-- 插入示例酒店数据
INSERT INTO `hotel` (`name`, `address`, `star_level`, `contact_phone`, `description`, `is_recommended`, `sort_order`) VALUES
('北京国际大酒店', '北京市朝阳区北辰东路8号', 5, '010-12345678', '距离会场步行5分钟，五星级豪华酒店，设施完善。', 1, 1),
('北京会议中心酒店', '北京市朝阳区北辰西路9号', 4, '010-87654321', '距离会场步行10分钟，商务型酒店，性价比高。', 1, 2),
('如家快捷酒店', '北京市朝阳区亚运村店', 3, '010-55556666', '经济型连锁酒店，干净舒适，距离会场地铁2站。', 0, 1);

-- 插入示例房型数据
INSERT INTO `hotel_room` (`hotel_id`, `room_type`, `price`, `stock`, `description`, `sort_order`) VALUES
(1, '标准间', 698.00, 50, '28平米，双床，免费WiFi', 1),
(1, '大床房', 758.00, 30, '28平米，大床，免费WiFi', 2),
(1, '行政套房', 1288.00, 10, '50平米，独立客厅，行政酒廊待遇', 3),
(2, '标准间', 398.00, 60, '25平米，双床，免费WiFi', 1),
(2, '大床房', 458.00, 40, '25平米，大床，免费WiFi', 2),
(3, '标准间', 268.00, 80, '20平米，双床，免费WiFi', 1);

-- =====================================================
-- 迁移完成
-- =====================================================
