<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <div class="auth-logo">
          <el-icon :size="40" color="#9b59b6"><star /></el-icon>
          <h2 class="brand-name">{{ t('app.name') }}</h2>
        </div>

        <h3 class="auth-title">邮箱验证</h3>
        <p class="auth-hint">验证码已发送至</p>
        <p class="auth-email">{{ email }}</p>

        <el-form @submit.prevent="handleVerify" style="margin-top: 24px">
          <el-form-item>
            <el-input
              v-model="code"
              placeholder="请输入6位验证码"
              size="large"
              maxlength="6"
              clearable
            />
          </el-form-item>
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="verifying"
              native-type="submit"
            >
              验证邮箱
            </el-button>
          </el-form-item>
        </el-form>

        <div class="resend-row">
          <span class="hint">没有收到验证码？</span>
          <el-button
            link
            type="primary"
            :disabled="countdown > 0"
            :loading="sending"
            @click="handleResend"
          >
            {{ countdown > 0 ? `重新发送 (${countdown}s)` : '重新发送' }}
          </el-button>
        </div>

        <div class="auth-footer">
          <el-button link type="primary" @click="router.push('/login')">返回登录</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const { t } = useI18n()
const authStore = useAuthStore()

const email = ref((route.query.email as string) || '')
const code = ref('')
const verifying = ref(false)
const sending = ref(false)
const countdown = ref(60)

let timer: ReturnType<typeof setInterval> | null = null

function startCountdown() {
  countdown.value = 60
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer!)
      timer = null
    }
  }, 1000)
}

onMounted(() => {
  if (!email.value) {
    router.push('/register')
    return
  }
  startCountdown()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

async function handleVerify() {
  if (!code.value || code.value.length !== 6) {
    ElMessage.warning('请输入6位验证码')
    return
  }
  verifying.value = true
  try {
    const res = await authApi.verifyEmail(email.value, code.value)
    authStore.setToken(res.token)
    authStore.setUserInfo(res.userInfo)
    ElMessage.success('邮箱验证成功，欢迎加入！')
    router.push('/')
  } catch {
    // error handled by axios interceptor
  } finally {
    verifying.value = false
  }
}

async function handleResend() {
  sending.value = true
  try {
    await authApi.sendVerificationCode(email.value)
    ElMessage.success('验证码已重新发送')
    startCountdown()
  } catch {
    // error handled by axios interceptor
  } finally {
    sending.value = false
  }
}
</script>

<style scoped lang="scss">
@import "@/assets/styles/variables.scss";

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
    margin-bottom: $spacing-2;
    text-align: center;
  }

  .auth-hint {
    font-size: $font-size-sm;
    color: $text-secondary;
    text-align: center;
    margin: 0;
  }

  .auth-email {
    font-size: $font-size-base;
    font-weight: $font-weight-medium;
    color: $primary-color;
    text-align: center;
    margin: $spacing-1 0 0;
  }

  .resend-row {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: $spacing-2;
    margin-top: $spacing-2;

    .hint {
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }

  .auth-footer {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-top: $spacing-4;
  }
}
</style>
