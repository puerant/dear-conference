<template>
  <div class="expert-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>专家管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加专家
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchKeyword" placeholder="姓名/机构" clearable @keyup.enter="loadList" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchIsKeynote" placeholder="全部" clearable>
            <el-option label="主旨演讲嘉宾" :value="1" />
            <el-option label="普通嘉宾" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="title" label="职称" width="120" />
        <el-table-column prop="organization" label="机构" min-width="150" />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.isKeynote ? 'warning' : 'info'">
              {{ row.isKeynote ? '主旨演讲' : '普通嘉宾' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑专家' : '新增专家'" width="600px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="姓名" prop="name">
          <el-input v-model="formData.name" placeholder="请输入姓名" maxlength="100" />
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-input v-model="formData.title" placeholder="请输入职称" maxlength="100" />
        </el-form-item>
        <el-form-item label="机构" prop="organization">
          <el-input v-model="formData.organization" placeholder="请输入所属机构" maxlength="200" />
        </el-form-item>
        <el-form-item label="简介" prop="bio">
          <el-input v-model="formData.bio" type="textarea" :rows="4" placeholder="请输入简介" />
        </el-form-item>
        <el-form-item label="头像URL" prop="avatarUrl">
          <el-input v-model="formData.avatarUrl" placeholder="请输入头像图片URL" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="类型" prop="isKeynote">
              <el-switch
                v-model="formData.isKeynote"
                :active-value="1"
                :inactive-value="0"
                active-text="主旨演讲"
                inactive-text="普通嘉宾"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import { getExpertList, createExpert, updateExpert, deleteExpert } from '@/api/conference'
import type { ExpertVo, ExpertDto } from '@/api/conference'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref<ExpertVo[]>([])
const dialogVisible = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const searchKeyword = ref('')
const searchIsKeynote = ref<number | undefined>()

const formData = reactive<ExpertDto>({
  name: '',
  title: '',
  organization: '',
  bio: '',
  avatarUrl: '',
  isKeynote: 0,
  sortOrder: 0
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const loadList = async () => {
  try {
    loading.value = true
    const data = await getExpertList({
      keyword: searchKeyword.value || undefined,
      isKeynote: searchIsKeynote.value
    })
    tableData.value = data || []
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  formData.name = ''
  formData.title = ''
  formData.organization = ''
  formData.bio = ''
  formData.avatarUrl = ''
  formData.isKeynote = 0
  formData.sortOrder = 0
  editingId.value = null
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: ExpertVo) => {
  resetForm()
  editingId.value = row.id
  Object.assign(formData, {
    name: row.name,
    title: row.title || '',
    organization: row.organization || '',
    bio: row.bio || '',
    avatarUrl: row.avatarUrl || '',
    isKeynote: row.isKeynote,
    sortOrder: row.sortOrder
  })
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      submitting.value = true
      if (editingId.value) {
        await updateExpert(editingId.value, formData)
        ElMessage.success('更新成功')
      } else {
        await createExpert(formData)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      loadList()
    } catch (error: any) {
      ElMessage.error(error.message || '操作失败')
    } finally {
      submitting.value = false
    }
  })
}

const handleDelete = async (row: ExpertVo) => {
  try {
    await ElMessageBox.confirm(`确定要删除专家"${row.name}"吗？`, '提示', {
      type: 'warning'
    })
    await deleteExpert(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.expert-manage {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-bottom: 16px;
}
</style>
