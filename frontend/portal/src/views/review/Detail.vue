<template>
  <div class="review-detail-page">
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">{{ t('review.detail') }}</h1>
      </div>
    </section>

    <div class="container page-body">
      <el-button link @click="router.back()" style="margin-bottom: 16px">
        <el-icon><arrow-left /></el-icon> 返回列表
      </el-button>

      <div v-loading="loading" class="review-layout">
        <!-- Paper info (from GET /submissions/{submissionId}) -->
        <el-card v-if="submission" class="paper-card">
          <template #header><h3>论文信息</h3></template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="标题">{{ submission.title }}</el-descriptions-item>
            <el-descriptions-item label="分类">{{ submission.category }}</el-descriptions-item>
            <el-descriptions-item label="摘要">
              <div style="white-space: pre-wrap; line-height: 1.7; max-height: 200px; overflow-y: auto">
                {{ submission.abstract }}
              </div>
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- Review form / result (from GET /reviews/submission/{submissionId}) -->
        <el-card class="review-card">
          <template #header>
            <h3>{{ existingReview?.result ? '审稿结果' : t('review.detail') }}</h3>
          </template>

          <!-- Already reviewed -->
          <template v-if="existingReview?.result">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="审稿结论">
                <el-tag :type="getReviewResultType(existingReview.result) as 'success' | 'warning' | 'danger' | 'info'">
                  {{ getReviewResultLabel(existingReview.result) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="评分">{{ existingReview.score ?? '-' }} 分</el-descriptions-item>
              <el-descriptions-item label="审稿意见" :span="2">
                {{ existingReview.comment || '（无意见）' }}
              </el-descriptions-item>
            </el-descriptions>
          </template>

          <!-- Review form -->
          <template v-else>
            <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent="handleSubmit">
              <el-form-item :label="t('review.form.result')" prop="result">
                <el-radio-group v-model="form.result">
                  <el-radio :value="1">{{ t('review.result.accept') }}</el-radio>
                  <el-radio :value="2">{{ t('review.result.reject') }}</el-radio>
                  <el-radio :value="3">{{ t('review.result.revise') }}</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item :label="t('review.form.score')" prop="score">
                <el-input-number v-model="form.score" :min="0" :max="100" :step="5" style="width: 200px" />
                <span class="score-hint">（0 - 100分）</span>
              </el-form-item>
              <el-form-item :label="t('review.form.comment')" prop="comment">
                <el-input
                  v-model="form.comment"
                  type="textarea"
                  :rows="6"
                  placeholder="请填写详细的审稿意见..."
                  maxlength="2000"
                  show-word-limit
                />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="submitting" native-type="submit">
                  {{ t('review.form.submit') }}
                </el-button>
                <el-button @click="router.back()">{{ t('common.cancel') }}</el-button>
              </el-form-item>
            </el-form>
          </template>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { reviewApi } from '@/api/review'
import { submissionApi } from '@/api/submission'
import type { Review, Submission } from '@/api/types'
import { getReviewResultLabel, getReviewResultType } from '@/utils/format'

const route = useRoute()
const router = useRouter()
const { t } = useI18n()

const loading = ref(false)
const submitting = ref(false)
// Route param id is submissionId (navigated from Index with row.submissionId)
const submissionId = Number(route.params.id)
const submission = ref<Submission | null>(null)
const existingReview = ref<Review | null>(null)
const formRef = ref<FormInstance>()
const form = ref({ result: 1, score: 60, comment: '' })

const rules: FormRules = {
  result: [{ required: true, message: '请选择审稿结论', trigger: 'change' }],
  comment: [{ required: true, message: '请填写审稿意见', trigger: 'blur' }]
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    // POST /reviews with { submissionId, comment, result, score }
    await reviewApi.create({
      submissionId,
      result: form.value.result,
      score: form.value.score,
      comment: form.value.comment
    })
    ElMessage.success('审稿意见提交成功')
    router.push({ name: 'Review' })
  } catch {
    // Error message already displayed by the request interceptor
  } finally {
    submitting.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    // Fetch submission info and existing reviews in parallel
    const [sub, reviews] = await Promise.all([
      submissionApi.getDetail(submissionId),
      reviewApi.getBySubmission(submissionId)
    ])
    submission.value = sub
    // Take the first review returned (reviewer's own review for this submission)
    existingReview.value = reviews[0] ?? null
  } catch {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.page-body {
  padding: $spacing-8 $spacing-6;
}

.review-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: $spacing-6;
  align-items: start;

  @include respond-to(md) {
    grid-template-columns: 1fr;
  }
}

.score-hint {
  margin-left: $spacing-3;
  color: $text-secondary;
  font-size: $font-size-sm;
}
</style>
