<template>
  <el-dialog
    v-model="visible"
    title="选择支付方式"
    width="480px"
    :close-on-click-modal="false"
    @closed="onClosed"
  >
    <!-- Step 1: method selection -->
    <div v-if="step === 'select'" class="method-select">
      <p class="hint">请选择支付方式完成订单</p>
      <div class="method-list">
        <div class="method-item" @click="selectMethod('wechat')">
          <el-icon class="method-icon wechat-icon"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M9.5 4C5.36 4 2 6.69 2 10c0 1.89 1.08 3.56 2.78 4.66L4 17l2.5-1.5c.97.27 2 .5 3 .5.17 0 .34 0 .5-.01C9.84 16.66 10 17.32 10 18c0 2.21 2.24 4 5 4 .88 0 1.71-.2 2.44-.55L20 23l-.78-2.34C20.62 19.74 22 18.45 22 17c0-2.21-2.24-4-5-4-.19 0-.38.01-.57.03C16.12 11.57 13.06 9 9.5 9V4z" opacity=".3"/><path d="M9.5 4C5.36 4 2 6.69 2 10c0 1.89 1.08 3.56 2.78 4.66L4 17l2.5-1.5c.97.27 2 .5 3 .5.17 0 .34 0 .5-.01C9.84 16.66 10 17.32 10 18c0 2.21 2.24 4 5 4 .88 0 1.71-.2 2.44-.55L20 23l-.78-2.34C20.62 19.74 22 18.45 22 17c0-2.21-2.24-4-5-4-.19 0-.38.01-.57.03C16.12 11.57 13.06 9 9.5 9V4zm-3 7c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1zm3 0c-.55 0-1-.45-1-1s.45-1 1-1 1 .45 1 1-.45 1-1 1z"/></svg></el-icon>
          <span class="method-name">微信支付</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
        <div class="method-item" @click="selectMethod('alipay')">
          <el-icon class="method-icon alipay-icon"><svg viewBox="0 0 24 24" fill="currentColor"><path d="M21.422 15.358C20.2 14.791 17.99 13.68 16.31 12.918c.9-1.327 1.552-2.9 1.866-4.718H14V6.8h4.8V5.6H14V3.2h-2.4v2.4H7.2v1.2h4.4v1.4H7.2v1.2h7.752c-.277 1.323-.77 2.499-1.437 3.479-1.97-.853-3.96-1.462-5.115-1.462-2.64 0-4.8 1.521-4.8 4.041 0 2.401 2.16 3.841 4.8 3.841 2.34 0 4.594-1.173 6.233-3.213 1.726.83 4.413 2.132 5.573 2.702l1.016-2.43z"/></svg></el-icon>
          <span class="method-name">支付宝</span>
          <el-icon class="arrow"><ArrowRight /></el-icon>
        </div>
      </div>
      <!-- Dev mode bypass -->
      <div v-if="isDev" class="dev-mock-pay">
        <el-divider>开发模式</el-divider>
        <el-button type="warning" plain size="small" :loading="mockPaying" @click="handleMockPay">
          跳过支付（模拟成功）
        </el-button>
      </div>
    </div>

    <!-- Step 2: WeChat QR code -->
    <div v-else-if="step === 'wechat-qr'" class="wechat-qr">
      <p class="hint">请使用微信扫码完成支付</p>
      <div class="qr-wrapper">
        <canvas ref="qrCanvas" class="qr-canvas" />
        <div v-if="polling" class="polling-badge">
          <el-icon class="loading-icon"><Loading /></el-icon>
          <span>等待支付...</span>
        </div>
      </div>
      <p class="amount-hint">应付金额：<strong>¥{{ paymentData?.amount?.toFixed(2) }}</strong></p>
    </div>

    <!-- Step 3: Alipay redirect -->
    <div v-else-if="step === 'alipay-redirect'" class="alipay-redirect">
      <el-icon class="redirect-icon"><SuccessFilled /></el-icon>
      <p class="redirect-title">已在新标签页打开支付宝收银台</p>
      <p class="redirect-sub">完成支付后页面将自动更新，请勿关闭此窗口</p>
      <div v-if="polling" class="polling-indicator">
        <el-icon class="loading-icon"><Loading /></el-icon>
        <span>等待支付确认...</span>
      </div>
    </div>

    <!-- Step 4: Success -->
    <div v-else-if="step === 'success'" class="result-panel success">
      <el-icon class="result-icon"><SuccessFilled /></el-icon>
      <p class="result-title">支付成功！</p>
      <p class="result-sub">凭证已生成，请前往「个人中心」查看入场凭证</p>
    </div>

    <!-- Step 5: Expired / failed -->
    <div v-else-if="step === 'expired'" class="result-panel expired">
      <el-icon class="result-icon expired-icon"><CircleCloseFilled /></el-icon>
      <p class="result-title">支付已失效</p>
      <p class="result-sub">二维码或支付链接已过期，请重新发起支付</p>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button v-if="step === 'select'" @click="visible = false">取消</el-button>
        <el-button v-if="step === 'wechat-qr' || step === 'alipay-redirect'" @click="goBack">返回</el-button>
        <el-button v-if="step === 'success'" type="primary" @click="onDone">完成</el-button>
        <el-button v-if="step === 'expired'" type="primary" @click="goBack">重新选择</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, nextTick } from 'vue'
import { ArrowRight, Loading, SuccessFilled, CircleCloseFilled } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import QRCode from 'qrcode'
import { paymentApi } from '@/api/payment'
import request from '@/utils/request'
import type { InitiatePaymentData } from '@/api/payment'
import type { PaymentResponse } from '@/api/types'
import { usePaymentPolling } from '@/composables/usePaymentPolling'

const props = defineProps<{
  orderId: number
}>()

const emit = defineEmits<{
  paid: []
}>()

const visible = ref(false)
const step = ref<'select' | 'wechat-qr' | 'alipay-redirect' | 'success' | 'expired'>('select')
const paymentData = ref<PaymentResponse | null>(null)
const qrCanvas = ref<HTMLCanvasElement | null>(null)
const initiating = ref(false)
const isDev = import.meta.env.DEV
const mockPaying = ref(false)

const { polling, start: startPolling, stop: stopPolling } = usePaymentPolling(
  () => {
    step.value = 'success'
    emit('paid')
  },
  () => {
    step.value = 'expired'
  }
)

const open = () => {
  step.value = 'select'
  paymentData.value = null
  visible.value = true
}

const onClosed = () => {
  stopPolling()
  step.value = 'select'
  paymentData.value = null
}

const goBack = () => {
  stopPolling()
  step.value = 'select'
}

const onDone = () => {
  visible.value = false
}

const selectMethod = async (method: InitiatePaymentData['method']) => {
  if (initiating.value) return
  initiating.value = true
  try {
    const data = await paymentApi.initiatePayment(props.orderId, { method })
    paymentData.value = data

    if (method === 'wechat') {
      step.value = 'wechat-qr'
      await nextTick()
      if (qrCanvas.value && data.qrCode) {
        await QRCode.toCanvas(qrCanvas.value, data.qrCode, { width: 220, margin: 2 })
      }
      startPolling(data.paymentNo)
    } else {
      // Alipay: open pay URL in new tab
      if (data.payUrl) {
        window.open(data.payUrl, '_blank')
      }
      step.value = 'alipay-redirect'
      startPolling(data.paymentNo)
    }
  } catch (e: unknown) {
    const err = e as { message?: string }
    ElMessage.error(err.message || '发起支付失败，请稍后重试')
  } finally {
    initiating.value = false
  }
}

const handleMockPay = async () => {
  mockPaying.value = true
  try {
    await request.post(`/dev/orders/${props.orderId}/mock-pay`)
    step.value = 'success'
    emit('paid')
  } catch {
    ElMessage.error('模拟支付失败，请确认后端已在 dev profile 下运行')
  } finally {
    mockPaying.value = false
  }
}

defineExpose({ open })
</script>

<style scoped lang="scss">
.hint {
  color: var(--el-text-color-secondary);
  text-align: center;
  margin-bottom: $spacing-6;
}

.method-list {
  display: flex;
  flex-direction: column;
  gap: $spacing-3;
}

.method-item {
  display: flex;
  align-items: center;
  gap: $spacing-4;
  padding: $spacing-4 $spacing-6;
  border: 1px solid var(--el-border-color);
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s, background-color 0.2s;

  &:hover {
    border-color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);
  }

  .method-icon {
    font-size: 32px;
  }

  .wechat-icon { color: #07c160; }
  .alipay-icon { color: #1677ff; }

  .method-name {
    flex: 1;
    font-size: 16px;
    font-weight: 500;
  }

  .arrow {
    color: var(--el-text-color-placeholder);
  }
}

.wechat-qr {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-4;

  .qr-wrapper {
    position: relative;
    display: inline-block;
  }

  .qr-canvas {
    display: block;
    border-radius: 8px;
  }

  .polling-badge {
    position: absolute;
    bottom: -28px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    align-items: center;
    gap: $spacing-2;
    color: var(--el-color-primary);
    font-size: 13px;
    white-space: nowrap;
  }

  .amount-hint {
    color: var(--el-text-color-secondary);
    margin-top: $spacing-8;

    strong {
      color: var(--el-color-danger);
      font-size: 18px;
    }
  }
}

.alipay-redirect {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-3;
  padding: $spacing-6 0;

  .redirect-icon {
    font-size: 48px;
    color: #1677ff;
  }

  .redirect-title {
    font-size: 16px;
    font-weight: 600;
  }

  .redirect-sub {
    color: var(--el-text-color-secondary);
    font-size: 13px;
  }
}

.result-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: $spacing-3;
  padding: $spacing-6 0;

  .result-icon {
    font-size: 56px;
    color: var(--el-color-success);
  }

  .expired-icon {
    color: var(--el-color-danger);
  }

  .result-title {
    font-size: 18px;
    font-weight: 600;
  }

  .result-sub {
    color: var(--el-text-color-secondary);
    font-size: 13px;
    text-align: center;
  }
}

.polling-indicator {
  display: flex;
  align-items: center;
  gap: $spacing-2;
  color: var(--el-color-primary);
  font-size: 13px;
  margin-top: $spacing-2;
}

.dev-mock-pay {
  margin-top: $spacing-4;
  text-align: center;
}

.loading-icon {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: $spacing-3;
}
</style>
