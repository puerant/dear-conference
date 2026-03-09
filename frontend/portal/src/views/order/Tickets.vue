<template>
  <div class="tickets-page">
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">{{ t('order.tickets') }}</h1>
      </div>
    </section>

    <div class="container page-body">
      <div v-loading="loading" class="tickets-grid">
        <div v-for="ticket in tickets" :key="ticket.id" class="ticket-card">
          <div class="ticket-header">
            <div class="ticket-top-row">
              <h3 class="ticket-name">{{ ticket.name }}</h3>
              <el-tag :type="ticket.ticketType === 2 ? 'warning' : 'success'" size="small">
                {{ ticket.ticketType === 2 ? '团体票' : '个人票' }}
              </el-tag>
            </div>
            <div class="ticket-price">
              <span class="currency">¥</span>
              <span class="amount">{{ ticket.price.toFixed(2) }}</span>
              <span v-if="ticket.ticketType === 2" class="per-person">/人</span>
            </div>
          </div>

          <div class="ticket-body">
            <p class="ticket-desc">{{ ticket.description }}</p>
            <div v-if="ticket.ticketType === 2" class="ticket-group-info">
              <el-icon><user /></el-icon>
              <span>
                {{ ticket.minPersons ? `最少 ${ticket.minPersons} 人` : '' }}
                {{ ticket.minPersons && ticket.maxPersons ? ' ~ ' : '' }}
                {{ ticket.maxPersons ? `最多 ${ticket.maxPersons} 人` : '' }}
              </span>
            </div>
            <div class="ticket-stock">
              <el-icon><goods /></el-icon>
              <span>剩余: {{ ticket.stock - ticket.soldCount }} 张</span>
            </div>
          </div>

          <div class="ticket-footer">
            <el-button
              type="primary"
              style="width: 100%"
              :disabled="ticket.stock - ticket.soldCount <= 0"
              @click="openBuyDialog(ticket)"
            >
              {{ ticket.stock - ticket.soldCount > 0 ? t('order.buy') : '已售罄' }}
            </el-button>
          </div>
        </div>
      </div>

      <el-empty v-if="!loading && tickets.length === 0" description="暂无可购买的票务" />
    </div>

    <!-- Individual ticket buy dialog -->
    <el-dialog v-model="individualDialogVisible" title="确认购票（个人票）" width="440px">
      <div v-if="selectedTicket" class="buy-info">
        <p><strong>票务名称：</strong>{{ selectedTicket.name }}</p>
        <p><strong>价格：</strong>¥{{ selectedTicket.price.toFixed(2) }}</p>
        <p class="hint-text">姓名和邮箱将自动使用您注册时填写的信息</p>
      </div>
      <template #footer>
        <el-button @click="individualDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="buying" @click="handleBuyIndividual">确认购买</el-button>
      </template>
    </el-dialog>

    <!-- Group ticket buy dialog -->
    <el-dialog v-model="groupDialogVisible" title="购买团体票" width="500px">
      <div v-if="selectedTicket" class="buy-info">
        <p><strong>票务名称：</strong>{{ selectedTicket.name }}</p>
        <p><strong>单价：</strong>¥{{ selectedTicket.price.toFixed(2) }} / 人</p>
        <p v-if="selectedTicket.minPersons || selectedTicket.maxPersons" class="hint-text">
          购买人数范围：{{ selectedTicket.minPersons ?? 1 }} ~
          {{ selectedTicket.maxPersons ? selectedTicket.maxPersons + ' 人' : '不限' }}
        </p>
      </div>
      <el-form ref="groupFormRef" :model="groupForm" :rules="groupRules" label-position="top" style="margin-top: 16px">
        <el-form-item label="团体名称" prop="groupName">
          <el-input v-model="groupForm.groupName" placeholder="请输入团体/单位名称" />
        </el-form-item>
        <el-form-item label="购买人数" prop="quantity">
          <el-input-number
            v-model="groupForm.quantity"
            :min="selectedTicket?.minPersons ?? 1"
            :max="selectedTicket?.maxPersons ?? 999"
            style="width: 160px"
          />
          <span style="margin-left: 12px; color: #7f8c8d">
            总计: ¥{{ groupTotalPrice }}
          </span>
        </el-form-item>
      </el-form>
      <el-alert
        type="info"
        :closable="false"
        show-icon
        style="margin-top: 8px"
        description="支付完成后，您可在我的订单中管理成员信息，或通过邀请链接让成员自助填写"
      />
      <template #footer>
        <el-button @click="groupDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="buying" @click="handleBuyGroup">确认购买</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { orderApi } from '@/api/order'
import type { Ticket } from '@/api/types'

const router = useRouter()
const { t } = useI18n()

const loading = ref(false)
const tickets = ref<Ticket[]>([])
const buying = ref(false)
const selectedTicket = ref<Ticket | null>(null)

// Individual ticket dialog
const individualDialogVisible = ref(false)

// Group ticket dialog
const groupDialogVisible = ref(false)
const groupFormRef = ref<FormInstance>()
const groupForm = ref({ groupName: '', quantity: 1 })

const groupTotalPrice = computed(() => {
  if (!selectedTicket.value) return '0.00'
  return (selectedTicket.value.price * groupForm.value.quantity).toFixed(2)
})

const groupRules: FormRules = {
  groupName: [{ required: true, message: '请填写团体名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请填写购买人数', trigger: 'change' }]
}

const openBuyDialog = (ticket: Ticket) => {
  selectedTicket.value = ticket
  if (ticket.ticketType === 2) {
    groupForm.value = {
      groupName: '',
      quantity: ticket.minPersons ?? 1
    }
    groupDialogVisible.value = true
  } else {
    individualDialogVisible.value = true
  }
}

const handleBuyIndividual = async () => {
  if (!selectedTicket.value) return
  buying.value = true
  try {
    await orderApi.createOrder({
      ticketId: selectedTicket.value.id,
      orderType: 1
    })
    ElMessage.success('订单创建成功！')
    individualDialogVisible.value = false
    router.push({ name: 'OrderList' })
  } catch {
    ElMessage.error('购买失败，请稍后重试')
  } finally {
    buying.value = false
  }
}

const handleBuyGroup = async () => {
  const valid = await groupFormRef.value?.validate().catch(() => false)
  if (!valid || !selectedTicket.value) return

  buying.value = true
  try {
    await orderApi.createOrder({
      ticketId: selectedTicket.value.id,
      orderType: 2,
      quantity: groupForm.value.quantity,
      groupName: groupForm.value.groupName
    })
    ElMessage.success('团体订单创建成功，请前往订单列表完成支付！')
    groupDialogVisible.value = false
    router.push({ name: 'OrderList' })
  } catch {
    ElMessage.error('购买失败，请稍后重试')
  } finally {
    buying.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try {
    tickets.value = await orderApi.getTickets()
  } catch {
    ElMessage.error('加载票务失败')
  } finally {
    loading.value = false
  }
})
</script>

<style scoped lang="scss">
.page-body {
  padding: $spacing-10 $spacing-6;
}

.tickets-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-8;

  @include respond-to(lg) { grid-template-columns: repeat(2, 1fr); }
  @include respond-to(sm) { grid-template-columns: 1fr; }
}

.ticket-card {
  background: white;
  border-radius: $radius-xl;
  box-shadow: $shadow-base;
  overflow: hidden;
  transition: all 0.3s;
  border: 2px solid transparent;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $shadow-lg;
    border-color: $primary-lighter;
  }

  .ticket-header {
    background: $gradient-primary;
    color: white;
    padding: $spacing-6 $spacing-6 $spacing-5;

    .ticket-top-row {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      margin-bottom: $spacing-4;

      .ticket-name {
        font-size: $font-size-xl;
        font-weight: $font-weight-bold;
        flex: 1;
        margin-right: $spacing-2;
      }
    }

    .ticket-price {
      .currency { font-size: $font-size-xl; }
      .amount { font-size: 40px; font-weight: $font-weight-bold; margin-left: 4px; }
      .per-person { font-size: $font-size-base; margin-left: 4px; opacity: 0.85; }
    }
  }

  .ticket-body {
    padding: $spacing-6;
    min-height: 120px;

    .ticket-desc {
      color: $text-secondary;
      line-height: $line-height-relaxed;
      margin-bottom: $spacing-4;
    }

    .ticket-group-info,
    .ticket-stock {
      display: flex;
      align-items: center;
      gap: $spacing-2;
      font-size: $font-size-sm;
      color: $text-secondary;
      margin-bottom: $spacing-2;
    }
  }

  .ticket-footer {
    padding: $spacing-4 $spacing-6 $spacing-6;
  }
}

.buy-info {
  background: $bg-secondary;
  padding: $spacing-4;
  border-radius: $radius-base;

  p {
    margin-bottom: $spacing-2;
    font-size: $font-size-base;
    color: $text-primary;

    &:last-child { margin-bottom: 0; }
  }

  .hint-text {
    color: $text-secondary;
    font-size: $font-size-sm;
    margin-top: $spacing-2;
  }
}
</style>
