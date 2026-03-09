<template>
  <div class="hotel-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>酒店管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            添加酒店
          </el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchKeyword" placeholder="名称/地址" clearable @keyup.enter="loadList" />
        </el-form-item>
        <el-form-item label="推荐">
          <el-select v-model="searchIsRecommended" placeholder="全部" clearable>
            <el-option label="推荐" :value="1" />
            <el-option label="普通" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadList">搜索</el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="酒店名称" min-width="180" />
        <el-table-column label="星级" width="100">
          <template #default="{ row }">
            <el-rate v-model="row.starLevel" disabled />
          </template>
        </el-table-column>
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="contactPhone" label="电话" width="130" />
        <el-table-column label="推荐" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isRecommended ? 'success' : 'info'">
              {{ row.isRecommended ? '推荐' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="房型" width="100">
          <template #default="{ row }">
            <el-button type="primary" link @click="showRooms(row)">
              {{ row.rooms?.length || 0 }} 种
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleAddRoom(row)">房型</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 酒店对话框 -->
    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑酒店' : '新增酒店'" width="700px">
      <el-form ref="formRef" :model="formData" :rules="rules" label-width="100px">
        <el-form-item label="酒店名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入酒店名称" maxlength="200" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" placeholder="请输入地址" maxlength="500" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="星级" prop="starLevel">
              <el-rate v-model="formData.starLevel" :max="5" show-score />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="电话" prop="contactPhone">
              <el-input v-model="formData.contactPhone" placeholder="联系电话" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入酒店描述" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="图片URL" prop="imageUrl">
              <el-input v-model="formData.imageUrl" placeholder="酒店图片URL" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预订链接" prop="bookingUrl">
              <el-input v-model="formData.bookingUrl" placeholder="预订链接URL" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="推荐" prop="isRecommended">
          <el-switch
            v-model="formData.isRecommended"
            :active-value="1"
            :inactive-value="0"
            active-text="推荐"
            inactive-text="普通"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>

    <!-- 房型管理对话框 -->
    <el-dialog v-model="roomDialogVisible" title="房型管理" width="800px">
      <div class="room-header">
        <span>房型列表</span>
        <el-button type="primary" size="small" @click="handleAddRoomRow">添加房型</el-button>
      </div>
      <el-table :data="roomList" stripe>
        <el-table-column label="房型名称" min-width="150">
          <template #default="{ row }">
            <el-input v-model="row.roomType" size="small" placeholder="如：标准间" />
          </template>
        </el-table-column>
        <el-table-column label="价格" width="130">
          <template #default="{ row }">
            <el-input-number v-model="row.price" :min="0" :precision="2" size="small" style="width: 120px" />
          </template>
        </el-table-column>
        <el-table-column label="库存" width="100">
          <template #default="{ row }">
            <el-input-number v-model="row.stock" :min="0" size="small" style="width: 90px" />
          </template>
        </el-table-column>
        <el-table-column label="描述" min-width="180">
          <template #default="{ row }">
            <el-input v-model="row.description" size="small" placeholder="房型描述" />
          </template>
        </el-table-column>
        <el-table-column label="排序" width="80">
          <template #default="{ row }">
            <el-input-number v-model="row.sortOrder" :min="0" size="small" style="width: 70px" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template #default="{ row, $index }">
            <el-button type="danger" link size="small" @click="removeRoomRow($index, row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="roomDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRooms" :loading="savingRooms">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import {
  getHotelList,
  createHotel,
  updateHotel,
  deleteHotel,
  addHotelRoom,
  updateHotelRoom,
  deleteHotelRoom
} from '@/api/conference'
import type { HotelVo, HotelDto, HotelRoomDto } from '@/api/conference'

const loading = ref(false)
const submitting = ref(false)
const savingRooms = ref(false)
const tableData = ref<HotelVo[]>([])
const dialogVisible = ref(false)
const roomDialogVisible = ref(false)
const editingId = ref<number | null>(null)
const currentHotelId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const searchKeyword = ref('')
const searchIsRecommended = ref<number | undefined>()
const roomList = ref<(HotelRoomDto & { id?: number; isNew?: boolean })[]>([])

const formData = reactive<HotelDto>({
  name: '',
  address: '',
  starLevel: 3,
  contactPhone: '',
  description: '',
  imageUrl: '',
  bookingUrl: '',
  isRecommended: 0,
  sortOrder: 0
})

const rules: FormRules = {
  name: [{ required: true, message: '请输入酒店名称', trigger: 'blur' }]
}

const loadList = async () => {
  try {
    loading.value = true
    const data = await getHotelList({
      keyword: searchKeyword.value || undefined,
      isRecommended: searchIsRecommended.value
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
  formData.address = ''
  formData.starLevel = 3
  formData.contactPhone = ''
  formData.description = ''
  formData.imageUrl = ''
  formData.bookingUrl = ''
  formData.isRecommended = 0
  formData.sortOrder = 0
  editingId.value = null
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row: HotelVo) => {
  resetForm()
  editingId.value = row.id
  Object.assign(formData, {
    name: row.name,
    address: row.address || '',
    starLevel: row.starLevel || 3,
    contactPhone: row.contactPhone || '',
    description: row.description || '',
    imageUrl: row.imageUrl || '',
    bookingUrl: row.bookingUrl || '',
    isRecommended: row.isRecommended,
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
        await updateHotel(editingId.value, formData)
        ElMessage.success('更新成功')
      } else {
        await createHotel(formData)
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

const handleDelete = async (row: HotelVo) => {
  try {
    await ElMessageBox.confirm(`确定要删除酒店"${row.name}"吗？`, '提示', { type: 'warning' })
    await deleteHotel(row.id)
    ElMessage.success('删除成功')
    loadList()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const showRooms = (row: HotelVo) => {
  currentHotelId.value = row.id
  roomList.value = (row.rooms || []).map(room => ({ ...room }))
  roomDialogVisible.value = true
}

const handleAddRoom = (row: HotelVo) => {
  currentHotelId.value = row.id
  roomList.value = (row.rooms || []).map(room => ({ ...room }))
  handleAddRoomRow()
  roomDialogVisible.value = true
}

const handleAddRoomRow = () => {
  roomList.value.push({
    hotelId: currentHotelId.value || undefined,
    roomType: '',
    price: 0,
    stock: 0,
    description: '',
    sortOrder: roomList.value.length,
    isNew: true
  })
}

const removeRoomRow = async (index: number, row: any) => {
  if (row.id && !row.isNew) {
    try {
      await deleteHotelRoom(row.id)
    } catch (error) {
      console.error('删除失败:', error)
    }
  }
  roomList.value.splice(index, 1)
}

const saveRooms = async () => {
  if (!currentHotelId.value) return

  try {
    savingRooms.value = true
    for (const room of roomList.value) {
      if (!room.roomType) continue

      if (room.isNew || !room.id) {
        await addHotelRoom(currentHotelId.value, {
          roomType: room.roomType,
          price: room.price,
          stock: room.stock,
          description: room.description,
          sortOrder: room.sortOrder
        })
      } else {
        await updateHotelRoom(room.id, {
          roomType: room.roomType,
          price: room.price,
          stock: room.stock,
          description: room.description,
          sortOrder: room.sortOrder
        })
      }
    }
    ElMessage.success('保存成功')
    roomDialogVisible.value = false
    loadList()
  } catch (error: any) {
    ElMessage.error(error.message || '保存失败')
  } finally {
    savingRooms.value = false
  }
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.hotel-manage {
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

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 600;
}
</style>
