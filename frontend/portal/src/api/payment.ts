import request from '@/utils/request'
import type { PaymentResponse, PaymentStatusVo } from './types'

export interface InitiatePaymentData {
  method: 'wechat' | 'alipay'
}

export const paymentApi = {
  initiatePayment(orderId: number, data: InitiatePaymentData) {
    return request.post<PaymentResponse, PaymentResponse>(`/orders/${orderId}/pay`, data)
  },

  getStatus(paymentNo: string) {
    return request.get<PaymentStatusVo, PaymentStatusVo>(`/payment/${paymentNo}/status`)
  }
}
