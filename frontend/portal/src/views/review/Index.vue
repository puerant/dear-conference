<template>
  <div class="review-page">
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">{{ t('review.title') }}</h1>
      </div>
    </section>

    <div class="container page-body">
      <!-- Tabs: pending / completed (client-side filter, backend returns all at once) -->
      <el-tabs v-model="activeTab">
        <el-tab-pane :label="t('review.pending')" name="pending" />
        <el-tab-pane :label="t('review.completed')" name="completed" />
      </el-tabs>

      <el-table v-loading="loading" :data="filteredList" style="width: 100%" empty-text="暂无审稿记录">
        <el-table-column label="论文标题" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.submission?.title || `投稿 #${row.submissionId}` }}
          </template>
        </el-table-column>
        <el-table-column label="分类" width="120">
          <template #default="{ row }">{{ row.submission?.category || '-' }}</template>
        </el-table-column>
        <el-table-column label="审稿结果" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.result" :type="getReviewResultType(row.result) as 'success' | 'warning' | 'danger' | 'info'">
              {{ getReviewResultLabel(row.result) }}
            </el-tag>
            <el-tag v-else type="info">待审稿</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评分" width="80">
          <template #default="{ row }">{{ row.score ?? '-' }}</template>
        </el-table-column>
        <el-table-column label="时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <!-- Navigate with submissionId — detail page uses GET /submissions/{id} + GET /reviews/submission/{id} -->
            <el-button link type="primary" @click="router.push({ name: 'ReviewDetail', params: { id: row.submissionId } })">
              {{ row.result ? '查看' : '审稿' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { reviewApi } from '@/api/review'
import type { Review } from '@/api/types'
import { formatDate, getReviewResultLabel, getReviewResultType } from '@/utils/format'

const router = useRouter()
const { t } = useI18n()

const loading = ref(false)
const allReviews = ref<Review[]>([])
const activeTab = ref<'pending' | 'completed'>('pending')

// Backend GET /reviews/my returns List<Review> (non-paginated) — filter client-side
const filteredList = computed(() => {
  if (activeTab.value === 'pending') return allReviews.value.filter(r => !r.result)
  return allReviews.value.filter(r => !!r.result)
})

const loadList = async () => {
  loading.value = true
  try {
    allReviews.value = await reviewApi.getMyList()
  } catch {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(loadList)
</script>

<style scoped lang="scss">
.page-body {
  padding: $spacing-8 $spacing-6;
}
</style>
