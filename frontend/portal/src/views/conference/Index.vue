<template>
  <div class="conference-page">
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <template v-else-if="conferenceInfo">
      <!-- Banner -->
      <div v-if="conferenceInfo.bannerUrl" class="conference-banner">
        <img :src="conferenceInfo.bannerUrl" :alt="conferenceInfo.title" />
        <div class="banner-overlay">
          <h1 class="conference-title">{{ conferenceInfo.title }}</h1>
          <p v-if="conferenceInfo.subtitle" class="conference-subtitle">{{ conferenceInfo.subtitle }}</p>
        </div>
      </div>

      <!-- Conference Info -->
      <el-card class="info-card">
        <template #header>
          <div class="card-header">
            <el-icon><Calendar /></el-icon>
            <span>会议信息</span>
          </div>
        </template>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="会议日期">
            {{ formatDate(conferenceInfo.startDate) }} - {{ formatDate(conferenceInfo.endDate) }}
          </el-descriptions-item>
          <el-descriptions-item label="会议地点">
            {{ conferenceInfo.location || '待定' }}
          </el-descriptions-item>
          <el-descriptions-item label="详细地址">
            {{ conferenceInfo.address || '待定' }}
          </el-descriptions-item>
          <el-descriptions-item label="联系人">
            {{ conferenceInfo.contactName || '会务组' }}
          </el-descriptions-item>
          <el-descriptions-item label="联系电话">
            {{ conferenceInfo.contactPhone || '-' }}
          </el-descriptions-item>
          <el-descriptions-item label="联系邮箱">
            {{ conferenceInfo.contactEmail || '-' }}
          </el-descriptions-item>
        </el-descriptions>

        <div v-if="conferenceInfo.description" class="description-section">
          <h3>会议简介</h3>
          <p>{{ conferenceInfo.description }}</p>
        </div>
      </el-card>
    </template>

    <template v-else>
      <el-empty description="会议信息尚未发布，敬请期待！" />
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Calendar } from '@element-plus/icons-vue'
import { getConferenceInfo } from '@/api/conference'

const loading = ref(true)
const conferenceInfo = ref<API.ConferenceInfo | null>(null)

const loadConferenceInfo = async () => {
  try {
    loading.value = true
    const res = await getConferenceInfo()
    conferenceInfo.value = res.data
  } catch (error) {
    console.error('加载会议信息失败:', error)
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`
}

onMounted(() => {
  loadConferenceInfo()
})
</script>

<style scoped>
.conference-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.loading-container {
  padding: 40px;
}

.conference-banner {
  position: relative;
  width: 100%;
  height: 300px;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 24px;
}

.conference-banner img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.6));
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
}

.conference-title {
  font-size: 36px;
  font-weight: bold;
  margin-bottom: 12px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.5);
}

.conference-subtitle {
  font-size: 20px;
  opacity: 0.9;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.5);
}

.info-card {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
}

.description-section {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid var(--el-border-color-lighter);
}

.description-section h3 {
  font-size: 16px;
  margin-bottom: 12px;
  color: var(--el-text-color-primary);
}

.description-section p {
  line-height: 1.8;
  color: var(--el-text-color-regular);
  white-space: pre-wrap;
}
</style>
