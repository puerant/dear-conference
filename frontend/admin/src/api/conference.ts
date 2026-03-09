import request from '@/utils/request'

// ==================== 类型定义 ====================

export interface ConferenceInfo {
  id: number
  title: string
  subtitle: string | null
  description: string | null
  startDate: string
  endDate: string
  location: string | null
  address: string | null
  bannerUrl: string | null
  contactName: string | null
  contactPhone: string | null
  contactEmail: string | null
  isPublished: number
  createdAt: string
  updatedAt: string
}

export interface ConferenceInfoDto {
  id?: number
  title: string
  subtitle?: string
  description?: string
  startDate: string
  endDate: string
  location?: string
  address?: string
  bannerUrl?: string
  contactName?: string
  contactPhone?: string
  contactEmail?: string
  isPublished?: number
}

export interface ExpertVo {
  id: number
  name: string
  title: string | null
  organization: string | null
  bio: string | null
  avatarUrl: string | null
  isKeynote: number
  sortOrder: number
  createdAt: string
  updatedAt: string
}

export interface ExpertDto {
  id?: number
  name: string
  title?: string
  organization?: string
  bio?: string
  avatarUrl?: string
  isKeynote?: number
  sortOrder?: number
}

export interface ExpertSimpleVo {
  id: number
  name: string
  title: string | null
  organization: string | null
  avatarUrl: string | null
}

export interface ScheduleVo {
  id: number
  date: string
  startTime: string | null
  endTime: string | null
  title: string
  description: string | null
  venue: string | null
  sortOrder: number
  createdAt: string
  updatedAt: string
  items: ScheduleItemVo[]
}

export interface ScheduleDto {
  id?: number
  date: string
  startTime?: string
  endTime?: string
  title: string
  description?: string
  venue?: string
  sortOrder?: number
}

export interface ScheduleItemVo {
  id: number
  scheduleId: number
  time: string
  title: string
  description: string | null
  speakerId: number | null
  sortOrder: number
  createdAt: string
  speaker: ExpertSimpleVo | null
}

export interface ScheduleItemDto {
  id?: number
  scheduleId?: number
  time: string
  title: string
  description?: string
  speakerId?: number
  sortOrder?: number
}

export interface ScheduleGroupVo {
  date: string
  schedules: ScheduleVo[]
}

export interface HotelVo {
  id: number
  name: string
  address: string | null
  starLevel: number
  contactPhone: string | null
  description: string | null
  imageUrl: string | null
  bookingUrl: string | null
  isRecommended: number
  sortOrder: number
  createdAt: string
  updatedAt: string
  rooms: HotelRoomVo[]
}

export interface HotelDto {
  id?: number
  name: string
  address?: string
  starLevel?: number
  contactPhone?: string
  description?: string
  imageUrl?: string
  bookingUrl?: string
  isRecommended?: number
  sortOrder?: number
}

export interface HotelRoomVo {
  id: number
  hotelId: number
  roomType: string
  price: number
  stock: number
  description: string | null
  sortOrder: number
  createdAt: string
  updatedAt: string
}

export interface HotelRoomDto {
  id?: number
  hotelId?: number
  roomType: string
  price: number
  stock?: number
  description?: string
  sortOrder?: number
}

export interface HotelListVo {
  recommended: HotelVo[]
  hotels: HotelVo[]
}

export interface ExpertListVo {
  keynoteSpeakers: ExpertVo[]
  speakers: ExpertVo[]
}

// ==================== 会议模块管理端 API ====================

// ---------- 会议信息 ----------

/**
 * 获取会议信息
 */
export function getConferenceInfo() {
  return request.get<API.ConferenceInfo>('/admin/conference')
}

/**
 * 保存/更新会议信息
 */
export function saveConferenceInfo(data: API.ConferenceInfoDto) {
  return request.post('/admin/conference', data)
}

// ---------- 日程管理 ----------

/**
 * 获取日程列表
 */
export function getScheduleList(params?: {
  startDate?: string
  endDate?: string
}) {
  return request.get<API.ScheduleVo[]>('/admin/schedule', { params })
}

/**
 * 获取日程详情
 */
export function getScheduleDetail(id: number) {
  return request.get<API.ScheduleVo>(`/admin/schedule/${id}`)
}

/**
 * 创建日程
 */
export function createSchedule(data: API.ScheduleDto) {
  return request.post('/admin/schedule', data)
}

/**
 * 更新日程
 */
export function updateSchedule(id: number, data: API.ScheduleDto) {
  return request.put(`/admin/schedule/${id}`, data)
}

/**
 * 删除日程
 */
export function deleteSchedule(id: number) {
  return request.delete(`/admin/schedule/${id}`)
}

/**
 * 添加日程项
 */
export function addScheduleItem(scheduleId: number, data: API.ScheduleItemDto) {
  return request.post(`/admin/schedule/${scheduleId}/items`, data)
}

/**
 * 更新日程项
 */
export function updateScheduleItem(id: number, data: API.ScheduleItemDto) {
  return request.put(`/admin/schedule-item/${id}`, data)
}

/**
 * 删除日程项
 */
export function deleteScheduleItem(id: number) {
  return request.delete(`/admin/schedule-item/${id}`)
}

// ---------- 专家管理 ----------

/**
 * 获取专家列表
 */
export function getExpertList(params?: {
  keyword?: string
  isKeynote?: number
}) {
  return request.get<API.ExpertVo[]>('/admin/experts', { params })
}

/**
 * 获取专家详情
 */
export function getExpertDetail(id: number) {
  return request.get<API.ExpertVo>(`/admin/expert/${id}`)
}

/**
 * 创建专家
 */
export function createExpert(data: API.ExpertDto) {
  return request.post('/admin/expert', data)
}

/**
 * 更新专家
 */
export function updateExpert(id: number, data: API.ExpertDto) {
  return request.put(`/admin/expert/${id}`, data)
}

/**
 * 删除专家
 */
export function deleteExpert(id: number) {
  return request.delete(`/admin/expert/${id}`)
}

/**
 * 上传专家头像
 */
export function uploadExpertAvatar(id: number, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post(`/api/admin/expert/${id}/avatar`, formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// ---------- 黨馆管理 ----------

/**
 * 获取酒店列表
 */
export function getHotelList(params?: {
  keyword?: string
  isRecommended?: number
}) {
  return request.get<API.HotelVo[]>('/admin/hotels', { params })
}

/**
 * 获取酒店详情
 */
export function getHotelDetail(id: number) {
  return request.get<API.HotelVo>(`/admin/hotel/${id}`)
}

/**
 * 创建酒店
 */
export function createHotel(data: API.HotelDto) {
  return request.post('/admin/hotel', data)
}

/**
 * 更新酒店
 */
export function updateHotel(id: number, data: API.HotelDto) {
  return request.put(`/admin/hotel/${id}`, data)
}

/**
 * 删除酒店
 */
export function deleteHotel(id: number) {
  return request.delete(`/admin/hotel/${id}`)
}

/**
 * 添加房型
 */
export function addHotelRoom(hotelId: number, data: API.HotelRoomDto) {
  return request.post(`/admin/hotel/${hotelId}/rooms`, data)
}

/**
 * 更新房型
 */
export function updateHotelRoom(id: number, data: API.HotelRoomDto) {
  return request.put(`/admin/hotel/room/${id}`, data)
}

/**
 * 删除房型
 */
export function deleteHotelRoom(id: number) {
  return request.delete(`/admin/hotel/room/${id}`)
}
