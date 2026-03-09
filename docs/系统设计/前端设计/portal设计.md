# Portal 门户网站设计文档

## 1. 项目概述

### 1.1 项目简介

Portal 门户网站是学术会议管理系统的对外展示平台，面向投稿讲者、审稿人、参会者三类用户提供服务。网站采用现代简约的设计风格，以浅紫色为主题色，支持多语言国际化，提供良好的用户体验。

### 1.2 用户角色

| 角色 | 描述 | 主要功能 |
|------|------|----------|
| 投稿讲者 (Speaker) | 提交论文投稿的作者 | 投稿管理、查看审稿状态、接收通知 |
| 审稿人 (Reviewer) | 负责审阅投稿的专家 | 审稿管理、查看待审稿件、提交审稿意见 |
| 参会者 (Attendee) | 注册参会的普通用户 | 参会注册、票务购买、查看入会凭证 |

### 1.3 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 渐进式 JavaScript 框架 |
| TypeScript | 5.x | JavaScript 类型超集 |
| Vite | 5.x | 新一代前端构建工具 |
| Vue Router | 4.x | Vue 官方路由管理器 |
| Pinia | 2.x | Vue 官方状态管理库 |
| Element Plus | Latest | 基于 Vue 3 的组件库 |
| Axios | Latest | HTTP 请求库 |
| vue-i18n | 9.x | Vue 国际化插件 |
| SCSS | Latest | CSS 预处理器 |

---

## 2. 设计规范

### 2.1 主题色系

```scss
// 主题色 - 浅紫色
$primary-color: #9b59b6;        // 主色调
$primary-light: #bb8fce;        // 浅色变体
$primary-lighter: #d4b7db;     // 极浅色变体
$primary-dark: #7d3c98;         // 深色变体

// 辅助色
$secondary-color: #8e44ad;      // 次要色（深紫）
$accent-color: #e74c3c;         // 强调色（红色）
$success-color: #27ae60;         // 成功色（绿色）
$warning-color: #f39c12;         // 警告色（橙色）
$info-color: #3498db;           // 信息色（蓝色）

// 中性色
$text-primary: #2c3e50;         // 主要文字
$text-secondary: #7f8c8d;       // 次要文字
$text-disabled: #bdc3c7;         // 禁用文字
$border-color: #e5e7eb;         // 边框色
$divider-color: #f0f0f0;        // 分割线色

// 背景色
$bg-primary: #ffffff;             // 主背景
$bg-secondary: #f9f9fb;         // 次背景
$bg-tertiary: #f3f4f6;         // 三级背景

// 渐变色
$gradient-primary: linear-gradient(135deg, $primary-color, $secondary-color);
$gradient-light: linear-gradient(135deg, $primary-lighter, $primary-light);
```

### 2.2 字体系统

```scss
// 字体家族
$font-family-base: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
$font-family-heading: 'Noto Serif SC', 'Source Serif Pro', Georgia, serif;
$font-family-mono: 'Fira Code', 'Consolas', 'Monaco', monospace;

// 字号
$font-size-xs: 12px;
$font-size-sm: 14px;
$font-size-base: 16px;
$font-size-lg: 18px;
$font-size-xl: 20px;
$font-size-2xl: 24px;
$font-size-3xl: 32px;
$font-size-4xl: 40px;
$font-size-5xl: 48px;

// 字重
$font-weight-light: 300;
$font-weight-normal: 400;
$font-weight-medium: 500;
$font-weight-semibold: 600;
$font-weight-bold: 700;

// 行高
$line-height-tight: 1.25;
$line-height-normal: 1.5;
$line-height-relaxed: 1.75;
```

### 2.3 间距系统

```scss
// 间距单位
$spacing-0: 0;
$spacing-1: 4px;
$spacing-2: 8px;
$spacing-3: 12px;
$spacing-4: 16px;
$spacing-5: 20px;
$spacing-6: 24px;
$spacing-8: 32px;
$spacing-10: 40px;
$spacing-12: 48px;
$spacing-16: 64px;
$spacing-20: 80px;
$spacing-24: 96px;
```

### 2.4 圆角与阴影

```scss
// 圆角
$radius-none: 0;
$radius-sm: 4px;
$radius-base: 8px;
$radius-lg: 12px;
$radius-xl: 16px;
$radius-2xl: 24px;
$radius-full: 9999px;

// 阴影
$shadow-sm: 0 1px 2px 0 rgba(155, 89, 182, 0.05);
$shadow-base: 0 1px 3px 0 rgba(155, 89, 182, 0.1);
$shadow-md: 0 4px 6px -1px rgba(155, 89, 182, 0.1), 0 2px 4px -1px rgba(155, 89, 182, 0.06);
$shadow-lg: 0 10px 15px -3px rgba(155, 89, 182, 0.1), 0 4px 6px -2px rgba(155, 89, 182, 0.05);
$shadow-xl: 0 20px 25px -5px rgba(155, 89, 182, 0.1), 0 10px 10px -5px rgba(155, 89, 182, 0.04);
```

### 2.5 响应式断点

```scss
// 断点
$breakpoint-xs: 480px;
$breakpoint-sm: 576px;
$breakpoint-md: 768px;
$breakpoint-lg: 992px;
$breakpoint-xl: 1200px;
$breakpoint-2xl: 1400px;

// Mixins
@mixin respond-to($breakpoint) {
  @if $breakpoint == xs {
    @media (max-width: $breakpoint-xs) { @content; }
  } @else if $breakpoint == sm {
    @media (max-width: $breakpoint-sm) { @content; }
  } @else if $breakpoint == md {
    @media (max-width: $breakpoint-md) { @content; }
  } @else if $breakpoint == lg {
    @media (max-width: $breakpoint-lg) { @content; }
  } @else if $breakpoint == xl {
    @media (max-width: $breakpoint-xl) { @content; }
  } @else if $breakpoint == 2xl {
    @media (max-width: $breakpoint-2xl) { @content; }
  }
}
```

---

## 3. 国际化配置 (vue-i18n)

### 3.1 目录结构

```
src/
├── i18n/
│   ├── index.ts              # i18n 配置文件
│   ├── locales/
│   │   ├── zh-CN/           # 简体中文
│   │   │   ├── common.ts
│   │   │   ├── home.ts
│   │   │   ├── venue.ts
│   │   │   └── schedule.ts
│   │   ├── en-US/           # 英文
│   │   │   ├── common.ts
│   │   │   ├── home.ts
│   │   │   ├── venue.ts
│   │   │   └── schedule.ts
│   │   └── zh-TW/           # 繁体中文
│   │       ├── common.ts
│   │       ├── home.ts
│   │       ├── venue.ts
│   │       └── schedule.ts
```

### 3.2 配置文件

```typescript
// src/i18n/index.ts
import { createI18n } from 'vue-i18n'
import zhCN from './locales/zh-CN'
import enUS from './locales/en-US'
import zhTW from './locales/zh-TW'

const messages = {
  'zh-CN': zhCN,
  'en-US': enUS,
  'zh-TW': zhTW
}

const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem('locale') || 'zh-CN',
  fallbackLocale: 'zh-CN',
  messages,
  globalInjection: true
})

export default i18n

// 支持的语言列表
export const languages = [
  { code: 'zh-CN', name: '简体中文', flag: '🇨🇳' },
  { code: 'en-US', name: 'English', flag: '🇺🇸' },
  { code: 'zh-TW', name: '繁體中文', flag: '🇹🇼' }
]
```

### 3.3 语言切换组件

```vue
<!-- src/components/common/LangSwitcher.vue -->
<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { languages } from '@/i18n'
import { ElMessage } from 'element-plus'

const { locale } = useI18n()

const currentLang = computed({
  get: () => locale.value,
  set: (value: string) => {
    locale.value = value
    localStorage.setItem('locale', value)
    ElMessage.success('语言切换成功')
  }
})
</script>

<template>
  <el-dropdown trigger="click" @command="currentLang = $event">
    <span class="lang-switcher">
      {{ languages.find(l => l.code === currentLang)?.flag }} {{ languages.find(l => l.code === currentLang)?.name }}
      <el-icon><arrow-down /></el-icon>
    </span>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item
          v-for="lang in languages"
          :key="lang.code"
          :command="lang.code"
          :class="{ active: lang.code === currentLang }"
        >
          {{ lang.flag }} {{ lang.name }}
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<style scoped lang="scss">
.lang-switcher {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.3s;

  &:hover {
    background: $bg-secondary;
  }
}

.active {
  color: $primary-color;
  font-weight: 600;
}
</style>
```

### 3.4 翻译示例

```typescript
// src/i18n/locales/zh-CN/home.ts
export default {
  hero: {
    title: '2026 学术会议',
    subtitle: '汇聚学术精英，共创美好未来',
    description: '欢迎参加2026年度学术会议，与来自全球的学者、专家共同探讨前沿学术话题。',
    cta: '立即报名'
  },
  features: {
    title: '会议亮点',
    submission: '投稿讲者可以提交学术论文',
    review: '审稿人专家审阅投稿内容',
    attend: '参会者购买票务参加会议'
  },
  news: {
    title: '最新公告',
    more: '查看更多'
  }
}

// src/i18n/locales/en-US/home.ts
export default {
  hero: {
    title: '2026 Academic Conference',
    subtitle: 'Gather academic elites, create a better future',
    description: 'Welcome to the 2026 Academic Conference to discuss frontier academic topics with scholars and experts from around the world.',
    cta: 'Register Now'
  },
  features: {
    title: 'Conference Highlights',
    submission: 'Speakers can submit academic papers',
    review: 'Reviewers review submissions',
    attend: 'Attendees purchase tickets to attend'
  },
  news: {
    title: 'Latest News',
    more: 'View More'
  }
}
```

---

## 4. 页面结构

### 4.1 整体布局

```
┌─────────────────────────────────────────────────────────────┐
│                    顶部导航栏 (Header)                    │
│  Logo | 首页 | 会场信息 | 日程 | 投稿 | 语言 | 登录  │
├─────────────────────────────────────────────────────────────┤
│                                                       │
│                     主内容区域 (Main)                      │
│                                                       │
│                                                       │
│                                                       │
├─────────────────────────────────────────────────────────────┤
│                    底部信息栏 (Footer)                    │
│           关于我们 | 联系方式 | 版权信息                   │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 路由配置

```typescript
// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/home/Index.vue'),
    meta: { title: 'home.title' }
  },
  {
    path: '/venue',
    name: 'Venue',
    component: () => import('@/views/venue/Index.vue'),
    meta: { title: 'venue.title' }
  },
  {
    path: '/schedule',
    name: 'Schedule',
    component: () => import('@/views/schedule/Index.vue'),
    meta: { title: 'schedule.title' }
  },
  {
    path: '/submission',
    name: 'Submission',
    component: () => import('@/views/submission/Index.vue'),
    meta: { title: 'submission.title', requiresAuth: true }
  },
  {
    path: '/submission/create',
    name: 'SubmissionCreate',
    component: () => import('@/views/submission/Create.vue'),
    meta: { title: 'submission.create', requiresAuth: true, roles: ['speaker'] }
  },
  {
    path: '/order/tickets',
    name: 'Tickets',
    component: () => import('@/views/order/Tickets.vue'),
    meta: { title: 'order.tickets', requiresAuth: true, roles: ['attendee'] }
  },
  {
    path: '/order/list',
    name: 'OrderList',
    component: () => import('@/views/order/List.vue'),
    meta: { title: 'order.list', requiresAuth: true, roles: ['attendee'] }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/profile/Index.vue'),
    meta: { title: 'profile.title', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局前置守卫 - 设置页面标题
router.beforeEach((to, from, next) => {
  const { t } = useI18n()
  document.title = `${t(to.meta.title as string)} - 学术会议系统`
  next()
})

export default router
```

---

## 5. 页面设计

### 5.1 首页 (Home)

#### 5.1.1 页面结构

```
┌─────────────────────────────────────────────────────────────┐
│                   Hero 区域 (全屏)                       │
│                                                       │
│              2026 学术会议                              │
│         汇聚学术精英，共创美好未来                         │
│                                                       │
│                [立即报名]  [了解更多]                       │
│                                                       │
├─────────────────────────────────────────────────────────────┤
│                   特性介绍区域                             │
│   ┌─────────┐  ┌─────────┐  ┌─────────┐              │
│   │ 投稿讲者 │  │  审稿人  │  │  参会者  │              │
│   │   图标   │  │   图标   │  │   图标   │              │
│   └─────────┘  └─────────┘  └─────────┘              │
│                                                       │
├─────────────────────────────────────────────────────────────┤
│                   最新公告区域                             │
│   ┌─────────────────────────────────────────────────┐      │
│   │ 公告1                    [2026-02-25]         │      │
│   └─────────────────────────────────────────────────┘      │
│   ┌─────────────────────────────────────────────────┐      │
│   │ 公告2                    [2026-02-24]         │      │
│   └─────────────────────────────────────────────────┘      │
│                                                       │
├─────────────────────────────────────────────────────────────┤
│                   重要日期倒计时                             │
│   会议开幕:  28 天                                  │
│   投稿截止:  15 天                                  │
│   早鸟票:  7 天                                    │
│                                                       │
├─────────────────────────────────────────────────────────────┤
│                   赞助商展示区                             │
│   白金赞助商  |  钻石赞助商  |  特别赞助                 │
│                                                       │
└─────────────────────────────────────────────────────────────┘
```

#### 5.1.2 组件代码

```vue
<!-- src/views/home/Index.vue -->
<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// 重要日期数据
const importantDates = ref([
  { label: 'dates.conferenceOpen', days: 28, color: '#9b59b6' },
  { label: 'dates.submissionDeadline', days: 15, color: '#e74c3c' },
  { label: 'dates.earlyBird', days: 7, color: '#f39c12' }
])

// 最新公告数据
const announcements = ref([
  { id: 1, title: '2026年学术会议正式启动报名', date: '2026-02-25' },
  { id: 2, title: '投稿系统已开放', date: '2026-02-24' },
  { id: 3, title: '早鸟票火热销售中', date: '2026-02-23' }
])
</script>

<template>
  <div class="home-page">
    <!-- Hero 区域 -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">{{ t('hero.title') }}</h1>
        <p class="hero-subtitle">{{ t('hero.subtitle') }}</p>
        <p class="hero-description">{{ t('hero.description') }}</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" class="cta-button">
            {{ t('hero.cta') }}
          </el-button>
          <el-button size="large" class="learn-more-button">
            {{ t('hero.learnMore') }}
          </el-button>
        </div>
      </div>
    </section>

    <!-- 特性介绍 -->
    <section class="features-section">
      <div class="container">
        <h2 class="section-title">{{ t('features.title') }}</h2>
        <div class="features-grid">
          <div v-for="(feature, index) in 3" :key="index" class="feature-card">
            <div class="feature-icon">
              <el-icon :size="40"><user /></el-icon>
            </div>
            <h3 class="feature-title">{{ t(`features.${index}.title`) }}</h3>
            <p class="feature-desc">{{ t(`features.${index}.description`) }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新公告 -->
    <section class="news-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title">{{ t('news.title') }}</h2>
          <el-link type="primary">{{ t('news.more') }} →</el-link>
        </div>
        <div class="news-list">
          <div v-for="item in announcements" :key="item.id" class="news-item">
            <div class="news-content">
              <h3 class="news-title">{{ item.title }}</h3>
              <span class="news-date">{{ item.date }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 倒计时 -->
    <section class="countdown-section">
      <div class="container">
        <h2 class="section-title">{{ t('countdown.title') }}</h2>
        <div class="countdown-grid">
          <div v-for="date in importantDates" :key="date.label" class="countdown-card">
            <h4 class="countdown-label">{{ t(date.label) }}</h4>
            <div class="countdown-days" :style="{ color: date.color }">
              {{ date.days }}
              <span class="unit">{{ t('countdown.days') }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.home-page {
  min-height: 100vh;
}

.hero-section {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%);
  color: white;
  padding: 120px 24px 80px;

  @include respond-to(md) {
    padding: 80px 24px 60px;
  }

  .hero-content {
    max-width: 800px;
    text-align: center;
  }

  .hero-title {
    font-size: $font-size-5xl;
    font-weight: $font-weight-bold;
    margin-bottom: $spacing-6;
    line-height: $line-height-tight;

    @include respond-to(md) {
      font-size: $font-size-4xl;
    }
  }

  .hero-subtitle {
    font-size: $font-size-2xl;
    font-weight: $font-weight-medium;
    margin-bottom: $spacing-4;

    @include respond-to(md) {
      font-size: $font-size-xl;
    }
  }

  .hero-description {
    font-size: $font-size-lg;
    margin-bottom: $spacing-8;
    opacity: 0.9;
  }

  .hero-actions {
    display: flex;
    gap: $spacing-4;
    justify-content: center;

    .cta-button {
      padding: 16px 40px;
      font-size: $font-size-lg;
      background: white;
      color: $primary-color;
      border: none;

      &:hover {
        background: $primary-lighter;
        transform: translateY(-2px);
      }
    }

    .learn-more-button {
      padding: 16px 40px;
      font-size: $font-size-lg;
      background: transparent;
      border-color: white;
      color: white;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
      }
    }
  }
}

.features-section,
.news-section,
.countdown-section {
  padding: $spacing-24 0;
  background: $bg-primary;

  .section-title {
    font-size: $font-size-3xl;
    font-weight: $font-weight-bold;
    text-align: center;
    margin-bottom: $spacing-16;
    color: $text-primary;
  }
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-8;

  @include respond-to(md) {
    grid-template-columns: 1fr;
  }

  .feature-card {
    padding: $spacing-10;
    text-align: center;
    border-radius: $radius-lg;
    background: $bg-secondary;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: $shadow-lg;
    }

    .feature-icon {
      color: $primary-color;
      margin-bottom: $spacing-4;
    }

    .feature-title {
      font-size: $font-size-xl;
      font-weight: $font-weight-semibold;
      margin-bottom: $spacing-3;
    }

    .feature-desc {
      color: $text-secondary;
      line-height: $line-height-relaxed;
    }
  }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-10;
}

.news-list {
  display: grid;
  gap: $spacing-4;

  .news-item {
    padding: $spacing-6;
    border-radius: $radius-base;
    background: $bg-secondary;
    transition: all 0.3s;

    &:hover {
      background: $primary-lighter;
      transform: translateX(8px);
    }

    .news-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .news-title {
      font-size: $font-size-lg;
      font-weight: $font-weight-medium;
    }

    .news-date {
      color: $text-secondary;
      font-size: $font-size-sm;
      white-space: nowrap;
    }
  }
}

.countdown-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-8;

  @include respond-to(md) {
    grid-template-columns: 1fr;
  }

  .countdown-card {
    padding: $spacing-10;
    text-align: center;
    border-radius: $radius-lg;
    background: white;
    box-shadow: $shadow-base;

    .countdown-label {
      font-size: $font-size-lg;
      font-weight: $font-weight-medium;
      margin-bottom: $spacing-4;
    }

    .countdown-days {
      font-size: $font-size-5xl;
      font-weight: $font-weight-bold;

      .unit {
        font-size: $font-size-base;
        margin-left: $spacing-2;
      }
    }
  }
}
</style>
```

### 5.2 会场信息页 (Venue)

#### 5.2.1 页面结构

```
┌─────────────────────────────────────────────────────────────┐
│              会场信息页标题                              │
│         了解会议举办地点及住宿安排                           │
├─────────────────────────────────────────────────────────────┤
│                    酒店地图展示                            │
│                                                       │
│                    [地图组件]                             │
│                                                       │
├─────────────────────────────────────────────────────────────┤
│                    酒店信息卡片区                           │
│   ┌─────────────────┐  ┌─────────────────┐           │
│   │   主会场酒店     │  │   推荐酒店A     │           │
│   │   [图片]        │  │   [图片]        │           │
│   │   酒店名称       │  │   酒店名称       │           │
│   │   地址           │  │   地址           │           │
│   │   距离会场       │  │   距离会场       │           │
│   │   预订链接       │  │   预订链接       │           │
│   └─────────────────┘  └─────────────────┘           │
│                                                       │
├─────────────────────────────────────────────────────────────┤
│                    交通指南                                │
│   ✈️ 机场 -> 会场                                    │
│   🚇 火车站 -> 会场                                  │
│   🚕 地铁 -> 会场                                    │
│                                                       │
└─────────────────────────────────────────────────────────────┘
```

#### 5.2.2 组件代码

```vue
<!-- src/views/venue/Index.vue -->
<script setup lang="ts">
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// 酒店数据
const hotels = ref([
  {
    id: 1,
    name: '国际会议中心酒店',
    type: 'main',
    image: '/images/hotel-main.jpg',
    address: '北京市朝阳区会议中心路1号',
    distance: '会场内',
    phone: '010-12345678',
    bookingUrl: '#',
    amenities: ['wifi', 'breakfast', 'parking', 'gym']
  },
  {
    id: 2,
    name: '希尔顿酒店',
    type: 'recommended',
    image: '/images/hilton.jpg',
    address: '北京市朝阳区建国路88号',
    distance: '步行5分钟',
    phone: '010-87654321',
    bookingUrl: '#',
    amenities: ['wifi', 'breakfast', 'pool']
  },
  {
    id: 3,
    name: '万豪酒店',
    type: 'recommended',
    image: '/images/marriott.jpg',
    address: '北京市朝阳区国贸路66号',
    distance: '步行10分钟',
    phone: '010-11223344',
    bookingUrl: '#',
    amenities: ['wifi', 'breakfast', 'spa']
  }
])

// 交通指南数据
const transportGuide = ref([
  { type: 'flight', from: '首都国际机场', to: '会场', time: '45分钟' },
  { type: 'train', from: '北京南站', to: '会场', time: '30分钟' },
  { type: 'subway', from: '地铁1号线', to: '会场', time: '20分钟' }
])
</script>

<template>
  <div class="venue-page">
    <!-- 页面标题 -->
    <section class="page-header">
      <div class="container">
        <h1 class="page-title">{{ t('venue.title') }}</h1>
        <p class="page-subtitle">{{ t('venue.subtitle') }}</p>
      </div>
    </section>

    <!-- 地图展示 -->
    <section class="map-section">
      <div class="container">
        <div class="map-container">
          <!-- 使用高德/百度地图组件 -->
          <VenueMap :hotels="hotels" />
        </div>
      </div>
    </section>

    <!-- 酒店列表 -->
    <section class="hotels-section">
      <div class="container">
        <h2 class="section-title">{{ t('venue.hotels') }}</h2>
        <div class="hotels-grid">
          <div
            v-for="hotel in hotels"
            :key="hotel.id"
            :class="['hotel-card', hotel.type]"
          >
            <div class="hotel-image">
              <img :src="hotel.image" :alt="hotel.name" />
              <span v-if="hotel.type === 'main'" class="badge main">
                {{ t('venue.mainVenue') }}
              </span>
            </div>
            <div class="hotel-info">
              <h3 class="hotel-name">{{ hotel.name }}</h3>
              <div class="hotel-detail">
                <el-icon><location /></el-icon>
                <span>{{ hotel.address }}</span>
              </div>
              <div class="hotel-detail">
                <el-icon><timer /></el-icon>
                <span>{{ t('venue.distance') }}: {{ hotel.distance }}</span>
              </div>
              <div class="hotel-amenities">
                <el-tag
                  v-for="amenity in hotel.amenities"
                  :key="amenity"
                  size="small"
                >
                  {{ t(`amenities.${amenity}`) }}
                </el-tag>
              </div>
            </div>
            <div class="hotel-actions">
              <el-button type="primary" @click="hotel.bookingUrl">
                {{ t('venue.bookNow') }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 交通指南 -->
    <section class="transport-section">
      <div class="container">
        <h2 class="section-title">{{ t('venue.transport') }}</h2>
        <div class="transport-list">
          <div
            v-for="(item, index) in transportGuide"
            :key="index"
            class="transport-item"
          >
            <div class="transport-icon">
              <el-icon :size="32">
                <component :is="item.type === 'flight' ? 'plane' : item.type === 'train' ? 'train' : 'location'" />
              </el-icon>
            </div>
            <div class="transport-content">
              <h4 class="transport-title">
                {{ item.from }} {{ t('venue.to') }} {{ item.to }}
              </h4>
              <p class="transport-time">{{ t('venue.duration') }}: {{ item.time }}</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.page-header {
  background: linear-gradient(135deg, $primary-color, $secondary-color);
  color: white;
  padding: $spacing-16 0;
  text-align: center;

  .page-title {
    font-size: $font-size-4xl;
    font-weight: $font-weight-bold;
    margin-bottom: $spacing-4;
  }

  .page-subtitle {
    font-size: $font-size-lg;
    opacity: 0.9;
  }
}

.map-section {
  padding: $spacing-16 0;

  .map-container {
    height: 400px;
    border-radius: $radius-lg;
    overflow: hidden;
    box-shadow: $shadow-base;
  }
}

.hotels-section,
.transport-section {
  padding: $spacing-24 0;

  .section-title {
    font-size: $font-size-3xl;
    font-weight: $font-weight-bold;
    text-align: center;
    margin-bottom: $spacing-16;
  }
}

.hotels-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-8;

  @include respond-to(lg) {
    grid-template-columns: repeat(2, 1fr);
  }

  @include respond-to(sm) {
    grid-template-columns: 1fr;
  }

  .hotel-card {
    background: white;
    border-radius: $radius-lg;
    overflow: hidden;
    box-shadow: $shadow-base;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: $shadow-lg;
    }

    &.main {
      border: 2px solid $primary-color;
    }

    .hotel-image {
      position: relative;
      height: 200px;
      overflow: hidden;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .badge {
        position: absolute;
        top: $spacing-4;
        left: $spacing-4;
        padding: $spacing-2 $spacing-4;
        background: $primary-color;
        color: white;
        border-radius: $radius-full;
        font-size: $font-size-sm;
        font-weight: $font-weight-semibold;
      }
    }

    .hotel-info {
      padding: $spacing-6;

      .hotel-name {
        font-size: $font-size-xl;
        font-weight: $font-weight-bold;
        margin-bottom: $spacing-4;
      }

      .hotel-detail {
        display: flex;
        align-items: center;
        gap: $spacing-2;
        color: $text-secondary;
        font-size: $font-size-sm;
        margin-bottom: $spacing-3;
      }

      .hotel-amenities {
        display: flex;
        gap: $spacing-2;
        flex-wrap: wrap;
        margin-top: $spacing-4;
      }
    }

    .hotel-actions {
      padding: $spacing-4 $spacing-6;
      border-top: 1px solid $border-color;

      .el-button {
        width: 100%;
      }
    }
  }
}

.transport-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-8;

  @include respond-to(sm) {
    grid-template-columns: 1fr;
  }

  .transport-item {
    display: flex;
    align-items: center;
    gap: $spacing-4;
    padding: $spacing-6;
    background: $bg-secondary;
    border-radius: $radius-lg;

    .transport-icon {
      flex-shrink: 0;
      width: 64px;
      height: 64px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: $primary-lighter;
      border-radius: $radius-full;
      color: $primary-color;
    }

    .transport-content {
      .transport-title {
        font-size: $font-size-lg;
        font-weight: $font-weight-semibold;
        margin-bottom: $spacing-2;
      }

      .transport-time {
        color: $text-secondary;
        font-size: $font-size-sm;
      }
    }
  }
}
</style>
```

### 5.3 日程页面 (Schedule)

#### 5.3.1 页面结构

```
┌─────────────────────────────────────────────────────────────┐
│              会议日程页标题                              │
│         查看会议日程安排，做好参会准备                         │
├─────────────────────────────────────────────────────────────┤
│                    日期选择器                             │
│   [第一天] [第二天] [第三天] [第四天]                       │
│                                                       │
├─────────────────────────────────────────────────────────────┤
│                    日程时间轴                               │
│                                                       │
│   时间轴                                           │
│     │                                               │
│     ├─ 09:00 - 开幕式                               │
│     │     └─ [主会场]                                │
│     │                                               │
│     ├─ 10:00 - 主题演讲1                              │
│     │     └─ [主会场] [讲者信息]                          │
│     │                                               │
│     ├─ 11:00 - 主题演讲2                              │
│     │     └─ [主会场] [讲者信息]                          │
│     │                                               │
│     ├─ 12:00 - 午餐休息                              │
│     │                                               │
│     ├─ 14:00 - 分会场A: 技术分享                       │
│     │     └─ [分会场A] [讲者信息]                          │
│     │                                               │
│     └─ 15:00 - 分会场B: 圆桌讨论                       │
│           └─ [分会场B] [讲者信息]                          │
│                                                       │
└─────────────────────────────────────────────────────────────┘
```

#### 5.3.2 组件代码

```vue
<!-- src/views/schedule/Index.vue -->
<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

// 选中的日期
const selectedDay = ref(1)

// 日期列表
const days = ref([
  { id: 1, label: 'Day 1', date: '2026-06-15' },
  { id: 2, label: 'Day 2', date: '2026-06-16' },
  { id: 3, label: 'Day 3', date: '2026-06-17' },
  { id: 4, label: 'Day 4', date: '2026-06-18' }
])

// 日程数据
interface Session {
  id: number
  startTime: string
  endTime: string
  title: string
  description: string
  venue: string
  speaker?: {
    name: string
    title: string
    avatar: string
  }
  type: 'keynote' | 'session' | 'break' | 'workshop'
  track?: string
}

const scheduleData = ref<Record<number, Session[]>>({
  1: [
    { id: 1, startTime: '09:00', endTime: '10:00', title: '开幕式', description: '会议开幕致辞', venue: '主会场', type: 'keynote' },
    { id: 2, startTime: '10:00', endTime: '11:30', title: '主题演讲1', description: '人工智能在学术研究中的应用', venue: '主会场', speaker: { name: '张教授', title: '清华大学', avatar: '/images/speaker1.jpg' }, type: 'keynote' },
    { id: 3, startTime: '11:30', endTime: '13:00', title: '主题演讲2', description: '大数据分析的前沿进展', venue: '主会场', speaker: { name: '李教授', title: '北京大学', avatar: '/images/speaker2.jpg' }, type: 'keynote' },
    { id: 4, startTime: '13:00', endTime: '14:00', title: '午餐休息', description: '', venue: '', type: 'break' },
    { id: 5, startTime: '14:00', endTime: '15:30', title: '技术分享A', description: '深度学习框架对比研究', venue: '分会场A', speaker: { name: '王博士', title: '中科院', avatar: '/images/speaker3.jpg' }, type: 'session', track: 'AI' },
    { id: 6, startTime: '14:00', endTime: '15:30', title: '技术分享B', description: '自然语言处理新进展', venue: '分会场B', speaker: { name: '陈博士', title: '复旦大学', avatar: '/images/speaker4.jpg' }, type: 'session', track: 'NLP' }
  ]
  // ... 其他日期的数据
})

// 当前日期的日程
const currentSchedule = computed(() => scheduleData.value[selectedDay.value] || [])
</script>

<template>
  <div class="schedule-page">
    <!-- 页面标题 -->
    <section class="page-header">
      <div class="container">
        <h1 class="page-title">{{ t('schedule.title') }}</h1>
        <p class="page-subtitle">{{ t('schedule.subtitle') }}</p>
      </div>
    </section>

    <!-- 日期选择 -->
    <section class="days-selector">
      <div class="container">
        <div class="days-tabs">
          <button
            v-for="day in days"
            :key="day.id"
            :class="['day-tab', { active: selectedDay === day.id }]"
            @click="selectedDay = day.id"
          >
            <span class="day-label">{{ day.label }}</span>
            <span class="day-date">{{ day.date }}</span>
          </button>
        </div>
      </div>
    </section>

    <!-- 日程列表 -->
    <section class="schedule-section">
      <div class="container">
        <div class="timeline">
          <div
            v-for="(item, index) in currentSchedule"
            :key="item.id"
            :class="['timeline-item', `type-${item.type}`]"
          >
            <div class="timeline-time">
              <span class="start-time">{{ item.startTime }}</span>
              <span class="end-time">{{ item.endTime }}</span>
            </div>
            <div class="timeline-content">
              <div v-if="item.speaker" class="speaker-info">
                <img :src="item.speaker.avatar" :alt="item.speaker.name" class="speaker-avatar" />
                <div class="speaker-detail">
                  <span class="speaker-name">{{ item.speaker.name }}</span>
                  <span class="speaker-title">{{ item.speaker.title }}</span>
                </div>
              </div>
              <div class="session-card">
                <div class="session-header">
                  <h3 class="session-title">{{ item.title }}</h3>
                  <el-tag v-if="item.track" size="small" type="primary">
                    {{ item.track }}
                  </el-tag>
                  <el-tag v-if="item.type === 'keynote'" size="small" type="warning">
                    {{ t('schedule.keynote') }}
                  </el-tag>
                </div>
                <p v-if="item.description" class="session-desc">
                  {{ item.description }}
                </p>
                <div class="session-venue">
                  <el-icon><location /></el-icon>
                  <span>{{ item.venue }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.page-header {
  background: linear-gradient(135deg, $primary-color, $secondary-color);
  color: white;
  padding: $spacing-16 0;
  text-align: center;

  .page-title {
    font-size: $font-size-4xl;
    font-weight: $font-weight-bold;
    margin-bottom: $spacing-4;
  }

  .page-subtitle {
    font-size: $font-size-lg;
    opacity: 0.9;
  }
}

.days-selector {
  padding: $spacing-8 0;
  background: $bg-secondary;
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: $shadow-sm;

  .days-tabs {
    display: flex;
    gap: $spacing-2;
    justify-content: center;

    .day-tab {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: $spacing-4 $spacing-8;
      border: none;
      background: transparent;
      cursor: pointer;
      border-radius: $radius-base;
      transition: all 0.3s;

      &.active {
        background: $primary-color;
        color: white;
      }

      &:hover:not(.active) {
        background: $bg-tertiary;
      }

      .day-label {
        font-size: $font-size-base;
        font-weight: $font-weight-semibold;
      }

      .day-date {
        font-size: $font-size-sm;
        opacity: 0.8;
      }
    }
  }
}

.schedule-section {
  padding: $spacing-16 0 $spacing-24;

  .timeline {
    max-width: 900px;
    margin: 0 auto;

    .timeline-item {
      display: flex;
      gap: $spacing-6;
      padding-bottom: $spacing-10;
      position: relative;

      &:not(:last-child)::after {
        content: '';
        position: absolute;
        left: 70px;
        top: 100px;
        bottom: 0;
        width: 2px;
        background: $border-color;
      }

      &.type-break {
        .timeline-content {
          background: $bg-secondary;
        }

        .session-card {
          padding: $spacing-4;
        }
      }

      .timeline-time {
        flex-shrink: 0;
        width: 80px;
        text-align: right;
        padding-top: $spacing-4;

        .start-time {
          display: block;
          font-size: $font-size-xl;
          font-weight: $font-weight-bold;
          color: $primary-color;
        }

        .end-time {
          display: block;
          font-size: $font-size-sm;
          color: $text-secondary;
          margin-top: $spacing-1;
        }
      }

      .timeline-content {
        flex: 1;
        position: relative;

        .speaker-info {
          display: flex;
          align-items: center;
          gap: $spacing-3;
          margin-bottom: $spacing-4;

          .speaker-avatar {
            width: 48px;
            height: 48px;
            border-radius: $radius-full;
            object-fit: cover;
          }

          .speaker-detail {
            display: flex;
            flex-direction: column;

            .speaker-name {
              font-size: $font-size-base;
              font-weight: $font-weight-semibold;
            }

            .speaker-title {
              font-size: $font-size-sm;
              color: $text-secondary;
            }
          }
        }

        .session-card {
          background: white;
          padding: $spacing-6;
          border-radius: $radius-lg;
          box-shadow: $shadow-base;
          border-left: 4px solid $primary-color;

          .session-header {
            display: flex;
            align-items: center;
            gap: $spacing-3;
            margin-bottom: $spacing-3;

            .session-title {
              font-size: $font-size-lg;
              font-weight: $font-weight-semibold;
            }
          }

          .session-desc {
            color: $text-secondary;
            margin-bottom: $spacing-3;
            line-height: $line-height-relaxed;
          }

          .session-venue {
            display: flex;
            align-items: center;
            gap: $spacing-2;
            color: $primary-color;
            font-size: $font-size-sm;
          }
        }
      }
    }
  }
}
</style>
```

---

## 6. 公共组件

### 6.1 顶部导航栏 (Header)

```vue
<!-- src/components/common/Header.vue -->
<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()
const authStore = useAuthStore()

const activeMenu = computed(() => route.name)

// 导航菜单
const menuItems = computed(() => [
  { name: 'Home', label: 'menu.home' },
  { name: 'Venue', label: 'menu.venue' },
  { name: 'Schedule', label: 'menu.schedule' },
  ...(authStore.isLogin ? [
    { name: 'Submission', label: 'menu.submission', roles: ['speaker', 'reviewer'] },
    { name: 'Tickets', label: 'menu.tickets', roles: ['attendee'] },
    { name: 'Profile', label: 'menu.profile' }
  ] : [])
])

const handleMenuClick = (name: string) => {
  router.push({ name })
}

const handleLogin = () => {
  router.push({ name: 'Login' })
}

const handleLogout = () => {
  authStore.logout()
  router.push({ name: 'Home' })
}
</script>

<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <!-- Logo -->
        <div class="logo" @click="router.push('/')">
          <img src="/logo.svg" alt="Logo" />
          <span>{{ t('app.name') }}</span>
        </div>

        <!-- 导航菜单 -->
        <nav class="nav-menu">
          <a
            v-for="item in menuItems"
            :key="item.name"
            :class="['nav-item', { active: activeMenu === item.name }]"
            @click="handleMenuClick(item.name)"
          >
            {{ t(item.label) }}
          </a>
        </nav>

        <!-- 右侧操作区 -->
        <div class="header-actions">
          <LangSwitcher />
          <template v-if="authStore.isLogin">
            <el-dropdown trigger="click">
              <div class="user-info">
                <el-avatar :size="36" :src="authStore.userInfo?.avatar" />
                <span class="user-name">{{ authStore.userInfo?.name }}</span>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/profile')">
                    {{ t('menu.profile') }}
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="handleLogout">
                    {{ t('menu.logout') }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <el-button v-else type="primary" @click="handleLogin">
            {{ t('menu.login') }}
          </el-button>
        </div>
      </div>
    </div>
  </header>
</template>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid $border-color;
  box-shadow: $shadow-sm;

  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-4 0;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: $spacing-3;
    font-size: $font-size-xl;
    font-weight: $font-weight-bold;
    color: $primary-color;
    cursor: pointer;

    img {
      height: 40px;
    }
  }

  .nav-menu {
    display: flex;
    gap: $spacing-8;

    .nav-item {
      padding: $spacing-3 $spacing-5;
      color: $text-primary;
      text-decoration: none;
      border-radius: $radius-base;
      transition: all 0.3s;
      cursor: pointer;

      &:hover {
        color: $primary-color;
        background: $primary-lighter;
      }

      &.active {
        color: $primary-color;
        font-weight: $font-weight-semibold;
      }
    }
  }

  .header-actions {
    display: flex;
    align-items: center;
    gap: $spacing-4;

    .user-info {
      display: flex;
      align-items: center;
      gap: $spacing-3;
      padding: $spacing-2 $spacing-4;
      border-radius: $radius-full;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        background: $bg-secondary;
      }

      .user-name {
        font-size: $font-size-sm;
      }
    }
  }
}

@media (max-width: $breakpoint-md) {
  .nav-menu {
    display: none;
  }
}
</style>
```

### 6.2 底部信息栏 (Footer)

```vue
<!-- src/components/common/Footer.vue -->
<script setup lang="ts">
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'

const { t } = useI18n()
const router = useRouter()

const currentYear = new Date().getFullYear()

const footerLinks = [
  { label: 'footer.about', path: '/about' },
  { label: 'footer.contact', path: '/contact' },
  { label: 'footer.privacy', path: '/privacy' },
  { label: 'footer.terms', path: '/terms' }
]
</script>

<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h4 class="footer-title">{{ t('footer.aboutUs') }}</h4>
          <p class="footer-text">{{ t('footer.description') }}</p>
        </div>
        <div class="footer-section">
          <h4 class="footer-title">{{ t('footer.quickLinks') }}</h4>
          <ul class="footer-links">
            <li v-for="link in footerLinks" :key="link.label">
              <a @click="router.push(link.path)">{{ t(link.label) }}</a>
            </li>
          </ul>
        </div>
        <div class="footer-section">
          <h4 class="footer-title">{{ t('footer.contactUs') }}</h4>
          <div class="contact-info">
            <p><el-icon><message /></el-icon> contact@conference.com</p>
            <p><el-icon><phone /></el-icon> +86 10 1234 5678</p>
          </div>
        </div>
        <div class="footer-section">
          <h4 class="footer-title">{{ t('footer.followUs') }}</h4>
          <div class="social-links">
            <a href="#" class="social-icon"><el-icon><platform /></el-icon></a>
            <a href="#" class="social-icon"><el-icon><chat-dot-round /></el-icon></a>
            <a href="#" class="social-icon"><el-icon><video-camera /></el-icon></a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <p>© {{ currentYear }} {{ t('app.name') }}. {{ t('footer.rightsReserved') }}</p>
      </div>
    </div>
  </footer>
</template>

<style scoped lang="scss">
@import '@/assets/styles/variables.scss';

.app-footer {
  background: $bg-tertiary;
  color: $text-secondary;
  padding: $spacing-16 0;

  .footer-content {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: $spacing-10;
    margin-bottom: $spacing-16;

    @include respond-to(md) {
      grid-template-columns: repeat(2, 1fr);
    }

    @include respond-to(sm) {
      grid-template-columns: 1fr;
    }

    .footer-section {
      .footer-title {
        font-size: $font-size-lg;
        font-weight: $font-weight-semibold;
        color: $text-primary;
        margin-bottom: $spacing-4;
      }

      .footer-text {
        line-height: $line-height-relaxed;
        font-size: $font-size-sm;
      }

      .footer-links {
        list-style: none;
        padding: 0;

        li {
          margin-bottom: $spacing-2;

          a {
            color: $text-secondary;
            text-decoration: none;
            transition: color 0.3s;

            &:hover {
              color: $primary-color;
            }
          }
        }
      }

      .contact-info {
        p {
          display: flex;
          align-items: center;
          gap: $spacing-2;
          margin-bottom: $spacing-2;
          font-size: $font-size-sm;
        }
      }

      .social-links {
        display: flex;
        gap: $spacing-4;

        .social-icon {
          width: 40px;
          height: 40px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: white;
          border-radius: $radius-base;
          color: $primary-color;
          transition: all 0.3s;

          &:hover {
            background: $primary-color;
            color: white;
            transform: translateY(-2px);
          }
        }
      }
    }
  }

  .footer-bottom {
    text-align: center;
    padding-top: $spacing-8;
    border-top: 1px solid $border-color;
    font-size: $font-size-sm;
  }
}
</style>
```

---

## 7. 项目结构

```
frontend/portal/
├── public/
│   ├── favicon.ico
│   ├── logo.svg
│   └── images/
├── src/
│   ├── api/
│   │   ├── auth.ts
│   │   ├── submission.ts
│   │   ├── review.ts
│   │   ├── order.ts
│   │   ├── venue.ts
│   │   ├── schedule.ts
│   │   └── types.ts
│   ├── assets/
│   │   ├── images/
│   │   ├── styles/
│   │   │   ├── index.scss
│   │   │   ├── variables.scss
│   │   │   ├── mixins.scss
│   │   │   └── reset.scss
│   │   └── icons/
│   ├── components/
│   │   ├── common/
│   │   │   ├── Header.vue
│   │   │   ├── Footer.vue
│   │   │   ├── LangSwitcher.vue
│   │   │   └── Loading.vue
│   │   ├── venue/
│   │   │   ├── HotelCard.vue
│   │   │   └── VenueMap.vue
│   │   └── schedule/
│   │       ├── TimelineItem.vue
│   │       └── SpeakerInfo.vue
│   ├── composables/
│   │   ├── useAuth.ts
│   │   ├── useI18n.ts
│   │   └── useScroll.ts
│   ├── i18n/
│   │   ├── index.ts
│   │   └── locales/
│   │       ├── zh-CN/
│   │       ├── en-US/
│   │       └── zh-TW/
│   ├── router/
│   │   ├── index.ts
│   │   └── guards.ts
│   ├── stores/
│   │   ├── auth.ts
│   │   ├── app.ts
│   │   └── locale.ts
│   ├── utils/
│   │   ├── request.ts
│   │   ├── storage.ts
│   │   └── format.ts
│   ├── views/
│   │   ├── home/
│   │   │   └── Index.vue
│   │   ├── venue/
│   │   │   └── Index.vue
│   │   ├── schedule/
│   │   │   └── Index.vue
│   │   ├── submission/
│   │   │   ├── Index.vue
│   │   │   ├── Create.vue
│   │   │   └── Detail.vue
│   │   ├── order/
│   │   │   ├── Tickets.vue
│   │   │   └── List.vue
│   │   ├── auth/
│   │   │   ├── Login.vue
│   │   │   └── Register.vue
│   │   └── profile/
│   │       └── Index.vue
│   ├── App.vue
│   └── main.ts
├── .env.development
├── .env.production
├── index.html
├── package.json
├── tsconfig.json
├── vite.config.ts
└── README.md
```

---

## 8. 开发配置

### 8.1 环境变量

```bash
# .env.development
VITE_APP_TITLE=学术会议系统
VITE_API_BASE_URL=http://localhost:8080/api
VITE_APP_BASE_URL=http://localhost:3000
VITE_DEFAULT_LOCALE=zh-CN
```

```bash
# .env.production
VITE_APP_TITLE=学术会议系统
VITE_API_BASE_URL=https://api.conference.com/api
VITE_APP_BASE_URL=https://www.conference.com
VITE_DEFAULT_LOCALE=zh-CN
```

### 8.2 Vite 配置

```typescript
// vite.config.ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@import "@/assets/styles/variables.scss";`
      }
    }
  },
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

---

**文档版本：** v1.0
**最后更新：** 2026-02-25
