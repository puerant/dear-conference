<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center;">
          <span>OSS 供应商管理</span>
          <el-button type="primary" @click="openCreate">新增供应商</el-button>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="providerName" label="名称" min-width="180" />
        <el-table-column prop="providerType" label="类型" width="120">
          <template #default="{ row }">{{ typeText(row.providerType) }}</template>
        </el-table-column>
        <el-table-column prop="endpoint" label="Endpoint" min-width="220" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.isEnabled === 1 ? 'success' : 'info'">{{ row.isEnabled === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="默认" width="90">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault === 1" type="success">默认</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="330" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handleTest(row)">测试</el-button>
            <el-button link type="success" @click="handleSetDefault(row)" :disabled="row.isDefault === 1">设默认</el-button>
            <el-button link type="warning" @click="handleToggle(row)">{{ row.isEnabled === 1 ? '禁用' : '启用' }}</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑供应商' : '新增供应商'" width="560px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="名称">
          <el-input v-model="form.providerName" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.providerType" style="width:100%">
            <el-option label="本地" :value="1" />
            <el-option label="阿里云 OSS" :value="2" />
            <el-option label="腾讯云 COS" :value="3" />
            <el-option label="MinIO/S3" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="Endpoint">
          <el-input v-model="form.endpoint" />
        </el-form-item>
        <el-form-item label="Region">
          <el-input v-model="form.region" />
        </el-form-item>
        <el-form-item label="AccessKey">
          <el-input v-model="form.accessKey" />
        </el-form-item>
        <el-form-item label="SecretKey">
          <el-input v-model="form.secretKey" show-password />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="enabled" />
        </el-form-item>
        <el-form-item label="默认">
          <el-switch v-model="isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { systemApi, type StorageProvider } from '@/api/system'

const loading = ref(false)
const dialogVisible = ref(false)
const list = ref<StorageProvider[]>([])
const form = reactive<StorageProvider>({
  providerName: '',
  providerType: 1,
  endpoint: '',
  region: '',
  accessKey: '',
  secretKey: '',
  isEnabled: 1,
  isDefault: 0
})

const enabled = computed({
  get: () => form.isEnabled === 1,
  set: (val: boolean) => { form.isEnabled = val ? 1 : 0 }
})
const isDefault = computed({
  get: () => form.isDefault === 1,
  set: (val: boolean) => { form.isDefault = val ? 1 : 0 }
})

function typeText(type: number) {
  if (type === 1) return 'LOCAL'
  if (type === 2) return 'ALI_OSS'
  if (type === 3) return 'TENCENT_COS'
  if (type === 4) return 'MINIO_S3'
  return 'UNKNOWN'
}

function resetForm() {
  form.id = undefined
  form.providerName = ''
  form.providerType = 1
  form.endpoint = ''
  form.region = ''
  form.accessKey = ''
  form.secretKey = ''
  form.isEnabled = 1
  form.isDefault = 0
}

async function fetchList() {
  loading.value = true
  try {
    list.value = await systemApi.listStorageProviders()
  } finally {
    loading.value = false
  }
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row: StorageProvider) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.providerName) {
    ElMessage.warning('请输入名称')
    return
  }
  if (!form.providerType) {
    ElMessage.warning('请选择类型')
    return
  }
  if (form.id) {
    await systemApi.updateStorageProvider(form.id, form)
  } else {
    await systemApi.createStorageProvider(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  fetchList()
}

async function handleToggle(row: StorageProvider) {
  await systemApi.updateStorageProviderEnabled(row.id!, row.isEnabled === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  fetchList()
}

async function handleSetDefault(row: StorageProvider) {
  await systemApi.setDefaultStorageProvider(row.id!)
  ElMessage.success('已设为默认')
  fetchList()
}

async function handleTest(row: StorageProvider) {
  await systemApi.testStorageProvider(row.id!)
  ElMessage.success('连接测试通过')
}

async function handleDelete(row: StorageProvider) {
  await ElMessageBox.confirm(`确认删除供应商「${row.providerName}」吗？`, '提示', { type: 'warning' })
  await systemApi.deleteStorageProvider(row.id!)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(fetchList)
</script>

