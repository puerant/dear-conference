import request from '@/utils/request'

// ==================== 会议模块 API ====================

/**
 * 获取会议信息
 */
export function getConferenceInfo() {
  return request.get<API.ConferenceInfo>('/api/conference/info')
}

/**
 * 获取日程列表
 */
export function getScheduleList(params?: {
  startDate?: string
  endDate?: string
}) {
  return request.get<API.ScheduleGroupVo[]>('/api/conference/schedule', { params })
}

/**
 * 获取专家列表
 */
export function getExpertList(params?: {
  isKeynote?: boolean
}) {
  return request.get<API.ExpertListVo>('/api/conference/experts', { params })
}

/**
 * 获取酒店列表
 */
export function getHotelList(params?: {
  isRecommended?: boolean
}) {
  return request.get<API.HotelListVo>('/api/conference/hotels', { params })
}
