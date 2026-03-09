import request from '@/utils/request'

export const credentialApi = {
  /**
   * 获取订单凭证（含 userId 归属校验，仅限凭证归属人访问）
   */
  getByOrder(orderId: number) {
    return request.get<PortalCredential, PortalCredential>(`/credentials/order/${orderId}`)
  }
}

export interface PortalCredential {
  id: number
  orderId: number
  credentialNo: string
  /** Base64 编码的 PNG 二维码图片，前端渲染：data:image/png;base64,{qrCode} */
  qrCode: string | null
  seatInfo: string | null
  /** 凭证状态：valid | used | expired */
  status: string
  expireAt: string | null
  createdAt: string
  usedAt: string | null
}
