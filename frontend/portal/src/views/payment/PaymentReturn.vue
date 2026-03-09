<template>
  <div class="payment-return-page">
    <div class="return-card">
      <!-- Checking -->
      <template v-if="state === 'checking'">
        <el-icon class="state-icon loading-icon"><Loading /></el-icon>
        <p class="state-title">正在确认支付结果...</p>
        <p class="state-sub">请稍候，请勿关闭此页面</p>
      </template>

      <!-- Success -->
      <template v-else-if="state === 'success'">
        <el-icon class="state-icon success-icon"><SuccessFilled /></el-icon>
        <p class="state-title">支付成功！</p>
        <p class="state-sub">您的入场凭证已生成，可前往「个人中心」查看</p>
        <div class="actions">
          <el-button type="primary" @click="goProfile">查看凭证</el-button>
          <el-button @click="goOrders">我的订单</el-button>
        </div>
      </template>

      <!-- Failed / expired -->
      <template v-else-if="state === 'failed'">
        <el-icon class="state-icon failed-icon"><CircleCloseFilled /></el-icon>
        <p class="state-title">支付未完成</p>
        <p class="state-sub">支付可能未成功或已超时，请返回订单页重试</p>
        <div class="actions">
          <el-button type="primary" @click="goOrders">返回订单</el-button>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Loading, SuccessFilled, CircleCloseFilled } from '@element-plus/icons-vue'
import { paymentApi } from '@/api/payment'

const route = useRoute()
const router = useRouter()

type State = 'checking' | 'success' | 'failed'
const state = ref<State>('checking')

const POLL_INTERVAL_MS = 3000
const MAX_POLLS = 20   // 1 minute max
let pollCount = 0
let timer: ReturnType<typeof setInterval> | null = null

const stop = () => {
  if (timer !== null) {
    clearInterval(timer)
    timer = null
  }
}

const poll = async (paymentNo: string) => {
  try {
    const res = await paymentApi.getStatus(paymentNo)
    if (res.status === 'success') {
      stop()
      state.value = 'success'
    } else if (res.status === 'failed' || res.status === 'closed') {
      stop()
      state.value = 'failed'
    } else {
      pollCount++
      if (pollCount >= MAX_POLLS) {
        stop()
        state.value = 'failed'
      }
    }
  } catch {
    // Ignore transient errors
  }
}

onMounted(() => {
  const paymentNo = route.query.paymentNo as string | undefined
  if (!paymentNo) {
    state.value = 'failed'
    return
  }
  poll(paymentNo)
  timer = setInterval(() => poll(paymentNo), POLL_INTERVAL_MS)
})

onUnmounted(stop)

const goProfile = () => router.push({ name: 'Profile' })
const goOrders = () => router.push({ name: 'OrderList' })
</script>

<style scoped lang="scss">
.payment-return-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: var(--el-bg-color-page);
}

.return-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-4;
  background: var(--el-bg-color);
  border-radius: 16px;
  padding: $spacing-12 $spacing-10;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  min-width: 360px;
  text-align: center;
}

.state-icon {
  font-size: 64px;
}

.loading-icon {
  color: var(--el-color-primary);
  animation: spin 1s linear infinite;
}

.success-icon { color: var(--el-color-success); }
.failed-icon  { color: var(--el-color-danger); }

.state-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.state-sub {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  margin: 0;
}

.actions {
  display: flex;
  gap: $spacing-3;
  margin-top: $spacing-4;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to   { transform: rotate(360deg); }
}
</style>
