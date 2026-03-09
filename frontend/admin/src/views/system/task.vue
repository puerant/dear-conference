<template>
  <div class="task-config-page">
    <el-card>
      <template #header>
        <span>定时任务配置</span>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="150px" v-loading="loading">
        <el-form-item label="订单超时时间" prop="timeoutMinutes">
          <el-input-number v-model="form.timeoutMinutes" :min="1" :max="1440" />
          <span style="margin-left: 10px">分钟（默认30分钟）</span>
        </el-form-item>
        <el-form-item label="检查间隔" prop="checkIntervalMinutes">
          <el-input-number v-model="form.checkIntervalMinutes" :min="1" :max="60" />
          <span style="margin-left: 10px">分钟（默认5分钟）</span>
        </el-form-item>
        <el-form-item label="超时取消任务">
          <el-switch v-model="form.cancelTaskEnabled" />
          <span style="margin-left: 10px; color: #909399">自动取消超时未支付的订单</span>
        </el-form-item>
        <el-form-item label="退款检查任务">
          <el-switch v-model="form.refundTaskEnabled" />
          <span style="margin-left: 10px; color: #909399">定期检查退款状态</span>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSave" :loading="saving">保存配置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top: 20px">
      <template #header>
        <span>任务说明</span>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="超时取消任务">
          定时扫描未支付订单，超过设定时间后自动取消订单
        </el-descriptions-item>
        <el-descriptions-item label="退款检查任务">
          定时检查处理中的退款记录，更新退款状态
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { systemApi, type OrderTaskConfig } from '@/api/system'

const formRef = ref<FormInstance>()
const loading = ref(false)
const saving = ref(false)

const form = reactive<OrderTaskConfig>({
  timeoutMinutes: 30,
  checkIntervalMinutes: 5,
  cancelTaskEnabled: true,
  refundTaskEnabled: true
})

const rules: FormRules ={
  timeoutMinutes: [{ required: true, message: '请输入订单超时时间', trigger: 'blur' }],
  checkIntervalMinutes: [{ required: true, message: '请输入检查间隔', trigger: 'blur' }]
}

async function loadConfig() {
  loading.value = true
  try {
    const res = await systemApi.getSystemConfig()
    if (res.orderTaskConfig) {
      Object.assign(form, res.orderTaskConfig)
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
    await systemApi.updateOrderTaskConfig(form)
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
.task-config-page {
  .el-form {
    max-width: 600px;
  }
}
</style>
