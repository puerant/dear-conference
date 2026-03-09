import request from '@/utils/request'
import type { PageData } from '@/utils/request'

export interface Order {
  id: number
  orderNo: string
  userId: number
  ticketId: number
  ticketName: string
  quantity: number
  totalAmount: number
  status: string
  orderType: number   // 1=个人 2=团体
  groupName: string | null
  attendeeName: string
  attendeeEmail: string
  createdAt: string
  paidAt: string | null
}

export interface Credential {
  id: number
  credentialNo: string
  status: string
  seatInfo: string
  usedAt: string | null
}

export interface GroupMember {
  id: number
  sequenceNo: number
  memberName: string | null
  memberEmail: string | null
  status: number
  filledAt: string | null
}

export interface OrderDetail extends Order {
  credentials: Credential[]
}

export interface OrderListParams {
  page?: number
  pageSize?: number
  orderNo?: string
  status?: string
  keyword?: string
}

export const orderApi = {
  getList(params: OrderListParams) {
    return request.get<PageData<Order>, PageData<Order>>('/admin/orders', { params })
  },

  getDetail(id: number) {
    return request.get<OrderDetail, OrderDetail>(`/admin/orders/${id}`)
  },

  refund(id: number) {
    return request.post(`/admin/orders/${id}/refund`)
  },

  getGroupMembers(id: number) {
    return request.get<GroupMember[], GroupMember[]>(`/admin/orders/${id}/group/members`)
  }
}

