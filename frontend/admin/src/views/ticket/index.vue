<template>
  <div class="ticket-page">
    <el-card class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList(1)">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Plus" @click="openCreate">创建票务</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="票务名称" min-width="160" show-overflow-tooltip />
        <el-table-column label="价格" width="100">
          <template #default="{ row }">{{ formatAmount(row.price) }}</template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="90" align="center" />
        <el-table-column prop="soldCount" label="已售" width="90" align="center" />
        <el-table-column label="可用" width="90" align="center">
          <template #default="{ row }">{{ row.stock - row.soldCount }}</template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="票种" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.ticketType === 2 ? 'warning' : 'success'" size="small">
              {{ row.ticketType === 2 ? '团体票' : '个人票' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEdit(row)">编辑</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button type="info" link size="small" @click="openAdjustStock(row)">调整库存</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
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

    <!-- Create/Edit Dialog -->
    <el-dialog
      v-model="formVisible"
      :title="formMode === 'create' ? '创建票务' : '编辑票务'"
      width="520px"
      @close="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="票务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入票务名称" />
        </el-form-item>
        <el-form-item label="票种类型" prop="ticketType">
          <el-radio-group v-model="form.ticketType">
            <el-radio :value="1">个人票</el-radio>
            <el-radio :value="2">团体票</el-radio>
          </el-radio-group>
        </el-form-item>
        <template v-if="form.ticketType === 2">
          <el-form-item label="最少人数">
            <el-input-number v-model="form.minPersons" :min="1" :max="999" style="width: 100%" placeholder="不设下限" />
          </el-form-item>
          <el-form-item label="最多人数">
            <el-input-number v-model="form.maxPersons" :min="1" :max="9999" style="width: 100%" placeholder="不设上限" />
          </el-form-item>
        </template>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存" prop="stock">
          <el-input-number v-model="form.stock" :min="0" :step="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请输入票务描述（支持Markdown）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- Adjust Stock Dialog -->
    <el-dialog v-model="stockVisible" title="调整库存" width="420px">
      <div v-if="stockTarget" class="stock-info">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="票务名称" :span="2">{{ stockTarget.name }}</el-descriptions-item>
          <el-descriptions-item label="当前库存">{{ stockTarget.stock }}</el-descriptions-item>
          <el-descriptions-item label="已售数量">{{ stockTarget.soldCount }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <el-divider />
      <el-form label-width="80px">
        <el-form-item label="调整类型">
          <el-radio-group v-model="stockForm.type">
            <el-radio value="increase">增加库存</el-radio>
            <el-radio value="decrease">减少库存</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="调整数量">
          <el-input-number v-model="stockForm.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input v-model="stockForm.reason" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockVisible = false">取消</el-button>
        <el-button type="primary" :loading="stockSubmitting" @click="handleAdjustStock">确认调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { ticketApi, type Ticket } from '@/api/ticket'
import { formatDate, formatAmount } from '@/utils/format'

const list = ref<Ticket[]>([])
const total = ref(0)
const loading = ref(false)
const formVisible = ref(false)
const stockVisible = ref(false)
const submitting = ref(false)
const stockSubmitting = ref(false)
const formMode = ref<'create' | 'edit'>('create')
const editId = ref<number | null>(null)
const stockTarget = ref<Ticket | null>(null)
const formRef = ref<FormInstance>()

const query = reactive({
  page: 1,
  pageSize: 10,
  status: undefined as number | undefined
})

const form = reactive({
  name: '',
  price: 0,
  stock: 0,
  description: '',
  ticketType: 1 as number,
  minPersons: null as number | null,
  maxPersons: null as number | null
})

const stockForm = reactive({
  type: 'increase',
  quantity: 1,
  reason: ''
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入票务名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }]
}

async function fetchList(page?: number) {
  if (page) query.page = page
  loading.value = true
  try {
    const res = await ticketApi.getList({
      page: query.page,
      pageSize: query.pageSize,
      status: query.status
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
  query.status = undefined
  fetchList(1)
}

function openCreate() {
  formMode.value = 'create'
  editId.value = null
  resetForm()
  formVisible.value = true
}

function openEdit(row: Ticket) {
  formMode.value = 'edit'
  editId.value = row.id
  form.name = row.name
  form.price = row.price
  form.stock = row.stock
  form.description = row.description
  form.ticketType = row.ticketType ?? 1
  form.minPersons = row.minPersons ?? null
  form.maxPersons = row.maxPersons ?? null
  formVisible.value = true
}

function resetForm() {
  form.name = ''
  form.price = 0
  form.stock = 0
  form.description = ''
  form.ticketType = 1
  form.minPersons = null
  form.maxPersons = null
  formRef.value?.clearValidate()
}

async function handleSubmit() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (formMode.value === 'create') {
      await ticketApi.create(form)
      ElMessage.success('创建成功')
    } else {
      await ticketApi.update(editId.value!, form)
      ElMessage.success('更新成功')
    }
    formVisible.value = false
    fetchList()
  } catch {
    // ignore
  } finally {
    submitting.value = false
  }
}

async function handleToggleStatus(row: Ticket) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '上架' : '下架'
  await ElMessageBox.confirm(`确定要${action}票务 "${row.name}" 吗？`, '提示', { type: 'warning' })
  try {
    await ticketApi.updateStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    fetchList()
  } catch {
    // ignore
  }
}

function openAdjustStock(row: Ticket) {
  stockTarget.value = row
  stockForm.type = 'increase'
  stockForm.quantity = 1
  stockForm.reason = ''
  stockVisible.value = true
}

async function handleAdjustStock() {
  if (!stockTarget.value) return
  const delta = stockForm.type === 'increase' ? stockForm.quantity : -stockForm.quantity
  const newStock = stockTarget.value.stock + delta
  stockSubmitting.value = true
  try {
    await ticketApi.adjustStock(stockTarget.value.id, newStock)
    ElMessage.success('库存调整成功')
    stockVisible.value = false
    fetchList()
  } catch {
    // ignore
  } finally {
    stockSubmitting.value = false
  }
}

async function handleDelete(row: Ticket) {
  await ElMessageBox.confirm(`确定要删除票务 "${row.name}" 吗？此操作不可恢复！`, '警告', {
    type: 'error',
    confirmButtonText: '删除',
    confirmButtonClass: 'el-button--danger'
  })
  try {
    await ticketApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    // ignore
  }
}

onMounted(() => fetchList())
</script>

<style lang="scss" scoped>
.ticket-page {
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

  .stock-info {
    margin-bottom: 8px;
  }
}
</style>
