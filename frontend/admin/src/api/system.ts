import request from '@/utils/request'
import type { PageData } from '@/utils/request'

export interface SystemConfigResponse {
  emailConfig?: EmailConfig
  credentialConfig?: CredentialConfig
  paymentConfig?: PaymentConfig[]
  orderTaskConfig?: OrderTaskConfig
}

export interface EmailConfig {
  id?: number
  configName?: string
  host: string
  port: number
  username: string
  password: string
  fromName: string
  fromEmail: string
  sslEnabled: boolean
  isEnabled: boolean
  isDefault?: boolean
  priority?: number
  remark?: string
}

export interface EmailConfigItem {
  id: number
  configName: string
  host: string
  port: number
  username: string
  fromName: string
  fromEmail: string
  sslEnabled: number
  isEnabled: number
  isDefault: number
  priority: number
  remark?: string
  createdAt: string
  updatedAt: string
}

export interface CredentialConfig {
  id?: number
  templateId: string
  backgroundImage?: string
  watermarkText?: string
  watermarkOpacity: number
  watermarkX: number
  watermarkY: number
  expiryDays: number
  qrCodePosition: string
}

export interface PaymentConfig {
  id?: number
  paymentType: number
  appId: string
  appSecret: string
  merchantId: string
  apiUrl: string
  notifyUrl: string
  isEnabled: boolean
  sandboxMode: boolean
}

export interface OrderTaskConfig {
  id?: number
  timeoutMinutes: number
  checkIntervalMinutes: number
  cancelTaskEnabled: boolean
  refundTaskEnabled: boolean
}

export interface EmailTemplateItem {
  id: number
  sceneCode: string
  sceneName: string
  emailConfigId?: number
  emailConfigName?: string
  subject: string
  variablesDesc?: string
  isEnabled: number
  createdAt: string
  updatedAt: string
}

export interface EmailTemplateDetail extends EmailTemplateItem {
  body: string
}

export interface UpdateEmailTemplateDto {
  emailConfigId?: number | null
  subject: string
  body: string
  isEnabled: number
}

export interface EmailTemplateActionDto {
  toEmail?: string
  variables?: Record<string, string>
}

export interface EmailPreviewVo {
  subject: string
  body: string
  fromEmail: string
  fromName: string
}

export interface EmailLogItem {
  id: number
  sceneCode: string
  templateId?: number
  emailConfigId?: number
  sendType: number
  toEmail: string
  fromEmail?: string
  subject?: string
  variablesJson?: string
  status: number
  errorMessage?: string
  triggeredBy?: number
  sentAt?: string
  createdAt: string
}

export interface EmailLogQuery {
  page?: number
  pageSize?: number
  sceneCode?: string
  status?: number
  toEmail?: string
  sendType?: number
  startTime?: string
  endTime?: string
}

export interface SystemFile {
  id: number
  fileName: string
  fileUrl: string
  fileType: string
  fileExtension: string
  fileSize: number
  fileSizeDisplay: string
  fileCategory?: string
  categoryId?: number
  uploadUserId?: number
  uploadUserName?: string
  providerId?: number
  bucketId?: number
  visibility?: number
  isMigrated?: number
  createdAt: string
}

export interface SystemFileQuery {
  page?: number
  pageSize?: number
  fileType?: string
  fileCategory?: string
  categoryId?: number
  startDate?: string
  endDate?: string
  keyword?: string
}

export interface StorageProvider {
  id?: number
  providerName: string
  providerType: number
  endpoint?: string
  region?: string
  accessKey?: string
  secretKey?: string
  stsEnabled?: number
  isEnabled?: number
  isDefault?: number
  remark?: string
  createdAt?: string
  updatedAt?: string
}

export interface StorageBucket {
  id?: number
  providerId: number
  bucketName: string
  basePath?: string
  domain?: string
  aclType?: number
  isEnabled?: number
  isDefault?: number
  createdAt?: string
  updatedAt?: string
}

export interface FileStoragePolicy {
  id?: number
  policyName: string
  fileCategory?: string
  fileType?: string
  providerId: number
  bucketId: number
  pathRule?: string
  maxSizeMb?: number
  allowedExt?: string
  priority?: number
  isEnabled?: number
  remark?: string
  createdAt?: string
  updatedAt?: string
}

export interface FileMigrationTask {
  id: number
  taskNo: string
  sourceProviderId: number
  targetProviderId: number
  fileCount: number
  successCount: number
  failCount: number
  status: number
  errorMessage?: string
  startedAt?: string
  finishedAt?: string
  createdAt: string
}

export const systemApi = {
  getSystemConfig() {
    return request.get<SystemConfigResponse, SystemConfigResponse>('/admin/system/config')
  },

  // 邮箱配置
  updateEmailConfig(data: EmailConfig) {
    return request.post('/admin/system/email-config', {
      ...data,
      sslEnabled: data.sslEnabled ? 1 : 0,
      isEnabled: data.isEnabled ? 1 : 0,
    })
  },

  testEmail(data: { toEmail: string }) {
    return request.post('/admin/system/email/test', data)
  },

  listEmailConfigs() {
    return request.get<EmailConfigItem[], EmailConfigItem[]>('/admin/system/email-configs')
  },

  getEmailConfig(id: number) {
    return request.get<EmailConfigItem, EmailConfigItem>(`/admin/system/email-configs/${id}`)
  },

  createEmailConfig(data: Record<string, unknown>) {
    return request.post<number, number>('/admin/system/email-configs', data)
  },

  updateEmailConfigById(id: number, data: Record<string, unknown>) {
    return request.put(`/admin/system/email-configs/${id}`, data)
  },

  updateEmailConfigEnabled(id: number, enabled: number) {
    return request.put(`/admin/system/email-configs/${id}/enable`, { enabled })
  },

  setDefaultEmailConfig(id: number) {
    return request.put(`/admin/system/email-configs/${id}/default`)
  },

  deleteEmailConfig(id: number) {
    return request.delete(`/admin/system/email-configs/${id}`)
  },

  testEmailByConfig(id: number, toEmail: string) {
    return request.post(`/admin/system/email-configs/${id}/test`, { toEmail })
  },

  // 凭证配置
  updateCredentialConfig(data: CredentialConfig) {
    return request.post('/admin/system/credential-config', data)
  },

  // 支付配置
  updatePaymentConfig(data: PaymentConfig) {
    return request.post('/admin/system/payment-config', {
      ...data,
      isEnabled: data.isEnabled ? 1 : 0,
      sandboxMode: data.sandboxMode ? 1 : 0,
    })
  },

  deletePaymentConfig(id: number) {
    return request.delete(`/admin/system/payment-config/${id}`)
  },

  // 定时任务配置
  updateOrderTaskConfig(data: OrderTaskConfig) {
    return request.post('/admin/system/order-task-config', {
      ...data,
      cancelTaskEnabled: data.cancelTaskEnabled ? 1 : 0,
      refundTaskEnabled: data.refundTaskEnabled ? 1 : 0,
    })
  },

  // 邮件模板
  listEmailTemplates() {
    return request.get<EmailTemplateItem[], EmailTemplateItem[]>('/admin/system/email-templates')
  },

  getEmailTemplate(sceneCode: string) {
    return request.get<EmailTemplateDetail, EmailTemplateDetail>(`/admin/system/email-templates/${sceneCode}`)
  },

  updateEmailTemplate(sceneCode: string, data: UpdateEmailTemplateDto) {
    return request.put(`/admin/system/email-templates/${sceneCode}`, data)
  },

  previewEmailTemplate(sceneCode: string, data: EmailTemplateActionDto) {
    return request.post<EmailPreviewVo, EmailPreviewVo>(`/admin/system/email-templates/${sceneCode}/preview`, data)
  },

  testSendEmailTemplate(sceneCode: string, data: EmailTemplateActionDto) {
    return request.post(`/admin/system/email-templates/${sceneCode}/test-send`, data)
  },

  // 邮件发送日志
  getEmailLogs(params: EmailLogQuery) {
    return request.get<PageData<EmailLogItem>, PageData<EmailLogItem>>('/admin/system/email-logs', { params })
  },

  getEmailLogDetail(id: number) {
    return request.get<EmailLogItem, EmailLogItem>(`/admin/system/email-logs/${id}`)
  },

  // 文件管理
  uploadFile(file: File, params?: { category?: string; categoryId?: number; providerId?: number; bucketId?: number; visibility?: number }) {
    const formData = new FormData()
    formData.append('file', file)
    if (params?.category) {
      formData.append('category', params.category)
    }
    if (params?.categoryId) {
      formData.append('categoryId', String(params.categoryId))
    }
    if (params?.providerId) {
      formData.append('providerId', String(params.providerId))
    }
    if (params?.bucketId) {
      formData.append('bucketId', String(params.bucketId))
    }
    if (params?.visibility) {
      formData.append('visibility', String(params.visibility))
    }
    return request.post<SystemFile, SystemFile>('/admin/file/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  getFileList(params: SystemFileQuery) {
    return request.get<PageData<SystemFile>, PageData<SystemFile>>('/admin/file/list', { params })
  },

  getFileDetail(id: number) {
    return request.get<SystemFile, SystemFile>(`/admin/system/file/${id}`)
  },

  deleteFile(id: number) {
    return request.delete(`/admin/file/${id}`)
  },

  restoreFile(id: number) {
    return request.post(`/admin/file/${id}/restore`)
  },

  batchDeleteFiles(ids: number[]) {
    return request.post('/admin/file/batch-delete', { ids })
  },

  getFilePreviewUrl(id: number) {
    return request.get<string, string>(`/admin/file/${id}/preview-url`)
  },

  downloadFile(id: number) {
    return request.get(`/admin/system/file/${id}/download`, { responseType: 'blob' })
  },

  // 存储供应商
  listStorageProviders() {
    return request.get<StorageProvider[], StorageProvider[]>('/admin/file/providers')
  },

  getStorageProvider(id: number) {
    return request.get<StorageProvider, StorageProvider>(`/admin/file/providers/${id}`)
  },

  createStorageProvider(data: StorageProvider) {
    return request.post<number, number>('/admin/file/providers', data)
  },

  updateStorageProvider(id: number, data: StorageProvider) {
    return request.put(`/admin/file/providers/${id}`, data)
  },

  updateStorageProviderEnabled(id: number, enabled: number) {
    return request.put(`/admin/file/providers/${id}/enable`, { enabled })
  },

  setDefaultStorageProvider(id: number) {
    return request.put(`/admin/file/providers/${id}/default`)
  },

  testStorageProvider(id: number) {
    return request.post(`/admin/file/providers/${id}/test`)
  },

  deleteStorageProvider(id: number) {
    return request.delete(`/admin/file/providers/${id}`)
  },

  // 存储桶
  listStorageBuckets(providerId?: number) {
    return request.get<StorageBucket[], StorageBucket[]>('/admin/file/buckets', { params: { providerId } })
  },

  createStorageBucket(data: StorageBucket) {
    return request.post<number, number>('/admin/file/buckets', data)
  },

  updateStorageBucket(id: number, data: StorageBucket) {
    return request.put(`/admin/file/buckets/${id}`, data)
  },

  updateStorageBucketEnabled(id: number, enabled: number) {
    return request.put(`/admin/file/buckets/${id}/enable`, { enabled })
  },

  setDefaultStorageBucket(id: number) {
    return request.put(`/admin/file/buckets/${id}/default`)
  },

  deleteStorageBucket(id: number) {
    return request.delete(`/admin/file/buckets/${id}`)
  },

  // 存储策略
  listFileStoragePolicies() {
    return request.get<FileStoragePolicy[], FileStoragePolicy[]>('/admin/file/policies')
  },

  createFileStoragePolicy(data: FileStoragePolicy) {
    return request.post<number, number>('/admin/file/policies', data)
  },

  updateFileStoragePolicy(id: number, data: FileStoragePolicy) {
    return request.put(`/admin/file/policies/${id}`, data)
  },

  updateFileStoragePolicyEnabled(id: number, enabled: number) {
    return request.put(`/admin/file/policies/${id}/enable`, { enabled })
  },

  deleteFileStoragePolicy(id: number) {
    return request.delete(`/admin/file/policies/${id}`)
  },

  // 迁移任务
  createFileMigrationTask(data: { sourceProviderId: number; targetProviderId: number }) {
    return request.post<number, number>('/admin/file/migration/tasks', data)
  },

  listFileMigrationTasks() {
    return request.get<FileMigrationTask[], FileMigrationTask[]>('/admin/file/migration/tasks')
  },

  getFileMigrationTask(id: number) {
    return request.get<FileMigrationTask, FileMigrationTask>(`/admin/file/migration/tasks/${id}`)
  },

  retryFileMigrationTask(id: number) {
    return request.post(`/admin/file/migration/tasks/${id}/retry`)
  }
}
