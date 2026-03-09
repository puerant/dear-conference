<template>
  <div class="expert-page">
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">{{ t('expert.title') }}</h1>
        <p class="page-subtitle">{{ t('expert.subtitle') }}</p>
      </div>
    </section>

    <section class="experts-section" v-loading="loading">
      <div class="container">
        <!-- 主旨演讲嘉宾 -->
        <div v-if="keynoteSpeakers.length > 0" class="expert-group">
          <h2 class="group-title">
            <el-icon><Star /></el-icon>
            主旨演讲嘉宾
          </h2>
          <div class="experts-grid">
            <div v-for="expert in keynoteSpeakers" :key="expert.id" class="expert-card keynote">
              <div class="expert-avatar">
                <el-avatar :size="120" :src="expert.avatarUrl">
                  {{ expert.name?.charAt(0) }}
                </el-avatar>
              </div>
              <div class="expert-info">
                <h3 class="expert-name">{{ expert.name }}</h3>
                <p class="expert-title">{{ expert.title }}</p>
                <p class="expert-org">{{ expert.organization }}</p>
                <p class="expert-bio">{{ expert.bio }}</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 普通嘉宾 -->
        <div v-if="speakers.length > 0" class="expert-group">
          <h2 class="group-title">
            <el-icon><User /></el-icon>
            嘉宾介绍
          </h2>
          <div class="experts-grid">
            <div v-for="expert in speakers" :key="expert.id" class="expert-card">
              <div class="expert-avatar">
                <el-avatar :size="100" :src="expert.avatarUrl">
                  {{ expert.name?.charAt(0) }}
                </el-avatar>
              </div>
              <div class="expert-info">
                <h3 class="expert-name">{{ expert.name }}</h3>
                <p class="expert-title">{{ expert.title }}</p>
                <p class="expert-org">{{ expert.organization }}</p>
                <p class="expert-bio">{{ expert.bio }}</p>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-if="!loading && keynoteSpeakers.length === 0 && speakers.length === 0" description="暂无专家信息" />
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { Star, User } from '@element-plus/icons-vue'
import { getExpertList } from '@/api/conference'
import type { ExpertVo } from '@/api/types'

const { t } = useI18n()

const loading = ref(false)
const keynoteSpeakers = ref<ExpertVo[]>([])
const speakers = ref<ExpertVo[]>([])

const loadExperts = async () => {
  try {
    loading.value = true
    const res = await getExpertList()
    keynoteSpeakers.value = res.data?.keynoteSpeakers || []
    speakers.value = res.data?.speakers || []
  } catch (error) {
    console.error('加载专家失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadExperts()
})
</script>

<style scoped lang="scss">
.expert-page {}

.experts-section {
  padding: $spacing-12 0;
}

.expert-group {
  margin-bottom: $spacing-12;
}

.group-title {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  font-size: $font-size-xl;
  font-weight: $font-weight-bold;
  color: $text-primary;
  margin-bottom: $spacing-6;
}

.experts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: $spacing-6;
}

.expert-card {
  background: white;
  border-radius: $radius-lg;
  padding: $spacing-6;
  text-align: center;
  transition: all 0.3s;
  box-shadow: $shadow-sm;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-lg;
  }

  &.keynote {
    border: 2px solid $warning-color;
  }

  .expert-avatar {
    margin-bottom: $spacing-4;
  }

  .expert-info {
    .expert-name {
      font-size: $font-size-lg;
      font-weight: $font-weight-semibold;
      color: $text-primary;
      margin-bottom: $spacing-1;
    }

    .expert-title {
      font-size: $font-size-sm;
      color: $text-secondary;
      margin-bottom: $spacing-1;
    }

    .expert-org {
      font-size: $font-size-sm;
      color: $primary-color;
      margin-bottom: $spacing-2;
    }

    .expert-bio {
      font-size: $font-size-sm;
      color: $text-placeholder;
      line-height: $line-height-relaxed;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
  }
}
</style>
