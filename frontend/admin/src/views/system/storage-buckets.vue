<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center;">
          <div>
            <span style="margin-right:12px;">存储桶管理</span>
            <el-select v-model="providerFilter" placeholder="按供应商筛选" clearable style="width:220px" @change="fetchBuckets">
              <el-option v-for="item in providers" :key="item.id" :label="item.providerName" :value="item.id" />
            </el-select>
          </div>
          <el-button type="primary" @click="openCreate">新增存储桶</el-button>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="providerId" label="供应商ID" width="100" />
        <el-table-column prop="bucketName" label="Bucket" min-width="180" />
        <el-table-column prop="basePath" label="BasePath" min-width="160" />
        <el-table-column prop="domain" label="Domain" min-width="200" show-overflow-tooltip />
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
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="success" @click="handleSetDefault(row)" :disabled="row.isDefault === 1">设默认</el-button>
            <el-button link type="warning" @click="handleToggle(row)">{{ row.isEnabled === 1 ? '禁用' : '启用' }}</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑存储桶' : '新增存储桶'" width="560px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="供应商">
          <el-select v-model="form.providerId" style="width:100%">
            <el-option v-for="item in providers" :key="item.id" :label="item.providerName" :value="item.id!" />
          </el-select>
        </el-form-item>
        <el-form-item label="Bucket 名称">
          <el-input v-model="form.bucketName" />
        </el-form-item>
        <el-form-item label="BasePath">
          <el-input v-model="form.basePath" />
        </el-form-item>
        <el-form-item label="Domain">
          <el-input v-model="form.domain" />
        </el-form-item>
        <el-form-item label="ACL">
          <el-select v-model="form.aclType" style="width:100%">
            <el-option label="private" :value="1" />
            <el-option label="public-read" :value="2" />
          </el-select>
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
import { systemApi, type StorageBucket, type StorageProvider } from '@/api/system'

const loading = ref(false)
const dialogVisible = ref(false)
const providerFilter = ref<number>()
const providers = ref<StorageProvider[]>([])
const list = ref<StorageBucket[]>([])

const form = reactive<StorageBucket>({
  providerId: 0,
  bucketName: '',
  basePath: '',
  domain: '',
  aclType: 1,
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

function resetForm() {
  form.id = undefined
  form.providerId = providerFilter.value || providers.value[0]?.id || 0
  form.bucketName = ''
  form.basePath = ''
  form.domain = ''
  form.aclType = 1
  form.isEnabled = 1
  form.isDefault = 0
}

async function fetchProviders() {
  providers.value = await systemApi.listStorageProviders()
}

async function fetchBuckets() {
  loading.value = true
  try {
    list.value = await systemApi.listStorageBuckets(providerFilter.value)
  } finally {
    loading.value = false
  }
}

function openCreate() {
  resetForm()
  dialogVisible.value = true
}

function openEdit(row: StorageBucket) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.providerId || !form.bucketName) {
    ElMessage.warning('请完整填写信息')
    return
  }
  if (form.id) {
    await systemApi.updateStorageBucket(form.id, form)
  } else {
    await systemApi.createStorageBucket(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  fetchBuckets()
}

async function handleToggle(row: StorageBucket) {
  await systemApi.updateStorageBucketEnabled(row.id!, row.isEnabled === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  fetchBuckets()
}

async function handleSetDefault(row: StorageBucket) {
  await systemApi.setDefaultStorageBucket(row.id!)
  ElMessage.success('已设为默认')
  fetchBuckets()
}

async function handleDelete(row: StorageBucket) {
  await ElMessageBox.confirm(`确认删除存储桶「${row.bucketName}」吗？`, '提示', { type: 'warning' })
  await systemApi.deleteStorageBucket(row.id!)
  ElMessage.success('删除成功')
  fetchBuckets()
}

onMounted(async () => {
  await fetchProviders()
  await fetchBuckets()
})
</script>

