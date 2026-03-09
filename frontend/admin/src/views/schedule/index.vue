<template>
  <div class="schedule-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>日程管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加日程
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="loadList"
          />
        </el-form-item>
      </el-form>

      <!-- 按日期分组显示 -->
      <div v-for="group in groupedSchedules" :key="group.date" class="schedule-group">
        <div class="group-header">
          <el-tag type="primary" size="large">{{ formatDate(group.date) }}</el-tag>
        </div>
        <el-table :data="group.schedules" stripe>
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="时间" width="150">
            <template #default="{ row }">
              {{ formatTime(row.startTime) }} - {{ formatTime(row.endTime) }}
            </template>
          </el-table-column>
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="venue" label="场地" width="150" />
          <el-table-column label="日程项" width="100">
            <template #default="{ row }">
              <el-button type="primary" link @click="showItems(row)">
                {{ row.items?.length || 0 }} 项
              </el-button>
            </template>
          </el-table-column>
          <el-table-column prop="sortOrder" label="排序" width="80" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
              <el-button type="success" link @click="handleAddItem(row)">添加项</el-button>
              <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <el-empty v-if="groupedSchedules.length === 0 && !loading" description="暂无日程数据" />
    </el-card>

    <!-- 日程对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑日程' : '新增日程'" width="600px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="日期" prop="date">
          <el-date-picker v-model="formData.date" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-time-picker v-model="formData.startTime" format="HH:mm" value-format="HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-time-picker v-model="formData.endTime" format="HH:mm" value-format="HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="标题" prop="title">
          <el-input v-model="formData.title" placeholder="请输入标题" maxlength="200" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="场地" prop="venue">
              <el-input v-model="formData.venue" placeholder="请输入场地" maxlength="200" />
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

    <!-- 日程项对话框 -->
    <el-dialog v-model="itemDialogVisible" title="日程项管理" width="800px">
      <div class="item-header">
        <span>日程项列表</span>
        <el-button type="primary" size="small" @click="handleAddItemRow">添加日程项</el-button>
      </div>
      <el-table :data="itemList" stripe>
        <el-table-column label="时间" width="120">
          <template #default="{ row }">
            <el-time-picker v-model="row.time" format="HH:mm" value-format="HH:mm:ss" size="small" style="width: 100px" />
          </template>
        </el-table-column>
        <el-table-column label="标题" min-width="200">
          <template #default="{ row }">
            <el-input v-model="row.title" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="演讲人" width="150">
          <template #default="{ row }">
            <el-select v-model="row.speakerId" placeholder="选择" size="small" clearable style="width: 130px">
              <el-option v-for="e in expertList" :key="e.id" :label="e.name" :value="e.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="排序" width="80">
          <template #default="{ row }">
            <el-input-number v-model="row.sortOrder" :min="0" size="small" style="width: 70px" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row, $index }">
            <el-button type="danger" link size="small" @click="removeItemRow($index, row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveItems" :loading="savingItems">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getScheduleList,
  createSchedule,
  updateSchedule,
  deleteSchedule,
  addScheduleItem,
  updateScheduleItem,
  deleteScheduleItem,
  getExpertList
} from '@/api/conference'
import type { ScheduleVo, ScheduleDto, ScheduleItemDto, ExpertVo } from '@/api/conference'

const loading = ref(false)
const submitting = ref(false)
const savingItems = ref(false)
const scheduleList = ref<ScheduleVo[]>([])
const expertList = ref<ExpertVo[]>([])
const dialogVisible = ref(false)
const itemDialogVisible = ref(false)
const editingId = ref<number | null>(null)
const currentScheduleId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const dateRange = ref<[string, string] | null>(null)
const itemList = ref<(ScheduleItemDto & { id?: number; isNew?: boolean })[]>([])

const formData = reactive<ScheduleDto>({
  date: '',
  startTime: '',
  endTime: '',
  title: '',
  description: '',
  venue: '',
  sortOrder: 0
})

const rules: FormRules = {
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }]
}

const groupedSchedules = computed(() => {
  const groups: { date: string; schedules: ScheduleVo[] }[] = []
  const map = new Map<string, ScheduleVo[]>()

  scheduleList.value.forEach(s => {
    const list = map.get(s.date) || []
    list.push(s)
    map.set(s.date, list)
  })

  map.forEach((schedules, date) => {
    groups.push({ date, schedules: schedules.sort((a, b) => (a.sortOrder || 0) - (b.sortOrder || 0)) })
  })

  return groups.sort((a, b) => a.date.localeCompare(b.date))
})

const loadList = async () => {
  try {
    loading.value = true
    const params: Record<string, string> = {}
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const data = await getScheduleList(params)
    scheduleList.value = data || []
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
  } finally {
    loading.value = false
  }
}

const loadExperts = async () => {
  try {
    const data = await getExpertList({})
    expertList.value = data || []
  } catch (error) {
    console.error('加载专家列表失败:', error)
  }
}

const formatDate = (dateStr: string) => {
  const date = new Date(dateStr)
  const days = ['日', '一', '二', '三', '四', '五', '六']
  return `${date.getMonth() + 1}月${date.getDate()}日 星期${days[date.getDay()]}`
}

const formatTime = (time: string | null) => {
  return time ? time.substring(0, 5) : '--'
}

const resetForm = () => {
  formData.date = ''
  formData.startTime = ''
  formData.endTime = ''
  formData.title = ''
  formData.description = ''
  formData.venue = ''
  formData.sortOrder = 0
  editingId.value = null
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: ScheduleVo) => {
  resetForm()
  editingId.value = row.id
  Object.assign(formData, {
    date: row.date,
    startTime: row.startTime || '',
    endTime: row.endTime || '',
    title: row.title,
    description: row.description || '',
    venue: row.venue || '',
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
        await updateSchedule(editingId.value, formData)
        ElMessage.success('更新成功')
      } else {
        await createSchedule(formData)
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

const handleDelete = async (row: ScheduleVo) => {
  try {
    await ElMessageBox.confirm(`确定要删除日程"${row.title}"吗？`, '提示', { type: 'warning' })
    await deleteSchedule(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const showItems = (row: ScheduleVo) => {
  currentScheduleId.value = row.id
  itemList.value = (row.items || []).map(item => ({ ...item }))
  itemDialogVisible.value = true
}

const handleAddItem = (row: ScheduleVo) => {
  currentScheduleId.value = row.id
  itemList.value = (row.items || []).map(item => ({ ...item }))
  handleAddItemRow()
  itemDialogVisible.value = true
}

const handleAddItemRow = () => {
  itemList.value.push({
    scheduleId: currentScheduleId.value || undefined,
    time: '09:00:00',
    title: '',
    speakerId: undefined,
    sortOrder: itemList.value.length,
    isNew: true
  })
}

const removeItemRow = async (index: number, row: any) => {
  if (row.id && !row.isNew) {
    try {
      await deleteScheduleItem(row.id)
    } catch (error) {
      console.error('删除失败:', error)
    }
  }
  itemList.value.splice(index, 1)
}

const saveItems = async () => {
  if (!currentScheduleId.value) return

  try {
    savingItems.value = true
    for (const item of itemList.value) {
      if (!item.title) continue

      if (item.isNew || !item.id) {
        await addScheduleItem(currentScheduleId.value, {
          time: item.time,
          title: item.title,
          description: item.description,
          speakerId: item.speakerId,
          sortOrder: item.sortOrder
        })
      } else {
        await updateScheduleItem(item.id, {
          time: item.time,
          title: item.title,
          description: item.description,
          speakerId: item.speakerId,
          sortOrder: item.sortOrder
        })
      }
    }
    ElMessage.success('保存成功')
    itemDialogVisible.value = false
    loadList()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    savingItems.value = false
  }
}

onMounted(() => {
  loadList()
  loadExperts()
})
</script>

<style scoped>
.schedule-manage {
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

.schedule-group {
  margin-bottom: 24px;
}

.group-header {
  margin-bottom: 12px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 600;
}
</style>
