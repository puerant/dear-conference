<template>
  <div class="page-container">
    <div class="page-header">
      <h2>邮件模板</h2>
      <p class="page-desc">管理各场景的邮件内容模板，支持自定义主题、正文及发件账号</p>
    </div>

    <!-- 模板列表 -->
    <el-card shadow="never">
      <el-table :data="templateList" v-loading="loading" border stripe>
        <el-table-column prop="sceneName" label="场景名称" min-width="120" />
        <el-table-column prop="sceneCode" label="场景代码" min-width="160" />
        <el-table-column prop="subject" label="邮件主题" min-width="200" show-overflow-tooltip />
        <el-table-column label="发件账号" min-width="120">
          <template #default="{ row }">
            <span v-if="row.emailConfigId">{{ row.emailConfigName || `账号 #${row.emailConfigId}` }}</span>
            <el-tag v-else type="info" size="small">系统默认</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="row.isEnabled === 1 ? 'success' : 'danger'" size="small">
              {{ row.isEnabled === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="success" @click="openPreview(row)">预览</el-button>
            <el-button link type="warning" @click="openTestSend(row)">测试发送</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑抽屉 -->
    <el-drawer
      v-model="editDrawerVisible"
      :title="`编辑模板 - ${editForm.sceneName}`"
      size="60%"
      destroy-on-close
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="场景代码">
          <el-input :value="editForm.sceneCode" disabled />
        </el-form-item>
        <el-form-item label="发件账号">
          <el-select v-model="editForm.emailConfigId" clearable placeholder="系统默认账号" style="width:100%">
            <el-option
              v-for="cfg in emailConfigOptions"
              :key="cfg.id"
              :label="`${cfg.configName || cfg.fromName} <${cfg.fromEmail}>${cfg.isEnabled === 1 ? '' : '（已禁用）'}`"
              :value="cfg.id"
              :disabled="cfg.isEnabled !== 1"
            />
          </el-select>
          <div class="form-tip">留空则使用系统当前启用的默认账号</div>
        </el-form-item>
        <el-form-item label="邮件主题" prop="subject">
          <el-input v-model="editForm.subject" placeholder="支持 {{变量名}} 占位符" />
        </el-form-item>
        <el-form-item label="邮件正文" prop="body">
          <div class="body-editor-wrap">
            <div class="variables-tip" v-if="editForm.variablesDesc">
              <el-tag
                v-for="(desc, key) in parsedVariables"
                :key="key"
                size="small"
                type="info"
                class="var-tag"
                @click="insertVariable(key)"
              >{{ varPlaceholder(key) }} {{ desc }}</el-tag>
            </div>
            <el-input
              v-model="editForm.body"
              type="textarea"
              :rows="18"
              placeholder="HTML 内容，支持 {{变量名}} 占位符"
              ref="bodyInputRef"
            />
          </div>
        </el-form-item>
        <el-form-item label="启用状态" prop="isEnabled">
          <el-switch
            :model-value="editForm.isEnabled === 1"
            @change="(v: boolean) => editForm.isEnabled = v ? 1 : 0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDrawerVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-drawer>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewDialogVisible" title="邮件预览" width="700px" destroy-on-close>
      <div v-loading="previewing">
        <el-descriptions :column="2" border class="preview-meta">
          <el-descriptions-item label="发件人">
            {{ previewResult.fromName }} &lt;{{ previewResult.fromEmail }}&gt;
          </el-descriptions-item>
          <el-descriptions-item label="主题">{{ previewResult.subject }}</el-descriptions-item>
        </el-descriptions>
        <div class="preview-body" v-html="previewResult.body" />
      </div>
      <template #footer>
        <el-button @click="previewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 测试发送对话框 -->
    <el-dialog v-model="testSendDialogVisible" title="测试发送" width="420px" destroy-on-close>
      <el-form :model="testSendForm" label-width="90px">
        <el-form-item label="收件人">
          <el-input v-model="testSendForm.toEmail" placeholder="输入测试收件人邮箱" />
        </el-form-item>
        <el-divider content-position="left">占位符变量（可选）</el-divider>
        <template v-for="(desc, key) in parsedVariables" :key="key">
          <el-form-item :label="String(key)">
            <el-input v-model="testSendForm.variables[key]" :placeholder="desc" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="testSendDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="testing" @click="handleTestSend">发送</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { systemApi } from '@/api/system'
import type { EmailTemplateItem, EmailTemplateDetail, EmailPreviewVo, EmailConfigItem } from '@/api/system'

// ---- 列表 ----
const loading = ref(false)
const templateList = ref<EmailTemplateItem[]>([])

async function loadList() {
  loading.value = true
  try {
    const data = await systemApi.listEmailTemplates()
    templateList.value = Array.isArray(data) ? data : (data as any)?.list ?? []
  } finally {
    loading.value = false
  }
}

// ---- 邮箱账号选项 ----
const emailConfigOptions = ref<EmailConfigItem[]>([])

async function loadEmailConfigs() {
  try {
    const res = await systemApi.listEmailConfigs()
    emailConfigOptions.value = Array.isArray(res) ? res : []
  } catch { /* ignore */ }
}

// ---- 编辑抽屉 ----
const editDrawerVisible = ref(false)
const saving = ref(false)
const editFormRef = ref<FormInstance>()
const bodyInputRef = ref()

const editForm = reactive({
  sceneCode: '',
  sceneName: '',
  emailConfigId: null as number | null,
  subject: '',
  body: '',
  variablesDesc: '',
  isEnabled: 1,
})

const editRules: FormRules = {
  subject: [{ required: true, message: '请输入邮件主题', trigger: 'blur' }],
  body: [{ required: true, message: '请输入邮件正文', trigger: 'blur' }],
}

const parsedVariables = computed<Record<string, string>>(() => {
  if (!editForm.variablesDesc) return {}
  try {
    return JSON.parse(editForm.variablesDesc)
  } catch {
    return {}
  }
})

function varPlaceholder(key: string) {
  return '{' + '{' + key + '}' + '}'
}

async function openEdit(row: EmailTemplateItem) {
  const detail = await systemApi.getEmailTemplate(row.sceneCode)
  Object.assign(editForm, {
    sceneCode: detail.sceneCode,
    sceneName: detail.sceneName,
    emailConfigId: detail.emailConfigId ?? null,
    subject: detail.subject ?? '',
    body: detail.body ?? '',
    variablesDesc: detail.variablesDesc ?? '',
    isEnabled: detail.isEnabled ?? 1,
  })
  editDrawerVisible.value = true
}

function insertVariable(key: string) {
  const placeholder = `{{${key}}}`
  const textarea = bodyInputRef.value?.$el?.querySelector('textarea')
  if (textarea) {
    const start = textarea.selectionStart
    const end = textarea.selectionEnd
    editForm.body = editForm.body.slice(0, start) + placeholder + editForm.body.slice(end)
  } else {
    editForm.body += placeholder
  }
}

async function handleSave() {
  await editFormRef.value?.validate()
  saving.value = true
  try {
    await systemApi.updateEmailTemplate(editForm.sceneCode, {
      emailConfigId: editForm.emailConfigId,
      subject: editForm.subject,
      body: editForm.body,
      isEnabled: editForm.isEnabled,
    })
    ElMessage.success('保存成功')
    editDrawerVisible.value = false
    await loadList()
  } finally {
    saving.value = false
  }
}

// ---- 预览 ----
const previewDialogVisible = ref(false)
const previewing = ref(false)
const previewSceneCode = ref('')
const previewResult = reactive<EmailPreviewVo>({
  subject: '',
  body: '',
  fromEmail: '',
  fromName: '',
})
const previewVariables = ref<Record<string, string>>({})

async function openPreview(row: EmailTemplateItem) {
  previewSceneCode.value = row.sceneCode

  // 用 variablesDesc 里的 key，填充占位符
  let vars: Record<string, string> = {}
  if (row.variablesDesc) {
    try {
      const desc = JSON.parse(row.variablesDesc) as Record<string, string>
      vars = Object.fromEntries(Object.entries(desc).map(([k, v]) => [k, `[${v}]`]))
    } catch { /* ignore */ }
  }
  previewVariables.value = vars

  previewDialogVisible.value = true
  previewing.value = true
  try {
    const res = await systemApi.previewEmailTemplate(row.sceneCode, { variables: vars })
    Object.assign(previewResult, res)
  } finally {
    previewing.value = false
  }
}

// ---- 测试发送 ----
const testSendDialogVisible = ref(false)
const testing = ref(false)
const testSendSceneCode = ref('')
const testSendForm = reactive<{
  toEmail: string
  variables: Record<string, string>
}>({
  toEmail: '',
  variables: {},
})

function openTestSend(row: EmailTemplateItem) {
  testSendSceneCode.value = row.sceneCode

  // reset
  testSendForm.toEmail = ''
  testSendForm.variables = {}
  if (row.variablesDesc) {
    try {
      const desc = JSON.parse(row.variablesDesc) as Record<string, string>
      Object.keys(desc).forEach(k => { testSendForm.variables[k] = '' })
      // copy parsedVariables to editForm so template #default can use it
      editForm.variablesDesc = row.variablesDesc
    } catch { /* ignore */ }
  }

  testSendDialogVisible.value = true
}

async function handleTestSend() {
  if (!testSendForm.toEmail) {
    ElMessage.warning('请输入收件人邮箱')
    return
  }
  testing.value = true
  try {
    await systemApi.testSendEmailTemplate(testSendSceneCode.value, {
      toEmail: testSendForm.toEmail,
      variables: testSendForm.variables,
    })
    ElMessage.success('测试邮件已发送，请检查收件箱')
    testSendDialogVisible.value = false
  } finally {
    testing.value = false
  }
}

onMounted(() => {
  loadList()
  loadEmailConfigs()
})
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
    color: #1a1a1a;
  }

  .page-desc {
    margin: 0;
    color: #888;
    font-size: 13px;
  }
}

.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.body-editor-wrap {
  width: 100%;

  .variables-tip {
    margin-bottom: 8px;
    display: flex;
    flex-wrap: wrap;
    gap: 6px;

    .var-tag {
      cursor: pointer;

      &:hover {
        opacity: 0.8;
      }
    }
  }
}

.preview-meta {
  margin-bottom: 16px;
}

.preview-body {
  border: 1px solid #e8e8e8;
  border-radius: 6px;
  padding: 16px;
  min-height: 200px;
  background: #fafafa;
}
</style>
