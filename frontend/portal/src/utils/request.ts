import axios, { type AxiosInstance, type InternalAxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { storage } from './storage'

export interface ApiResponse<T = unknown> {
  code: number
  message: string
  data: T
  timestamp: number
}

export interface PageData<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

const request: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// ── Token refresh state ──────────────────────────────────────────────────────
let isRefreshing = false
let refreshQueue: Array<(newToken: string) => void> = []

type RetryConfig = InternalAxiosRequestConfig & { _retried?: boolean }

function flushQueue(newToken: string) {
  refreshQueue.forEach(cb => cb(newToken))
  refreshQueue = []
}

function clearAndRedirect() {
  storage.clear()
  import('@/router').then(({ default: router }) => router.push('/login'))
}

/** 调用刷新接口（使用原生 axios，不经过 request 拦截器避免循环） */
async function doRefresh(oldToken: string): Promise<string> {
  const res = await axios.post<ApiResponse<{ token: string }>>(
    `${BASE_URL}/auth/refresh`,
    null,
    { headers: { Authorization: `Bearer ${oldToken}` } }
  )
  if (res.data.code !== 200) throw new Error('refresh failed')
  return res.data.data.token
}

/** 401 统一处理：尝试刷新 token，成功则重试原请求，失败则跳登录页 */
function handleUnauthorized(config: RetryConfig): Promise<AxiosResponse> {
  const oldToken = storage.getToken()

  // 没有 token 或本次请求已经是刷新后的重试 → 直接跳登录
  if (!oldToken || config._retried) {
    ElMessage.error('登录已过期，请重新登录')
    clearAndRedirect()
    return Promise.reject(new Error('Unauthorized'))
  }

  config._retried = true

  // 已有其他请求在刷新中 → 排队等待
  if (isRefreshing) {
    return new Promise<string>(resolve => {
      refreshQueue.push(resolve)
    }).then(newToken => {
      config.headers['Authorization'] = `Bearer ${newToken}`
      return request(config)
    })
  }

  // 本请求触发刷新
  isRefreshing = true
  return doRefresh(oldToken)
    .then(newToken => {
      storage.setToken(newToken)
      import('@/stores/auth').then(({ useAuthStore }) => {
        useAuthStore().setToken(newToken)
      })
      flushQueue(newToken)
      config.headers['Authorization'] = `Bearer ${newToken}`
      return request(config)
    })
    .catch(() => {
      refreshQueue = []
      ElMessage.error('登录已过期，请重新登录')
      clearAndRedirect()
      return Promise.reject(new Error('Token refresh failed'))
    })
    .finally(() => {
      isRefreshing = false
    })
}
// ────────────────────────────────────────────────────────────────────────────

// Request interceptor — attach token
request.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = storage.getToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor — unwrap data or handle errors
request.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const { code, message, data } = response.data

    if (code === 200) {
      return data as unknown as AxiosResponse
    }

    if (code === 401) {
      return handleUnauthorized(response.config as RetryConfig)
    }

    ElMessage.error(message || '操作失败，请稍后重试')
    return Promise.reject(new Error(message))
  },
  (error) => {
    if (error.response?.status === 401) {
      return handleUnauthorized(error.config as RetryConfig)
    }
    ElMessage.error(error.message || '网络错误，请检查连接')
    return Promise.reject(error)
  }
)

export default request
