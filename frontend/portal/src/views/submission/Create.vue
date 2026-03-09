<template>
  <div class="submission-create-page">
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">{{ t('submission.create') }}</h1>
      </div>
    </section>

    <div class="container page-body">
      <el-card class="form-card">
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          @submit.prevent="handleSubmit"
        >
          <el-form-item :label="t('submission.form.title')" prop="title">
            <el-input v-model="form.title" :placeholder="t('submission.form.title')" maxlength="200" show-word-limit />
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

          <el-form-item :label="t('submission.form.abstract')" prop="abstract">
            <el-input
              v-model="form.abstract"
              type="textarea"
              :rows="8"
              placeholder="请输入论文摘要（建议300-500字）"
              maxlength="2000"
              show-word-limit
            />
          </el-form-item>

          <el-form-item :label="t('submission.form.file')">
            <el-upload
              :auto-upload="false"
              :limit="1"
              accept=".pdf,.doc,.docx"
              :on-change="handleFileChange"
              :on-remove="() => form.fileUrl = ''"
            >
              <el-button><el-icon><upload-filled /></el-icon>选择文件</el-button>
              <template #tip>
                <div class="el-upload__tip">支持 PDF / Word 格式，大小不超过 10MB</div>
              </template>
            </el-upload>
          </el-form-item>

          <el-form-item>
            <div class="form-actions">
              <el-button @click="router.back()">{{ t('common.cancel') }}</el-button>
              <el-button type="primary" :loading="submitting" native-type="submit">
                {{ t('submission.form.submit') }}
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, type FormInstance, type FormRules, type UploadFile } from 'element-plus'
import { submissionApi } from '@/api/submission'

const router = useRouter()
const { t } = useI18n()

const formRef = ref<FormInstance>()
const submitting = ref(false)
const form = ref({ title: '', category: '', abstract: '', fileUrl: '' })

const rules: FormRules = {
  title: [{ required: true, message: '请输入论文标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  abstract: [{ required: true, message: '请输入摘要', trigger: 'blur' }]
}

const handleFileChange = async (file: UploadFile) => {
  if (!file.raw) return
  if (file.raw.size > 10 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 10MB')
    return
  }
  // In production, upload file and set fileUrl
  // const res = await submissionApi.uploadFile(file.raw)
  // form.value.fileUrl = res.url
  form.value.fileUrl = file.name
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await submissionApi.create({
      title: form.value.title,
      category: form.value.category,
      abstract: form.value.abstract,
      fileUrl: form.value.fileUrl || undefined
    })
    ElMessage.success('投稿提交成功！')
    router.push({ name: 'Submission' })
  } catch {
    // Error message already displayed by the request interceptor
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
</style>
