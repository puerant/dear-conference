import request from '@/utils/request'
import type { PageData } from '@/utils/request'

export interface Submission {
  id: number
  title: string
  abstract: string  // backend now serializes abstract0 as "abstract" via @JsonProperty
  category: string
  fileUrl: string
  status: string    // backend serializes SubmissionStatus enum code: "pending"|"reviewing"|"accepted"|"rejected"
  userId: number
  speakerName: string
  speakerEmail: string
  reviewCount: number
  createdAt: string
  updatedAt: string
}

export interface Review {
  id: number
  reviewerId: number
  reviewerName: string
  result: string
  score: number
  comment: string
  createdAt: string
}

export interface SubmissionDetail extends Submission {
  reviews: Review[]
}

export interface SubmissionListParams {
  page?: number
  pageSize?: number
  keyword?: string
  status?: string
  category?: string
}

export const submissionApi = {
  getList(params: SubmissionListParams) {
    return request.get<PageData<Submission>, PageData<Submission>>('/admin/submissions', { params })
  },

  getDetail(id: number) {
    return request.get<SubmissionDetail, SubmissionDetail>(`/admin/submissions/${id}`)
  },

  updateStatus(id: number, status: string) {
    return request.put(`/admin/submissions/${id}/status`, { status })
  },

  assignReviewer(id: number, reviewerIds: number[]) {
    return request.put(`/admin/submissions/${id}/assign`, { reviewerIds })
  },

  delete(id: number) {
    return request.delete(`/admin/submissions/${id}`)
  },

  // Get reviewer list for assignment dialog
  getReviewers() {
    return request.get<PageData<{ id: number; name: string; email: string }>, PageData<{ id: number; name: string; email: string }>>('/admin/users', {
      params: { role: 'reviewer', pageSize: 100 }
    })
  }
}
