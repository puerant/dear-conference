<template>
  <div class="submission-page">
    <el-card class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="query.keyword"
            placeholder="投稿标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待审稿" value="pending" />
            <el-option label="审稿中" value="reviewing" />
            <el-option label="已录用" value="accepted" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="query.category" placeholder="全部" clearable style="width: 140px">
            <el-option label="人工智能" value="人工智能" />
            <el-option label="大数据" value="大数据" />
            <el-option label="云计算" value="云计算" />
            <el-option label="网络安全" value="网络安全" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList(1)">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="title" label="投稿标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="speakerName" label="讲者" width="100" />
        <el-table-column prop="category" label="分类" width="110" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getSubmissionStatusType(row.status)" size="small">
              {{ getSubmissionStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="reviewCount" label="审稿人数" width="90" align="center" />
        <el-table-column label="提交时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="goDetail(row.id)">详情</el-button>
            <el-button
              type="warning"
              link
              size="small"
              @click="openAssign(row)"
            >
              分配
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        class="pagination"
        @current-change="fetchList"
        @size-change="fetchList(1)"
      />
    </el-card>

    <!-- Assign Reviewer Dialog -->
    <el-dialog v-model="assignVisible" title="分配审稿人" width="500px" @open="loadReviewers">
      <div v-if="assignTarget" class="assign-info">
        <p><strong>投稿：</strong>{{ assignTarget.title }}</p>
      </div>
      <el-divider />

      <div v-if="reviewerListLoading" style="text-align: center; padding: 20px">
        <el-icon class="is-loading"><Loading /></el-icon>
        <span style="margin-left: 8px; color: #8c8c8c">加载审稿人列表...</span>
      </div>

      <el-empty
        v-else-if="reviewerList.length === 0"
        description="暂无审稿人，请先在用户管理中添加审稿人账号"
        :image-size="80"
      >
        <el-button type="primary" size="small" @click="assignVisible = false; $router.push('/user')">
          前往用户管理
        </el-button>
      </el-empty>

      <template v-else>
        <p class="assign-hint">选择审稿人（可多选）：</p>
        <el-checkbox-group v-model="selectedReviewers" class="reviewer-list">
          <el-checkbox
            v-for="reviewer in reviewerList"
            :key="reviewer.id"
            :value="reviewer.id"
            border
          >
            {{ reviewer.name }}
            <span class="reviewer-email">({{ reviewer.email }})</span>
          </el-checkbox>
        </el-checkbox-group>
      </template>

      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="assigning"
          :disabled="reviewerList.length === 0"
          @click="handleAssign"
        >
          确认分配
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Loading } from '@element-plus/icons-vue'
import { submissionApi, type Submission } from '@/api/submission'
import { formatDate, getSubmissionStatusLabel, getSubmissionStatusType } from '@/utils/format'

const router = useRouter()
const list = ref<Submission[]>([])
const total = ref(0)
const loading = ref(false)

const query = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  status: '',
  category: ''
})

// Assign dialog
const assignVisible = ref(false)
const assignTarget = ref<Submission | null>(null)
const selectedReviewers = ref<number[]>([])
const reviewerList = ref<{ id: number; name: string; email: string }[]>([])
const reviewerListLoading = ref(false)
const assigning = ref(false)

async function fetchList(page?: number) {
  if (page) query.page = page
  loading.value = true
  try {
    const res = await submissionApi.getList({
      page: query.page,
      pageSize: query.pageSize,
      keyword: query.keyword || undefined,
      status: query.status || undefined,
      category: query.category || undefined
    })
    list.value = res.list
    total.value = res.total
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

function handleReset() {
  query.keyword = ''
  query.status = ''
  query.category = ''
  fetchList(1)
}

function goDetail(id: number) {
  router.push(`/submission/${id}`)
}

function openAssign(row: Submission) {
  assignTarget.value = row
  selectedReviewers.value = []
  assignVisible.value = true
}

async function loadReviewers() {
  reviewerList.value = []
  reviewerListLoading.value = true
  try {
    const res = await submissionApi.getReviewers()
    reviewerList.value = res.list as unknown as { id: number; name: string; email: string }[]
  } catch {
    ElMessage.error('加载审稿人列表失败')
  } finally {
    reviewerListLoading.value = false
  }
}

async function handleAssign() {
  if (!assignTarget.value) return
  if (selectedReviewers.value.length === 0) {
    ElMessage.warning('请至少选择一位审稿人')
    return
  }
  assigning.value = true
  try {
    await submissionApi.assignReviewer(assignTarget.value.id, selectedReviewers.value)
    ElMessage.success('分配成功')
    assignVisible.value = false
    fetchList()
  } catch {
    // ignore
  } finally {
    assigning.value = false
  }
}

async function handleDelete(row: Submission) {
  await ElMessageBox.confirm(`确定要删除投稿 "${row.title}" 吗？此操作不可恢复！`, '警告', {
    type: 'error',
    confirmButtonText: '删除',
    confirmButtonClass: 'el-button--danger'
  })
  try {
    await submissionApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    // ignore
  }
}

onMounted(() => fetchList())
</script>

<style lang="scss" scoped>
.submission-page {
  .filter-card {
    margin-bottom: 16px;

    :deep(.el-card__body) {
      padding: 16px 20px 0;
    }
  }

  .table-card {
    .pagination {
      margin-top: 16px;
      justify-content: flex-end;
    }
  }
}

.assign-info {
  color: #595959;
  font-size: 14px;
}

.assign-hint {
  font-size: 13px;
  color: #8c8c8c;
  margin-bottom: 12px;
}

.reviewer-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  .reviewer-email {
    font-size: 12px;
    color: #8c8c8c;
    margin-left: 4px;
  }
}
</style>
