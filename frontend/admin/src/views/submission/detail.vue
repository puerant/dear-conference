<template>
  <div class="submission-detail">
    <div class="page-header">
      <el-button :icon="ArrowLeft" @click="$router.back()">返回列表</el-button>
      <h2 class="page-title">投稿详情</h2>
    </div>

    <div v-loading="loading">
      <el-row :gutter="20" v-if="detail">
        <el-col :span="16">
          <el-card class="info-card" header="基本信息">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="投稿ID" :span="1">{{ detail.id }}</el-descriptions-item>
              <el-descriptions-item label="状态" :span="1">
                <el-tag :type="getSubmissionStatusType(detail.status)">
                  {{ getSubmissionStatusLabel(detail.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="投稿标题" :span="2">{{ detail.title }}</el-descriptions-item>
              <el-descriptions-item label="讲者" :span="1">{{ detail.speakerName }}</el-descriptions-item>
              <el-descriptions-item label="讲者邮箱" :span="1">{{ detail.speakerEmail }}</el-descriptions-item>
              <el-descriptions-item label="分类" :span="1">{{ detail.category }}</el-descriptions-item>
              <el-descriptions-item label="提交时间" :span="1">{{ formatDate(detail.createdAt) }}</el-descriptions-item>
              <el-descriptions-item label="摘要" :span="2">
                <div class="abstract-text">{{ detail.abstract || '-' }}</div>
              </el-descriptions-item>
              <el-descriptions-item label="论文附件" :span="2">
                <el-link
                  v-if="detail.fileUrl"
                  type="primary"
                  :href="detail.fileUrl"
                  target="_blank"
                >
                  下载论文
                </el-link>
                <span v-else>-</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>

          <el-card class="reviews-card" header="审稿记录" style="margin-top: 20px">
            <el-empty v-if="!detail.reviews?.length" description="暂无审稿记录" />
            <el-table v-else :data="detail.reviews" border>
              <el-table-column prop="reviewerName" label="审稿人" width="120" />
              <el-table-column label="结果" width="90">
                <template #default="{ row }">
                  <el-tag :type="getReviewResultType(row.result)" size="small">
                    {{ getReviewResultLabel(row.result) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="score" label="评分" width="80" align="center" />
              <el-table-column prop="comment" label="审稿意见" min-width="160" show-overflow-tooltip />
              <el-table-column label="时间" width="160">
                <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card header="操作">
            <div class="action-section">
              <p class="action-label">修改状态</p>
              <el-select v-model="newStatus" placeholder="选择状态" style="width: 100%; margin-bottom: 10px">
                <el-option label="待审稿" value="pending" />
                <el-option label="审稿中" value="reviewing" />
                <el-option label="已录用" value="accepted" />
                <el-option label="已拒绝" value="rejected" />
              </el-select>
              <el-button
                type="primary"
                style="width: 100%"
                :loading="statusUpdating"
                @click="handleUpdateStatus"
              >
                确认修改
              </el-button>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { submissionApi, type SubmissionDetail } from '@/api/submission'
import {
  formatDate,
  getSubmissionStatusLabel,
  getSubmissionStatusType,
  getReviewResultLabel,
  getReviewResultType
} from '@/utils/format'

const route = useRoute()
const detail = ref<SubmissionDetail | null>(null)
const loading = ref(false)
const newStatus = ref('')
const statusUpdating = ref(false)

async function fetchDetail() {
  const id = Number(route.params.id)
  if (!id) return
  loading.value = true
  try {
    const res = await submissionApi.getDetail(id)
    detail.value = res
    newStatus.value = res.status
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function handleUpdateStatus() {
  if (!detail.value || !newStatus.value) return
  statusUpdating.value = true
  try {
    await submissionApi.updateStatus(detail.value.id, newStatus.value)
    ElMessage.success('状态更新成功')
    detail.value.status = newStatus.value
  } catch {
    // ignore
  } finally {
    statusUpdating.value = false
  }
}

onMounted(() => fetchDetail())
</script>

<style lang="scss" scoped>
.submission-detail {
  .page-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 20px;

    .page-title {
      font-size: 18px;
      font-weight: 600;
      color: #262626;
      margin: 0;
    }
  }

  .abstract-text {
    line-height: 1.6;
    color: #595959;
    white-space: pre-wrap;
  }

  .action-section {
    .action-label {
      font-size: 13px;
      color: #8c8c8c;
      margin-bottom: 8px;
    }
  }
}
</style>
