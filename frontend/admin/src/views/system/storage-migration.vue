<template>
  <div>
    <el-card style="margin-bottom: 16px;">
      <template #header>
        <span>创建迁移任务</span>
      </template>
      <el-form :model="createForm" inline>
        <el-form-item label="源供应商">
          <el-select v-model="createForm.sourceProviderId" style="width: 220px">
            <el-option v-for="item in providers" :key="item.id" :label="item.providerName" :value="item.id!" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标供应商">
          <el-select v-model="createForm.targetProviderId" style="width: 220px">
            <el-option v-for="item in providers" :key="item.id" :label="item.providerName" :value="item.id!" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="creating" @click="createTask">创建并执行</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center;">
          <span>迁移任务列表</span>
          <el-button @click="fetchTasks">刷新</el-button>
        </div>
      </template>
      <el-table :data="tasks" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="taskNo" label="任务号" min-width="220" />
        <el-table-column prop="sourceProviderId" label="源" width="80" />
        <el-table-column prop="targetProviderId" label="目标" width="80" />
        <el-table-column prop="fileCount" label="总数" width="80" />
        <el-table-column prop="successCount" label="成功" width="80" />
        <el-table-column prop="failCount" label="失败" width="80" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="errorMessage" label="错误信息" min-width="240" show-overflow-tooltip />
        <el-table-column label="操作" width="140">
          <template #default="{ row }">
            <el-button link type="primary" @click="retry(row.id)" :disabled="row.status !== 4">重试</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { systemApi, type FileMigrationTask, type StorageProvider } from '@/api/system'

const providers = ref<StorageProvider[]>([])
const tasks = ref<FileMigrationTask[]>([])
const loading = ref(false)
const creating = ref(false)

const createForm = reactive({
  sourceProviderId: 0,
  targetProviderId: 0
})

function statusText(status: number) {
  if (status === 1) return '待执行'
  if (status === 2) return '执行中'
  if (status === 3) return '完成'
  if (status === 4) return '失败'
  return '未知'
}

function statusType(status: number) {
  if (status === 3) return 'success'
  if (status === 4) return 'danger'
  if (status === 2) return 'warning'
  return 'info'
}

async function fetchProviders() {
  providers.value = await systemApi.listStorageProviders()
  if (!createForm.sourceProviderId && providers.value.length > 0) {
    createForm.sourceProviderId = providers.value[0].id!
  }
  if (!createForm.targetProviderId && providers.value.length > 1) {
    createForm.targetProviderId = providers.value[1].id!
  }
}

async function fetchTasks() {
  loading.value = true
  try {
    tasks.value = await systemApi.listFileMigrationTasks()
  } finally {
    loading.value = false
  }
}

async function createTask() {
  if (!createForm.sourceProviderId || !createForm.targetProviderId) {
    ElMessage.warning('请选择源和目标供应商')
    return
  }
  creating.value = true
  try {
    await systemApi.createFileMigrationTask({
      sourceProviderId: createForm.sourceProviderId,
      targetProviderId: createForm.targetProviderId
    })
    ElMessage.success('任务已创建')
    fetchTasks()
  } finally {
    creating.value = false
  }
}

async function retry(id: number) {
  await systemApi.retryFileMigrationTask(id)
  ElMessage.success('已重试')
  fetchTasks()
}

onMounted(async () => {
  await fetchProviders()
  await fetchTasks()
})
</script>

