import request from '@/utils/request'
import type { GroupOrderVo, GroupMember, GroupInviteInfoVo } from './types'

export interface AddMemberData {
  memberName: string
  memberEmail: string
}

export const groupApi = {
  getGroupDetail(orderId: number) {
    return request.get<GroupOrderVo, GroupOrderVo>(`/orders/${orderId}/group`)
  },
  async getInviteUrl(orderId: number) {
    const res = await request.get<{ inviteUrl: string }, { inviteUrl: string }>(`/orders/${orderId}/group/invite`)
    return res.inviteUrl
  },
  addMember(orderId: number, data: AddMemberData) {
    return request.post<GroupMember, GroupMember>(`/orders/${orderId}/group/members`, data)
  },
  updateMember(orderId: number, memberId: number, data: AddMemberData) {
    return request.put<GroupMember, GroupMember>(`/orders/${orderId}/group/members/${memberId}`, data)
  },
  clearMember(orderId: number, memberId: number) {
    return request.delete(`/orders/${orderId}/group/members/${memberId}`)
  },
  regenerateCredential(orderId: number, memberId: number) {
    return request.post(`/orders/${orderId}/group/members/${memberId}/credential`)
  },
  // Public invite endpoints (no auth)
  getInviteInfo(token: string) {
    return request.get<GroupInviteInfoVo, GroupInviteInfoVo>(`/group/invite/${token}`)
  },
  fillMemberByToken(token: string, data: AddMemberData) {
    return request.post(`/group/invite/${token}/fill`, data)
  }
}
