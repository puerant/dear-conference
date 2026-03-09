<template>
  <div class="credential-page">
    <el-card class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="凭证号">
          <el-input v-model="query.credentialNo" placeholder="请输入凭证号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="有效" value="valid" />
            <el-option label="已使用" value="used" />
            <el-option label="已过期" value="expired" />
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
        <el-table-column prop="credentialNo" label="凭证号" min-width="160" show-overflow-tooltip />
        <el-table-column prop="attendeeName" label="参会人" width="100" />
        <el-table-column prop="attendeeEmail" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="ticketName" label="票务类型" width="120" show-overflow-tooltip />
        <el-table-column prop="seatInfo" label="座位" width="90">
          <template #default="{ row }">{{ row.seatInfo || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getCredentialStatusType(row.status)" size="small">
              {{ getCredentialStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="有效期至" width="160">
          <template #default="{ row }">
            <span :class="{ 'text-danger': isExpiredRow(row) }">
              {{ row.expireAt ? formatDate(row.expireAt) : '永久' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="使用时间" width="160">
          <template #default="{ row }">{{ formatDate(row.usedAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">详情</el-button>
            <el-button
              v-if="row.status === 'valid'"
              type="success"
              link
              size="small"
              @click="handleUse(row)"
            >
              核销
            </el-button>
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

    <!-- Detail Dialog -->
    <el-dialog v-model="detailVisible" title="凭证详情" width="540px">
      <template v-if="currentCredential">
        <!-- 二维码 -->
        <div class="qr-wrapper">
          <QrCodeImg :text="currentCredential.credentialNo" :size="160" />
        </div>

        <el-descriptions :column="2" border>
          <el-descriptions-item label="凭证号" :span="2">
            <span class="cred-no">{{ currentCredential.credentialNo }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="参会人">{{ currentCredential.attendeeName }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ currentCredential.attendeeEmail }}</el-descriptions-item>
          <el-descriptions-item label="票务">{{ currentCredential.ticketName }}</el-descriptions-item>
          <el-descriptions-item label="座位">{{ currentCredential.seatInfo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="getCredentialStatusType(currentCredential.status)" size="small">
              {{ getCredentialStatusLabel(currentCredential.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="有效期至">
            <span :class="{ 'text-danger': isExpiredRow(currentCredential) }">
              {{ currentCredential.expireAt ? formatDate(currentCredential.expireAt) : '永久有效' }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(currentCredential.createdAt) }}</el-descriptions-item>
          <el-descriptions-item label="使用时间">{{ formatDate(currentCredential.usedAt) }}</el-descriptions-item>
        </el-descriptions>
      </template>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
        <el-button
          v-if="currentCredential?.status === 'valid'"
          type="success"
          @click="handleUse(currentCredential!)"
        >
          核销
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { credentialApi, type Credential } from '@/api/credential'
import { formatDate, getCredentialStatusLabel, getCredentialStatusType } from '@/utils/format'
import QrCodeImg from '@/components/QrCodeImg.vue'

const list = ref<Credential[]>([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const currentCredential = ref<Credential | null>(null)

const query = reactive({
  page: 1,
  pageSize: 10,
  credentialNo: '',
  status: ''
})

function isExpiredRow(row: Credential) {
  return row.expireAt ? new Date(row.expireAt) < new Date() : false
}

async function fetchList(page?: number) {
  if (page) query.page = page
  loading.value = true
  try {
    const res = await credentialApi.getList({
      page: query.page,
      pageSize: query.pageSize,
      credentialNo: query.credentialNo || undefined,
      status: query.status || undefined
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
  query.credentialNo = ''
  query.status = ''
  fetchList(1)
}

async function handleView(row: Credential) {
  // 点详情时重新拉取完整数据（含完整 qrCode 字段）
  try {
    currentCredential.value = await credentialApi.getDetail(row.credentialNo)
  } catch {
    currentCredential.value = row
  }
  detailVisible.value = true
}

async function handleUse(row: Credential) {
  await ElMessageBox.confirm(`确定要核销凭证 "${row.credentialNo}" 吗？`, '核销确认', {
    type: 'warning',
    confirmButtonText: '确定核销'
  })
  try {
    await credentialApi.use(row.credentialNo)
    ElMessage.success('核销成功')
    detailVisible.value = false
    fetchList()
  } catch {
    // ignore
  }
}

onMounted(() => fetchList())
</script>

<style lang="scss" scoped>
.credential-page {
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

  .text-danger {
    color: var(--el-color-danger);
  }
}

.qr-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.cred-no {
  font-family: monospace;
  font-size: 13px;
  letter-spacing: 0.5px;
}
</style>
