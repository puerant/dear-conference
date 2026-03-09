import request from '@/utils/request'

export interface LoginParams {
  email: string
  password: string
}

export interface LoginResult {
  token: string
  userInfo: {
    id: number
    email: string
    name: string
    role: string
    avatar: string
  }
}

export const authApi = {
  login(data: LoginParams) {
    return request.post<LoginResult, LoginResult>('/auth/login', data)
  },
  refreshToken() {
    return request.post<LoginResult, LoginResult>('/auth/refresh')
  },
  logout() {
    return request.post('/auth/logout')
  },
  getProfile() {
    return request.get('/user/profile')
  }
}
