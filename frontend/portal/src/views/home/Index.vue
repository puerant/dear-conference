<template>
  <div class="home-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">{{ t('hero.title') }}</h1>
        <p class="hero-subtitle">{{ t('hero.subtitle') }}</p>
        <p class="hero-description">{{ t('hero.description') }}</p>

        <div class="hero-meta">
          <div class="meta-item">
            <el-icon><calendar /></el-icon>
            <span>2026年6月15-17日</span>
          </div>
          <div class="meta-item">
            <el-icon><location /></el-icon>
            <span>北京国际会议中心</span>
          </div>
        </div>

        <div class="hero-actions">
          <el-button size="large" class="hero-btn-primary" @click="handleCta">
            {{ t('hero.cta') }}
          </el-button>
          <el-button size="large" class="hero-btn-secondary" @click="router.push('/schedule')">
            {{ t('hero.learnMore') }}
          </el-button>
        </div>
      </div>

      <!-- Decorative elements -->
      <div class="hero-decoration">
        <div class="decoration-circle decoration-1"></div>
        <div class="decoration-circle decoration-2"></div>
        <div class="decoration-circle decoration-3"></div>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="container">
        <h2 class="section-title">{{ t('features.title') }}</h2>
        <p class="section-subtitle">{{ t('features.subtitle') }}</p>
        <div class="features-grid">
          <div
            v-for="feature in features"
            :key="feature.key"
            class="feature-card"
            @click="feature.action"
          >
            <div class="feature-icon">
              <el-icon :size="40"><component :is="feature.icon" /></el-icon>
            </div>
            <h3 class="feature-title">{{ t(feature.titleKey) }}</h3>
            <p class="feature-desc">{{ t(feature.descKey) }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Countdown Section -->
    <section class="countdown-section">
      <div class="container">
        <h2 class="section-title">重要日期倒计时</h2>
        <div class="countdown-grid">
          <div
            v-for="item in importantDates"
            :key="item.labelKey"
            class="countdown-card"
            :style="{ borderTopColor: item.color }"
          >
            <div class="countdown-days">{{ item.days }}</div>
            <span class="countdown-unit">天</span>
            <p class="countdown-label">{{ t(item.labelKey) }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- News Section -->
    <section class="news-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title" style="margin-bottom: 0">{{ t('news.title') }}</h2>
          <el-button type="primary" link @click="router.push('/news')">
            {{ t('news.more') }}
          </el-button>
        </div>
        <div class="news-list">
          <div v-for="item in announcements" :key="item.id" class="news-card">
            <div class="news-dot"></div>
            <div class="news-content">
              <span class="news-title">{{ item.title }}</span>
              <span class="news-date">{{ item.date }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const { t } = useI18n()
const authStore = useAuthStore()

const handleCta = () => {
  if (authStore.isLogin) {
    router.push('/profile')
  } else {
    router.push('/register')
  }
}

const features = [
  { key: 'speaker', icon: 'EditPen', titleKey: 'features.speaker.title', descKey: 'features.speaker.desc', action: () => router.push('/register') },
  { key: 'reviewer', icon: 'Document', titleKey: 'features.reviewer.title', descKey: 'features.reviewer.desc', action: () => router.push('/register') },
  { key: 'attendee', icon: 'Ticket', titleKey: 'features.attendee.title', descKey: 'features.attendee.desc', action: () => router.push('/register') }
]

const announcements = ref([
  { id: 1, title: '2026年学术会议正式启动报名', date: '2026-02-25' },
  { id: 2, title: '投稿系统已开放，欢迎提交论文', date: '2026-02-24' },
  { id: 3, title: '早鸟票火热销售中，先到先得', date: '2026-02-23' },
  { id: 4, title: '会议日程安排正式发布', date: '2026-02-20' }
])

const importantDates = ref([
  { labelKey: 'countdown.conferenceOpen', days: 110, color: '#3B82F6' },
  { labelKey: 'countdown.submissionDeadline', days: 45, color: '#EF4444' },
  { labelKey: 'countdown.earlyBird', days: 20, color: '#F59E0B' }
])
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100vh;
}

// Hero Section
.hero-section {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $brand-gradient;
  color: white;
  padding: 120px $spacing-6 $spacing-16;
  overflow: hidden;

  .hero-content {
    position: relative;
    z-index: 2;
    max-width: 800px;
    text-align: center;
  }

  .hero-title {
    @include heading-1;
    color: white;
    margin-bottom: $spacing-4;
  }

  .hero-subtitle {
    font-size: $font-size-xl;
    font-weight: $font-weight-medium;
    margin-bottom: $spacing-2;
    opacity: 0.95;
  }

  .hero-description {
    font-size: $font-size-lg;
    margin-bottom: $spacing-8;
    opacity: 0.9;
    max-width: 560px;
    margin-left: auto;
    margin-right: auto;
    line-height: $line-height-relaxed;
  }

  .hero-meta {
    display: flex;
    justify-content: center;
    gap: $spacing-6;
    margin-bottom: $spacing-8;
    flex-wrap: wrap;
  }

  .meta-item {
    display: flex;
    align-items: center;
    gap: $spacing-2;
    padding: $spacing-3 $spacing-5;
    background: rgba(255, 255, 255, 0.15);
    border-radius: $radius-full;
    font-size: $font-size-sm;
    backdrop-filter: blur(4px);
  }

  .hero-actions {
    display: flex;
    justify-content: center;
    gap: $spacing-4;
    flex-wrap: wrap;
  }

  .hero-btn-primary {
    padding: $spacing-4 $spacing-8;
    height: 56px;
    font-size: $font-size-lg;
    font-weight: $font-weight-semibold;
    background: white !important;
    color: $primary-600 !important;
    border: none;
    border-radius: $radius-full;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
    }
  }

  .hero-btn-secondary {
    padding: $spacing-4 $spacing-8;
    height: 56px;
    font-size: $font-size-lg;
    font-weight: $font-weight-medium;
    background: transparent !important;
    border: 2px solid rgba(255, 255, 255, 0.4) !important;
    color: white !important;
    border-radius: $radius-full;

    &:hover {
      background: rgba(255, 255, 255, 0.1) !important;
      border-color: rgba(255, 255, 255, 0.6) !important;
    }
  }
}

// Hero decoration
.hero-decoration {
  position: absolute;
  inset: 0;
  pointer-events: none;
  overflow: hidden;

  .decoration-circle {
    position: absolute;
    border-radius: 50%;
    animation: float 6s ease-in-out infinite;

    &.decoration-1 {
      top: 10%;
      left: 10%;
      width: 300px;
      height: 300px;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
    }

    &.decoration-2 {
      top: 50%;
      right: 5%;
      width: 200px;
      height: 200px;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.08) 0%, transparent 70%);
      animation-delay: 0.5s;
    }

    &.decoration-3 {
      bottom: 20%;
      left: 5%;
      width: 150px;
      height: 150px;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.06) 0%, transparent 70%);
      animation-delay: 1s;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

// Features Section
.features-section {
  padding: $spacing-24 0;
  background: $bg-page;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-8;

  @include respond-to(lg) {
    grid-template-columns: 1fr;
  }
}

.feature-card {
  background: $bg-card;
  border-radius: $radius-2xl;
  padding: $spacing-10;
  text-align: center;
  cursor: pointer;
  transition: all $transition-base;
  border: 1px solid $border-light;

  &:hover {
    transform: translateY(-8px);
    box-shadow: $shadow-lg;

    .feature-icon {
      background: $primary-100;
      transform: scale(1.1);
    }
  }

  .feature-icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 80px;
    height: 80px;
    background: $primary-50;
    border-radius: $radius-xl;
    margin: 0 auto $spacing-6;
    color: $primary-500;
    transition: all $transition-base;
  }

  .feature-title {
    font-size: $font-size-xl;
    font-weight: $font-weight-semibold;
    color: $text-primary;
    margin-bottom: $spacing-3;
  }

  .feature-desc {
    font-size: $font-size-base;
    color: $text-secondary;
    line-height: $line-height-relaxed;
  }
}

// Countdown Section
.countdown-section {
  padding: $spacing-24 0;
  background: white;
}

.countdown-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-8;

  @include respond-to(md) {
    grid-template-columns: 1fr;
  }
}

.countdown-card {
  padding: $spacing-10;
  text-align: center;
  background: $bg-card;
  border-radius: $radius-2xl;
  box-shadow: $shadow-card;
  border-top: 4px solid;
  transition: all $transition-base;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-lg;
  }

  .countdown-days {
    font-size: 64px;
    font-weight: $font-weight-bold;
    line-height: 1;
    margin-bottom: $spacing-1;
    background: $brand-gradient;
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .countdown-unit {
    font-size: $font-size-lg;
    color: $text-secondary;
    margin-bottom: $spacing-4;
    display: block;
  }

  .countdown-label {
    font-size: $font-size-base;
    color: $text-secondary;
    font-weight: $font-weight-medium;
  }
}

// News Section
.news-section {
  padding: $spacing-24 0;
  background: $bg-page;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-8;

  @include respond-to(md) {
    flex-direction: column;
    gap: $spacing-4;
  }
}

.news-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-4;
}

.news-card {
  display: flex;
  align-items: center;
  gap: $spacing-4;
  padding: $spacing-5 $spacing-6;
  background: white;
  border-radius: $radius-lg;
  cursor: pointer;
  transition: all $transition-base;

  &:hover {
    transform: translateX(8px);
    box-shadow: $shadow-md;

    .news-dot {
      background: $primary-500;
    }
  }

  .news-dot {
    width: 8px;
    height: 8px;
    border-radius: $radius-full;
    background: $primary-200;
    flex-shrink: 0;
    transition: background $transition-fast;
  }

  .news-content {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: $spacing-4;
  }

  .news-title {
    font-size: $font-size-base;
    font-weight: $font-weight-medium;
    color: $text-primary;
  }

  .news-date {
    font-size: $font-size-sm;
    color: $text-tertiary;
    white-space: nowrap;
  }
}
</style>
