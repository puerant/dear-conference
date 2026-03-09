<template>
  <div class="credential-config-page">
    <el-card>
      <template #header>
        <span>凭证配置</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" v-loading="loading">
        <el-form-item label="凭证模板ID" prop="templateId">
          <el-input v-model="form.templateId" placeholder="default" />
        </el-form-item>
        <el-form-item label="背景图片">
          <el-input v-model="form.backgroundImage" placeholder="https://example.com/bg.jpg" />
        </el-form-item>
        <el-form-item label="水印文字">
          <el-input v-model="form.watermarkText" placeholder="会务通内部资料" />
        </el-form-item>
        <el-form-item label="水印透明度">
          <el-slider v-model="form.watermarkOpacity" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item label="水印X坐标">
          <el-input-number v-model="form.watermarkX" :min="0" />
        </el-form-item>
        <el-form-item label="水印Y坐标">
          <el-input-number v-model="form.watermarkY" :min="0" />
        </el-form-item>
        <el-form-item label="凭证有效期" prop="expiryDays">
          <el-input-number v-model="form.expiryDays" :min="1" :max="365" />
          <span style="margin-left: 10px">天</span>
        </el-form-item>
        <el-form-item label="二维码位置">
          <el-select v-model="form.qrCodePosition" style="width: 200px">
            <el-option label="左上角" value="top_left" />
            <el-option label="右上角" value="top_right" />
            <el-option label="左下角" value="bottom_left" />
            <el-option label="右下角" value="bottom_right" />
            <el-option label="居中" value="center" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { systemApi, type CredentialConfig } from '@/api/system'

const formRef = ref<FormInstance>()
const loading = ref(false)
const saving = ref(false)

const form = reactive<CredentialConfig>({
  templateId: 'default',
  backgroundImage: '',
  watermarkText: '',
  watermarkOpacity: 20,
  watermarkX: 50,
  watermarkY: 50,
  expiryDays: 30,
  qrCodePosition: 'bottom_right'
})

const rules: FormRules = {
  templateId: [{ required: true, message: '请输入凭证模板ID', trigger: 'blur' }],
  expiryDays: [{ required: true, message: '请输入凭证有效期', trigger: 'blur' }]
}

async function loadConfig() {
  loading.value = true
  try {
    const res = await systemApi.getSystemConfig()
    if (res.credentialConfig) {
      Object.assign(form, res.credentialConfig)
    }
  } catch {
    ElMessage.error('加载配置失败')
  } finally {
    loading.value = false
  }
}

async function handleSave() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    await systemApi.updateCredentialConfig(form)
    ElMessage.success('保存成功')
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadConfig()
})
</script>

<style lang="scss" scoped>
.credential-config-page {
  .el-form {
    max-width: 600px;
  }
}
</style>
