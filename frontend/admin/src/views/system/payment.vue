<template>
  <div class="payment-config-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>支付配置</span>
          <el-button type="primary" :icon="Plus" @click="openDialog()">添加配置</el-button>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="paymentType" label="支付类型" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.paymentType === 1" type="success">微信支付</el-tag>
            <el-tag v-else-if="row.paymentType === 2" type="primary">支付宝</el-tag>
            <el-tag v-else type="info">其他</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="appId" label="应用ID" min-width="150" />
        <el-table-column prop="merchantId" label="商户号" min-width="150" />
        <el-table-column prop="notifyUrl" label="回调地址" min-width="200" />
        <el-table-column label="沙盒模式" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.sandboxMode ? 'warning' : 'success'">
              {{ row.sandboxMode ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isEnabled ? 'success' : 'danger'">
              {{ row.isEnabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openDialog(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 配置对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑配置' : '添加配置'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="支付类型" prop="paymentType">
          <el-select v-model="form.paymentType" style="width: 100%" :disabled="!!editingId">
            <el-option :value="1" label="微信支付" />
            <el-option :value="2" label="支付宝" />
          </el-select>
        </el-form-item>
        <el-form-item label="应用ID" prop="appId">
          <el-input v-model="form.appId" placeholder="请输入应用ID" />
        </el-form-item>
        <el-form-item label="应用密钥" prop="appSecret">
          <el-input v-model="form.appSecret" type="password" show-password placeholder="请输入应用密钥" />
        </el-form-item>
        <el-form-item label="商户号" prop="merchantId">
          <el-input v-model="form.merchantId" placeholder="请输入商户号" />
        </el-form-item>
        <el-form-item label="API地址" prop="apiUrl">
          <el-input v-model="form.apiUrl" placeholder="请输入API地址" />
        </el-form-item>
        <el-form-item label="回调地址" prop="notifyUrl">
          <el-input v-model="form.notifyUrl" placeholder="/api/payment/callback/wechat" />
        </el-form-item>
        <el-form-item label="沙盒模式">
          <el-switch v-model="form.sandboxMode" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.isEnabled" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { systemApi, type PaymentConfig } from '@/api/system'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const tableData = ref<PaymentConfig[]>([])

const formRef = ref<FormInstance>()
const form = reactive<PaymentConfig>({
  paymentType: 1,
  appId: '',
  appSecret: '',
  merchantId: '',
  apiUrl: '',
  notifyUrl: '',
  sandboxMode: true,
  isEnabled: false
})

const rules: FormRules = {
  paymentType: [{ required: true, message: '请选择支付类型', trigger: 'change' }],
  appId: [{ required: true, message: '请输入应用ID', trigger: 'blur' }],
  appSecret: [{ required: true, message: '请输入应用密钥', trigger: 'blur' }],
  merchantId: [{ required: true, message: '请输入商户号', trigger: 'blur' }],
  apiUrl: [{ required: true, message: '请输入API地址', trigger: 'blur' }],
  notifyUrl: [{ required: true, message: '请输入回调地址', trigger: 'blur' }]
}

async function loadData() {
  loading.value = true
  try {
    const res = await systemApi.getSystemConfig()
    tableData.value = res.paymentConfig || []
  } catch {
    ElMessage.error('加载数据失败')
  } finally {
    loading.value = false
  }
}

function openDialog(row?: PaymentConfig) {
  if (row) {
    editingId.value = row.id || null
    Object.assign(form, row)
    form.appSecret = '******'
  } else {
    editingId.value = null
    Object.assign(form, {
      paymentType: 1,
      appId: '',
      appSecret: '',
      merchantId: '',
      apiUrl: '',
      notifyUrl: '',
      sandboxMode: true,
      isEnabled: false
    })
  }
  dialogVisible.value = true
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    await systemApi.updatePaymentConfig(form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function handleDelete(row: PaymentConfig) {
  if (!row.id) return
  await ElMessageBox.confirm('确定要删除该支付配置吗？', '提示', { type: 'warning' })
  try {
    await systemApi.deletePaymentConfig(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.payment-config-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
ENDOFFILE
