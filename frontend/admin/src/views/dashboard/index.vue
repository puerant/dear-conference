<template>
  <div class="dashboard">
    <h2 class="page-title">控制台</h2>

    <!-- Stat Cards -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <div class="stat-card" :style="{ borderTopColor: card.color }">
          <div class="stat-icon" :style="{ background: card.color }">
            <el-icon size="24" color="#fff">
              <component :is="card.icon" />
            </el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- Recent Data -->
    <el-row :gutter="20" class="data-row">
      <el-col :span="12">
        <el-card header="最新投稿" class="data-card">
          <el-table :data="recentSubmissions" size="small" v-loading="loadingSubmissions">
            <el-table-column prop="title" label="标题" show-overflow-tooltip />
            <el-table-column prop="speakerName" label="讲者" width="100" />
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="getSubmissionStatusType(row.status)" size="small">
                  {{ getSubmissionStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createdAt" label="时间" width="110">
              <template #default="{ row }">{{ formatDateOnly(row.createdAt) }}</template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="12">
        <el-card header="最新订单" class="data-card">
          <el-table :data="recentOrders" size="small" v-loading="loadingOrders">
            <el-table-column prop="orderNo" label="订单号" show-overflow-tooltip />
            <el-table-column prop="attendeeName" label="参会人" width="90" />
            <el-table-column label="金额" width="90">
              <template #default="{ row }">{{ formatAmount(row.totalAmount) }}</template>
            </el-table-column>
            <el-table-column label="状态" width="90">
              <template #default="{ row }">
                <el-tag :type="getOrderStatusType(row.status)" size="small">
                  {{ getOrderStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { User, Document, Ticket, ShoppingCart } from '@element-plus/icons-vue'
import { userApi } from '@/api/user'
import { submissionApi, type Submission } from '@/api/submission'
import { ticketApi } from '@/api/ticket'
import { orderApi, type Order } from '@/api/order'
import {
  formatDateOnly,
  formatAmount,
  getSubmissionStatusLabel,
  getSubmissionStatusType,
  getOrderStatusLabel,
  getOrderStatusType
} from '@/utils/format'

const statCards = ref([
  { label: '用户总数', value: 0, color: '#1677ff', icon: User },
  { label: '投稿总数', value: 0, color: '#52c41a', icon: Document },
  { label: '票务总数', value: 0, color: '#fa8c16', icon: Ticket },
  { label: '订单总数', value: 0, color: '#f5222d', icon: ShoppingCart }
])

const recentSubmissions = ref<Submission[]>([])
const recentOrders = ref<Order[]>([])
const loadingSubmissions = ref(false)
const loadingOrders = ref(false)

async function fetchStats() {
  try {
    const [users, submissions, tickets, orders] = await Promise.allSettled([
      userApi.getList({ page: 1, pageSize: 1 }),
      submissionApi.getList({ page: 1, pageSize: 1 }),
      ticketApi.getList({ page: 1, pageSize: 1 }),
      orderApi.getList({ page: 1, pageSize: 1 })
    ])

    if (users.status === 'fulfilled') statCards.value[0].value = users.value.total
    if (submissions.status === 'fulfilled') statCards.value[1].value = submissions.value.total
    if (tickets.status === 'fulfilled') statCards.value[2].value = tickets.value.total
    if (orders.status === 'fulfilled') statCards.value[3].value = orders.value.total
  } catch {
    // ignore
  }
}

async function fetchRecentSubmissions() {
  loadingSubmissions.value = true
  try {
    const res = await submissionApi.getList({ page: 1, pageSize: 5 })
    recentSubmissions.value = res.list
  } catch {
    // ignore
  } finally {
    loadingSubmissions.value = false
  }
}

async function fetchRecentOrders() {
  loadingOrders.value = true
  try {
    const res = await orderApi.getList({ page: 1, pageSize: 5 })
    recentOrders.value = res.list
  } catch {
    // ignore
  } finally {
    loadingOrders.value = false
  }
}

onMounted(() => {
  fetchStats()
  fetchRecentSubmissions()
  fetchRecentOrders()
})
</script>

<style lang="scss" scoped>
.dashboard {
  .page-title {
    font-size: 20px;
    font-weight: 600;
    color: #262626;
    margin: 0 0 20px;
  }

  .stat-row {
    margin-bottom: 20px;

    .stat-card {
      background: #fff;
      border-radius: 8px;
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 16px;
      border-top: 3px solid;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
      transition: box-shadow 0.2s;

      &:hover {
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
      }

      .stat-icon {
        width: 52px;
        height: 52px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
      }

      .stat-value {
        font-size: 28px;
        font-weight: 700;
        color: #262626;
        line-height: 1;
        margin-bottom: 4px;
      }

      .stat-label {
        font-size: 13px;
        color: #8c8c8c;
      }
    }
  }

  .data-row {
    .data-card {
      :deep(.el-card__header) {
        font-weight: 600;
        color: #262626;
      }
    }
  }
}
</style>
