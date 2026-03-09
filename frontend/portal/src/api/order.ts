import request from '@/utils/request'
import type { PageData } from '@/utils/request'
import type { Ticket, Order } from './types'

export interface CreateIndividualOrderData {
  ticketId: number
  orderType: 1
}

export interface CreateGroupOrderData {
  ticketId: number
  orderType: 2
  quantity: number
  groupName: string
}

export type CreateOrderData = CreateIndividualOrderData | CreateGroupOrderData

export const orderApi = {
  async getTickets() {
    const res = await request.get<PageData<Ticket>, PageData<Ticket>>('/ticket/available')
    return res.list
  },
  getOrders(params?: { page?: number; pageSize?: number; status?: string }) {
    return request.get<PageData<Order>, PageData<Order>>('/orders/my', { params })
  },
  getOrderDetail(id: number) {
    return request.get<Order, Order>(`/orders/${id}`)
  },
  createOrder(data: CreateOrderData) {
    return request.post<Order, Order>('/orders', data)
  },
  payOrder(id: number) {
    return request.post(`/orders/${id}/pay`)
  },
  cancelOrder(id: number) {
    return request.post(`/orders/${id}/cancel`)
  }
}

