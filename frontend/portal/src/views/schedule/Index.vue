<template>
  <div class="schedule-page">
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">{{ t('schedule.title') }}</h1>
        <p class="page-subtitle">{{ t('schedule.subtitle') }}</p>
      </div>
    </section>

    <!-- Day tabs -->
    <section class="days-bar">
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

    <!-- Timeline -->
    <section class="timeline-section">
      <div class="container">
        <div class="timeline">
          <div
            v-for="item in currentSchedule"
            :key="item.id"
            :class="['timeline-item', `type-${item.type}`]"
          >
            <div class="time-col">
              <span class="start-time">{{ item.startTime }}</span>
              <span class="end-time">{{ item.endTime }}</span>
            </div>
            <div class="dot-col">
              <div class="dot" :class="`dot-${item.type}`"></div>
              <div class="line"></div>
            </div>
            <div class="content-col">
              <div class="session-card" :class="`card-${item.type}`">
                <div class="session-head">
                  <h3 class="session-title">{{ item.title }}</h3>
                  <div class="tags">
                    <el-tag v-if="item.track" size="small" type="primary">{{ item.track }}</el-tag>
                    <el-tag v-if="item.type === 'keynote'" size="small" type="warning">{{ t('schedule.keynote') }}</el-tag>
                    <el-tag v-if="item.type === 'break'" size="small" type="info">{{ t('schedule.break') }}</el-tag>
                    <el-tag v-if="item.type === 'workshop'" size="small" type="success">{{ t('schedule.workshop') }}</el-tag>
                  </div>
                </div>
                <p v-if="item.description" class="session-desc">{{ item.description }}</p>
                <div v-if="item.venue" class="session-venue">
                  <el-icon><location /></el-icon>
                  <span>{{ item.venue }}</span>
                </div>
                <div v-if="item.speaker" class="speaker-info">
                  <el-avatar :size="28">{{ item.speaker.name.charAt(0) }}</el-avatar>
                  <span class="speaker-name">{{ item.speaker.name }}</span>
                  <span class="speaker-org">· {{ item.speaker.title }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const selectedDay = ref(1)

const days = ref([
  { id: 1, label: 'Day 1', date: '2026-06-15' },
  { id: 2, label: 'Day 2', date: '2026-06-16' },
  { id: 3, label: 'Day 3', date: '2026-06-17' },
  { id: 4, label: 'Day 4', date: '2026-06-18' }
])

interface Session {
  id: number
  startTime: string
  endTime: string
  title: string
  description: string
  venue: string
  type: 'keynote' | 'session' | 'break' | 'workshop'
  track?: string
  speaker?: { name: string; title: string }
}

const scheduleData: Record<number, Session[]> = {
  1: [
    { id: 1, startTime: '09:00', endTime: '10:00', title: '开幕式', description: '欢迎致辞与会议主旨宣读', venue: '主会场', type: 'keynote' },
    { id: 2, startTime: '10:00', endTime: '11:30', title: '主题演讲：人工智能的学术前沿', description: '探讨AI在科学研究中的最新进展与应用', venue: '主会场', type: 'keynote', speaker: { name: '张教授', title: '清华大学' } },
    { id: 3, startTime: '11:30', endTime: '13:00', title: '大数据分析前沿进展', description: '大规模数据处理技术与案例分析', venue: '主会场', type: 'keynote', speaker: { name: '李教授', title: '北京大学' } },
    { id: 4, startTime: '13:00', endTime: '14:00', title: '午餐休息', description: '', venue: '', type: 'break' },
    { id: 5, startTime: '14:00', endTime: '15:30', title: '技术分享：深度学习框架', description: '主流深度学习框架对比与最佳实践', venue: '分会场A', type: 'session', track: 'AI', speaker: { name: '王博士', title: '中科院' } },
    { id: 6, startTime: '14:00', endTime: '15:30', title: '自然语言处理新进展', description: 'LLM技术现状与未来展望', venue: '分会场B', type: 'session', track: 'NLP', speaker: { name: '陈博士', title: '复旦大学' } },
    { id: 7, startTime: '15:30', endTime: '16:00', title: '茶歇', description: '', venue: '', type: 'break' },
    { id: 8, startTime: '16:00', endTime: '17:30', title: 'Workshop: 实践工作坊', description: '动手实践AI工具与方法', venue: '工作坊室', type: 'workshop' }
  ],
  2: [
    { id: 9, startTime: '09:30', endTime: '11:00', title: '量子计算与量子信息', description: '量子算法与硬件实现最新进展', venue: '主会场', type: 'keynote', speaker: { name: '赵教授', title: '中国科学技术大学' } },
    { id: 10, startTime: '11:00', endTime: '12:30', title: '生物信息学前沿', description: '基因组学与蛋白质结构预测', venue: '主会场', type: 'keynote', speaker: { name: '孙博士', title: '北京大学' } },
    { id: 11, startTime: '12:30', endTime: '13:30', title: '午餐休息', description: '', venue: '', type: 'break' },
    { id: 12, startTime: '13:30', endTime: '15:00', title: '物联网与边缘计算', description: '边缘智能与IoT应用', venue: '分会场A', type: 'session', track: 'IoT' }
  ],
  3: [
    { id: 13, startTime: '09:00', endTime: '10:30', title: '机器人与自动化', description: '智能机器人系统设计', venue: '主会场', type: 'keynote', speaker: { name: '刘教授', title: '哈尔滨工业大学' } },
    { id: 14, startTime: '10:30', endTime: '11:00', title: '茶歇', description: '', venue: '', type: 'break' },
    { id: 15, startTime: '11:00', endTime: '12:30', title: '网络安全与密码学', description: '新型密码协议与安全协议设计', venue: '主会场', type: 'session', track: '安全' }
  ],
  4: [
    { id: 16, startTime: '09:30', endTime: '11:00', title: '学术交流与讨论', description: '自由讨论与开放问答', venue: '主会场', type: 'session' },
    { id: 17, startTime: '11:00', endTime: '12:00', title: '颁奖典礼', description: '优秀论文奖与最佳演讲奖颁奖', venue: '主会场', type: 'keynote' },
    { id: 18, startTime: '12:00', endTime: '13:00', title: '闭幕式', description: '会议总结与下届展望', venue: '主会场', type: 'keynote' }
  ]
}

const currentSchedule = computed(() => scheduleData[selectedDay.value] || [])
</script>

<style scoped lang="scss">
.schedule-page {}

.days-bar {
  padding: $spacing-6 0;
  background: $bg-secondary;
  position: sticky;
  top: 65px;
  z-index: 10;
  border-bottom: 1px solid $border-color;

  .days-tabs {
    display: flex;
    gap: $spacing-2;
    justify-content: center;
    flex-wrap: wrap;

    .day-tab {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: $spacing-3 $spacing-8;
      border: none;
      background: transparent;
      cursor: pointer;
      border-radius: $radius-base;
      transition: all 0.2s;
      min-width: 100px;

      &.active {
        background: $primary-color;
        color: white;
      }

      &:hover:not(.active) { background: $bg-tertiary; }

      .day-label {
        font-size: $font-size-base;
        font-weight: $font-weight-semibold;
      }

      .day-date {
        font-size: $font-size-xs;
        opacity: 0.8;
        margin-top: 2px;
      }
    }
  }
}

.timeline-section {
  padding: $spacing-12 0 $spacing-24;
}

.timeline {
  max-width: 860px;
  margin: 0 auto;

  .timeline-item {
    display: grid;
    grid-template-columns: 80px 24px 1fr;
    gap: 0 $spacing-4;
    min-height: 80px;

    .time-col {
      text-align: right;
      padding-top: $spacing-4;

      .start-time {
        display: block;
        font-size: $font-size-xl;
        font-weight: $font-weight-bold;
        color: $primary-color;
        line-height: 1;
      }

      .end-time {
        display: block;
        font-size: $font-size-xs;
        color: $text-disabled;
        margin-top: 4px;
      }
    }

    .dot-col {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding-top: $spacing-4;

      .dot {
        width: 14px;
        height: 14px;
        border-radius: $radius-full;
        background: $primary-color;
        flex-shrink: 0;
        border: 2px solid white;
        box-shadow: 0 0 0 2px $primary-color;
        z-index: 1;

        &.dot-break { background: $text-disabled; box-shadow: 0 0 0 2px $text-disabled; }
        &.dot-keynote { background: $warning-color; box-shadow: 0 0 0 2px $warning-color; }
        &.dot-workshop { background: $success-color; box-shadow: 0 0 0 2px $success-color; }
      }

      .line {
        flex: 1;
        width: 2px;
        background: $border-color;
        margin-top: 4px;
      }
    }

    &:last-child .line { display: none; }

    .content-col {
      padding-bottom: $spacing-8;

      .session-card {
        background: white;
        border-radius: $radius-lg;
        padding: $spacing-5;
        box-shadow: $shadow-sm;
        border-left: 3px solid $primary-color;
        transition: box-shadow 0.2s;

        &:hover { box-shadow: $shadow-md; }

        &.card-break {
          background: $bg-secondary;
          border-color: $text-disabled;
          padding: $spacing-3 $spacing-5;
        }

        &.card-keynote { border-color: $warning-color; }
        &.card-workshop { border-color: $success-color; }

        .session-head {
          display: flex;
          align-items: flex-start;
          gap: $spacing-3;
          margin-bottom: $spacing-2;
          flex-wrap: wrap;

          .session-title {
            font-size: $font-size-lg;
            font-weight: $font-weight-semibold;
            color: $text-primary;
            flex: 1;
            min-width: 0;
          }

          .tags { display: flex; gap: $spacing-2; flex-wrap: wrap; }
        }

        .session-desc {
          color: $text-secondary;
          font-size: $font-size-sm;
          line-height: $line-height-relaxed;
          margin-bottom: $spacing-3;
        }

        .session-venue {
          display: flex;
          align-items: center;
          gap: $spacing-2;
          font-size: $font-size-sm;
          color: $primary-color;
          margin-bottom: $spacing-2;
        }

        .speaker-info {
          display: flex;
          align-items: center;
          gap: $spacing-2;
          margin-top: $spacing-3;
          padding-top: $spacing-3;
          border-top: 1px solid $divider-color;

          .speaker-name {
            font-size: $font-size-sm;
            font-weight: $font-weight-medium;
          }

          .speaker-org {
            font-size: $font-size-sm;
            color: $text-secondary;
          }
        }
      }
    }
  }
}
</style>
