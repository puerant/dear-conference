<template>
  <div class="submission-page">
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">{{ t('submission.title') }}</h1>
      </div>
    </section>

    <div class="container page-body">
      <div class="toolbar">
        <div class="filters">
          <el-select v-model="query.status" clearable placeholder="状态筛选" style="width: 140px" @change="loadList">
            <el-option label="待审稿" value="pending" />
            <el-option label="审稿中" value="reviewing" />
            <el-option label="已录用" value="accepted" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
          <el-input
            v-model="query.keyword"
            placeholder="搜索标题..."
            style="width: 220px"
            clearable
            @keyup.enter="loadList"
          >
            <template #prefix><el-icon><search /></el-icon></template>
          </el-input>
          <el-button @click="loadList">搜索</el-button>
        </div>
        <el-button type="primary" @click="router.push({ name: 'SubmissionCreate' })">
          <el-icon><plus /></el-icon>{{ t('submission.create') }}
        </el-button>
      </div>

      <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无投稿记录">
        <el-table-column prop="title" label="论文标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getSubmissionStatusType(row.status) as 'success' | 'warning' | 'danger' | 'info'">
              {{ getSubmissionStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="提交时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="router.push({ name: 'SubmissionDetail', params: { id: row.id } })">
              查看
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              link
              type="warning"
              @click="router.push({ name: 'SubmissionEdit', params: { id: row.id } })"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.pageSize"
          :total="total"
          :page-sizes="[10, 20]"
          layout="total, sizes, prev, pager, next"
          @change="loadList"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { submissionApi } from '@/api/submission'
import type { Submission } from '@/api/types'
import { formatDate, getSubmissionStatusLabel, getSubmissionStatusType } from '@/utils/format'

const router = useRouter()
const { t } = useI18n()

const loading = ref(false)
const list = ref<Submission[]>([])
const total = ref(0)
const query = ref({ page: 1, pageSize: 10, status: undefined as string | undefined, keyword: '' })

const loadList = async () => {
  loading.value = true
  try {
    const res = await submissionApi.getList(query.value)
    list.value = res.list
    total.value = res.total
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

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: $spacing-6;
  gap: $spacing-4;

  .filters {
    display: flex;
    gap: $spacing-3;
    align-items: center;
    flex-wrap: wrap;
  }
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: $spacing-6;
}
</style>
