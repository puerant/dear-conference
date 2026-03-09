<template>
  <div class="dictionary-page">
    <el-card class="filter-card">
      <el-form :model="query" inline>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchList(1)">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" :icon="Plus" @click="openCreateDict">创建字典</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table
        :data="list"
        v-loading="loading"
        border
        row-key="id"
        @expand-change="handleExpandChange"
      >
        <el-table-column type="expand">
          <template #default="{ row }">
            <div class="expand-area">
              <div class="expand-header">
                <span>字典项列表</span>
                <el-button type="primary" size="small" :icon="Plus" @click="openCreateItem(row)">
                  添加字典项
                </el-button>
              </div>
              <el-table :data="dictItemsMap[row.dictCode] || []" border size="small">
                <el-table-column prop="itemCode" label="编码" width="120" />
                <el-table-column prop="itemValue" label="值" width="70" align="center" />
                <el-table-column prop="itemName" label="名称" width="100" />
                <el-table-column prop="description" label="描述" min-width="160" show-overflow-tooltip />
                <el-table-column prop="sort" label="排序" width="70" align="center" />
                <el-table-column label="状态" width="80">
                  <template #default="{ row: item }">
                    <el-tag :type="item.status === 1 ? 'success' : 'info'" size="small">
                      {{ item.status === 1 ? '启用' : '禁用' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="{ row: item }">
                    <el-button type="primary" link size="small" @click="openEditItem(item)">编辑</el-button>
                    <el-button type="danger" link size="small" @click="handleDeleteItem(item)">删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </template>
        </el-table-column>

        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="dictCode" label="字典编码" width="160" />
        <el-table-column prop="dictName" label="字典名称" width="150" />
        <el-table-column prop="description" label="描述" min-width="180" show-overflow-tooltip />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="70" align="center" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="openEditDict(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDeleteDict(row)">删除</el-button>
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

    <!-- Dictionary Form Dialog -->
    <el-dialog
      v-model="dictFormVisible"
      :title="dictFormMode === 'create' ? '创建字典' : '编辑字典'"
      width="480px"
      @close="resetDictForm"
    >
      <el-form ref="dictFormRef" :model="dictForm" :rules="dictRules" label-width="90px">
        <el-form-item label="字典编码" prop="dictCode">
          <el-input
            v-model="dictForm.dictCode"
            placeholder="如：user_role"
            :disabled="dictFormMode === 'edit'"
          />
        </el-form-item>
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="dictForm.dictName" placeholder="如：用户角色" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="dictForm.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dictForm.sort" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dictFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="dictSubmitting" @click="handleDictSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- Dictionary Item Form Dialog -->
    <el-dialog
      v-model="itemFormVisible"
      :title="itemFormMode === 'create' ? '添加字典项' : '编辑字典项'"
      width="480px"
      @close="resetItemForm"
    >
      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="90px">
        <el-form-item label="字典编码">
          <el-input v-model="itemForm.dictCode" disabled />
        </el-form-item>
        <el-form-item label="项编码" prop="itemCode">
          <el-input v-model="itemForm.itemCode" placeholder="如：speaker" />
        </el-form-item>
        <el-form-item label="项名称" prop="itemName">
          <el-input v-model="itemForm.itemName" placeholder="如：讲者" />
        </el-form-item>
        <el-form-item label="值" prop="itemValue">
          <el-input-number v-model="itemForm.itemValue" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="itemForm.description" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="itemForm.sort" :min="0" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemFormVisible = false">取消</el-button>
        <el-button type="primary" :loading="itemSubmitting" @click="handleItemSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { dictionaryApi, type Dictionary, type DictionaryItem } from '@/api/dictionary'

const list = ref<Dictionary[]>([])
const total = ref(0)
const loading = ref(false)
const dictItemsMap = ref<Record<string, DictionaryItem[]>>({})

// Dict form
const dictFormVisible = ref(false)
const dictFormMode = ref<'create' | 'edit'>('create')
const dictEditId = ref<number | null>(null)
const dictSubmitting = ref(false)
const dictFormRef = ref<FormInstance>()

const dictForm = reactive({
  dictCode: '',
  dictName: '',
  description: '',
  sort: 0
})

const dictRules: FormRules = {
  dictCode: [{ required: true, message: '请输入字典编码', trigger: 'blur' }],
  dictName: [{ required: true, message: '请输入字典名称', trigger: 'blur' }]
}

// Item form
const itemFormVisible = ref(false)
const itemFormMode = ref<'create' | 'edit'>('create')
const itemEditId = ref<number | null>(null)
const itemSubmitting = ref(false)
const itemFormRef = ref<FormInstance>()
const currentDictCode = ref('')

const itemForm = reactive({
  dictCode: '',
  itemCode: '',
  itemName: '',
  itemValue: 0,
  description: '',
  sort: 0
})

const itemRules: FormRules = {
  itemCode: [{ required: true, message: '请输入项编码', trigger: 'blur' }],
  itemName: [{ required: true, message: '请输入项名称', trigger: 'blur' }]
}

const query = reactive({
  page: 1,
  pageSize: 10,
  status: undefined as number | undefined
})

async function fetchList(page?: number) {
  if (page) query.page = page
  loading.value = true
  try {
    const res = await dictionaryApi.getList({ page: query.page, pageSize: query.pageSize, status: query.status })
    list.value = res.list
    total.value = res.total
  } catch {
    // ignore
  } finally {
    loading.value = false
  }
}

function handleReset() {
  query.status = undefined
  fetchList(1)
}

async function handleExpandChange(row: Dictionary, expanded: Dictionary[]) {
  if (expanded.find((r) => r.dictCode === row.dictCode) && !dictItemsMap.value[row.dictCode]) {
    try {
      const items = await dictionaryApi.getItems(row.dictCode)
      dictItemsMap.value = { ...dictItemsMap.value, [row.dictCode]: items }
    } catch {
      // ignore
    }
  }
}

function openCreateDict() {
  dictFormMode.value = 'create'
  dictEditId.value = null
  resetDictForm()
  dictFormVisible.value = true
}

function openEditDict(row: Dictionary) {
  dictFormMode.value = 'edit'
  dictEditId.value = row.id
  dictForm.dictCode = row.dictCode
  dictForm.dictName = row.dictName
  dictForm.description = row.description
  dictForm.sort = row.sort
  dictFormVisible.value = true
}

function resetDictForm() {
  dictForm.dictCode = ''
  dictForm.dictName = ''
  dictForm.description = ''
  dictForm.sort = 0
  dictFormRef.value?.clearValidate()
}

async function handleDictSubmit() {
  const valid = await dictFormRef.value?.validate().catch(() => false)
  if (!valid) return
  dictSubmitting.value = true
  try {
    if (dictFormMode.value === 'create') {
      await dictionaryApi.create(dictForm)
      ElMessage.success('创建成功')
    } else {
      await dictionaryApi.update(dictEditId.value!, dictForm)
      ElMessage.success('更新成功')
    }
    dictFormVisible.value = false
    fetchList()
  } catch {
    // ignore
  } finally {
    dictSubmitting.value = false
  }
}

async function handleDeleteDict(row: Dictionary) {
  await ElMessageBox.confirm(`确定要删除字典 "${row.dictName}" 吗？`, '警告', { type: 'error' })
  try {
    await dictionaryApi.delete(row.id)
    ElMessage.success('删除成功')
    fetchList()
  } catch {
    // ignore
  }
}

function openCreateItem(dict: Dictionary) {
  itemFormMode.value = 'create'
  itemEditId.value = null
  currentDictCode.value = dict.dictCode
  resetItemForm()
  itemForm.dictCode = dict.dictCode
  itemFormVisible.value = true
}

function openEditItem(item: DictionaryItem) {
  itemFormMode.value = 'edit'
  itemEditId.value = item.id
  itemForm.dictCode = item.dictCode
  itemForm.itemCode = item.itemCode
  itemForm.itemName = item.itemName
  itemForm.itemValue = item.itemValue
  itemForm.description = item.description
  itemForm.sort = item.sort
  currentDictCode.value = item.dictCode
  itemFormVisible.value = true
}

function resetItemForm() {
  itemForm.dictCode = ''
  itemForm.itemCode = ''
  itemForm.itemName = ''
  itemForm.itemValue = 0
  itemForm.description = ''
  itemForm.sort = 0
  itemFormRef.value?.clearValidate()
}

async function handleItemSubmit() {
  const valid = await itemFormRef.value?.validate().catch(() => false)
  if (!valid) return
  itemSubmitting.value = true
  try {
    if (itemFormMode.value === 'create') {
      await dictionaryApi.createItem(itemForm)
      ElMessage.success('添加成功')
    } else {
      await dictionaryApi.updateItem(itemEditId.value!, itemForm)
      ElMessage.success('更新成功')
    }
    itemFormVisible.value = false
    // Refresh items for this dict
    const items = await dictionaryApi.getItems(currentDictCode.value)
    dictItemsMap.value = { ...dictItemsMap.value, [currentDictCode.value]: items }
  } catch {
    // ignore
  } finally {
    itemSubmitting.value = false
  }
}

async function handleDeleteItem(item: DictionaryItem) {
  await ElMessageBox.confirm(`确定要删除字典项 "${item.itemName}" 吗？`, '警告', { type: 'error' })
  try {
    await dictionaryApi.deleteItem(item.id)
    ElMessage.success('删除成功')
    const items = await dictionaryApi.getItems(item.dictCode)
    dictItemsMap.value = { ...dictItemsMap.value, [item.dictCode]: items }
  } catch {
    // ignore
  }
}

onMounted(() => fetchList())
</script>

<style lang="scss" scoped>
.dictionary-page {
  .filter-card {
    margin-bottom: 16px;

    :deep(.el-card__body) {
      padding: 16px 20px 0;
    }
  }

  .table-card {
    .pagination {
      margin-top: 16px;
      justify-content: flex-end;
    }
  }

  .expand-area {
    padding: 16px;
    background: #fafafa;

    .expand-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
      font-size: 14px;
      font-weight: 600;
      color: #262626;
    }
  }
}
</style>
