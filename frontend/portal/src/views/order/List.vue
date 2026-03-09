<template>
  <div class="order-list-page">
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">{{ t('order.list') }}</h1>
      </div>
    </section>

    <div class="container page-body">
      <div class="toolbar">
        <el-select v-model="statusFilter" clearable placeholder="状态筛选" style="width: 140px" @change="loadList">
          <el-option label="待支付" value="unpaid" />
          <el-option label="已支付" value="paid" />
          <el-option label="已取消" value="cancelled" />
          <el-option label="已退款" value="refunded" />
        </el-select>
      </div>

      <el-table v-loading="loading" :data="list" style="width: 100%" empty-text="暂无订单记录">
        <el-table-column prop="orderNo" label="订单号" width="180" />
        <el-table-column label="票务" min-width="160">
          <template #default="{ row }">{{ row.ticketName || `票务 #${row.ticketId}` }}</template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column label="总金额" width="120">
          <template #default="{ row }">¥{{ row.totalAmount.toFixed(2) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.status) as 'success' | 'warning' | 'danger' | 'info'">
              {{ getOrderStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 'unpaid'" link type="primary" @click="handlePay(row.id)">支付</el-button>
            <el-button v-if="row.status === 'unpaid'" link type="danger" @click="handleCancel(row.id)">取消</el-button>
            <el-button
              v-if="row.status === 'paid'"
              link
              type="primary"
              @click="handleViewCredential(row)"
            >凭证</el-button>
            <el-button
              v-if="row.orderType === 2 && row.status === 'paid'"
              link
              type="success"
              :loading="sharingId === row.id"
              @click="handleShare(row.id)"
            >分享</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next"
          @change="loadList"
        />
      </div>
    </div>

    <!-- Payment modal -->
    <PaymentModal ref="paymentModal" :order-id="payingOrderId" @paid="onPaid" />

    <!-- Credential Dialog（个人票） -->
    <el-dialog v-model="credentialVisible" title="入场凭证" width="400px" align-center>
      <div v-loading="credentialLoading" class="credential-dialog-body">
        <template v-if="currentCredential">
          <!-- 二维码 -->
          <div class="qr-wrapper">
            <QrCodeImg :text="currentCredential.credentialNo" :size="200" class="qr-img" />
          </div>

          <!-- 凭证信息 -->
          <el-descriptions :column="1" border class="cred-desc">
            <el-descriptions-item label="凭证号">
              <span class="cred-no">{{ currentCredential.credentialNo }}</span>
            </el-descriptions-item>
            <el-descriptions-item label="座位">
              {{ currentCredential.seatInfo || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="credentialStatusType" size="small">{{ credentialStatusLabel }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="有效期至">
              <span :class="{ 'text-danger': isExpired }">
                {{ currentCredential.expireAt ? formatDate(currentCredential.expireAt) : '永久有效' }}
              </span>
            </el-descriptions-item>
          </el-descriptions>

          <p class="cred-hint">请凭此二维码在入场时出示，工作人员扫码核销后方可入场</p>
        </template>
      </div>
      <template #footer>
        <el-button @click="credentialVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api/order'
import { groupApi } from '@/api/group'
import { credentialApi } from '@/api/credential'
import type { Order } from '@/api/types'
import type { PortalCredential } from '@/api/credential'
import { formatDate, getOrderStatusLabel, getOrderStatusType } from '@/utils/format'
import PaymentModal from '@/components/PaymentModal.vue'
import QrCodeImg from '@/components/common/QrCodeImg.vue'

const router = useRouter()
const { t } = useI18n()

const loading = ref(false)
const list = ref<Order[]>([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const statusFilter = ref<string | undefined>()

const paymentModal = ref<InstanceType<typeof PaymentModal> | null>(null)
const payingOrderId = ref(0)
const sharingId = ref<number | null>(null)

// 凭证弹窗状态
const credentialVisible = ref(false)
const credentialLoading = ref(false)
const currentCredential = ref<PortalCredential | null>(null)

const credentialStatusLabel = computed(() => {
  const map: Record<string, string> = { valid: '有效', used: '已使用', expired: '已过期' }
  return map[currentCredential.value?.status ?? ''] ?? '-'
})

const credentialStatusType = computed(() => {
  const map: Record<string, string> = { valid: 'success', used: 'info', expired: 'warning' }
  return map[currentCredential.value?.status ?? ''] ?? 'info'
})

const isExpired = computed(() => {
  const expireAt = currentCredential.value?.expireAt
  return expireAt ? new Date(expireAt) < new Date() : false
})

const loadList = async () => {
  loading.value = true
  try {
    const res = await orderApi.getOrders({ page: page.value, pageSize: pageSize.value, status: statusFilter.value })
    list.value = res.list
    total.value = res.total
  } catch {
    ElMessage.error('加载失败')
  } finally {
    loading.value = false
  }
}

const handlePay = (id: number) => {
  payingOrderId.value = id
  paymentModal.value?.open()
}

const onPaid = () => {
  ElMessage.success('支付成功！凭证已生成')
  loadList()
}

const handleCancel = async (id: number) => {
  try {
    await ElMessageBox.confirm('确认取消该订单？', '取消确认', { type: 'warning' })
    await orderApi.cancelOrder(id)
    ElMessage.success('订单已取消')
    loadList()
  } catch {/* cancelled */}
}

const handleViewCredential = async (row: Order) => {
  if (row.orderType === 2) {
    // 团体票：进入团体管理页
    router.push({ name: 'GroupDetail', params: { id: row.id } })
    return
  }
  // 个人票：弹窗展示凭证
  credentialVisible.value = true
  credentialLoading.value = true
  currentCredential.value = null
  try {
    currentCredential.value = await credentialApi.getByOrder(row.id)
  } catch {
    ElMessage.error('获取凭证失败，请稍后重试')
    credentialVisible.value = false
  } finally {
    credentialLoading.value = false
  }
}

const handleShare = async (orderId: number) => {
  sharingId.value = orderId
  try {
    const inviteUrl = await groupApi.getInviteUrl(orderId)
    const fullUrl = window.location.origin + inviteUrl
    await navigator.clipboard.writeText(fullUrl)
    ElMessage.success('邀请链接已复制，快去分享给成员吧！')
  } catch {
    ElMessage.error('获取邀请链接失败，请稍后重试')
  } finally {
    sharingId.value = null
  }
}

onMounted(loadList)
</script>

<style scoped lang="scss">
.page-body {
  padding: $spacing-8 $spacing-6;
}

.toolbar {
  margin-bottom: $spacing-6;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: $spacing-6;
}

.credential-dialog-body {
  min-height: 200px;
}

.qr-wrapper {
  display: flex;
  justify-content: center;
  margin-bottom: $spacing-5;
}

.cred-desc {
  margin-bottom: $spacing-4;

  .cred-no {
    font-family: monospace;
    font-size: $font-size-sm;
    letter-spacing: 0.5px;
  }
}

.cred-hint {
  text-align: center;
  font-size: $font-size-sm;
  color: $text-secondary;
  margin-top: $spacing-3;
}

.text-danger {
  color: var(--el-color-danger);
}
</style>
