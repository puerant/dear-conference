<template>
  <div class="home-page">
    <!-- Hero -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">{{ t('hero.title') }}</h1>
        <p class="hero-subtitle">{{ t('hero.subtitle') }}</p>
        <p class="hero-description">{{ t('hero.description') }}</p>
        <div class="hero-actions">
          <el-button
            size="large"
            class="cta-btn"
            @click="handleCta"
          >
            {{ t('hero.cta') }}
          </el-button>
          <el-button
            size="large"
            class="learn-btn"
            @click="router.push('/schedule')"
          >
            {{ t('hero.learnMore') }}
          </el-button>
        </div>
      </div>
    </section>

    <!-- Features -->
    <section class="features-section">
      <div class="container">
        <h2 class="section-title">{{ t('features.title') }}</h2>
        <div class="features-grid">
          <div
            v-for="feature in features"
            :key="feature.key"
            class="feature-card"
            @click="feature.action"
          >
            <div class="feature-icon">
              <el-icon :size="48"><component :is="feature.icon" /></el-icon>
            </div>
            <h3 class="feature-title">{{ t(feature.titleKey) }}</h3>
            <p class="feature-desc">{{ t(feature.descKey) }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Latest News -->
    <section class="news-section">
      <div class="container">
        <div class="section-header">
          <h2 class="section-title" style="margin-bottom: 0">{{ t('news.title') }}</h2>
          <el-button type="primary" link>{{ t('news.more') }} →</el-button>
        </div>
        <div class="news-list">
          <div v-for="item in announcements" :key="item.id" class="news-item">
            <div class="news-dot"></div>
            <div class="news-content">
              <span class="news-title">{{ item.title }}</span>
              <span class="news-date">{{ item.date }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Countdown -->
    <section class="countdown-section">
      <div class="container">
        <h2 class="section-title">{{ t('countdown.title') }}</h2>
        <div class="countdown-grid">
          <div
            v-for="item in importantDates"
            :key="item.labelKey"
            class="countdown-card"
            :style="{ borderTopColor: item.color }"
          >
            <div class="countdown-days" :style="{ color: item.color }">
              {{ item.days }}
              <span class="unit">{{ t('countdown.days') }}</span>
            </div>
            <p class="countdown-label">{{ t(item.labelKey) }}</p>
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
    if (authStore.isSpeaker) router.push({ name: 'Submission' })
    else if (authStore.isAttendee) router.push({ name: 'Tickets' })
    else if (authStore.isReviewer) router.push({ name: 'Review' })
    else router.push('/profile')
  } else {
    router.push('/register')
  }
}

const features = [
  {
    key: 'speaker',
    icon: 'EditPen',
    titleKey: 'features.speaker.title',
    descKey: 'features.speaker.desc',
    action: () => router.push('/register')
  },
  {
    key: 'reviewer',
    icon: 'Document',
    titleKey: 'features.reviewer.title',
    descKey: 'features.reviewer.desc',
    action: () => router.push('/register')
  },
  {
    key: 'attendee',
    icon: 'Ticket',
    titleKey: 'features.attendee.title',
    descKey: 'features.attendee.desc',
    action: () => router.push('/register')
  }
]

const announcements = ref([
  { id: 1, title: '2026年学术会议正式启动报名', date: '2026-02-25' },
  { id: 2, title: '投稿系统已开放，欢迎提交论文', date: '2026-02-24' },
  { id: 3, title: '早鸟票火热销售中，先到先得', date: '2026-02-23' },
  { id: 4, title: '会议日程安排正式发布', date: '2026-02-20' }
])

const importantDates = ref([
  { labelKey: 'countdown.conferenceOpen', days: 110, color: '#9b59b6' },
  { labelKey: 'countdown.submissionDeadline', days: 45, color: '#e74c3c' },
  { labelKey: 'countdown.earlyBird', days: 20, color: '#f39c12' }
])
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100vh;
}

.hero-section {
  min-height: 92vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $gradient-primary;
  color: white;
  padding: 120px $spacing-6 80px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background: url("data:image/svg+xml,%3Csvg width='60' height='60' viewBox='0 0 60 60' xmlns='http://www.w3.org/2000/svg'%3E%3Cg fill='none' fill-rule='evenodd'%3E%3Cg fill='%23ffffff' fill-opacity='0.05'%3E%3Cpath d='M36 34v-4h-2v4h-4v2h4v4h2v-4h4v-2h-4zm0-30V0h-2v4h-4v2h4v4h2V6h4V4h-4zM6 34v-4H4v4H0v2h4v4h2v-4h4v-2H6zM6 4V0H4v4H0v2h4v4h2V6h4V4H6z'/%3E%3C/g%3E%3C/g%3E%3C/svg%3E");
  }

  .hero-content {
    max-width: 800px;
    text-align: center;
    position: relative;
    z-index: 1;
  }

  .hero-title {
    font-size: $font-size-5xl;
    font-weight: $font-weight-bold;
    margin-bottom: $spacing-6;
    line-height: $line-height-tight;

    @include respond-to(md) { font-size: $font-size-4xl; }
    @include respond-to(sm) { font-size: $font-size-3xl; }
  }

  .hero-subtitle {
    font-size: $font-size-2xl;
    font-weight: $font-weight-medium;
    margin-bottom: $spacing-4;
    opacity: 0.95;

    @include respond-to(md) { font-size: $font-size-xl; }
  }

  .hero-description {
    font-size: $font-size-lg;
    margin-bottom: $spacing-10;
    opacity: 0.85;
    line-height: $line-height-relaxed;
  }

  .hero-actions {
    display: flex;
    gap: $spacing-4;
    justify-content: center;
    flex-wrap: wrap;

    .cta-btn {
      padding: 0 $spacing-10;
      height: 52px;
      font-size: $font-size-lg;
      background: white;
      color: $primary-color;
      border: none;
      font-weight: $font-weight-semibold;
      border-radius: $radius-full;
      transition: all 0.3s;

      &:hover {
        transform: translateY(-3px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
      }
    }

    .learn-btn {
      padding: 0 $spacing-10;
      height: 52px;
      font-size: $font-size-lg;
      background: transparent;
      border: 2px solid rgba(255, 255, 255, 0.8);
      color: white;
      border-radius: $radius-full;
      transition: all 0.3s;

      &:hover {
        background: rgba(255, 255, 255, 0.15);
        transform: translateY(-3px);
      }
    }
  }
}

.features-section {
  padding: $spacing-24 0;
  background: $bg-primary;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-8;

  @include respond-to(md) { grid-template-columns: 1fr; }

  .feature-card {
    padding: $spacing-12 $spacing-10;
    text-align: center;
    border-radius: $radius-xl;
    background: $bg-secondary;
    cursor: pointer;
    transition: all 0.3s;
    border: 2px solid transparent;

    &:hover {
      transform: translateY(-6px);
      box-shadow: $shadow-lg;
      border-color: $primary-lighter;
    }

    .feature-icon {
      color: $primary-color;
      margin-bottom: $spacing-6;
    }

    .feature-title {
      font-size: $font-size-xl;
      font-weight: $font-weight-semibold;
      margin-bottom: $spacing-3;
      color: $text-primary;
    }

    .feature-desc {
      color: $text-secondary;
      line-height: $line-height-relaxed;
      font-size: $font-size-base;
    }
  }
}

.news-section {
  padding: $spacing-24 0;
  background: $bg-secondary;

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: $spacing-10;
  }
}

.news-list {
  display: grid;
  gap: $spacing-3;

  .news-item {
    display: flex;
    align-items: center;
    gap: $spacing-4;
    padding: $spacing-5 $spacing-6;
    border-radius: $radius-base;
    background: white;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: $primary-lighter;
      transform: translateX(6px);
    }

    .news-dot {
      width: 8px;
      height: 8px;
      border-radius: $radius-full;
      background: $primary-color;
      flex-shrink: 0;
    }

    .news-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex: 1;
      gap: $spacing-4;
    }

    .news-title {
      font-size: $font-size-base;
      font-weight: $font-weight-medium;
      color: $text-primary;
    }

    .news-date {
      color: $text-secondary;
      font-size: $font-size-sm;
      white-space: nowrap;
    }
  }
}

.countdown-section {
  padding: $spacing-24 0;
  background: $bg-primary;
}

.countdown-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-8;

  @include respond-to(md) { grid-template-columns: 1fr; }

  .countdown-card {
    padding: $spacing-10;
    text-align: center;
    border-radius: $radius-lg;
    background: white;
    box-shadow: $shadow-base;
    border-top: 4px solid;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: $shadow-lg;
    }

    .countdown-days {
      font-size: 72px;
      font-weight: $font-weight-bold;
      line-height: 1;
      margin-bottom: $spacing-2;

      .unit {
        font-size: $font-size-xl;
        margin-left: $spacing-2;
      }
    }

    .countdown-label {
      font-size: $font-size-lg;
      color: $text-secondary;
      font-weight: $font-weight-medium;
    }
  }
}
</style>
