<template>
  <div class="page-container">
    <div class="page-header">
      <h2>邮件日志</h2>
      <p class="page-desc">查看邮件发送记录与失败原因</p>
    </div>

    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="query">
        <el-form-item label="场景码">
          <el-input v-model="query.sceneCode" placeholder="如 VERIFICATION_CODE" clearable />
        </el-form-item>
        <el-form-item label="收件人">
          <el-input v-model="query.toEmail" placeholder="邮箱关键字" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable style="width: 120px">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.sendType" clearable style="width: 120px">
            <el-option label="业务发送" :value="1" />
            <el-option label="测试发送" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="sceneCode" label="场景码" min-width="170" />
        <el-table-column prop="toEmail" label="收件人" min-width="180" />
        <el-table-column prop="fromEmail" label="发件人" min-width="180" />
        <el-table-column label="发送类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.sendType === 2 ? 'warning' : 'primary'">
              {{ row.sendType === 2 ? '测试' : '业务' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorMessage" label="失败原因" min-width="220" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="创建时间" min-width="160" />
        <el-table-column prop="sentAt" label="发送时间" min-width="160" />
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row.id)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          background
          layout="total, prev, pager, next, sizes"
          :total="total"
          :page-size="query.pageSize"
          :current-page="query.page"
          :page-sizes="[10, 20, 50, 100]"
          @size-change="onSizeChange"
          @current-change="onPageChange"
        />
      </div>
    </el-card>

    <el-dialog v-model="detailVisible" title="日志详情" width="680px">
      <el-descriptions :column="2" border v-if="detail">
        <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="场景码">{{ detail.sceneCode }}</el-descriptions-item>
        <el-descriptions-item label="发送类型">{{ detail.sendType === 2 ? '测试发送' : '业务发送' }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ detail.status === 1 ? '成功' : '失败' }}</el-descriptions-item>
        <el-descriptions-item label="收件人">{{ detail.toEmail }}</el-descriptions-item>
        <el-descriptions-item label="发件人">{{ detail.fromEmail || '-' }}</el-descriptions-item>
        <el-descriptions-item label="主题" :span="2">{{ detail.subject || '-' }}</el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2">{{ detail.errorMessage || '-' }}</el-descriptions-item>
        <el-descriptions-item label="变量快照" :span="2">
          <pre class="json-block">{{ prettyVariables }}</pre>
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { systemApi, type EmailLogItem } from '@/api/system'

const loading = ref(false)
const list = ref<EmailLogItem[]>([])
const total = ref(0)

const query = reactive({
  page: 1,
  pageSize: 20,
  sceneCode: '',
  toEmail: '',
  status: undefined as number | undefined,
  sendType: undefined as number | undefined
})

const detailVisible = ref(false)
const detail = ref<EmailLogItem | null>(null)

const prettyVariables = computed(() => {
  if (!detail.value?.variablesJson) return '-'
  try {
    return JSON.stringify(JSON.parse(detail.value.variablesJson), null, 2)
  } catch {
    return detail.value.variablesJson
  }
})

async function loadList() {
  loading.value = true
  try {
    const res = await systemApi.getEmailLogs({
      page: query.page,
      pageSize: query.pageSize,
      sceneCode: query.sceneCode || undefined,
      toEmail: query.toEmail || undefined,
      status: query.status,
      sendType: query.sendType
    })
    list.value = res.list || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadList()
}

function resetQuery() {
  query.page = 1
  query.pageSize = 20
  query.sceneCode = ''
  query.toEmail = ''
  query.status = undefined
  query.sendType = undefined
  loadList()
}

function onPageChange(page: number) {
  query.page = page
  loadList()
}

function onSizeChange(size: number) {
  query.pageSize = size
  query.page = 1
  loadList()
}

async function openDetail(id: number) {
  detail.value = await systemApi.getEmailLogDetail(id)
  detailVisible.value = true
}

onMounted(loadList)
</script>

<style scoped lang="scss">
.page-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;

  h2 {
    margin: 0 0 6px;
    font-size: 20px;
    font-weight: 600;
  }

  .page-desc {
    margin: 0;
    color: #888;
    font-size: 13px;
  }
}

.filter-card {
  margin-bottom: 12px;
}

.pagination-wrap {
  margin-top: 14px;
  display: flex;
  justify-content: flex-end;
}

.json-block {
  margin: 0;
  white-space: pre-wrap;
  word-break: break-all;
}
</style>

