const TOKEN_KEY = 'portal_token'
const USER_INFO_KEY = 'portal_user_info'

export const storage = {
  getToken(): string {
    return localStorage.getItem(TOKEN_KEY) || ''
  },
  setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token)
  },
  removeToken(): void {
    localStorage.removeItem(TOKEN_KEY)
  },
  getUserInfo<T>(): T | null {
    const info = localStorage.getItem(USER_INFO_KEY)
    return info ? (JSON.parse(info) as T) : null
  },
  setUserInfo<T>(info: T): void {
    localStorage.setItem(USER_INFO_KEY, JSON.stringify(info))
  },
  removeUserInfo(): void {
    localStorage.removeItem(USER_INFO_KEY)
  },
  clear(): void {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_INFO_KEY)
  }
}
