export interface UserInfo {
  id: number
  email: string
  name: string
  role: string
  avatar: string | null
  createdAt?: string
}

export interface Submission {
  id: number
  userId: number
  title: string
  abstract: string
  category: string
  fileUrl: string | null
  status: string        // backend serializes SubmissionStatus enum as code: "pending"|"reviewing"|"accepted"|"rejected"
  createdAt: string
  updatedAt: string
}

export interface Review {
  id: number
  submissionId: number
  reviewerId: number
  comment: string | null
  result: string | null  // backend serializes ReviewResult enum as code: "accept"|"reject"|"revise"
  score: number | null
  createdAt: string
  submission?: Submission
}

export interface Ticket {
  id: number
  name: string
  price: number
  description: string
  stock: number
  soldCount: number
  status: number
  ticketType: number   // 1=个人票 2=团体票
  minPersons: number | null
  maxPersons: number | null
  createdAt: string
}

export interface Order {
  id: number
  orderNo: string
  userId: number
  ticketId: number
  ticketName: string | null
  quantity: number
  totalAmount: number
  status: string
  orderType: number   // 1=个人 2=团体
  groupName: string | null
  inviteToken: string | null
  attendeeName: string | null
  attendeeEmail: string | null
  createdAt: string
  paidAt: string | null
}

export interface GroupMember {
  id: number
  sequenceNo: number
  memberName: string | null
  memberEmail: string | null
  status: number   // 1=待填写 2=已填写
  filledAt: string | null
}

export interface GroupOrderVo {
  orderId: number
  orderNo: string
  groupName: string
  contactName: string
  contactEmail: string
  totalCount: number
  filledCount: number
  inviteUrl: string | null
  members: GroupMember[]
}

export interface GroupInviteInfoVo {
  groupName: string
  contactName: string
  ticketName: string
  totalCount: number
  filledCount: number
  remainSlots: number
}

export interface Credential {
  id: number
  orderId: number
  credentialNo: string
  /** Base64 编码的 PNG 二维码图片 */
  qrCode: string | null
  seatInfo: string | null
  /** 凭证状态：valid | used | expired */
  status: string
  expireAt: string | null
  createdAt: string
  usedAt: string | null
}

export interface PaymentResponse {
  paymentNo: string
  method: string
  status: string
  qrCode?: string   // WeChat: native pay code_url (needs frontend QR rendering)
  payUrl?: string   // Alipay: page pay URL (open in new tab)
  amount: number
  currency: string
  expireAt: string
}

export interface PaymentStatusVo {
  paymentNo: string
  status: string    // pending | success | failed | closed | refunded
  paidAt?: string
}

// ==================== 会议模块类型 ====================

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

export interface Expert {
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

export interface ExpertSimpleVo {
  id: number
  name: string
  title: string | null
  organization: string | null
  avatarUrl: string | null
}

export interface ExpertListVo {
  keynoteSpeakers: Expert[]
  speakers: Expert[]
}

export interface ScheduleItem {
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

export interface Schedule {
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
  items: ScheduleItem[]
}

export interface ScheduleGroupVo {
  date: string
  schedules: Schedule[]
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

export interface HotelListVo {
  recommended: HotelVo[]
  hotels: HotelVo[]
}
