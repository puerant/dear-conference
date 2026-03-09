import request from '@/utils/request'
import type { PageData } from '@/utils/request'

export interface Ticket {
  id: number
  name: string
  price: number
  description: string
  stock: number
  soldCount: number
  status: number
  ticketType: number   // 1=个人票 2=团体票
  minPersons: number | null
  maxPersons: number | null
  createdAt: string
  updatedAt: string
}

export interface TicketForm {
  name: string
  price: number | string
  stock: number | string
  description: string
  ticketType: number   // 1=个人票 2=团体票
  minPersons?: number | null
  maxPersons?: number | null
}

export interface TicketListParams {
  page?: number
  pageSize?: number
  status?: number
}

export const ticketApi = {
  getList(params: TicketListParams) {
    return request.get<PageData<Ticket>, PageData<Ticket>>('/admin/ticket', { params })
  },

  getDetail(id: number) {
    return request.get<Ticket, Ticket>(`/admin/ticket/${id}`)
  },

  create(data: TicketForm) {
    return request.post<Ticket, Ticket>('/admin/ticket', data)
  },

  update(id: number, data: TicketForm) {
    return request.put<Ticket, Ticket>(`/admin/ticket/${id}`, data)
  },

  updateStatus(id: number, status: number) {
    return request.put(`/admin/ticket/${id}/status`, { status })
  },

  adjustStock(id: number, stock: number) {
    return request.put(`/admin/ticket/${id}/stock`, { stock })
  },

  delete(id: number) {
    return request.delete(`/admin/ticket/${id}`)
  }
}
