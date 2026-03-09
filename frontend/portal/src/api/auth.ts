import request from '@/utils/request'
import type { UserInfo } from './types'

export interface LoginData {
  email: string
  password: string
}

export interface RegisterData {
  email: string
  password: string
  name: string
  role: string
}

export interface LoginResult {
  token: string
  userInfo: UserInfo
}

export const authApi = {
  login(data: LoginData) {
    return request.post<LoginResult, LoginResult>('/auth/login', data)
  },
  register(data: RegisterData) {
    return request.post<void, void>('/auth/register', data)
  },
  sendVerificationCode(email: string) {
    return request.post<void, void>('/auth/send-code', { email })
  },
  verifyEmail(email: string, code: string) {
    return request.post<LoginResult, LoginResult>('/auth/verify-email', { email, code })
  },
  refreshToken() {
    return request.post<LoginResult, LoginResult>('/auth/refresh')
  },
  logout() {
    return request.post('/auth/logout')
  },
  getProfile() {
    return request.get<UserInfo, UserInfo>('/user/profile')
  },
  updateProfile(data: Partial<UserInfo>) {
    return request.put('/user/profile', data)
  },
  changePassword(data: { oldPassword: string; newPassword: string }) {
    return request.put('/user/profile', data)
  }
}
