import request from '@/utils/request'
import type { PageData } from '@/utils/request'

export interface Credential {
  id: number
  credentialNo: string
  orderId: number
  orderNo: string
  attendeeName: string
  attendeeEmail: string
  ticketName: string
  seatInfo: string | null
  /** Base64 编码的 PNG 二维码图片 */
  qrCode: string | null
  /** 凭证状态：valid | used | expired */
  status: string
  expireAt: string | null
  createdAt: string
  usedAt: string | null
}

export interface CredentialListParams {
  page?: number
  pageSize?: number
  credentialNo?: string
  status?: string
}

export const credentialApi = {
  getList(params: CredentialListParams) {
    return request.get<PageData<Credential>, PageData<Credential>>('/admin/credentials', { params })
  },

  getDetail(credentialNo: string) {
    return request.get<Credential, Credential>(`/admin/credentials/${credentialNo}`)
  },

  use(credentialNo: string) {
    return request.post(`/credentials/${credentialNo}/use`)
  }
}
