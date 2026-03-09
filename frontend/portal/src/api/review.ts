import request from '@/utils/request'
import type { Review } from './types'

export interface CreateReviewData {
  submissionId: number
  comment: string
  score: number
  result: number
}

export const reviewApi = {
  // GET /reviews/my — reviewer's own review records (returns List<Review>, not paginated)
  getMyList() {
    return request.get<Review[], Review[]>('/reviews/my')
  },
  // GET /reviews/submission/{submissionId} — all reviews for a submission (reviewer/admin)
  getBySubmission(submissionId: number) {
    return request.get<Review[], Review[]>(`/reviews/submission/${submissionId}`)
  },
  // POST /reviews — submit review with {submissionId, comment, result, score}
  create(data: CreateReviewData) {
    return request.post<Review, Review>('/reviews', data)
  }
}
