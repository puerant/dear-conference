import request from '@/utils/request'
import type { PageData } from '@/utils/request'

export interface User {
  id: number
  email: string
  name: string
  role: string
  status: number
  avatar: string
  createdAt: string
}

export interface UserListParams {
  page?: number
  pageSize?: number
  keyword?: string
  role?: string
  status?: number
}

export interface CreateUserParams {
  email: string
  password: string
  name: string
  role: string
}

export const userApi = {
  getList(params: UserListParams) {
    return request.get<PageData<User>, PageData<User>>('/admin/users', { params })
  },

  getDetail(id: number) {
    return request.get<User, User>(`/admin/users/${id}`)
  },

  create(data: CreateUserParams) {
    return request.post<void, void>('/admin/users', data)
  },

  updateStatus(id: number, status: number) {
    return request.put(`/admin/users/${id}/status`, { status })
  },

  updateRole(id: number, role: string) {
    return request.put(`/admin/users/${id}/role`, { role })
  },

  delete(id: number) {
    return request.delete(`/admin/users/${id}`)
  }
}
