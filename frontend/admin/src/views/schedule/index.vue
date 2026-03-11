<template>
  <div class="schedule-manage">
    <el-card class="venue-card">
      <template #header>
        <div class="card-header">
          <span>会场与日程管理</span>
          <div class="header-actions">
            <el-button type="primary" @click="openCreateVenue">新建会场</el-button>
            <el-button :disabled="!selectedVenueId" @click="openEditVenue">编辑会场</el-button>
            <el-button type="danger" :disabled="!selectedVenueId" @click="handleDeleteVenue">删除会场</el-button>
          </div>
        </div>
      </template>

      <el-alert
        title="请先创建会场，再在该会场下创建日程。"
        type="info"
        :closable="false"
        class="mb-12"
      />

      <el-form :inline="true" class="search-form">
        <el-form-item label="会场">
          <el-select v-model="selectedVenueId" placeholder="请选择会场" clearable filterable @change="handleVenueChange">
            <el-option v-for="venue in venueList" :key="venue.id" :label="venue.name" :value="venue.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            value-format="YYYY-MM-DD"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            @change="loadList"
          />
        </el-form-item>
        <el-form-item>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16" class="content-row">
      <el-col :xs="24" :lg="9">
        <el-card class="panel-card">
          <template #header>
            <div class="card-header">
              <span>日程列表</span>
              <el-button type="primary" :disabled="!selectedVenueId" @click="handleAddSchedule">新建日程</el-button>
            </div>
          </template>

          <el-empty v-if="!selectedVenueId" description="请先选择会场" />
          <el-empty v-else-if="!scheduleList.length" description="当前会场暂无日程" />
          <el-menu
            v-else
            class="schedule-menu"
            :default-active="activeScheduleId ? String(activeScheduleId) : ''"
            @select="handleScheduleSelect"
          >
            <el-menu-item v-for="schedule in scheduleList" :key="schedule.id" :index="String(schedule.id)">
              <div class="menu-main">
                <span class="menu-title">{{ schedule.title }}</span>
                <span class="menu-sub">{{ schedule.date }} {{ formatTimeRange(schedule.startTime, schedule.endTime) }}</span>
              </div>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="15">
        <el-card class="panel-card">
          <template #header>
            <div class="card-header">
              <span>日程详情与日程项</span>
              <div class="header-actions">
                <el-button :disabled="!activeSchedule" @click="handleEditSchedule">编辑日程</el-button>
                <el-button type="danger" :disabled="!activeSchedule" @click="handleDeleteSchedule">删除日程</el-button>
                <el-button type="success" :disabled="!activeSchedule" @click="handleAddItem">新增日程项</el-button>
              </div>
            </div>
          </template>

          <el-empty v-if="!activeSchedule" description="请选择日程" />
          <template v-else>
            <div class="schedule-detail">
              <el-descriptions :column="2" border>
                <el-descriptions-item label="标题">{{ activeSchedule.title }}</el-descriptions-item>
                <el-descriptions-item label="会场">{{ activeSchedule.venue || '--' }}</el-descriptions-item>
                <el-descriptions-item label="日期">{{ activeSchedule.date }}</el-descriptions-item>
                <el-descriptions-item label="时间">{{ formatTimeRange(activeSchedule.startTime, activeSchedule.endTime) }}</el-descriptions-item>
                <el-descriptions-item label="描述" :span="2">{{ activeSchedule.description || '--' }}</el-descriptions-item>
              </el-descriptions>
            </div>

            <div class="item-section">
              <div class="item-title">日程项菜单</div>
              <el-empty v-if="!activeSchedule.items?.length" description="暂无日程项" />
              <el-menu
                v-else
                class="item-menu"
                :default-active="activeItemId ? String(activeItemId) : ''"
                @select="handleItemSelect"
              >
                <el-menu-item v-for="item in activeSchedule.items" :key="item.id" :index="String(item.id)">
                  <div class="menu-main">
                    <span class="menu-title">{{ item.title }}</span>
                    <span class="menu-sub">{{ formatTimeRange(item.startTime || item.time, item.endTime || item.time) }}</span>
                  </div>
                  <div class="menu-actions">
                    <el-button type="primary" link @click.stop="handleEditItem(item.id)">编辑</el-button>
                    <el-button type="danger" link @click.stop="handleDeleteItem(item.id)">删除</el-button>
                  </div>
                </el-menu-item>
              </el-menu>
            </div>
          </template>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="venueDialogVisible" :title="venueEditingId ? '编辑会场' : '新建会场'" width="640px">
      <el-form ref="venueFormRef" :model="venueForm" :rules="venueRules" label-width="100px">
        <el-form-item label="会场名称" prop="name">
          <el-input v-model="venueForm.name" maxlength="200" />
        </el-form-item>
        <el-form-item label="会场地址">
          <el-input v-model="venueForm.address" maxlength="500" />
        </el-form-item>
        <el-form-item label="会场描述">
          <el-input v-model="venueForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="venueForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="venueDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submittingVenue" @click="submitVenue">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="scheduleDialogVisible" :title="scheduleEditingId ? '编辑日程' : '新建日程'" width="640px">
      <el-form ref="scheduleFormRef" :model="scheduleForm" :rules="scheduleRules" label-width="100px">
        <el-form-item label="所属会场">
          <el-input :model-value="selectedVenue?.name || ''" disabled />
        </el-form-item>
        <el-form-item label="日期" prop="date">
          <el-date-picker v-model="scheduleForm.date" type="date" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="时间范围" prop="timeRange">
          <el-time-picker
            v-model="scheduleForm.timeRange"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="HH:mm:ss"
            format="HH:mm"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="scheduleForm.title" maxlength="200" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="scheduleForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="scheduleForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scheduleDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submittingSchedule" @click="submitSchedule">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="itemDialogVisible" :title="itemEditingId ? '编辑日程项' : '新增日程项'" width="640px">
      <el-alert
        v-if="activeSchedule"
        :title="`所属日程：${activeSchedule.title}（${formatTimeRange(activeSchedule.startTime, activeSchedule.endTime)}）`"
        type="info"
        :closable="false"
        class="mb-12"
      />
      <el-form ref="itemFormRef" :model="itemForm" :rules="itemRules" label-width="100px">
        <el-form-item label="时间范围" prop="timeRange">
          <el-time-picker
            v-model="itemForm.timeRange"
            is-range
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="HH:mm:ss"
            format="HH:mm"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="itemForm.title" maxlength="200" />
        </el-form-item>
        <el-form-item label="讲者">
          <el-select v-model="itemForm.speakerId" clearable filterable placeholder="请选择讲者" style="width: 100%">
            <el-option v-for="expert in expertList" :key="expert.id" :label="expert.name" :value="expert.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="itemForm.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="itemForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="itemDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submittingItem" @click="submitItem">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import {
  addScheduleItem,
  createSchedule,
  createScheduleVenue,
  deleteSchedule,
  deleteScheduleItem,
  deleteScheduleVenue,
  getExpertList,
  getScheduleList,
  getScheduleVenueList,
  updateSchedule,
  updateScheduleItem,
  updateScheduleVenue
} from '@/api/conference'
import type { ExpertVo, ScheduleDto, ScheduleItemDto, ScheduleVenueDto, ScheduleVenueVo, ScheduleVo } from '@/api/conference'

const loading = ref(false)
const submittingVenue = ref(false)
const submittingSchedule = ref(false)
const submittingItem = ref(false)

const venueList = ref<ScheduleVenueVo[]>([])
const scheduleList = ref<ScheduleVo[]>([])
const expertList = ref<ExpertVo[]>([])

const selectedVenueId = ref<number | undefined>(undefined)
const dateRange = ref<[string, string] | null>(null)

const activeScheduleId = ref<number | null>(null)
const activeItemId = ref<number | null>(null)

const selectedVenue = computed(() => venueList.value.find((venue) => venue.id === selectedVenueId.value))
const activeSchedule = computed(() => scheduleList.value.find((schedule) => schedule.id === activeScheduleId.value) || null)

const venueDialogVisible = ref(false)
const venueEditingId = ref<number | null>(null)
const venueFormRef = ref<FormInstance>()
const venueForm = reactive({
  name: '',
  address: '',
  description: '',
  sortOrder: 0
})

const scheduleDialogVisible = ref(false)
const scheduleEditingId = ref<number | null>(null)
const scheduleFormRef = ref<FormInstance>()
const scheduleForm = reactive({
  date: '',
  title: '',
  description: '',
  timeRange: [] as string[],
  sortOrder: 0
})

const itemDialogVisible = ref(false)
const itemEditingId = ref<number | null>(null)
const itemFormRef = ref<FormInstance>()
const itemForm = reactive({
  timeRange: [] as string[],
  title: '',
  description: '',
  speakerId: undefined as number | undefined,
  sortOrder: 0
})

const venueRules: FormRules = {
  name: [{ required: true, message: '请输入会场名称', trigger: 'blur' }]
}

const scheduleRules: FormRules = {
  date: [{ required: true, message: '请选择日期', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  timeRange: [{ required: true, message: '请选择时间范围', trigger: 'change' }]
}

const itemRules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  timeRange: [{ required: true, message: '请选择时间范围', trigger: 'change' }]
}

const formatTimeRange = (start: string | null | undefined, end: string | null | undefined) => {
  const startText = start ? start.slice(0, 5) : '--:--'
  const endText = end ? end.slice(0, 5) : '--:--'
  return `${startText} - ${endText}`
}

const loadVenues = async () => {
  try {
    venueList.value = (await getScheduleVenueList()) || []
    if (!selectedVenueId.value && venueList.value.length) {
      selectedVenueId.value = venueList.value[0].id
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载会场失败')
  }
}

const loadList = async () => {
  try {
    loading.value = true
    if (!selectedVenueId.value) {
      scheduleList.value = []
      activeScheduleId.value = null
      activeItemId.value = null
      return
    }
    const params: Record<string, string | number> = {
      venueId: selectedVenueId.value
    }
    if (dateRange.value?.length === 2) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }
    const data = await getScheduleList(params)
    scheduleList.value = data || []
    if (!scheduleList.value.find((s) => s.id === activeScheduleId.value)) {
      activeScheduleId.value = scheduleList.value[0]?.id || null
    }
    if (!activeSchedule.value?.items?.find((item) => item.id === activeItemId.value)) {
      activeItemId.value = activeSchedule.value?.items?.[0]?.id || null
    }
  } catch (error: any) {
    ElMessage.error(error.message || '加载日程失败')
  } finally {
    loading.value = false
  }
}

const loadExperts = async () => {
  try {
    expertList.value = (await getExpertList({})) || []
  } catch {
    ElMessage.error('加载讲者失败')
  }
}

const handleVenueChange = async () => {
  activeScheduleId.value = null
  activeItemId.value = null
  await loadList()
}

const resetFilters = async () => {
  dateRange.value = null
  await loadList()
}

const resetVenueForm = () => {
  venueEditingId.value = null
  venueForm.name = ''
  venueForm.address = ''
  venueForm.description = ''
  venueForm.sortOrder = 0
}

const openCreateVenue = () => {
  resetVenueForm()
  venueDialogVisible.value = true
}

const openEditVenue = () => {
  const venue = selectedVenue.value
  if (!venue) {
    ElMessage.warning('请先选择会场')
    return
  }
  venueEditingId.value = venue.id
  venueForm.name = venue.name
  venueForm.address = venue.address || ''
  venueForm.description = venue.description || ''
  venueForm.sortOrder = venue.sortOrder || 0
  venueDialogVisible.value = true
}

const submitVenue = async () => {
  if (!venueFormRef.value) return
  await venueFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      submittingVenue.value = true
      const payload: ScheduleVenueDto = {
        name: venueForm.name,
        address: venueForm.address || undefined,
        description: venueForm.description || undefined,
        sortOrder: venueForm.sortOrder
      }
      if (venueEditingId.value) {
        await updateScheduleVenue(venueEditingId.value, payload)
        ElMessage.success('会场已更新')
      } else {
        await createScheduleVenue(payload)
        ElMessage.success('会场已创建')
      }
      venueDialogVisible.value = false
      await loadVenues()
      await loadList()
    } catch (error: any) {
      ElMessage.error(error.message || '保存会场失败')
    } finally {
      submittingVenue.value = false
    }
  })
}

const handleDeleteVenue = async () => {
  const venue = selectedVenue.value
  if (!venue) {
    ElMessage.warning('请先选择会场')
    return
  }
  try {
    await ElMessageBox.confirm(`确认删除会场“${venue.name}”吗？`, '提示', { type: 'warning' })
    await deleteScheduleVenue(venue.id)
    ElMessage.success('会场已删除')
    selectedVenueId.value = undefined
    await loadVenues()
    await loadList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除会场失败')
    }
  }
}

const resetScheduleForm = () => {
  scheduleEditingId.value = null
  scheduleForm.date = ''
  scheduleForm.title = ''
  scheduleForm.description = ''
  scheduleForm.timeRange = []
  scheduleForm.sortOrder = 0
}

const handleAddSchedule = () => {
  if (!selectedVenueId.value) {
    ElMessage.warning('请先创建并选择会场')
    return
  }
  resetScheduleForm()
  scheduleDialogVisible.value = true
}

const handleEditSchedule = () => {
  const schedule = activeSchedule.value
  if (!schedule) return
  scheduleEditingId.value = schedule.id
  scheduleForm.date = schedule.date
  scheduleForm.title = schedule.title
  scheduleForm.description = schedule.description || ''
  scheduleForm.timeRange = [schedule.startTime || '', schedule.endTime || '']
  scheduleForm.sortOrder = schedule.sortOrder || 0
  scheduleDialogVisible.value = true
}

const submitSchedule = async () => {
  if (!scheduleFormRef.value || !selectedVenueId.value) return
  await scheduleFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      submittingSchedule.value = true
      const payload: ScheduleDto = {
        date: scheduleForm.date,
        venueId: selectedVenueId.value as number,
        title: scheduleForm.title,
        description: scheduleForm.description || undefined,
        startTime: scheduleForm.timeRange[0],
        endTime: scheduleForm.timeRange[1],
        sortOrder: scheduleForm.sortOrder
      }
      if (scheduleEditingId.value) {
        await updateSchedule(scheduleEditingId.value, payload)
        ElMessage.success('日程已更新')
      } else {
        await createSchedule(payload)
        ElMessage.success('日程已创建')
      }
      scheduleDialogVisible.value = false
      await loadList()
    } catch (error: any) {
      ElMessage.error(error.message || '保存日程失败')
    } finally {
      submittingSchedule.value = false
    }
  })
}

const handleDeleteSchedule = async () => {
  const schedule = activeSchedule.value
  if (!schedule) return
  try {
    await ElMessageBox.confirm(`确认删除日程“${schedule.title}”吗？`, '提示', { type: 'warning' })
    await deleteSchedule(schedule.id)
    ElMessage.success('日程已删除')
    await loadList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除日程失败')
    }
  }
}

const resetItemForm = () => {
  itemEditingId.value = null
  itemForm.title = ''
  itemForm.description = ''
  itemForm.timeRange = []
  itemForm.speakerId = undefined
  itemForm.sortOrder = 0
}

const handleAddItem = () => {
  if (!activeSchedule.value) {
    ElMessage.warning('请先选择日程')
    return
  }
  resetItemForm()
  itemDialogVisible.value = true
}

const handleEditItem = (itemId: number) => {
  const schedule = activeSchedule.value
  const item = schedule?.items?.find((i) => i.id === itemId)
  if (!schedule || !item) return
  itemEditingId.value = item.id
  itemForm.title = item.title
  itemForm.description = item.description || ''
  itemForm.timeRange = [item.startTime || item.time, item.endTime || item.time]
  itemForm.speakerId = item.speakerId || undefined
  itemForm.sortOrder = item.sortOrder || 0
  itemDialogVisible.value = true
}

const submitItem = async () => {
  const schedule = activeSchedule.value
  if (!itemFormRef.value || !schedule) return
  await itemFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      submittingItem.value = true
      const payload: ScheduleItemDto = {
        title: itemForm.title,
        description: itemForm.description || undefined,
        startTime: itemForm.timeRange[0],
        endTime: itemForm.timeRange[1],
        speakerId: itemForm.speakerId ?? null,
        sortOrder: itemForm.sortOrder
      }
      if (itemEditingId.value) {
        await updateScheduleItem(itemEditingId.value, payload)
        ElMessage.success('日程项已更新')
      } else {
        await addScheduleItem(schedule.id, payload)
        ElMessage.success('日程项已创建')
      }
      itemDialogVisible.value = false
      await loadList()
    } catch (error: any) {
      ElMessage.error(error.message || '保存日程项失败')
    } finally {
      submittingItem.value = false
    }
  })
}

const handleDeleteItem = async (itemId: number) => {
  const schedule = activeSchedule.value
  const item = schedule?.items?.find((i) => i.id === itemId)
  if (!item) return
  try {
    await ElMessageBox.confirm(`确认删除日程项“${item.title}”吗？`, '提示', { type: 'warning' })
    await deleteScheduleItem(itemId)
    ElMessage.success('日程项已删除')
    await loadList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除日程项失败')
    }
  }
}

const handleScheduleSelect = (index: string) => {
  activeScheduleId.value = Number(index)
  activeItemId.value = activeSchedule.value?.items?.[0]?.id || null
}

const handleItemSelect = (index: string) => {
  activeItemId.value = Number(index)
}

onMounted(async () => {
  await Promise.all([loadVenues(), loadExperts()])
  await loadList()
})
</script>

<style scoped>
.schedule-manage {
  padding: 20px;
}

.venue-card {
  margin-bottom: 16px;
}

.content-row {
  margin-top: 8px;
}

.panel-card {
  min-height: 560px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.search-form {
  margin-bottom: 6px;
}

.mb-12 {
  margin-bottom: 12px;
}

.schedule-menu,
.item-menu {
  border-right: none;
}

.schedule-menu :deep(.el-menu-item),
.item-menu :deep(.el-menu-item) {
  height: auto;
  min-height: 56px;
  padding: 8px 16px;
  line-height: 1.5;
}

.schedule-menu :deep(.el-menu-item .el-menu-item-content),
.item-menu :deep(.el-menu-item .el-menu-item-content) {
  width: 100%;
}

.menu-main {
  display: flex;
  flex-direction: column;
  gap: 4px;
  width: 100%;
  min-width: 0;
}

.menu-title {
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.menu-sub {
  color: var(--el-text-color-secondary);
  font-size: 12px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.schedule-detail {
  margin-bottom: 16px;
}

.item-section {
  border-top: 1px solid var(--el-border-color-light);
  padding-top: 12px;
}

.item-title {
  margin-bottom: 8px;
  font-weight: 600;
}

.menu-actions {
  margin-left: auto;
  display: flex;
  gap: 4px;
}

@media (max-width: 992px) {
  .header-actions {
    flex-wrap: wrap;
  }
}
</style>
