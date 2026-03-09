<template>
  <div class="order-page">
    <el-card class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="订单号">
          <el-input v-model="query.orderNo" placeholder="请输入订单号" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="未支付" value="unpaid" />
            <el-option label="已支付" value="paid" />
            <el-option label="已取消" value="cancelled" />
            <el-option label="已退款" value="refunded" />
          </el-select>
        </el-form-item>
        <el-form-item label="参会人">
          <el-input v-model="query.keyword" placeholder="姓名/邮箱" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList(1)">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="orderNo" label="订单号" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="copyText(row.orderNo)">
              {{ row.orderNo }}
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="attendeeName" label="参会人" width="100" />
        <el-table-column prop="attendeeEmail" label="参会人邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="ticketName" label="票务" width="120" show-overflow-tooltip />
        <el-table-column prop="quantity" label="数量" width="70" align="center" />
        <el-table-column label="金额" width="100">
          <template #default="{ row }">{{ formatAmount(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusType(row.status)" size="small">
              {{ getOrderStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="支付时间" width="160">
          <template #default="{ row }">{{ formatDate(row.paidAt) }}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="goDetail(row.id)">详情</el-button>
            <el-button
              v-if="row.status === 'paid'"
              type="danger"
              link
              size="small"
              @click="handleRefund(row)"
            >
              退款
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="query.page"
        v-model:page-size="query.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        class="pagination"
        @current-change="fetchList"
        @size-change="fetchList(1)"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi, type Order } from '@/api/order'
import { formatDate, formatAmount, getOrderStatusLabel, getOrderStatusType } from '@/utils/format'

const router = useRouter()
const list = ref<Order[]>([])
const total = ref(0)
const loading = ref(false)

const query = reactive({
  page: 1,
  pageSize: 10,
  orderNo: '',
  status: '',
  keyword: ''
})

async function fetchList(page?: number) {
  if (page) query.page = page
  loading.value = true
  try {
    const res = await orderApi.getList({
      page: query.page,
      pageSize: query.pageSize,
      orderNo: query.orderNo || undefined,
      status: query.status || undefined,
      keyword: query.keyword || undefined
    })
    list.value = res.list
    total.value = res.total
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

function handleReset() {
  query.orderNo = ''
  query.status = ''
  query.keyword = ''
  fetchList(1)
}

function goDetail(id: number) {
  router.push(`/order/${id}`)
}

async function handleRefund(row: Order) {
  await ElMessageBox.confirm(
    `确定要对订单 "${row.orderNo}" 执行退款操作吗？`,
    '处理退款',
    { type: 'warning', confirmButtonText: '确定退款' }
  )
  try {
    await orderApi.refund(row.id)
    ElMessage.success('退款操作成功')
    fetchList()
  } catch {
    // ignore
  }
}

function copyText(text: string) {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  })
}

onMounted(() => fetchList())
</script>

<style lang="scss" scoped>
.order-page {
  .filter-card {
    margin-bottom: 16px;

    :deep(.el-card__body) {
      padding: 16px 20px 0;
    }
  }

  .table-card {
    .pagination {
      margin-top: 16px;
      justify-content: flex-end;
    }
  }
}
</style>
