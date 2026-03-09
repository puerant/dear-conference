/**
 * Format date string to readable format
 */
export function formatDate(dateStr: string | null | undefined): string {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return '-'
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

/**
 * Format date only (no time)
 */
export function formatDateOnly(dateStr: string | null | undefined): string {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  if (isNaN(date.getTime())) return '-'
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`
}

/**
 * Format amount to CNY
 */
export function formatAmount(amount: number | string | null | undefined): string {
  if (amount === null || amount === undefined || amount === '') return '-'
  return `¥${Number(amount).toFixed(2)}`
}

/**
 * User role label
 */
export function getRoleLabel(role: string): string {
  const map: Record<string, string> = {
    speaker: '讲者',
    reviewer: '审稿人',
    attendee: '参会者',
    admin: '管理员'
  }
  return map[role] || role
}

/**
 * User role tag type for Element Plus
 */
export function getRoleTagType(role: string): string {
  const map: Record<string, string> = {
    speaker: 'primary',
    reviewer: 'warning',
    attendee: 'success',
    admin: 'danger'
  }
  return map[role] || 'info'
}

/**
 * Submission status label
 */
export function getSubmissionStatusLabel(status: string): string {
  const map: Record<string, string> = {
    pending: '待审稿',
    reviewing: '审稿中',
    accepted: '已录用',
    rejected: '已拒绝'
  }
  return map[status] || status
}

/**
 * Submission status tag type
 */
export function getSubmissionStatusType(status: string): string {
  const map: Record<string, string> = {
    pending: 'info',
    reviewing: 'warning',
    accepted: 'success',
    rejected: 'danger'
  }
  return map[status] || 'info'
}

/**
 * Order status label
 */
export function getOrderStatusLabel(status: string): string {
  const map: Record<string, string> = {
    unpaid: '未支付',
    paid: '已支付',
    cancelled: '已取消',
    refunded: '已退款'
  }
  return map[status] || status
}

/**
 * Order status tag type
 */
export function getOrderStatusType(status: string): string {
  const map: Record<string, string> = {
    unpaid: 'warning',
    paid: 'success',
    cancelled: 'info',
    refunded: 'danger'
  }
  return map[status] || 'info'
}

/**
 * Credential status label
 */
export function getCredentialStatusLabel(status: string): string {
  const map: Record<string, string> = {
    valid: '有效',
    used: '已使用',
    expired: '已过期',
    cancelled: '已作废'
  }
  return map[status] || status
}

/**
 * Credential status tag type
 */
export function getCredentialStatusType(status: string): string {
  const map: Record<string, string> = {
    valid: 'success',
    used: 'info',
    expired: 'warning',
    cancelled: 'danger'
  }
  return map[status] || 'info'
}

/**
 * Review result label
 */
export function getReviewResultLabel(result: string): string {
  const map: Record<string, string> = {
    accept: '录用',
    reject: '拒绝',
    revise: '需修改'
  }
  return map[result] || result
}

/**
 * Review result tag type
 */
export function getReviewResultType(result: string): string {
  const map: Record<string, string> = {
    accept: 'success',
    reject: 'danger',
    revise: 'warning'
  }
  return map[result] || 'info'
}
