# Admin管理端设计文档

## 1. 项目概述

Admin管理端是学术会议管理平台的后台管理系统，为管理员提供全面的会议管理功能，包括用户管理、投稿管理、票务管理、订单管理、字典管理等核心模块。

### 1.1 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 渐进式JavaScript框架 |
| TypeScript | 5.x | JavaScript超集，提供类型安全 |
| Vite | 5.x | 下一代前端构建工具 |
| Element Plus | 最新 | Vue 3 UI组件库 |
| Pinia | 2.x | Vue 状态管理库 |
| Vue Router | 4.x | Vue 官方路由 |
| Axios | latest | HTTP 客户端 |
| SCSS | latest | CSS 预处理器 |
| ESLint + Prettier | latest | 代码规范工具 |

### 1.2 目录结构

```
admin/
├── public/                 # 静态资源
│   ├── favicon.ico
│   └── index.html
├── src/
│   ├── api/              # API接口层
│   │   ├── user.ts
│   │   ├── submission.ts
│   │   ├── ticket.ts
│   │   ├── order.ts
│   │   ├── credential.ts
│   │   └── dictionary.ts
│   ├── assets/           # 静态资源
│   │   ├── images/
│   │   ├── styles/
│   │   └── icons/
│   ├── components/       # 公共组件
│   │   ├── layout/
│   │   │   ├── Header.vue
│   │   │   ├── Sidebar.vue
│   │   │   └── Footer.vue
│   │   ├── common/
│   │   │   ├── TablePage.vue
│   │   │   ├── SearchForm.vue
│   │   │   └── DialogForm.vue
│   ├── composables/      # 组合式函数
│   │   ├── useAuth.ts
│   │   ├── useTable.ts
│   │   ├── usePagination.ts
│   │   └── useDialog.ts
│   ├── router/           # 路由配置
│   │   └── index.ts
│   ├── stores/           # Pinia状态管理
│   │   ├── auth.ts
│   │   ├── user.ts
│   │   └── app.ts
│   ├── styles/           # 全局样式
│   │   ├── variables.scss    # SCSS变量
│   │   ├── mixins.scss      # SCSS混合
│   │   └── index.scss      # 主样式
│   ├── types/            # TypeScript类型定义
│   │   ├── api.d.ts
│   │   └── common.d.ts
│   ├── utils/            # 工具函数
│   │   ├── request.ts      # Axios封装
│   │   ├── storage.ts      # 本地存储
│   │   ├── validate.ts     # 表单验证
│   │   └── format.ts       # 格式化工具
│   └── views/            # 页面组件
│       ├── dashboard/
│       │   └── index.vue
│       ├── user/
│       │   ├── index.vue
│       │   └── detail.vue
│       ├── submission/
│       │   ├── index.vue
│       │   ├── detail.vue
│       │   └── assign.vue
│       ├── ticket/
│       │   └── index.vue
│       ├── order/
│       │   ├── index.vue
│       │   └── detail.vue
│       ├── credential/
│       │   └── index.vue
│       ├── dictionary/
│       │   └── index.vue
│       └── login/
│           └── index.vue
├── .env.development      # 开发环境变量
├── .env.production       # 生产环境变量
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

## 2. 系统布局设计

### 2.1 整体布局

```
┌────────────────────────────────────────────────────────────────────┐
│  Header (顶部导航栏)                                      │
│  Logo | 面包屑 | 用户下拉 | 主题切换 | 全屏 │
├────────────────────────────────────────────────────────────────────┤
│              │                                       │    │
│              │                                       │    │
│   Sidebar    │           Main Content Area              │    │
│   (左侧菜单)  │                                       │    │
│              │    ┌─────────────────────────┐      │    │
│   Dashboard  │    │                     │      │    │
│   用户管理     │    │      页面内容          │      │    │
│   投稿管理     │    │                     │      │    │
│   票务管理     │    │                     │      │    │
│   订单管理     │    │                     │      │    │
│   凭证管理     │    │                     │      │    │
│   字典管理     │    └─────────────────────────┘      │    │
│              │                                       │    │
│              │                                       │    │
└──────────────┴───────────────────────────────────────┴────┴────┘
```

### 2.2 顶部导航栏 (Header)

| 组件 | 说明 |
|------|------|
| Logo | 系统Logo和名称，点击跳转首页 |
| 面包屑 | 显示当前页面路径 |
| 用户下拉 | 显示当前用户信息，包含退出登录选项 |
| 主题切换 | 切换浅色/深色主题 |
| 全屏 | 全屏显示/退出全屏 |

### 2.3 左侧菜单 (Sidebar)

菜单采用折叠式设计，支持图标+文字展示：

| 一级菜单 | 二级菜单 | 说明 |
|---------|---------|------|
| Dashboard | - | 首页仪表盘 |
| 用户管理 | 用户列表、用户详情 | 用户管理功能 |
| 投稿管理 | 投稿列表、分配审稿人 | 投稿管理功能 |
| 票务管理 | - | 票务管理功能 |
| 订单管理 | 订单列表、退款管理 | 订单管理功能 |
| 凭证管理 | 凭证列表、核销记录 | 凭证管理功能 |
| 字典管理 | - | 字典和字典项管理 |

## 3. 功能模块设计

### 3.1 首页 (Dashboard)

#### 3.1.1 数据统计卡片

```
┌─────────────┬─────────────┬─────────────┬─────────────┐
│  用户总数   │  投稿总数   │  票务总数   │  订单总数   │
│   1,234    │    856      │      12      │   3,456     │
│   +12% ↑    │   +5% ↑     │    -2 ↓     │   +8% ↑     │
└─────────────┴─────────────┴─────────────┴─────────────┘
```

#### 3.1.2 数据趋势图表

- 用户增长趋势（折线图，近7天）
- 投稿提交趋势（折线图，近7天）
- 订单销售趋势（折线图，近30天）

#### 3.1.3 最新动态列表

| 类型 | 说明 | 示例 |
|------|------|------|
| 新用户 | 新注册用户 | 用户 张三 刚刚注册 |
| 新投稿 | 新提交投稿 | 李四 提交了《AI研究》 |
| 新订单 | 新创建订单 | 王五 购买了普通票 |
| 退款申请 | 新退款申请 | 用户 赵六 申请退款 |

### 3.2 用户管理

#### 3.2.1 用户列表页面

**页面功能：**
- 分页查询用户列表
- 按角色、状态、关键词搜索
- 查看用户详情
- 修改用户状态（启用/禁用）
- 修改用户角色

**搜索表单：**
- 关键词（姓名/邮箱）
- 角色（下拉框：全部、讲者、审稿人、参会者、管理员）
- 状态（下拉框：全部、启用、禁用）

**表格列：**

| 列名 | 说明 |
|------|------|
| 用户ID | 系统生成的用户ID |
| 邮箱 | 用户登录邮箱 |
| 姓名 | 用户真实姓名 |
| 角色 | 角色名称，带颜色标签 |
| 状态 | 状态标签（启用绿色、禁用红色）|
| 头像 | 头像预览 |
| 注册时间 | 注册日期时间 |
| 操作 | 详情、编辑、删除按钮 |

**操作按钮：**
- 详情：查看用户完整信息
- 编辑：修改用户信息
- 禁用/启用：切换用户状态
- 删除：删除用户（二次确认）

#### 3.2.2 用户详情对话框

```
┌─────────────────────────────────────────────────┐
│              用户详情                        │
├─────────────────────────────────────────────────┤
│  邮箱:      zhangsan@example.com        │
│  姓名:       张三                          │
│  角色:       [讲者]                       │
│  状态:       [启用]                        │
│  头像:       [预览图]                      │
│  注册时间:    2026-02-25 10:30:00        │
│  最后登录:    2026-02-25 14:20:00        │
├─────────────────────────────────────────────────┤
│          [取消]      [保存]                   │
└─────────────────────────────────────────────────┘
```

### 3.3 投稿管理

#### 3.3.1 投稿列表页面

**页面功能：**
- 分页查询投稿列表
- 按状态、分类、讲者搜索
- 查看投稿详情
- 分配审稿人
- 修改投稿状态

**搜索表单：**
- 关键词（标题）
- 状态（下拉框：全部、待审稿、审稿中、已录用、已拒绝）
- 分类（下拉框：全部、人工智能、大数据等）

**表格列：**

| 列名 | 说明 |
|------|------|
| 投稿ID | 系统生成的ID |
| 投稿标题 | 投稿论文标题 |
| 讲者姓名 | 投稿者名称 |
| 分类 | 投稿分类 |
| 状态 | 状态标签，带颜色 |
| 审稿人数 | 已分配的审稿人数量 |
| 提交时间 | 投稿提交日期时间 |
| 操作 | 详情、分配、编辑、删除按钮 |

**操作按钮：**
- 详情：查看投稿完整内容和审稿记录
- 分配：分配审稿人（仅待审稿状态）
- 编辑：修改投稿信息
- 删除：删除投稿（二次确认）

#### 3.3.2 投稿详情页面

**页面布局：**

```
┌────────────────────────────────────────────────────────┐
│              投稿详情                              │
├────────────────────────────────────────────────────────┤
│  标题:       人工智能在会议管理中的应用             │
│  讲者:       张三 (zhangsan@example.com)         │
│  分类:       人工智能                           │
│  状态:       [审稿中] (黄色标签)               │
│  摘要:       本文研究了...                    │
│  附件:       [下载论文.pdf]                     │
├────────────────────────────────────────────────────────┤
│              审稿记录                            │
│  ┌─────────────────────────────────────┐      │
│  │审稿人  │结果  │评分  │意见  │时间   │      │
│  │李四    │录用  │ 85  │优秀  │02-26 │      │
│  │王五    │录用  │ 90  │优秀  │02-27 │      │
│  └─────────────────────────────────────┘      │
├────────────────────────────────────────────────────────┤
│          [返回列表]    [分配审稿人]    [修改状态]   │
└────────────────────────────────────────────────────────┘
```

#### 3.3.3 分配审稿人对话框

```
┌─────────────────────────────────────────────────┐
│              分配审稿人                      │
├─────────────────────────────────────────────────┤
│  投稿标题:    人工智能在会议管理中的应用       │
│  投稿ID:     123                          │
├─────────────────────────────────────────────────┤
│  可选审稿人（多选）:                  │
│  ┌─────────────┬─────────────┐           │
│  │ ☑ 李四     │ ☑ 王五     │           │
│  │ ☑ 赵六     │ ☐ 孙七     │           │
│  │ ☐ 周八     │ ☐ 吴九     │           │
│  └─────────────┴─────────────┘           │
├─────────────────────────────────────────────────┤
│          [取消]      [确认分配]               │
└─────────────────────────────────────────────────┘
```

### 3.4 票务管理

#### 3.4.1 票务列表页面

**页面功能：**
- 分页查询票务列表
- 按状态搜索
- 创建新票务
- 编辑票务信息
- 修改票务状态（上架/下架）
- 调整库存

**搜索表单：**
- 状态（下拉框：全部、上架、下架）

**表格列：**

| 列名 | 说明 |
|------|------|
| 票务ID | 系统生成的ID |
| 票务名称 | 票务类型名称 |
| 价格 | 票务价格，单位：元 |
| 库存 | 总库存数量 |
| 已售 | 已售出数量 |
| 可用 | 可用数量（库存-已售）|
| 状态 | 状态标签（上架绿色、下架灰色）|
| 创建时间 | 创建日期时间 |
| 操作 | 详情、编辑、下架、删除按钮 |

**操作按钮：**
- 详情：查看票务完整信息
- 编辑：修改票务信息
- 上架/下架：切换票务状态
- 调整库存：增加或减少库存
- 删除：删除票务（二次确认）

#### 3.4.2 票务表单对话框

```
┌─────────────────────────────────────────────────┐
│              创建/编辑票务                    │
├─────────────────────────────────────────────────┤
│  票务名称*: [________________________]         │
│  价格*:       [_____] 元                      │
│  库存*:       [_____] 张                      │
│  描述:       [________________________]         │
│              (支持Markdown编辑)                  │
│  排序:       [___]                            │
├─────────────────────────────────────────────────┤
│          [取消]      [保存]                   │
└─────────────────────────────────────────────────┘
```

#### 3.4.3 调整库存对话框

```
┌─────────────────────────────────────────────────┐
│              调整库存                          │
├─────────────────────────────────────────────────┤
│  票务名称:    普通票                          │
│  当前库存:    1000 张                          │
│  已售数量:    456 张                          │
│  可用库存:    544 张                           │
├─────────────────────────────────────────────────┤
│  调整类型:    ○ 增加库存  ● 减少库存      │
│  调整数量:    [_____] 张                       │
│  调整原因:    [________________________]         │
├─────────────────────────────────────────────────┤
│          [取消]      [确认调整]               │
└─────────────────────────────────────────────────┘
```

### 3.5 订单管理

#### 3.5.1 订单列表页面

**页面功能：**
- 分页查询订单列表
- 按状态、订单号、参会人搜索
- 查看订单详情
- 处理退款申请

**搜索表单：**
- 订单号（输入框）
- 状态（下拉框：全部、未支付、已支付、已取消、已退款）
- 参会人（姓名/邮箱）

**表格列：**

| 列名 | 说明 |
|------|------|
| 订单号 | 唯一订单号，点击可复制 |
| 参会人姓名 | 参会者真实姓名 |
| 参会人邮箱 | 参会者联系邮箱 |
| 票务名称 | 购买的票务类型 |
| 数量 | 购票数量 |
| 总金额 | 订单总金额，单位：元 |
| 状态 | 状态标签，带颜色 |
| 支付时间 | 支付完成时间 |
| 创建时间 | 订单创建时间 |
| 操作 | 详情、退款按钮 |

**操作按钮：**
- 详情：查看订单完整信息
- 退款：处理退款申请（仅已支付状态）

#### 3.5.2 订单详情页面

```
┌─────────────────────────────────────────────────┐
│              订单详情                          │
├─────────────────────────────────────────────────┤
│  订单号:     ORD20260225143000123456    │
│  参会人:     张三 (zhangsan@example.com)      │
│  票务类型:   普通票                          │
│  购票数量:   1 张                             │
│  订单金额:   ¥499.00                         │
│  订单状态:   [已支付] (绿色标签)               │
│  支付时间:   2026-02-25 15:30:00           │
├─────────────────────────────────────────────────┤
│              凭证信息                            │
│  ┌─────────────────────────────────────┐      │
│  │凭证号     │状态  │座位     │使用时间 │      │
│  │CRD...    │有效  │A区3排5座 │ -      │      │
│  └─────────────────────────────────────┘      │
├─────────────────────────────────────────────────┤
│          [返回列表]    [处理退款]               │
└─────────────────────────────────────────────────┘
```

#### 3.5.3 退款处理对话框

```
┌─────────────────────────────────────────────────┐
│              处理退款申请                      │
├─────────────────────────────────────────────────┤
│  订单号:     ORD20260225143000123456    │
│  参会人:     张三                          │
│  票务类型:   普通票 (1张)                  │
│  订单金额:   ¥499.00                         │
│  申请原因:   因个人原因无法参加               │
│  申请时间:   2026-02-25 16:00:00           │
├─────────────────────────────────────────────────┤
│  审核结果:    ● 通过  ○ 拒绝                │
│  拒绝原因:    [________________________]         │
├─────────────────────────────────────────────────┤
│          [取消]      [确认处理]               │
└─────────────────────────────────────────────────┘
```

### 3.6 凭证管理

#### 3.6.1 凭证列表页面

**页面功能：**
- 分页查询凭证列表
- 按状态搜索
- 查看凭证详情
- 查看核销记录

**搜索表单：**
- 凭证号（输入框）
- 状态（下拉框：全部、有效、已使用、已过期）

**表格列：**

| 列名 | 说明 |
|------|------|
| 凭证号 | 唯一凭证号 |
| 参会人姓名 | 参会者真实姓名 |
| 参会人邮箱 | 参会者联系邮箱 |
| 票务类型 | 对应的票务 |
| 座位信息 | 座位位置 |
| 状态 | 状态标签，带颜色 |
| 使用时间 | 核销时间，未使用则显示- |
| 操作 | 详情按钮 |

**操作按钮：**
- 详情：查看凭证完整信息
- 核销记录：查看该凭证的核销历史

#### 3.6.2 凭证核销记录页面

**页面功能：**
- 分页查询核销记录
- 按日期、凭证号搜索

**搜索表单：**
- 凭证号（输入框）
- 日期范围（日期选择器）
- 操作人员（下拉框）

**表格列：**

| 列名 | 说明 |
|------|------|
| 核销ID | 系统生成的ID |
| 凭证号 | 被核销的凭证号 |
| 参会人姓名 | 参会者姓名 |
| 操作人员 | 执行核销的工作人员 |
| 核销时间 | 核销日期时间 |
| 位置 | 核销地点 |

### 3.7 字典管理

#### 3.7.1 字典列表页面

**页面功能：**
- 分页查询字典列表
- 查看字典详情
- 创建新字典
- 编辑字典信息

**搜索表单：**
- 状态（下拉框：全部、启用、禁用）

**表格列：**

| 列名 | 说明 |
|------|------|
| 字典ID | 系统生成的ID |
| 字典编码 | 唯一字典编码 |
| 字典名称 | 字典中文名称 |
| 描述 | 字典说明 |
| 状态 | 状态标签（启用绿色、禁用灰色）|
| 字典项数 | 包含的字典项数量 |
| 排序 | 排序号 |
| 操作 | 详情、编辑、删除、字典项按钮 |

**操作按钮：**
- 详情：查看字典完整信息
- 编辑：修改字典信息
- 字典项：查看和管理字典项
- 删除：删除字典（二次确认）

#### 3.7.2 字典详情页面

**页面布局：**

```
┌─────────────────────────────────────────────────┐
│              字典详情 - 用户角色              │
├─────────────────────────────────────────────────┤
│  字典编码:    user_role                      │
│  字典名称:    用户角色                        │
│  描述:        系统用户的角色类型                │
│  状态:        [启用] (绿色标签)               │
│  排序:        1                              │
├─────────────────────────────────────────────────┤
│              字典项                            │
│  ┌─────────────────────────────────────┐      │
│  │编码   │值   │名称   │状态  │排序   │      │
│  │speaker │1    │讲者  │启用  │ 1     │      │
│  │reviewer│2    │审稿人│启用  │ 2     │      │
│  │attendee│3    │参会者│启用  │ 3     │      │
│  │admin   │4    │管理员│启用  │ 4     │      │
│  └─────────────────────────────────────┘      │
├─────────────────────────────────────────────────┤
│  [添加字典项]  [返回列表]                     │
└─────────────────────────────────────────────────┘
```

#### 3.7.3 字典项表单对话框

```
┌─────────────────────────────────────────────────┐
│              创建/编辑字典项                 │
├─────────────────────────────────────────────────┤
│  字典编码*:    user_role (不可修改)          │
│  编码*:       [speaker]                      │
│  名称*:       [讲者]                        │
│  值*:         [1]                           │
│  描述:         [投稿讲者，可以提交论文投稿]     │
│  排序:         [___]                          │
├─────────────────────────────────────────────────┤
│          [取消]      [保存]                   │
└─────────────────────────────────────────────────┘
```

## 4. 公共组件设计

### 4.1 TablePage 组件

分页表格组件，封装Element Plus的Table和Pagination：

```vue
<template>
  <div class="table-page">
    <el-table :data="data" v-loading="loading">
      <el-table-column
        v-for="column in columns"
        :key="column.prop"
        :prop="column.prop"
        :label="column.label"
        :width="column.width"
      >
        <template #default="{ row }">
          <slot :name="column.prop" :row="row" />
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:page="currentPage"
      v-model:page-size="pageSize"
      :total="total"
      @current-change="handlePageChange"
    />
  </div>
</template>
```

### 4.2 SearchForm 组件

搜索表单组件，支持多条件搜索：

```vue
<template>
  <el-form :model="form" inline>
    <el-form-item
      v-for="field in fields"
      :key="field.prop"
      :label="field.label"
    >
      <el-input v-if="field.type === 'input'" v-model="form[field.prop]" />
      <el-select v-if="field.type === 'select'" v-model="form[field.prop]">
        <el-option label="全部" value="" />
        <el-option
          v-for="option in field.options"
          :key="option.value"
          :label="option.label"
          :value="option.value"
        />
      </el-select>
    </el-form-item>
    <el-button type="primary" @click="handleSearch">搜索</el-button>
    <el-button @click="handleReset">重置</el-button>
  </el-form>
</template>
```

### 4.3 DialogForm 组件

对话框表单组件，支持新增和编辑：

```vue
<template>
  <el-dialog
    v-model="visible"
    :title="title"
    width="600px"
  >
    <el-form :model="form" :rules="rules" ref="formRef">
      <el-form-item
        v-for="field in fields"
        :key="field.prop"
        :label="field.label"
        :prop="field.prop"
      >
        <el-input v-if="field.type === 'input'" v-model="form[field.prop]" />
        <el-input
          v-if="field.type === 'textarea'"
          type="textarea"
          v-model="form[field.prop]"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>
```

### 4.4 StatusTag 组件

状态标签组件，根据状态显示不同颜色：

```vue
<template>
  <el-tag :type="tagType">{{ statusText }}</el-tag>
</template>

<script setup lang="ts">
const props = defineProps({
  status: {
    type: Number,
    required: true
  }
})

const statusMap = {
  1: { text: '启用/上架/有效', type: 'success' },
  2: { text: '审稿中', type: 'warning' },
  3: { text: '已录用/已支付', type: 'success' },
  4: { text: '已拒绝/已退款', type: 'danger' }
}

const current = statusMap[props.status] || { text: '未知', type: 'info' }
const statusText = current.text
const tagType = current.type
</script>
```

## 5. 路由设计

### 5.1 路由配置

```typescript
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', icon: 'DataAnalysis' }
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'submission',
        name: 'Submission',
        component: () => import('@/views/submission/index.vue'),
        meta: { title: '投稿管理', icon: 'Document' }
      },
      {
        path: 'ticket',
        name: 'Ticket',
        component: () => import('@/views/ticket/index.vue'),
        meta: { title: '票务管理', icon: 'Ticket' }
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('@/views/order/index.vue'),
        meta: { title: '订单管理', icon: 'ShoppingCart' }
      },
      {
        path: 'credential',
        name: 'Credential',
        component: () => import('@/views/credential/index.vue'),
        meta: { title: '凭证管理', icon: 'Tickets' }
      },
      {
        path: 'dictionary',
        name: 'Dictionary',
        component: () => import('@/views/dictionary/index.vue'),
        meta: { title: '字典管理', icon: 'Setting' }
      }
    ]
  }
]
```

### 5.2 路由守卫

```typescript
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  const token = authStore.token

  // 不需要登录的页面直接放行
  if (to.meta.requiresAuth === false) {
    next()
    return
  }

  // 未登录跳转到登录页
  if (!token) {
    next({ name: 'Login', query: { redirect: to.fullPath }})
    return
  }

  // 已登录访问登录页跳转到首页
  if (to.name === 'Login' && token) {
    next({ name: 'Dashboard' })
    return
  }

  next()
})
```

## 6. 状态管理 (Pinia)

### 6.1 Auth Store

```typescript
export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>('')
  const userInfo = ref<User | null>(null)

  function setToken(newToken: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  function setInfo(info: User) {
    userInfo.value = info
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    router.push('/login')
  }

  return { token, userInfo, setToken, setInfo, logout }
})
```

### 6.2 App Store

```typescript
export const useAppStore = defineStore('app', () => {
  const sidebarCollapsed = ref(false)
  const isDarkMode = ref(false)
  const breadcrumbs = ref<Breadcrumb[]>([])

  function toggleSidebar() {
    sidebarCollapsed.value = !sidebarCollapsed.value
  }

  function toggleTheme() {
    isDarkMode.value = !isDarkMode.value
    document.documentElement.className = isDarkMode.value ? 'dark' : ''
  }

  function setBreadcrumbs(crumbs: Breadcrumb[]) {
    breadcrumbs.value = crumbs
  }

  return {
    sidebarCollapsed,
    isDarkMode,
    breadcrumbs,
    toggleSidebar,
    toggleTheme,
    setBreadcrumbs
  }
})
```

## 7. API接口层设计

### 7.1 Axios封装

```typescript
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 15000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      config.headers.Authorization = `Bearer ${authStore.token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { code, message, data } = response.data

    if (code === 200) {
      return data
    }

    if (code === 401) {
      ElMessage.error('登录已过期，请重新登录')
      const authStore = useAuthStore()
      authStore.logout()
      return Promise.reject(new Error(message))
    }

    ElMessage.error(message || '系统繁忙，请稍后重试')
    return Promise.reject(new Error(message))
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)

export default request
```

### 7.2 API接口定义

```typescript
// api/user.ts
import request from '@/utils/request'

export interface User {
  id: number
  email: string
  name: string
  role: number
  status: number
  avatar: string
  createdAt: string
}

export interface UserListParams {
  page: number
  pageSize: number
  role?: number
  status?: number
  keyword?: string
}

export const userApi = {
  // 查询用户列表
  getList(params: UserListParams) {
    return request.get<User[]>('/admin/users', { params })
  },

  // 查询用户详情
  getDetail(id: number) {
    return request.get<User>(`/admin/users/${id}`)
  },

  // 修改用户状态
  updateStatus(id: number, status: number) {
    return request.put(`/admin/users/${id}/status`, { status })
  },

  // 修改用户角色
  updateRole(id: number, role: number) {
    return request.put(`/admin/users/${id}/role`, { role })
  },

  // 删除用户
  delete(id: number) {
    return request.delete(`/admin/users/${id}`)
  }
}
```

## 8. 设计规范

### 8.1 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 文件名 | kebab-case | user-list.vue |
| 组件名 | PascalCase | UserList.vue |
| 变量名 | camelCase | userName |
| 常量名 | UPPER_SNAKE_CASE | MAX_RETRY_COUNT |
| 函数名 | camelCase | getUserList |
| 类名 | PascalCase | UserService |

### 8.2 代码风格

1. 使用Vue 3 Composition API
2. 使用TypeScript严格模式
3. 组件使用`<script setup>`语法
4. 样式使用SCSS
5. 组件props和emits定义完整类型
6. 避免使用any类型

### 8.3 注释规范

```typescript
/**
 * 用户列表组件
 * @description 展示用户列表，支持分页、搜索、状态切换
 * @author Claude
 * @date 2026-02-25
 */
```

### 8.4 提交信息规范

Git commit message格式：

```
feat: 添加用户管理页面
fix: 修复订单状态标签显示错误
docs: 更新API接口文档
style: 优化表格样式
refactor: 重构公共组件
test: 添加单元测试
chore: 更新依赖包
```

## 9. 环境变量配置

```env
# 开发环境
VITE_APP_TITLE=学术会议管理平台
VITE_API_BASE_URL=http://localhost:8080/api
VITE_UPLOAD_URL=http://localhost:9000
VITE_APP_ENV=development
```

```env
# 生产环境
VITE_APP_TITLE=学术会议管理平台
VITE_API_BASE_URL=https://api.example.com/api
VITE_UPLOAD_URL=https://oss.example.com
VITE_APP_ENV=production
```

## 10. 构建和部署

### 10.1 开发命令

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 类型检查
npm run type-check

# 代码检查
npm run lint

# 修复代码格式
npm run format
```

### 10.2 生产构建

```bash
# 构建生产版本
npm run build

# 输出目录
dist/
```

### 10.3 部署说明

```bash
# 部署步骤
1. 执行 npm run build 构建项目
2. 将 dist/ 目录内容上传到服务器
3. 配置Nginx反向代理到后端API
4. 配置SSL证书（生产环境）
```

---

**文档版本：** v1.0
**最后更新：** 2026-02-25
