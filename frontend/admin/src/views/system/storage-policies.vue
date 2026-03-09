<template>
  <div>
    <el-card>
      <template #header>
        <div style="display:flex;justify-content:space-between;align-items:center;">
          <span>存储策略管理</span>
          <el-button type="primary" @click="openCreate">新增策略</el-button>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="policyName" label="策略名称" min-width="180" />
        <el-table-column prop="fileCategory" label="分类" width="150" />
        <el-table-column prop="fileType" label="类型" width="120" />
        <el-table-column prop="providerId" label="供应商ID" width="100" />
        <el-table-column prop="bucketId" label="BucketID" width="100" />
        <el-table-column prop="priority" label="优先级" width="90" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.isEnabled === 1 ? 'success' : 'info'">{{ row.isEnabled === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="warning" @click="handleToggle(row)">{{ row.isEnabled === 1 ? '禁用' : '启用' }}</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑策略' : '新增策略'" width="620px">
      <el-form :model="form" label-width="120px">
        <el-form-item label="策略名称">
          <el-input v-model="form.policyName" />
        </el-form-item>
        <el-form-item label="文件分类">
          <el-input v-model="form.fileCategory" placeholder="可空，表示匹配全部分类" />
        </el-form-item>
        <el-form-item label="文件类型">
          <el-select v-model="form.fileType" clearable style="width:100%">
            <el-option label="image" value="image" />
            <el-option label="document" value="document" />
            <el-option label="presentation" value="presentation" />
          </el-select>
        </el-form-item>
        <el-form-item label="供应商">
          <el-select v-model="form.providerId" style="width:100%" @change="onProviderChange">
            <el-option v-for="item in providers" :key="item.id" :label="item.providerName" :value="item.id!" />
          </el-select>
        </el-form-item>
        <el-form-item label="存储桶">
          <el-select v-model="form.bucketId" style="width:100%">
            <el-option v-for="item in buckets" :key="item.id" :label="item.bucketName" :value="item.id!" />
          </el-select>
        </el-form-item>
        <el-form-item label="路径规则">
          <el-input v-model="form.pathRule" />
        </el-form-item>
        <el-form-item label="最大MB">
          <el-input-number v-model="form.maxSizeMb" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="允许后缀">
          <el-input v-model="form.allowedExt" placeholder="jpg,png,pdf" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-input-number v-model="form.priority" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="enabled" />
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
import { systemApi, type FileStoragePolicy, type StorageBucket, type StorageProvider } from '@/api/system'

const loading = ref(false)
const dialogVisible = ref(false)
const list = ref<FileStoragePolicy[]>([])
const providers = ref<StorageProvider[]>([])
const buckets = ref<StorageBucket[]>([])

const form = reactive<FileStoragePolicy>({
  policyName: '',
  fileCategory: '',
  fileType: '',
  providerId: 0,
  bucketId: 0,
  pathRule: 'yyyy/MM/dd/{category}',
  maxSizeMb: 10,
  allowedExt: '',
  priority: 100,
  isEnabled: 1
})

const enabled = computed({
  get: () => form.isEnabled === 1,
  set: (val: boolean) => { form.isEnabled = val ? 1 : 0 }
})

function resetForm() {
  form.id = undefined
  form.policyName = ''
  form.fileCategory = ''
  form.fileType = ''
  form.providerId = providers.value[0]?.id || 0
  form.bucketId = 0
  form.pathRule = 'yyyy/MM/dd/{category}'
  form.maxSizeMb = 10
  form.allowedExt = ''
  form.priority = 100
  form.isEnabled = 1
}

async function fetchList() {
  loading.value = true
  try {
    list.value = await systemApi.listFileStoragePolicies()
  } finally {
    loading.value = false
  }
}

async function fetchProviders() {
  providers.value = await systemApi.listStorageProviders()
}

async function onProviderChange() {
  if (!form.providerId) return
  buckets.value = await systemApi.listStorageBuckets(form.providerId)
  if (!buckets.value.find(v => v.id === form.bucketId)) {
    form.bucketId = buckets.value[0]?.id || 0
  }
}

function openCreate() {
  resetForm()
  onProviderChange()
  dialogVisible.value = true
}

async function openEdit(row: FileStoragePolicy) {
  Object.assign(form, row)
  await onProviderChange()
  dialogVisible.value = true
}

async function handleSubmit() {
  if (!form.policyName || !form.providerId || !form.bucketId) {
    ElMessage.warning('请完整填写必填项')
    return
  }
  if (form.id) {
    await systemApi.updateFileStoragePolicy(form.id, form)
  } else {
    await systemApi.createFileStoragePolicy(form)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  fetchList()
}

async function handleToggle(row: FileStoragePolicy) {
  await systemApi.updateFileStoragePolicyEnabled(row.id!, row.isEnabled === 1 ? 0 : 1)
  ElMessage.success('操作成功')
  fetchList()
}

async function handleDelete(row: FileStoragePolicy) {
  await ElMessageBox.confirm(`确认删除策略「${row.policyName}」吗？`, '提示', { type: 'warning' })
  await systemApi.deleteFileStoragePolicy(row.id!)
  ElMessage.success('删除成功')
  fetchList()
}

onMounted(async () => {
  await fetchProviders()
  await fetchList()
})
</script>

