import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { storage } from '@/utils/storage'

export interface UserInfo {
  id: number
  email: string
  name: string
  role: string
  avatar: string | null
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(storage.getToken())
  const userInfo = ref<UserInfo | null>(storage.getUserInfo<UserInfo>())

  const isLogin = computed(() => !!token.value)
  const userName = computed(() => userInfo.value?.name || '')
  // Backend returns string role codes: "speaker" | "reviewer" | "attendee" | "admin"
  const userRole = computed(() => userInfo.value?.role ?? '')
  const isSpeaker = computed(() => userRole.value === 'speaker')
  const isReviewer = computed(() => userRole.value === 'reviewer')
  const isAttendee = computed(() => userRole.value === 'attendee')

  function setToken(newToken: string) {
    token.value = newToken
    storage.setToken(newToken)
  }

  function setUserInfo(info: UserInfo) {
    userInfo.value = info
    storage.setUserInfo(info)
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    storage.clear()
  }

  return {
    token,
    userInfo,
    isLogin,
    userName,
    userRole,
    isSpeaker,
    isReviewer,
    isAttendee,
    setToken,
    setUserInfo,
    logout
  }
})
