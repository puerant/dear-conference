<template>
  <div class="conference-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>会议信息管理</span>
          <el-button type="primary" @click="handleSave" :loading="saving">
            保存
          </el-button>
        </div>
      </template>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-width="120px"
        v-loading="loading"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="会议标题" prop="title">
              <el-input v-model="formData.title" placeholder="请输入会议标题" maxlength="200" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="副标题" prop="subtitle">
              <el-input v-model="formData.subtitle" placeholder="请输入副标题" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="会议描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="4"
            placeholder="请输入会议描述"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="formData.startDate"
                type="date"
                placeholder="选择开始日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="formData.endDate"
                type="date"
                placeholder="选择结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="会议地点" prop="location">
              <el-input v-model="formData.location" placeholder="请输入会议地点" maxlength="200" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="详细地址" prop="address">
              <el-input v-model="formData.address" placeholder="请输入详细地址" maxlength="500" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="横幅图片" prop="bannerUrl">
          <el-input v-model="formData.bannerUrl" placeholder="请输入横幅图片URL" />
        </el-form-item>

        <el-divider content-position="left">联系信息</el-divider>

        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="formData.contactName" placeholder="请输入联系人" maxlength="100" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="联系邮箱" prop="contactEmail">
              <el-input v-model="formData.contactEmail" placeholder="请输入联系邮箱" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="是否发布" prop="isPublished">
          <el-switch
            v-model="formData.isPublished"
            :active-value="1"
            :inactive-value="0"
            active-text="已发布"
            inactive-text="未发布"
          />
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getConferenceInfo, saveConferenceInfo } from '@/api/conference'
import type { ConferenceInfoDto } from '@/api/conference'

const loading = ref(false)
const saving = ref(false)
const formRef = ref<FormInstance>()

const formData = reactive<ConferenceInfoDto>({
  title: '',
  subtitle: '',
  description: '',
  startDate: '',
  endDate: '',
  location: '',
  address: '',
  bannerUrl: '',
  contactName: '',
  contactPhone: '',
  contactEmail: '',
  isPublished: 0
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入会议标题', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

const loadConferenceInfo = async () => {
  try {
    loading.value = true
    const data = await getConferenceInfo()
    if (data) {
      Object.assign(formData, data)
    }
  } catch (error) {
    console.error('加载会议信息失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSave = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      saving.value = true
      await saveConferenceInfo(formData)
      ElMessage.success('保存成功')
      loadConferenceInfo()
    } catch (error: any) {
      ElMessage.error(error.message || '保存失败')
    } finally {
      saving.value = false
    }
  })
}

onMounted(() => {
  loadConferenceInfo()
})
</script>

<style scoped>
.conference-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
