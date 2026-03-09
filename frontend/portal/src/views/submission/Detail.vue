<template>
  <div class="submission-detail-page">
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">{{ t('submission.detail') }}</h1>
      </div>
    </section>

    <div class="container page-body">
      <el-button link @click="router.push({ name: 'Submission' })" style="margin-bottom: 16px">
        <el-icon><arrow-left /></el-icon> 返回列表
      </el-button>

      <el-card v-loading="loading" class="detail-card">
        <template v-if="submission">
          <div v-if="submission.status === 'pending'" class="detail-actions">
            <el-button
              type="primary"
              @click="router.push({ name: 'SubmissionEdit', params: { id: submission.id } })"
            >
              编辑投稿
            </el-button>
          </div>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="论文标题" :span="2">{{ submission.title }}</el-descriptions-item>
            <el-descriptions-item label="分类">{{ submission.category }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getSubmissionStatusType(submission.status) as 'success' | 'warning' | 'danger' | 'info'">
                {{ getSubmissionStatusLabel(submission.status) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="提交时间">{{ formatDate(submission.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="最后更新">{{ formatDate(submission.updatedAt) }}</el-descriptions-item>
            <el-descriptions-item label="摘要" :span="2">
              <div style="white-space: pre-wrap; line-height: 1.7">{{ submission.abstract }}</div>
            </el-descriptions-item>
            <el-descriptions-item v-if="submission.fileUrl" label="附件" :span="2">
              <el-link type="primary" :href="submission.fileUrl" target="_blank">
                <el-icon><download /></el-icon> 下载附件
              </el-link>
            </el-descriptions-item>
          </el-descriptions>
        </template>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { submissionApi } from '@/api/submission'
import type { Submission } from '@/api/types'
import { formatDate, getSubmissionStatusLabel, getSubmissionStatusType } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const loading = ref(false)
const submission = ref<Submission | null>(null)

onMounted(async () => {
  loading.value = true
  try {
    submission.value = await submissionApi.getDetail(Number(route.params.id))
  } catch {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.page-body {
  padding: $spacing-8 $spacing-6;
}

.detail-card {
  max-width: 860px;
}

.detail-actions {
  display: flex;
  justify-content: flex-end;
  margin-bottom: $spacing-4;
}
</style>
