<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-logo">
          <el-icon :size="40" color="#9b59b6"><star /></el-icon>
          <h2 class="brand-name">{{ t('app.name') }}</h2>
        </div>

        <h3 class="auth-title">{{ t('auth.login') }}</h3>

        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-position="top"
          @submit.prevent="handleLogin"
        >
          <el-form-item :label="t('auth.email')" prop="email">
            <el-input v-model="form.email" type="email" :placeholder="t('auth.email')" size="large" />
          </el-form-item>
          <el-form-item :label="t('auth.password')" prop="password">
            <el-input v-model="form.password" type="password" :placeholder="t('auth.password')" size="large" show-password />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="loading"
              native-type="submit"
            >
              {{ t('auth.loginBtn') }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="auth-footer">
          <span class="hint">{{ t('auth.toRegister') }}</span>
          <el-button link type="primary" @click="router.push('/register')">{{ t('menu.register') }}</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()
const authStore = useAuthStore()

const formRef = ref<FormInstance>()
const loading = ref(false)
const form = ref({ email: '', password: '' })

const rules: FormRules = {
  email: [
    { required: true, message: t('common.required'), trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  password: [{ required: true, message: t('common.required'), trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await authApi.login(form.value)
    authStore.setToken(res.token)
    authStore.setUserInfo(res.userInfo)
    ElMessage.success('登录成功！')
    const redirect = route.query.redirect as string
    router.push(redirect || '/')
  } catch {
    // Error message already displayed by the request interceptor
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
