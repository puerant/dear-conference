# SQL 脚本管理文档

> 项目：huiwutong 学术会议管理平台
> 最后更新：2026-03-03

---

## 脚本说明

所有 SQL 脚本统一存放在 `docs/开发文档/sql/` 目录下。

---

## 脚本目录结构

```
sql/
├── migration/                    # 数据库迁移脚本目录
│   ├── 01_init_database.sql    # 数据库和基础表初始化
│   ├── 02_init_system_tables.sql # 系统模块表初始化
│   ├── 03_admin_user.sql       # 默认管理员账户
│   ├── 04_group_ticket_migration.sql # 团体票功能迁移
│   ├── 05_credential_expire_at.sql # 凭证过期时间字段添加
│   ├── 06_init_email_templates.sql # 邮件模板初始化
│   ├── 07_email_config_multi_and_log.sql # 邮箱多配置与发送日志
│   └── 08_file_module_oss.sql # 文件模块（多OSS供应商）
```

---

## 执行顺序

**重要**：必须按照以下顺序执行 SQL 脚本，确保依赖关系正确。

| 序号 | 脚本文件 | 说明 |
|------|-------------|------|
| 1 | migration/01_init_database.sql | 创建数据库、基础表结构、字典数据 |
| 2 | migration/02_init_system_tables.sql | 系统模块表（邮箱、凭证、支付、定时任务、文件） |
| 3 | migration/03_admin_user.sql | 创建默认管理员账户 |
| 4 | migration/04_group_ticket_migration.sql | 团体票功能迁移（票种类型、订单类型、团体成员） |
| 5 | migration/05_credential_expire_at.sql | 凭证表添加 expire_at 字段 |
| 6 | migration/06_init_email_templates.sql | 邮件模板初始化 |
| 7 | migration/07_email_config_multi_and_log.sql | 邮箱多配置与邮件发送日志 |
| 8 | migration/08_file_module_oss.sql | 文件模块（多OSS供应商）迁移 |

---

## 表清单

### 基础表（01_init_database.sql）
- `dictionary` - 字典表
- `dictionary_item` - 字典项表
- `users` - 用户表
- `submissions` - 投稿表
- `reviews` - 审稿记录表
- `tickets` - 票务表
- `orders` - 订单表
- `credentials` - 入会凭证表
- `payment_records` - 支付记录表
- `refund_records` - 退款记录表

### 系统模块表（02_init_system_tables.sql）
- `email_config` - 邮箱配置表
- `credential_config` - 凭证配置表
- `payment_config` - 支付配置表
- `order_task_config` - 订单定时任务配置表
- `system_file` - 系统文件表

### 迁移新增表（04_group_ticket_migration.sql）
- `group_members` - 团体成员表（新增）

### 迁移新增字段
- `tickets` 表：
  - `ticket_type` - 票种类型（1=个人票, 2=团体票）
  - `min_persons` - 团体最小购买人数
  - `max_persons` - 团体最大购买人数
- `orders` 表：
  - `order_type` - 订单类型（1=个人, 2=团体）
  - `group_name` - 团体名称
  - `invite_token` - 团体邀请链接 Token
- `credentials` 表：
  - `group_member_id` - 关联团体成员 ID（05_credential_expire_at.sql）
  - `expire_at` - 凭证有效期

---

## 执行方法

### 方法一：逐个执行

```bash
mysql -u root -p mclc < docs/开发文档/sql/migration/01_init_database.sql
mysql -u root -p mclc < docs/开发文档/sql/migration/02_init_system_tables.sql
mysql -u root -p mclc < docs/开发文档/sql/migration/03_admin_user.sql
mysql -u root -p mclc < docs/开发文档/sql/migration/04_group_ticket_migration.sql
mysql -u root -p mclc < docs/开发文档/sql/migration/05_credential_expire_at.sql
mysql -u root -p mclc < docs/开发文档/sql/migration/06_init_email_templates.sql
mysql -u root -p mclc < docs/开发文档/sql/migration/07_email_config_multi_and_log.sql
mysql -u root -p mclc < docs/开发文档/sql/migration/08_file_module_oss.sql
```

### 方法二：使用 source 命令

```bash
mysql -u root -p mclc
```

然后在 MySQL 命令行中依次执行：

```sql
source D:/Developer/Project/huiwutong/docs/开发文档/sql/migration/01_init_database.sql;
source D:/Developer/Project/huiwutong/docs/开发文档/sql/migration/02_init_system_tables.sql;
source D:/Developer/Project/huiwutong/docs/开发文档/sql/migration/03_admin_user.sql;
source D:/Developer/Project/huiwutong/docs/开发文档/sql/migration/04_group_ticket_migration.sql;
source D:/Developer/Project/huiwutong/docs/开发文档/sql/migration/05_credential_expire_at.sql;
source D:/Developer/Project/huiwutong/docs/开发文档/sql/migration/06_init_email_templates.sql;
source D:/Developer/Project/huiwutong/docs/开发文档/sql/migration/07_email_config_multi_and_log.sql;
source D:/Developer/Project/huiwutong/docs/开发文档/sql/migration/08_file_module_oss.sql;
```

---

## 初始管理员账户

- **邮箱**: `admin@huiwutong.com`
- **密码**: `Admin@2026`
- **角色**: 管理员 (role=4)
- **说明**: 默认管理员账户，首次登录后请立即修改密码

---

## 注意事项

1. **字符集**: 所有表使用 `utf8mb4` 字符集
2. **排序规则**: 中文排序使用 `COLLATE utf8mb4_unicode_ci`
3. **软删除**: system_file 表使用软删除（is_deleted 字段）
4. **加密存储**: 邮箱密码、支付密钥使用 AES 加密
5. **唯一约束**:
   - `email_config`: 无
   - `credential_config`: 无
   - `payment_config`: `uk_payment_type` (payment_type 唯一)
   - `order_task_config`: 无
   - `system_file`: 无
