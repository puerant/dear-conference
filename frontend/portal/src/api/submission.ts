import request from '@/utils/request'
import type { PageData } from '@/utils/request'
import type { Submission } from './types'

export interface SubmissionQueryDto {
  page?: number
  pageSize?: number
  status?: string   // string code: "pending"|"reviewing"|"accepted"|"rejected"
  keyword?: string
}

export interface CreateSubmissionData {
  title: string
  abstract: string
  category: string
  fileUrl?: string
}

export type UpdateSubmissionData = CreateSubmissionData

export const submissionApi = {
  // GET /submissions/my — speaker's own submissions (GET /submissions is reviewer/admin only)
  getList(params?: SubmissionQueryDto) {
    return request.get<PageData<Submission>, PageData<Submission>>('/submissions/my', { params })
  },
  getDetail(id: number) {
    return request.get<Submission, Submission>(`/submissions/${id}`)
  },
  create(data: CreateSubmissionData) {
    return request.post<Submission, Submission>('/submissions', data)
  },
  update(id: number, data: UpdateSubmissionData) {
    return request.put<Submission, Submission>(`/submissions/${id}`, data)
  },
  delete(id: number) {
    return request.delete(`/submissions/${id}`)
  },
  uploadFile(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post<{ url: string }, { url: string }>('/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  }
}
