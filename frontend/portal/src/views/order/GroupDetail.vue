<template>
  <div class="group-detail-page">
    <section class="page-header-section">
      <div class="container">
        <el-button :icon="ArrowLeft" text @click="router.push({ name: 'OrderList' })">返回订单列表</el-button>
        <h1 class="page-title">团体订单管理</h1>
      </div>
    </section>

    <div class="container page-body" v-loading="loading">
      <template v-if="groupDetail">
        <!-- Basic info card -->
        <el-card class="info-card">
          <template #header>
            <span class="card-title">团体信息</span>
          </template>
          <el-descriptions :column="2" border>
            <el-descriptions-item label="团体名称">{{ groupDetail.groupName }}</el-descriptions-item>
            <el-descriptions-item label="联系人">{{ groupDetail.contactName }}</el-descriptions-item>
            <el-descriptions-item label="联系邮箱">{{ groupDetail.contactEmail }}</el-descriptions-item>
            <el-descriptions-item label="成员进度">
              <el-progress
                :percentage="Math.round(groupDetail.filledCount / groupDetail.totalCount * 100)"
                :status="groupDetail.filledCount === groupDetail.totalCount ? 'success' : ''"
              />
              {{ groupDetail.filledCount }} / {{ groupDetail.totalCount }} 人已填写
            </el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- Invite link card -->
        <el-card class="info-card">
          <template #header>
            <span class="card-title">邀请链接</span>
          </template>
          <div v-if="inviteUrl" class="invite-url-row">
            <el-input v-model="inviteUrl" readonly style="flex: 1" />
            <el-button type="primary" @click="copyInviteUrl">复制链接</el-button>
          </div>
          <el-button v-else type="primary" :loading="generatingInvite" @click="generateInviteUrl">
            生成邀请链接
          </el-button>
          <p class="hint-text">成员可通过该链接自助填写个人信息，完成后将收到入会凭证邮件</p>
        </el-card>

        <!-- Members table -->
        <el-card class="info-card">
          <template #header>
            <div class="card-header-row">
              <span class="card-title">
                成员列表
                <span class="member-count">{{ groupDetail.filledCount }} / {{ groupDetail.totalCount }}</span>
              </span>
              <el-button
                v-if="groupDetail.filledCount < groupDetail.totalCount"
                type="primary"
                :icon="Plus"
                @click="openAddDialog"
              >
                添加成员
              </el-button>
            </div>
          </template>

          <el-empty v-if="filledMembers.length === 0" description="暂无已填写成员，点击「添加成员」或通过邀请链接邀请成员填写" />
          <el-table v-else :data="filledMembers" stripe border>
            <el-table-column prop="sequenceNo" label="序号" width="70" align="center" />
            <el-table-column prop="memberName" label="姓名" />
            <el-table-column prop="memberEmail" label="邮箱" />
            <el-table-column label="填写时间" width="180">
              <template #default="{ row }">
                {{ row.filledAt ? formatDate(row.filledAt) : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200" align="center">
              <template #default="{ row }">
                <el-button size="small" @click="openEditDialog(row)">修改</el-button>
                <el-button
                  size="small"
                  type="primary"
                  :loading="regeneratingId === row.id"
                  @click="handleRegenerateCredential(row)"
                >凭证</el-button>
                <el-button
                  size="small"
                  type="danger"
                  @click="handleClearMember(row)"
                >清除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </template>
    </div>

    <!-- Add / Edit member dialog -->
    <el-dialog v-model="memberDialogVisible" :title="editingMember ? '修改成员信息' : '添加成员'" width="420px">
      <el-form ref="memberFormRef" :model="memberForm" :rules="memberRules" label-position="top">
        <el-form-item label="姓名" prop="memberName">
          <el-input v-model="memberForm.memberName" placeholder="请输入成员姓名" />
        </el-form-item>
        <el-form-item label="邮箱" prop="memberEmail">
          <el-input v-model="memberForm.memberEmail" placeholder="请输入成员邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="memberDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmitMember">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { ArrowLeft, Plus } from '@element-plus/icons-vue'
import { groupApi } from '@/api/group'
import type { GroupOrderVo, GroupMember } from '@/api/types'

const route = useRoute()
const router = useRouter()
const orderId = Number(route.params.id)

const loading = ref(false)
const groupDetail = ref<GroupOrderVo | null>(null)
const inviteUrl = ref<string | null>(null)
const generatingInvite = ref(false)

const memberDialogVisible = ref(false)
const submitting = ref(false)
const editingMember = ref<GroupMember | null>(null)
const memberFormRef = ref<FormInstance>()
const memberForm = ref({ memberName: '', memberEmail: '' })
const regeneratingId = ref<number | null>(null)

const filledMembers = computed(() => groupDetail.value?.members.filter(m => m.status === 2) ?? [])

const memberRules: FormRules = {
  memberName: [{ required: true, message: '请填写成员姓名', trigger: 'blur' }],
  memberEmail: [
    { required: true, message: '请填写成员邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const formatDate = (dateStr: string) => {
  return new Date(dateStr).toLocaleString('zh-CN', { hour12: false })
}

const loadDetail = async () => {
  loading.value = true
  try {
    groupDetail.value = await groupApi.getGroupDetail(orderId)
    const rawPath = groupDetail.value.inviteUrl
    inviteUrl.value = rawPath ? window.location.origin + rawPath : null
  } catch {
    ElMessage.error('加载团体信息失败')
  } finally {
    loading.value = false
  }
}

const generateInviteUrl = async () => {
  generatingInvite.value = true
  try {
    inviteUrl.value = window.location.origin + await groupApi.getInviteUrl(orderId)
    ElMessage.success('邀请链接已生成')
  } catch {
    ElMessage.error('生成邀请链接失败')
  } finally {
    generatingInvite.value = false
  }
}

const copyInviteUrl = async () => {
  if (!inviteUrl.value) return
  await navigator.clipboard.writeText(inviteUrl.value)
  ElMessage.success('链接已复制到剪贴板')
}

const openAddDialog = () => {
  editingMember.value = null
  memberForm.value = { memberName: '', memberEmail: '' }
  memberDialogVisible.value = true
}

const openEditDialog = (member: GroupMember) => {
  editingMember.value = member
  memberForm.value = {
    memberName: member.memberName ?? '',
    memberEmail: member.memberEmail ?? ''
  }
  memberDialogVisible.value = true
}

const handleSubmitMember = async () => {
  const valid = await memberFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    if (editingMember.value) {
      await groupApi.updateMember(orderId, editingMember.value.id, memberForm.value)
      ElMessage.success('成员信息已更新')
    } else {
      await groupApi.addMember(orderId, memberForm.value)
      ElMessage.success('成员已添加，入会凭证将发送至其邮箱')
    }
    memberDialogVisible.value = false
    await loadDetail()
  } catch {
    ElMessage.error('操作失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const handleRegenerateCredential = async (member: GroupMember) => {
  try {
    await ElMessageBox.confirm(
      `为成员「${member.memberName}」重新生成凭证？旧凭证将立即失效，新凭证将发送至 ${member.memberEmail}。`,
      '重新生成凭证',
      { type: 'warning' }
    )
  } catch {
    return
  }
  regeneratingId.value = member.id
  try {
    await groupApi.regenerateCredential(orderId, member.id)
    ElMessage.success('凭证已重新生成，新凭证将发送至成员邮箱')
  } catch {
    ElMessage.error('生成失败，请稍后重试')
  } finally {
    regeneratingId.value = null
  }
}

const handleClearMember = async (member: GroupMember) => {  await ElMessageBox.confirm(
    `确认清除成员「${member.memberName}」的信息？清除后其入会凭证将失效，槽位可重新填写。`,
    '清除成员',
    { type: 'warning' }
  )
  try {
    await groupApi.clearMember(orderId, member.id)
    ElMessage.success('成员信息已清除')
    await loadDetail()
  } catch {
    ElMessage.error('清除失败，请稍后重试')
  }
}

onMounted(loadDetail)
</script>

<style scoped lang="scss">
.page-body {
  padding: $spacing-10 $spacing-6;
  display: flex;
  flex-direction: column;
  gap: $spacing-6;
}

.info-card {
  .card-title {
    font-weight: $font-weight-bold;
    font-size: $font-size-base;

    .member-count {
      margin-left: $spacing-2;
      font-weight: $font-weight-normal;
      font-size: $font-size-sm;
      color: $text-secondary;
    }
  }

  .card-header-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .invite-url-row {
    display: flex;
    gap: $spacing-3;
    align-items: center;
  }

  .hint-text {
    margin-top: $spacing-3;
    font-size: $font-size-sm;
    color: $text-secondary;
  }
}

.empty-text {
  color: $text-secondary;
  font-size: $font-size-sm;
}
</style>
