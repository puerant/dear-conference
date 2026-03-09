<template>
  <div class="page-container">
    <div class="page-header">
      <h2>邮箱配置</h2>
      <p class="page-desc">支持多个发件账号，模板可按场景绑定指定邮箱</p>
    </div>

    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>邮箱账号列表</span>
          <el-button type="primary" @click="openCreate">新增账号</el-button>
        </div>
      </template>

      <el-table :data="list" border stripe v-loading="loading">
        <el-table-column prop="configName" label="配置名称" min-width="140" />
        <el-table-column prop="host" label="SMTP 服务器" min-width="160" />
        <el-table-column prop="port" label="端口" width="80" />
        <el-table-column prop="fromEmail" label="发件邮箱" min-width="180" />
        <el-table-column label="启用" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isEnabled === 1 ? 'success' : 'info'" size="small">
              {{ row.isEnabled === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="默认" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isDefault === 1 ? 'warning' : 'info'" size="small">
              {{ row.isDefault === 1 ? '默认' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="90" />
        <el-table-column prop="updatedAt" label="更新时间" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" min-width="320" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link :type="row.isEnabled === 1 ? 'warning' : 'success'" @click="toggleEnabled(row)">
              {{ row.isEnabled === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="success" :disabled="row.isDefault === 1 || row.isEnabled !== 1" @click="setDefault(row)">设为默认</el-button>
            <el-button link type="info" @click="openTest(row)">测试发送</el-button>
            <el-button link type="danger" @click="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-drawer v-model="drawerVisible" :title="form.id ? '编辑邮箱配置' : '新增邮箱配置'" size="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="配置名称" prop="configName">
          <el-input v-model="form.configName" placeholder="例如：163 主账号" />
        </el-form-item>
        <el-form-item label="SMTP服务器" prop="host">
          <el-input v-model="form.host" placeholder="smtp.example.com" />
        </el-form-item>
        <el-form-item label="端口" prop="port">
          <el-input-number v-model="form.port" :min="1" :max="65535" />
        </el-form-item>
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="noreply@example.com" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password :placeholder="form.id ? '留空则不修改密码' : '请输入密码'" />
        </el-form-item>
        <el-form-item label="发件人名称" prop="fromName">
          <el-input v-model="form.fromName" />
        </el-form-item>
        <el-form-item label="发件人邮箱" prop="fromEmail">
          <el-input v-model="form.fromEmail" />
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-input-number v-model="form.priority" :min="1" :max="999" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="启用">
          <el-switch v-model="form.sslEnabled" active-text="SSL" inactive-text="SSL" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.isEnabled" active-text="启用" inactive-text="禁用" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="drawerVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-drawer>

    <el-dialog v-model="testVisible" title="测试发送" width="420px">
      <el-form :model="testForm" label-width="90px">
        <el-form-item label="配置">
          <el-input :model-value="testConfigName" disabled />
        </el-form-item>
        <el-form-item label="收件人">
          <el-input v-model="testForm.toEmail" placeholder="请输入收件人邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="testVisible = false">取消</el-button>
        <el-button type="primary" :loading="testing" @click="doTestSend">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { systemApi, type EmailConfigItem } from '@/api/system'

const loading = ref(false)
const saving = ref(false)
const testing = ref(false)
const list = ref<EmailConfigItem[]>([])

const drawerVisible = ref(false)
const formRef = ref<FormInstance>()
const form = reactive({
  id: undefined as number | undefined,
  configName: '',
  host: '',
  port: 465,
  username: '',
  password: '',
  fromName: '',
  fromEmail: '',
  sslEnabled: true,
  isEnabled: true,
  priority: 100,
  remark: ''
})

const rules = computed<FormRules>(() => ({
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  host: [{ required: true, message: '请输入 SMTP 服务器', trigger: 'blur' }],
  port: [{ required: true, message: '请输入端口', trigger: 'blur' }],
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: !form.id, message: '请输入密码', trigger: 'blur' }],
  fromName: [{ required: true, message: '请输入发件人名称', trigger: 'blur' }],
  fromEmail: [
    { required: true, message: '请输入发件人邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确邮箱格式', trigger: 'blur' }
  ]
}))

const testVisible = ref(false)
const testConfigId = ref<number | null>(null)
const testConfigName = ref('')
const testForm = reactive({
  toEmail: ''
})

async function loadList() {
  loading.value = true
  try {
    const res = await systemApi.listEmailConfigs()
    list.value = Array.isArray(res) ? res : []
  } finally {
    loading.value = false
  }
}

function resetForm() {
  form.id = undefined
  form.configName = ''
  form.host = ''
  form.port = 465
  form.username = ''
  form.password = ''
  form.fromName = ''
  form.fromEmail = ''
  form.sslEnabled = true
  form.isEnabled = true
  form.priority = 100
  form.remark = ''
}

function openCreate() {
  resetForm()
  drawerVisible.value = true
}

function openEdit(row: EmailConfigItem) {
  form.id = row.id
  form.configName = row.configName
  form.host = row.host
  form.port = row.port
  form.username = row.username
  form.password = ''
  form.fromName = row.fromName
  form.fromEmail = row.fromEmail
  form.sslEnabled = row.sslEnabled === 1
  form.isEnabled = row.isEnabled === 1
  form.priority = row.priority
  form.remark = row.remark || ''
  drawerVisible.value = true
}

async function submit() {
  const ok = await formRef.value?.validate().catch(() => false)
  if (!ok) return
  saving.value = true
  try {
    const payload = {
      configName: form.configName,
      host: form.host,
      port: form.port,
      username: form.username,
      password: form.password,
      fromName: form.fromName,
      fromEmail: form.fromEmail,
      sslEnabled: form.sslEnabled ? 1 : 0,
      isEnabled: form.isEnabled ? 1 : 0,
      priority: form.priority,
      remark: form.remark
    }
    if (form.id) {
      await systemApi.updateEmailConfigById(form.id, payload)
    } else {
      await systemApi.createEmailConfig(payload)
    }
    ElMessage.success('保存成功')
    drawerVisible.value = false
    await loadList()
  } finally {
    saving.value = false
  }
}

async function toggleEnabled(row: EmailConfigItem) {
  await systemApi.updateEmailConfigEnabled(row.id, row.isEnabled === 1 ? 0 : 1)
  ElMessage.success('状态已更新')
  await loadList()
}

async function setDefault(row: EmailConfigItem) {
  await systemApi.setDefaultEmailConfig(row.id)
  ElMessage.success('已设为默认')
  await loadList()
}

async function remove(row: EmailConfigItem) {
  await ElMessageBox.confirm(`确认删除邮箱配置「${row.configName}」？`, '提示', { type: 'warning' })
  await systemApi.deleteEmailConfig(row.id)
  ElMessage.success('删除成功')
  await loadList()
}

function openTest(row: EmailConfigItem) {
  testConfigId.value = row.id
  testConfigName.value = `${row.configName} <${row.fromEmail}>`
  testForm.toEmail = ''
  testVisible.value = true
}

async function doTestSend() {
  if (!testConfigId.value) return
  if (!testForm.toEmail) {
    ElMessage.warning('请输入收件人邮箱')
    return
  }
  testing.value = true
  try {
    await systemApi.testEmailByConfig(testConfigId.value, testForm.toEmail)
    ElMessage.success('测试邮件已发送，请检查收件箱')
    testVisible.value = false
  } finally {
    testing.value = false
  }
}

onMounted(loadList)
</script>

<style lang="scss" scoped>
.page-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;

  h2 {
    margin: 0 0 6px;
    font-size: 20px;
    font-weight: 600;
  }

  .page-desc {
    margin: 0;
    color: #888;
    font-size: 13px;
  }
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>

