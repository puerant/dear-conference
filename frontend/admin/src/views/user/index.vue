<template>
  <div class="user-page">
    <el-card class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="关键词">
          <el-input
            v-model="query.keyword"
            placeholder="姓名/邮箱"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="query.role" placeholder="全部" clearable style="width: 120px">
            <el-option label="讲者" value="speaker" />
            <el-option label="审稿人" value="reviewer" />
            <el-option label="参会者" value="attendee" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 100px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList(1)">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button type="primary" @click="handleCreate">新增用户</el-button>
        </div>
      </template>

      <el-table :data="list" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column label="角色" width="100">
          <template #default="{ row }">
            <el-tag :type="getRoleTagType(row.role)" size="small">
              {{ getRoleLabel(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="32" :src="row.avatar">{{ row.name?.charAt(0) }}</el-avatar>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="160">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">详情</el-button>
            <el-button type="warning" link size="small" @click="handleChangeRole(row)">修改角色</el-button>
            <el-button
              :type="row.status === 1 ? 'danger' : 'success'"
              link
              size="small"
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
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

    <!-- User Detail Dialog -->
    <el-dialog v-model="detailVisible" title="用户详情" width="500px">
      <el-descriptions :column="1" border v-if="currentUser">
        <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ currentUser.email }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ currentUser.name }}</el-descriptions-item>
        <el-descriptions-item label="角色">
          <el-tag :type="getRoleTagType(currentUser.role)" size="small">
            {{ getRoleLabel(currentUser.role) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'" size="small">
            {{ currentUser.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="头像">
          <el-avatar :size="48" :src="currentUser.avatar">
            {{ currentUser.name?.charAt(0) }}
          </el-avatar>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ formatDate(currentUser.createdAt) }}
        </el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- Create User Dialog -->
    <el-dialog v-model="createVisible" title="新增用户" width="480px" @closed="resetCreateForm">
      <el-form ref="createFormRef" :model="createForm" :rules="createRules" label-width="80px">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="createForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="createForm.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="createForm.password" type="password" placeholder="请输入初始密码（6-20位）" show-password />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="createForm.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="讲者" value="speaker" />
            <el-option label="审稿人" value="reviewer" />
            <el-option label="参会者" value="attendee" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createVisible = false">取消</el-button>
        <el-button type="primary" :loading="createLoading" @click="submitCreate">确定</el-button>
      </template>
    </el-dialog>

    <!-- Change Role Dialog -->
    <el-dialog v-model="roleVisible" title="修改角色" width="400px">
      <el-form label-width="80px">
        <el-form-item label="当前角色">
          <el-tag :type="getRoleTagType(currentUser?.role ?? '')" size="small">
            {{ getRoleLabel(currentUser?.role ?? '') }}
          </el-tag>
        </el-form-item>
        <el-form-item label="新角色">
          <el-select v-model="newRole" placeholder="请选择新角色" style="width: 100%">
            <el-option label="讲者" value="speaker" />
            <el-option label="审稿人" value="reviewer" />
            <el-option label="参会者" value="attendee" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleVisible = false">取消</el-button>
        <el-button type="primary" :loading="roleLoading" :disabled="!newRole" @click="submitRoleChange">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { userApi, type User, type CreateUserParams } from '@/api/user'
import { formatDate, getRoleLabel, getRoleTagType } from '@/utils/format'

const list = ref<User[]>([])
const total = ref(0)
const loading = ref(false)
const detailVisible = ref(false)
const currentUser = ref<User | null>(null)

const query = reactive({
  page: 1,
  pageSize: 10,
  keyword: '',
  role: '',
  status: undefined as number | undefined
})

async function fetchList(page?: number) {
  if (page) query.page = page
  loading.value = true
  try {
    const res = await userApi.getList({
      page: query.page,
      pageSize: query.pageSize,
      keyword: query.keyword || undefined,
      role: query.role || undefined,
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
  query.keyword = ''
  query.role = ''
  query.status = undefined
  fetchList(1)
}

function handleView(row: User) {
  currentUser.value = row
  detailVisible.value = true
}

async function handleToggleStatus(row: User) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 0 ? '禁用' : '启用'
  await ElMessageBox.confirm(`确定要${action}用户 "${row.name}" 吗？`, '提示', {
    type: 'warning'
  })
  try {
    await userApi.updateStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    fetchList()
  } catch {
    // ignore
  }
}

async function handleDelete(row: User) {
  await ElMessageBox.confirm(`确定要删除用户 "${row.name}" 吗？此操作不可恢复！`, '警告', {
    type: 'error',
    confirmButtonText: '删除',
    confirmButtonClass: 'el-button--danger'
  })
  try {
    await userApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    // ignore
  }
}

// ── Create User ──────────────────────────────────────────────────────────────
const createVisible = ref(false)
const createLoading = ref(false)
const createFormRef = ref<FormInstance>()
const createForm = reactive<CreateUserParams>({
  email: '',
  password: '',
  name: '',
  role: ''
})

const createRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20位', trigger: 'blur' }
  ],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

function handleCreate() {
  createVisible.value = true
}

function resetCreateForm() {
  createForm.email = ''
  createForm.password = ''
  createForm.name = ''
  createForm.role = ''
  createFormRef.value?.clearValidate()
}

async function submitCreate() {
  const valid = await createFormRef.value?.validate().catch(() => false)
  if (!valid) return
  createLoading.value = true
  try {
    await userApi.create({ ...createForm })
    ElMessage.success('用户创建成功')
    createVisible.value = false
    fetchList(1)
  } catch {
    // ignore
  } finally {
    createLoading.value = false
  }
}

// ── Change Role ──────────────────────────────────────────────────────────────
const roleVisible = ref(false)
const roleLoading = ref(false)
const newRole = ref('')

function handleChangeRole(row: User) {
  currentUser.value = row
  newRole.value = row.role
  roleVisible.value = true
}

async function submitRoleChange() {
  if (!currentUser.value || !newRole.value) return
  roleLoading.value = true
  try {
    await userApi.updateRole(currentUser.value.id, newRole.value)
    ElMessage.success('角色修改成功')
    roleVisible.value = false
    fetchList()
  } catch {
    // ignore
  } finally {
    roleLoading.value = false
  }
}

onMounted(() => fetchList())
</script>

<style lang="scss" scoped>
.user-page {
  .filter-card {
    margin-bottom: 16px;

    :deep(.el-card__body) {
      padding: 16px 20px 0;
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .pagination {
      margin-top: 16px;
      justify-content: flex-end;
    }
  }
}
</style>
