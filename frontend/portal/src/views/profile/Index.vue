<template>
  <div class="profile-page">
    <section class="page-header-section">
      <div class="container">
        <h1 class="page-title">{{ t('profile.title') }}</h1>
      </div>
    </section>

    <div class="container page-body">
      <div class="profile-layout">
        <!-- User card -->
        <el-card class="user-card">
          <div class="avatar-section">
            <el-avatar :size="80" :src="authStore.userInfo?.avatar || undefined">
              {{ authStore.userName.charAt(0) }}
            </el-avatar>
            <h3 class="user-name">{{ authStore.userName }}</h3>
            <el-tag>{{ roleLabel }}</el-tag>
            <p class="user-email">{{ authStore.userInfo?.email }}</p>
          </div>
        </el-card>

        <!-- Info form -->
        <div class="form-area">
          <el-card>
            <template #header>{{ t('profile.basicInfo') }}</template>
            <el-form
              ref="infoFormRef"
              :model="infoForm"
              :rules="infoRules"
              label-position="top"
              @submit.prevent="saveInfo"
            >
              <el-form-item label="姓名" prop="name">
                <el-input v-model="infoForm.name" />
              </el-form-item>
              <el-form-item label="邮箱">
                <el-input :value="authStore.userInfo?.email" disabled />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="savingInfo" native-type="submit">
                  {{ t('profile.save') }}
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <el-card style="margin-top: 24px">
            <template #header>{{ t('profile.changePassword') }}</template>
            <el-form
              ref="pwdFormRef"
              :model="pwdForm"
              :rules="pwdRules"
              label-position="top"
              @submit.prevent="changePassword"
            >
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input v-model="pwdForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="pwdForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认新密码" prop="confirmPassword">
                <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="changingPwd" native-type="submit">修改密码</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { authApi } from '@/api/auth'
import { useAuthStore } from '@/stores/auth'
import { getRoleLabel } from '@/utils/format'

const { t } = useI18n()
const authStore = useAuthStore()

const roleLabel = computed(() => getRoleLabel(authStore.userRole))

const infoFormRef = ref<FormInstance>()
const savingInfo = ref(false)
const infoForm = ref({ name: authStore.userInfo?.name || '' })
const infoRules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const saveInfo = async () => {
  const valid = await infoFormRef.value?.validate().catch(() => false)
  if (!valid) return

  savingInfo.value = true
  try {
    await authApi.updateProfile({ name: infoForm.value.name })
    if (authStore.userInfo) {
      authStore.setUserInfo({ ...authStore.userInfo, name: infoForm.value.name })
    }
    ElMessage.success('信息更新成功')
  } catch {
    // 错误提示由 Axios 响应拦截器统一处理
  } finally {
    savingInfo.value = false
  }
}

const pwdFormRef = ref<FormInstance>()
const changingPwd = ref(false)
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const pwdRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (_rule, value, callback) => {
        if (value !== pwdForm.value.newPassword) callback(new Error('两次密码不一致'))
        else callback()
      },
      trigger: 'blur'
    }
  ]
}

const changePassword = async () => {
  const valid = await pwdFormRef.value?.validate().catch(() => false)
  if (!valid) return

  changingPwd.value = true
  try {
    await authApi.changePassword({
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword
    })
    ElMessage.success('密码修改成功')
    pwdForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch {
    // 错误提示由 Axios 响应拦截器统一处理
  } finally {
    changingPwd.value = false
  }
}
</script>

<style scoped lang="scss">
.page-body {
  padding: $spacing-10 $spacing-6;
}

.profile-layout {
  display: grid;
  grid-template-columns: 260px 1fr;
  gap: $spacing-6;
  align-items: start;

  @include respond-to(md) {
    grid-template-columns: 1fr;
  }
}

.user-card {
  .avatar-section {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $spacing-3;
    padding: $spacing-4 0;

    .user-name {
      font-size: $font-size-xl;
      font-weight: $font-weight-bold;
      color: $text-primary;
    }

    .user-email {
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }
}
</style>
