<template>
  <div class="order-detail">
    <div class="page-header">
      <el-button :icon="ArrowLeft" @click="$router.back()">返回列表</el-button>
      <h2 class="page-title">订单详情</h2>
    </div>

    <div v-loading="loading">
      <el-row :gutter="20" v-if="detail">
        <el-col :span="16">
          <el-card class="info-card" header="订单信息">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="订单号" :span="2">
                <span>{{ detail.orderNo }}</span>
                <el-button size="small" text type="primary" @click="copyText(detail.orderNo)">
                  复制
                </el-button>
              </el-descriptions-item>
              <el-descriptions-item label="订单类型">
                <el-tag :type="detail.orderType === 2 ? 'warning' : 'success'" size="small">
                  {{ detail.orderType === 2 ? '团体票' : '个人票' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item v-if="detail.orderType === 2" label="团体名称">
                {{ detail.groupName }}
              </el-descriptions-item>
              <el-descriptions-item label="参会人">{{ detail.attendeeName }}</el-descriptions-item>
              <el-descriptions-item label="参会人邮箱">{{ detail.attendeeEmail }}</el-descriptions-item>
              <el-descriptions-item label="票务类型">{{ detail.ticketName }}</el-descriptions-item>
              <el-descriptions-item label="购票数量">{{ detail.quantity }} 张</el-descriptions-item>
              <el-descriptions-item label="订单金额">{{ formatAmount(detail.totalAmount) }}</el-descriptions-item>
              <el-descriptions-item label="订单状态">
                <el-tag :type="getOrderStatusType(detail.status)">
                  {{ getOrderStatusLabel(detail.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatDate(detail.createdAt) }}</el-descriptions-item>
              <el-descriptions-item label="支付时间">{{ formatDate(detail.paidAt) }}</el-descriptions-item>
            </el-descriptions>
          </el-card>

          <!-- Group members section (only for group orders) -->
          <el-card v-if="detail.orderType === 2" style="margin-top: 20px" header="团体成员">
            <div v-if="membersLoading" v-loading="true" style="min-height: 80px" />
            <template v-else>
              <el-table :data="members" border>
                <el-table-column prop="sequenceNo" label="序号" width="70" align="center" />
                <el-table-column label="姓名">
                  <template #default="{ row }">
                    <span v-if="row.memberName">{{ row.memberName }}</span>
                    <span v-else style="color: #909399; font-size: 12px">未填写</span>
                  </template>
                </el-table-column>
                <el-table-column label="邮箱">
                  <template #default="{ row }">
                    <span v-if="row.memberEmail">{{ row.memberEmail }}</span>
                    <span v-else style="color: #909399; font-size: 12px">未填写</span>
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="100" align="center">
                  <template #default="{ row }">
                    <el-tag :type="row.status === 2 ? 'success' : 'info'" size="small">
                      {{ row.status === 2 ? '已填写' : '待填写' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="填写时间" width="160">
                  <template #default="{ row }">{{ formatDate(row.filledAt) }}</template>
                </el-table-column>
              </el-table>
            </template>
          </el-card>

          <el-card style="margin-top: 20px" header="凭证信息">
            <el-empty v-if="!detail.credentials?.length" description="暂无凭证" />
            <el-table v-else :data="detail.credentials" border>
              <el-table-column prop="credentialNo" label="凭证号" min-width="160" show-overflow-tooltip />
              <el-table-column label="状态" width="90">
                <template #default="{ row }">
                  <el-tag :type="getCredentialStatusType(row.status)" size="small">
                    {{ getCredentialStatusLabel(row.status) }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="seatInfo" label="座位" width="120" />
              <el-table-column label="使用时间" width="160">
                <template #default="{ row }">{{ formatDate(row.usedAt) }}</template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card header="操作" v-if="detail.status === 'paid'">
            <el-button
              type="danger"
              style="width: 100%"
              :loading="refunding"
              @click="handleRefund"
            >
              处理退款
            </el-button>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import { orderApi, type OrderDetail, type GroupMember } from '@/api/order'
import {
  formatDate,
  formatAmount,
  getOrderStatusLabel,
  getOrderStatusType,
  getCredentialStatusLabel,
  getCredentialStatusType
} from '@/utils/format'

const route = useRoute()
const router = useRouter()
const detail = ref<OrderDetail | null>(null)
const loading = ref(false)
const refunding = ref(false)
const members = ref<GroupMember[]>([])
const membersLoading = ref(false)

async function fetchDetail() {
  const id = Number(route.params.id)
  if (!id) return
  loading.value = true
  try {
    const res = await orderApi.getDetail(id)
    detail.value = res
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

async function fetchGroupMembers(id: number) {
  membersLoading.value = true
  try {
    members.value = await orderApi.getGroupMembers(id)
  } catch {
    // ignore
  } finally {
    membersLoading.value = false
  }
}

watch(detail, (val) => {
  if (val && val.orderType === 2) {
    fetchGroupMembers(val.id)
  }
})

async function handleRefund() {
  if (!detail.value) return
  await ElMessageBox.confirm(
    `确定要对订单 "${detail.value.orderNo}" 执行退款操作吗？`,
    '处理退款',
    { type: 'warning', confirmButtonText: '确定退款' }
  )
  refunding.value = true
  try {
    await orderApi.refund(detail.value.id)
    ElMessage.success('退款操作成功')
    router.back()
  } catch {
    // ignore
  } finally {
    refunding.value = false
  }
}

function copyText(text: string) {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  })
}

onMounted(() => fetchDetail())
</script>

<style lang="scss" scoped>
.order-detail {
  .page-header {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 20px;

    .page-title {
      font-size: 18px;
      font-weight: 600;
      color: #262626;
      margin: 0;
    }
  }
}
</style>
