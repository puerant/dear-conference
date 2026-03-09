/**
 * Format a datetime string to a localized date string
 */
export function formatDate(dateStr: string | null | undefined): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
}

/**
 * Format a datetime string to a localized datetime string
 */
export function formatDateTime(dateStr: string | null | undefined): string {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

/**
 * Format amount to currency string
 */
export function formatAmount(amount: number): string {
  return `¥${amount.toFixed(2)}`
}

/** role string code → label (backend UserRole enum: speaker/reviewer/attendee/admin) */
export function getRoleLabel(role: string): string {
  const map: Record<string, string> = {
    speaker: '投稿讲者',
    reviewer: '审稿人',
    attendee: '参会者',
    admin: '管理员'
  }
  return map[role] ?? '未知'
}

/** submission status code → label (backend returns string code via @JsonValue) */
export function getSubmissionStatusLabel(status: string): string {
  const map: Record<string, string> = {
    pending: '待审稿',
    reviewing: '审稿中',
    accepted: '已录用',
    rejected: '已拒绝'
  }
  return map[status] ?? '未知'
}

/** submission status code → el-tag type */
export function getSubmissionStatusType(status: string): string {
  const map: Record<string, string> = {
    pending: 'info',
    reviewing: 'warning',
    accepted: 'success',
    rejected: 'danger'
  }
  return map[status] ?? 'info'
}

/** review result code → label (backend returns string code via @JsonValue) */
export function getReviewResultLabel(result: string): string {
  const map: Record<string, string> = { accept: '录用', reject: '拒绝', revise: '需修改' }
  return map[result] ?? '未知'
}

/** review result code → el-tag type */
export function getReviewResultType(result: string): string {
  const map: Record<string, string> = { accept: 'success', reject: 'danger', revise: 'warning' }
  return map[result] ?? 'info'
}

/** order status → label */
export function getOrderStatusLabel(status: string): string {
  const map: Record<string, string> = { unpaid: '待支付', paid: '已支付', cancelled: '已取消', refunded: '已退款' }
  return map[status] ?? '未知'
}

/** order status → el-tag type */
export function getOrderStatusType(status: string): string {
  const map: Record<string, string> = { unpaid: 'warning', paid: 'success', cancelled: 'info', refunded: 'danger' }
  return map[status] ?? 'info'
}

/** payment method code → label */
export function getPaymentMethodLabel(method: string): string {
  const map: Record<string, string> = { wechat: '微信支付', alipay: '支付宝', unionpay: '银联', paypal: 'PayPal' }
  return map[method] ?? '未知'
}

/** payment status code → label */
export function getPaymentStatusLabel(status: string): string {
  const map: Record<string, string> = {
    pending: '待支付',
    success: '支付成功',
    failed: '支付失败',
    closed: '已关闭',
    refunded: '已退款'
  }
  return map[status] ?? '未知'
}
