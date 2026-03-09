import { ref, onUnmounted } from 'vue'
import { paymentApi } from '@/api/payment'

const POLL_INTERVAL_MS = 3000

/**
 * Composable for polling payment status after initiating payment.
 *
 * @param onSuccess  Called when payment status becomes "success"
 * @param onExpired  Called when payment status becomes "closed" or "failed"
 */
export function usePaymentPolling(onSuccess: () => void, onExpired?: () => void) {
  const polling = ref(false)
  let timer: ReturnType<typeof setInterval> | null = null

  const start = (paymentNo: string) => {
    if (polling.value) return
    polling.value = true

    timer = setInterval(async () => {
      try {
        const status = await paymentApi.getStatus(paymentNo)
        if (status.status === 'success') {
          stop()
          onSuccess()
        } else if (status.status === 'closed' || status.status === 'failed') {
          stop()
          onExpired?.()
        }
      } catch {
        // Ignore transient network errors; keep polling
      }
    }, POLL_INTERVAL_MS)
  }

  const stop = () => {
    polling.value = false
    if (timer !== null) {
      clearInterval(timer)
      timer = null
    }
  }

  onUnmounted(stop)

  return { polling, start, stop }
}
