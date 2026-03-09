-- ========================================
-- 邮件模板表初始化脚本
-- 数据库名: mclc
-- 创建时间: 2026-03-04
-- 说明: 创建 email_template 表并插入8个内置场景默认模板
-- ========================================

USE `mclc`;

-- ----------------------------
-- Table: email_template
-- ----------------------------
CREATE TABLE IF NOT EXISTS `email_template` (
  `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
  `scene_code`      VARCHAR(64)   NOT NULL COMMENT '场景码（全局唯一，英文大写下划线）',
  `scene_name`      VARCHAR(100)  NOT NULL COMMENT '场景中文名称',
  `email_config_id` BIGINT        DEFAULT NULL COMMENT '绑定的邮箱配置 ID（NULL 时使用系统默认启用配置）',
  `subject`         VARCHAR(200)  NOT NULL COMMENT '邮件主题（支持 {{变量}} 占位符）',
  `body`            MEDIUMTEXT    NOT NULL COMMENT '邮件正文 HTML（支持 {{变量}} 占位符）',
  `variables_desc`  TEXT          DEFAULT NULL COMMENT '可用变量说明 JSON 字符串',
  `is_enabled`      TINYINT       NOT NULL DEFAULT 1 COMMENT '是否启用：1=启用，0=禁用',
  `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_scene_code` (`scene_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件模板表';

-- ----------------------------
-- 默认场景数据
-- ----------------------------

-- 1. 邮箱验证码
INSERT INTO `email_template` (`scene_code`, `scene_name`, `subject`, `body`, `variables_desc`, `is_enabled`) VALUES
('VERIFICATION_CODE', '邮箱验证码',
 '【会务通系统】邮箱验证码',
 '<div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;padding:24px;border:1px solid #e8e8e8;border-radius:8px"><h2 style="color:#9b59b6;margin-bottom:16px">学术会议系统</h2><p style="color:#595959">您正在进行邮箱验证，验证码为：</p><div style="font-size:32px;font-weight:bold;letter-spacing:8px;color:#9b59b6;padding:16px 0">{{code}}</div><p style="color:#8c8c8c;font-size:13px">验证码 <b>{{expireMinutes}} 分钟</b>内有效，请勿泄露给他人。</p><p style="color:#8c8c8c;font-size:13px">如非本人操作，请忽略此邮件。</p></div>',
 '{"code":"6位验证码","expireMinutes":"有效期分钟数"}',
 1);

-- 2. 投稿提交成功
INSERT INTO `email_template` (`scene_code`, `scene_name`, `subject`, `body`, `variables_desc`, `is_enabled`) VALUES
('SUBMISSION_CONFIRM', '投稿提交成功',
 '【会务通系统】投稿提交成功',
 '<div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;padding:24px;border:1px solid #e8e8e8;border-radius:8px"><h2 style="color:#9b59b6;margin-bottom:16px">学术会议系统</h2><p style="color:#595959">尊敬的 <b>{{speakerName}}</b>，您好！</p><p style="color:#595959">您的论文已成功提交，等待审稿人审阅。</p><div style="background:#f9f0ff;border-left:4px solid #9b59b6;padding:12px 16px;margin:16px 0;border-radius:0 4px 4px 0"><p style="margin:0;color:#595959">投稿标题：<b>{{submissionTitle}}</b></p><p style="margin:8px 0 0;color:#595959">当前状态：<b style="color:#9b59b6">待审稿</b></p></div><p style="color:#8c8c8c;font-size:13px">审稿结果将通过邮件通知您，请耐心等待。</p></div>',
 '{"speakerName":"讲者姓名","submissionTitle":"投稿标题"}',
 1);

-- 3. 审稿分配通知
INSERT INTO `email_template` (`scene_code`, `scene_name`, `subject`, `body`, `variables_desc`, `is_enabled`) VALUES
('REVIEW_ASSIGNMENT', '审稿分配通知',
 '【会务通系统】您有一篇待审稿投稿',
 '<div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;padding:24px;border:1px solid #e8e8e8;border-radius:8px"><h2 style="color:#9b59b6;margin-bottom:16px">学术会议系统</h2><p style="color:#595959">尊敬的 <b>{{reviewerName}}</b>，您好！</p><p style="color:#595959">您已被分配审阅以下投稿，请登录系统进行审稿。</p><div style="background:#f9f0ff;border-left:4px solid #9b59b6;padding:12px 16px;margin:16px 0;border-radius:0 4px 4px 0"><p style="margin:0;color:#595959">投稿标题：<b>{{submissionTitle}}</b></p></div><p style="color:#8c8c8c;font-size:13px">请及时登录系统完成审稿，感谢您的配合。</p></div>',
 '{"reviewerName":"审稿人姓名","submissionTitle":"投稿标题"}',
 1);

-- 4. 投稿审核结果
INSERT INTO `email_template` (`scene_code`, `scene_name`, `subject`, `body`, `variables_desc`, `is_enabled`) VALUES
('SUBMISSION_RESULT', '投稿审核结果',
 '【会务通系统】您的投稿审核结果',
 '<div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;padding:24px;border:1px solid #e8e8e8;border-radius:8px"><h2 style="color:#9b59b6;margin-bottom:16px">学术会议系统</h2><p style="color:#595959">尊敬的 <b>{{speakerName}}</b>，您好！</p><p style="color:#595959">您提交的投稿已完成审核，结果如下：</p><div style="background:#f9f0ff;border-left:4px solid {{resultColor}};padding:12px 16px;margin:16px 0;border-radius:0 4px 4px 0"><p style="margin:0;color:#595959">投稿标题：<b>{{submissionTitle}}</b></p><p style="margin:8px 0 0;color:#595959">审核结果：<b style="color:{{resultColor}}">{{result}}</b></p></div><p style="color:#8c8c8c;font-size:13px">如有疑问，请联系大会组委会。</p></div>',
 '{"speakerName":"讲者姓名","submissionTitle":"投稿标题","result":"审核结果文字","resultColor":"结果颜色（已录用=#27ae60，已拒绝=#e74c3c）"}',
 1);

-- 5. 订单创建成功
INSERT INTO `email_template` (`scene_code`, `scene_name`, `subject`, `body`, `variables_desc`, `is_enabled`) VALUES
('ORDER_CONFIRM', '订单创建成功',
 '【会务通系统】订单创建成功',
 '<div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;padding:24px;border:1px solid #e8e8e8;border-radius:8px"><h2 style="color:#9b59b6;margin-bottom:16px">学术会议系统</h2><p style="color:#595959">尊敬的 <b>{{attendeeName}}</b>，您好！</p><p style="color:#595959">您的订单已成功创建，请在 <b>{{expireMinutes}} 分钟</b>内完成支付，逾期将自动取消。</p><div style="background:#f9f0ff;border-left:4px solid #9b59b6;padding:12px 16px;margin:16px 0;border-radius:0 4px 4px 0"><p style="margin:0;color:#595959">订单号：<b>{{orderNo}}</b></p><p style="margin:8px 0 0;color:#595959">票务名称：<b>{{ticketName}}</b></p><p style="margin:8px 0 0;color:#595959">订单金额：<b>¥{{amount}}</b></p></div><p style="color:#8c8c8c;font-size:13px">请登录系统完成支付。</p></div>',
 '{"attendeeName":"参会者姓名","orderNo":"订单号","ticketName":"票务名称","amount":"订单金额","expireMinutes":"支付超时分钟数"}',
 1);

-- 6. 支付成功通知
INSERT INTO `email_template` (`scene_code`, `scene_name`, `subject`, `body`, `variables_desc`, `is_enabled`) VALUES
('PAYMENT_SUCCESS', '支付成功通知',
 '【会务通系统】支付成功，请查收凭证',
 '<div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;padding:24px;border:1px solid #e8e8e8;border-radius:8px"><h2 style="color:#9b59b6;margin-bottom:16px">学术会议系统</h2><p style="color:#595959">尊敬的 <b>{{attendeeName}}</b>，您好！</p><p style="color:#595959">您的订单已支付成功，入会凭证已生成，请妥善保管。</p><div style="background:#f9f0ff;border-left:4px solid #27ae60;padding:12px 16px;margin:16px 0;border-radius:0 4px 4px 0"><p style="margin:0;color:#595959">订单号：<b>{{orderNo}}</b></p><p style="margin:8px 0 0;color:#595959">票务名称：<b>{{ticketName}}</b></p><p style="margin:8px 0 0;color:#595959">凭证编号：<b>{{credentialNo}}</b></p></div><p style="color:#8c8c8c;font-size:13px">请登录系统查看并下载您的入会凭证。</p></div>',
 '{"attendeeName":"参会者姓名","orderNo":"订单号","ticketName":"票务名称","credentialNo":"凭证编号"}',
 1);

-- 7. 凭证发放通知
INSERT INTO `email_template` (`scene_code`, `scene_name`, `subject`, `body`, `variables_desc`, `is_enabled`) VALUES
('CREDENTIAL_ISSUED', '入会凭证发放通知',
 '【会务通系统】您的入会凭证已生成',
 '<div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;padding:24px;border:1px solid #e8e8e8;border-radius:8px"><h2 style="color:#9b59b6;margin-bottom:16px">学术会议系统</h2><p style="color:#595959">尊敬的 <b>{{attendeeName}}</b>，您好！</p><p style="color:#595959">您的入会凭证已生成，请在入场时出示二维码。</p><div style="background:#f9f0ff;border-left:4px solid #9b59b6;padding:12px 16px;margin:16px 0;border-radius:0 4px 4px 0"><p style="margin:0;color:#595959">凭证编号：<b>{{credentialNo}}</b></p><p style="margin:8px 0 0;color:#595959">座位信息：<b>{{seatInfo}}</b></p><p style="margin:8px 0 0;color:#595959">有效期至：<b>{{expireDate}}</b></p></div><p style="color:#8c8c8c;font-size:13px">请登录系统查看完整凭证及二维码。</p></div>',
 '{"attendeeName":"参会者姓名","credentialNo":"凭证编号","seatInfo":"座位信息","expireDate":"凭证有效期"}',
 1);

-- 8. 会议提醒
INSERT INTO `email_template` (`scene_code`, `scene_name`, `subject`, `body`, `variables_desc`, `is_enabled`) VALUES
('MEETING_REMINDER', '会议提醒通知',
 '【会务通系统】会议即将开始提醒',
 '<div style="font-family:Arial,sans-serif;max-width:480px;margin:0 auto;padding:24px;border:1px solid #e8e8e8;border-radius:8px"><h2 style="color:#9b59b6;margin-bottom:16px">学术会议系统</h2><p style="color:#595959">尊敬的 <b>{{attendeeName}}</b>，您好！</p><p style="color:#595959">您参加的学术会议将于 <b>{{conferenceDate}}</b> 开始，请提前做好准备。</p><div style="background:#f9f0ff;border-left:4px solid #9b59b6;padding:12px 16px;margin:16px 0;border-radius:0 4px 4px 0"><p style="margin:0;color:#595959">会议名称：<b>{{conferenceName}}</b></p><p style="margin:8px 0 0;color:#595959">会议地点：<b>{{venue}}</b></p><p style="margin:8px 0 0;color:#595959">开始时间：<b>{{conferenceDate}}</b></p></div><p style="color:#8c8c8c;font-size:13px">请携带入会凭证按时参加。</p></div>',
 '{"attendeeName":"参会者姓名","conferenceName":"会议名称","venue":"会议地点","conferenceDate":"会议日期时间"}',
 1);
