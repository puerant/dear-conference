<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-logo">
          <el-icon :size="40" color="#9b59b6"><star /></el-icon>
          <h2 class="brand-name">{{ t('app.name') }}</h2>
        </div>

        <h3 class="auth-title">{{ t('auth.register') }}</h3>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          @submit.prevent="handleRegister"
        >
          <el-form-item :label="t('auth.name')" prop="name">
            <el-input v-model="form.name" :placeholder="t('auth.name')" size="large" />
          </el-form-item>
          <el-form-item :label="t('auth.email')" prop="email">
            <el-input v-model="form.email" type="email" :placeholder="t('auth.email')" size="large" />
          </el-form-item>
          <el-form-item :label="t('auth.password')" prop="password">
            <el-input v-model="form.password" type="password" :placeholder="t('auth.password')" size="large" show-password />
          </el-form-item>
          <el-form-item :label="t('auth.role')" prop="role">
            <el-select v-model="form.role" style="width: 100%" size="large">
              <el-option value="speaker" label="投稿讲者 - 提交学术论文" />
              <el-option value="attendee" label="参会者 - 购票参加会议" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="loading"
              native-type="submit"
            >
              {{ t('auth.registerBtn') }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="auth-footer">
          <span class="hint">{{ t('auth.toLogin') }}</span>
          <el-button link type="primary" @click="router.push('/login')">{{ t('menu.login') }}</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { type FormInstance, type FormRules } from 'element-plus'
import { authApi } from '@/api/auth'

const router = useRouter()
const { t } = useI18n()

const formRef = ref<FormInstance>()
const loading = ref(false)
const form = ref({ name: '', email: '', password: '', role: 'attendee' })

const rules: FormRules = {
  name: [{ required: true, message: t('common.required'), trigger: 'blur' }],
  email: [
    { required: true, message: t('common.required'), trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [
    { required: true, message: t('common.required'), trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const handleRegister = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authApi.register(form.value)
    router.push({ name: 'VerifyEmail', query: { email: form.value.email } })
  } catch {
    // error handled by axios interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style scoped lang="scss">
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, $primary-lighter 0%, $bg-secondary 50%, $primary-lighter 100%);
  padding: $spacing-6;
}

.auth-container {
  width: 100%;
  max-width: 440px;
}

.auth-card {
  background: white;
  border-radius: $radius-2xl;
  padding: $spacing-10;
  box-shadow: $shadow-xl;

  .auth-logo {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: $spacing-6;
    gap: $spacing-2;

    .brand-name {
      font-size: $font-size-xl;
      font-weight: $font-weight-bold;
      color: $primary-color;
    }
  }

  .auth-title {
    font-size: $font-size-2xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
    margin-bottom: $spacing-6;
    text-align: center;
  }

  .auth-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-2;
    margin-top: $spacing-4;

    .hint {
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }
}
</style>
