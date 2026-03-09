<template>
  <div class="file-page">
    <el-card class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="文件类型">
          <el-select v-model="query.fileType" placeholder="全部" clearable style="width: 120px">
            <el-option label="图片" value="image" />
            <el-option label="文档" value="document" />
            <el-option label="演示" value="presentation" />
          </el-select>
        </el-form-item>
        <el-form-item label="文件分类">
          <el-select v-model="query.fileCategory" placeholder="全部" clearable style="width: 160px">
            <el-option label="会议横幅" value="conference_banner" />
            <el-option label="专家头像" value="expert_avatar" />
            <el-option label="酒店图片" value="hotel_image" />
            <el-option label="演示文稿" value="presentation" />
            <el-option label="文档" value="document" />
            <el-option label="票务图片" value="ticket_image" />
            <el-option label="凭证背景" value="credential_background" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="文件名搜索" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList(1)">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Upload" @click="openUpload">上传文件</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="list" v-loading="loading" border @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="fileName" label="文件名" min-width="200" show-overflow-tooltip />
        <el-table-column label="文件类型" width="90" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.fileType === 'image'" type="success" size="small">图片</el-tag>
            <el-tag v-else-if="row.fileType === 'document'" type="primary" size="small">文档</el-tag>
            <el-tag v-else-if="row.fileType === 'presentation'" type="warning" size="small">演示</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="文件大小" width="120">
          <template #default="{ row }">{{ row.fileSizeDisplay }}</template>
        </el-table-column>
        <el-table-column prop="fileCategory" label="分类" width="140" />
        <el-table-column prop="providerId" label="供应商ID" width="100" />
        <el-table-column prop="bucketId" label="BucketID" width="100" />
        <el-table-column label="可见性" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="row.visibility === 2 ? 'success' : 'info'">{{ row.visibility === 2 ? 'public' : 'private' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="uploadUserName" label="上传者" width="100" />
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handlePreview(row)">预览</el-button>
            <el-button type="success" link size="small" @click="handleDownload(row)">下载</el-button>
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

    <el-dialog v-model="uploadVisible" title="上传文件" width="500px">
      <el-upload
        ref="uploadRef"
        :auto-upload="false"
        :on-change="handleFileChange"
        :on-remove="handleFileRemove"
        :file-list="fileList"
        :limit="1"
        drag
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">
          拖拽文件到此处或 <em>点击上传</em>
        </div>
        <template #tip>
          <div class="el-upload__tip">
            图片最大5MB，PDF/Word最大20MB，PPT最大30MB<br>
            支持：jpg, png, gif, webp, pdf, doc, docx, ppt, pptx
          </div>
        </template>
      </el-upload>
      <el-form label-width="100px" style="margin-top: 16px;">
        <el-form-item label="文件分类">
          <el-select v-model="uploadForm.category" placeholder="选择分类">
            <el-option label="会议横幅" value="conference_banner" />
            <el-option label="专家头像" value="expert_avatar" />
            <el-option label="酒店图片" value="hotel_image" />
            <el-option label="演示文稿" value="presentation" />
            <el-option label="文档" value="document" />
            <el-option label="票务图片" value="ticket_image" />
            <el-option label="凭证背景" value="credential_background" />
          </el-select>
        </el-form-item>
        <el-form-item label="供应商">
          <el-select v-model="uploadForm.providerId" placeholder="自动路由（留空）" clearable style="width:100%" @change="handleProviderChange">
            <el-option v-for="item in providers" :key="item.id" :label="item.providerName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="存储桶">
          <el-select v-model="uploadForm.bucketId" placeholder="自动路由（留空）" clearable style="width:100%">
            <el-option v-for="item in buckets" :key="item.id" :label="item.bucketName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="可见性">
          <el-radio-group v-model="uploadForm.visibility">
            <el-radio :value="1">private</el-radio>
            <el-radio :value="2">public</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="uploadVisible = false">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="handleUpload">上传</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, UploadProps, UploadUserFile } from 'element-plus'
import { Upload, UploadFilled } from '@element-plus/icons-vue'
import { systemApi, type StorageBucket, type StorageProvider, type SystemFile, type SystemFileQuery } from '@/api/system'
import { formatDate } from '@/utils/format'

const list = ref<SystemFile[]>([])
const total = ref(0)
const loading = ref(false)
const uploadVisible = ref(false)
const uploading = ref(false)
const fileList = ref<UploadUserFile[]>([])
const selectedRows = ref<SystemFile[]>([])
const providers = ref<StorageProvider[]>([])
const buckets = ref<StorageBucket[]>([])

const query = reactive<SystemFileQuery>({
  page: 1,
  pageSize: 20
})

const uploadForm = reactive({
  category: '',
  providerId: undefined as number | undefined,
  bucketId: undefined as number | undefined,
  visibility: 1
})

async function fetchList(page?: number) {
  if (page) query.page = page
  loading.value = true
  try {
    const res = await systemApi.getFileList(query)
    list.value = res.list
    total.value = res.total
  } catch {
  } finally {
    loading.value = false
  }
}

function handleReset() {
  query.fileType = undefined
  query.fileCategory = undefined
  query.keyword = undefined
  fetchList(1)
}

function openUpload() {
  uploadVisible.value = true
  uploadForm.category = ''
  uploadForm.providerId = undefined
  uploadForm.bucketId = undefined
  uploadForm.visibility = 1
  fileList.value = []
}

async function handleProviderChange(providerId?: number) {
  uploadForm.bucketId = undefined
  if (!providerId) {
    buckets.value = []
    return
  }
  buckets.value = await systemApi.listStorageBuckets(providerId)
}

const handleFileChange: UploadProps['onChange'] = (uploadFile) => {
  fileList.value = [uploadFile]
}

const handleFileRemove: UploadProps['onRemove'] = () => {
  fileList.value = []
}

async function handleUpload() {
  if (fileList.value.length === 0) {
    ElMessage.warning('请选择文件')
    return
  }
  uploading.value = true
  try {
    await systemApi.uploadFile(fileList.value[0].raw!, {
      category: uploadForm.category || undefined,
      providerId: uploadForm.providerId,
      bucketId: uploadForm.bucketId,
      visibility: uploadForm.visibility
    })
    ElMessage.success('上传成功')
    uploadVisible.value = false
    fetchList()
  } catch {
  } finally {
    uploading.value = false
  }
}

function handlePreview(row: SystemFile) {
  systemApi.getFilePreviewUrl(row.id).then((url) => window.open(url, '_blank'))
}

function handleDownload(row: SystemFile) {
  window.open(row.fileUrl, '_blank')
}

async function handleDelete(row: SystemFile) {
  await ElMessageBox.confirm(`确定要删除文件 "${row.fileName}" 吗？`, '提示', { type: 'warning' })
  try {
    await systemApi.deleteFile(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
  }
}

function handleSelectionChange(val: SystemFile[]) {
  selectedRows.value = val
}

onMounted(() => fetchList())
onMounted(async () => {
  providers.value = await systemApi.listStorageProviders()
})
</script>

<style lang="scss" scoped>
.file-page {
  .filter-card {
    margin-bottom: 16px;
  }

  .table-card {
    .pagination {
      margin-top: 16px;
      justify-content: flex-end;
    }
  }
}
</style>
