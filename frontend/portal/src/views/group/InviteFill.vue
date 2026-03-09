<template>
  <div class="invite-fill-page">
    <div class="invite-container">
      <!-- Loading state -->
      <div v-if="loading" class="state-card" v-loading="true" style="min-height: 200px" />

      <!-- Error state -->
      <div v-else-if="error" class="state-card">
        <el-result icon="error" :title="error" sub-title="请检查链接是否正确，或联系团体负责人获取新链接" />
      </div>

      <!-- Success submitted state -->
      <div v-else-if="submitted" class="state-card">
        <el-result
          icon="success"
          title="信息提交成功"
          sub-title="入会凭证将通过邮件发送至您填写的邮箱，请注意查收"
        />
      </div>

      <!-- Fill form -->
      <template v-else-if="inviteInfo">
        <div class="invite-header">
          <h2 class="conference-title">学术会议系统</h2>
          <h3 class="group-name">{{ inviteInfo.groupName }}</h3>
          <p class="sub-title">您受邀加入此团体，请填写您的个人信息以获取入会凭证</p>
        </div>

        <el-card class="form-card">
          <el-descriptions :column="1" border class="info-desc">
            <el-descriptions-item label="票务名称">{{ inviteInfo.ticketName }}</el-descriptions-item>
            <el-descriptions-item label="团体联系人">{{ inviteInfo.contactName }}</el-descriptions-item>
            <el-descriptions-item label="剩余名额">
              <el-tag :type="inviteInfo.remainSlots > 0 ? 'success' : 'danger'">
                {{ inviteInfo.remainSlots > 0 ? `${inviteInfo.remainSlots} 个名额` : '名额已满' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>

          <div v-if="inviteInfo.remainSlots <= 0" style="margin-top: 16px">
            <el-alert type="warning" title="该团体名额已全部填写，无法继续提交" :closable="false" show-icon />
          </div>

          <el-form
            v-else
            ref="fillFormRef"
            :model="fillForm"
            :rules="fillRules"
            label-position="top"
            style="margin-top: 20px"
          >
            <el-form-item label="您的姓名" prop="memberName">
              <el-input v-model="fillForm.memberName" placeholder="请输入您的真实姓名" size="large" />
            </el-form-item>
            <el-form-item label="您的邮箱" prop="memberEmail">
              <el-input
                v-model="fillForm.memberEmail"
                placeholder="入会凭证将发送至此邮箱"
                size="large"
                type="email"
              />
            </el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%; margin-top: 8px"
              :loading="submitting"
              @click="handleSubmit"
            >
              提交信息
            </el-button>
          </el-form>
        </el-card>

        <p class="footer-tip">填写后如需修改，请联系团体负责人「{{ inviteInfo.contactName }}」</p>
      </template>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { groupApi } from '@/api/group'
import type { GroupInviteInfoVo } from '@/api/types'

const route = useRoute()
const token = route.params.token as string

const loading = ref(false)
const error = ref<string | null>(null)
const submitted = ref(false)
const inviteInfo = ref<GroupInviteInfoVo | null>(null)

const fillFormRef = ref<FormInstance>()
const fillForm = ref({ memberName: '', memberEmail: '' })
const submitting = ref(false)

const fillRules: FormRules = {
  memberName: [{ required: true, message: '请填写您的姓名', trigger: 'blur' }],
  memberEmail: [
    { required: true, message: '请填写您的邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const loadInviteInfo = async () => {
  loading.value = true
  try {
    inviteInfo.value = await groupApi.getInviteInfo(token)
  } catch (e: any) {
    const msg = e?.response?.data?.message
    error.value = msg === '邀请链接无效或已过期' ? msg : '链接无效或已过期，请联系团体负责人'
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  const valid = await fillFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await groupApi.fillMemberByToken(token, fillForm.value)
    submitted.value = true
  } catch (e: any) {
    const msg = e?.response?.data?.message
    ElMessage.error(msg || '提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

onMounted(loadInviteInfo)
</script>

<style scoped lang="scss">
.invite-fill-page {
  min-height: 100vh;
  background: $bg-secondary;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  padding: $spacing-12 $spacing-4;
}

.invite-container {
  width: 100%;
  max-width: 480px;
}

.state-card {
  background: white;
  border-radius: $radius-xl;
  padding: $spacing-8;
  box-shadow: $shadow-base;
}

.invite-header {
  text-align: center;
  margin-bottom: $spacing-6;

  .conference-title {
    font-size: $font-size-sm;
    color: $text-secondary;
    letter-spacing: 1px;
    margin-bottom: $spacing-2;
  }

  .group-name {
    font-size: $font-size-2xl;
    font-weight: $font-weight-bold;
    color: $text-primary;
    margin-bottom: $spacing-3;
  }

  .sub-title {
    font-size: $font-size-sm;
    color: $text-secondary;
  }
}

.form-card {
  border-radius: $radius-xl;
  box-shadow: $shadow-base;

  .info-desc {
    margin-bottom: $spacing-4;
  }
}

.footer-tip {
  text-align: center;
  margin-top: $spacing-4;
  font-size: $font-size-sm;
  color: $text-secondary;
}
</style>
