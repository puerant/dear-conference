import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { storage } from '@/utils/storage'

export interface UserInfo {
  id: number
  email: string
  name: string
  role: string
  avatar: string
}

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string>(storage.getToken())
  const userInfo = ref<UserInfo | null>(storage.getUserInfo<UserInfo>())

  const isLogin = computed(() => !!token.value)
  const userName = computed(() => userInfo.value?.name || '')

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
    setToken,
    setUserInfo,
    logout
  }
})
