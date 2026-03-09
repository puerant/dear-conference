<template>
  <div class="submission-edit-page">
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">编辑投稿</h1>
      </div>
    </section>

    <div class="container page-body">
      <el-card v-loading="pageLoading" class="form-card">
        <el-form
          v-if="!pageLoading"
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          @submit.prevent="handleSubmit"
        >
          <el-form-item label="论文标题" prop="title">
            <el-input v-model="form.title" placeholder="请输入论文标题" maxlength="200" show-word-limit />
          </el-form-item>

          <el-form-item label="分类" prop="category">
            <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
              <el-option label="人工智能" value="AI" />
              <el-option label="自然语言处理" value="NLP" />
              <el-option label="计算机视觉" value="CV" />
              <el-option label="数据科学" value="DS" />
              <el-option label="网络安全" value="Security" />
              <el-option label="量子计算" value="Quantum" />
              <el-option label="其他" value="Other" />
            </el-select>
          </el-form-item>

          <el-form-item label="摘要" prop="abstract">
            <el-input
              v-model="form.abstract"
              type="textarea"
              :rows="8"
              placeholder="请输入论文摘要（建议300-500字）"
              maxlength="2000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item label="论文附件">
            <el-upload
              :auto-upload="false"
              :limit="1"
              accept=".pdf,.doc,.docx"
              :on-change="handleFileChange"
              :on-remove="() => form.fileUrl = originalFileUrl"
            >
              <el-button><el-icon><upload-filled /></el-icon>重新上传</el-button>
              <template #tip>
                <div class="el-upload__tip">
                  支持 PDF / Word 格式，大小不超过 10MB
                  <span v-if="form.fileUrl" class="current-file">（当前：{{ form.fileUrl }}）</span>
                </div>
              </template>
            </el-upload>
          </el-form-item>

          <el-form-item>
            <div class="form-actions">
              <el-button @click="router.back()">取消</el-button>
              <el-button type="primary" :loading="submitting" native-type="submit">保存修改</el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules, type UploadFile } from 'element-plus'
import { submissionApi } from '@/api/submission'

const route = useRoute()
const router = useRouter()

const formRef = ref<FormInstance>()
const pageLoading = ref(false)
const submitting = ref(false)
const originalFileUrl = ref('')
const form = ref({ title: '', category: '', abstract: '', fileUrl: '' })

const rules: FormRules = {
  title: [{ required: true, message: '请输入论文标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  abstract: [{ required: true, message: '请输入摘要', trigger: 'blur' }]
}

onMounted(async () => {
  const id = Number(route.params.id)
  if (!id) return
  pageLoading.value = true
  try {
    const submission = await submissionApi.getDetail(id)
    form.value.title = submission.title
    form.value.category = submission.category
    form.value.abstract = submission.abstract
    form.value.fileUrl = submission.fileUrl || ''
    originalFileUrl.value = submission.fileUrl || ''
  } catch {
    // Error handled by interceptor
  } finally {
    pageLoading.value = false
  }
})

const handleFileChange = (file: UploadFile) => {
  if (!file.raw) return
  if (file.raw.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 10MB')
    return
  }
  form.value.fileUrl = file.name
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  const id = Number(route.params.id)
  submitting.value = true
  try {
    await submissionApi.update(id, {
      title: form.value.title,
      category: form.value.category,
      abstract: form.value.abstract,
      fileUrl: form.value.fileUrl || undefined
    })
    ElMessage.success('投稿修改成功！')
    router.push({ name: 'SubmissionDetail', params: { id } })
  } catch {
    // Error handled by interceptor
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped lang="scss">
.page-body {
  padding: $spacing-10 $spacing-6;
}

.form-card {
  max-width: 720px;
  margin: 0 auto;
}

.form-actions {
  display: flex;
  gap: $spacing-3;
  justify-content: flex-end;
  width: 100%;
}

.current-file {
  color: $text-secondary;
  margin-left: $spacing-2;
}
</style>
