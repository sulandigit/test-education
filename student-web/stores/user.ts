import { defineStore } from 'pinia'
import type { User, LoginRequest } from '@/types'
import { authApi } from '@/api/auth'

interface UserState {
  user: User | null
  token: string | null
  isLoggedIn: boolean
}

export const useUserStore = defineStore('user', {
  state: (): UserState => ({
    user: null,
    token: null,
    isLoggedIn: false
  }),

  getters: {
    // 用户头像
    userAvatar: (state): string => {
      return state.user?.avatar || '/images/default-avatar.png'
    },

    // 用户昵称或用户名
    displayName: (state): string => {
      return state.user?.nickname || state.user?.username || '未登录'
    },

    // 是否为VIP用户
    isVip: (state): boolean => {
      return state.user?.status === 1 // 假设status=1为VIP
    }
  },

  actions: {
    /**
     * 初始化用户状态（从本地存储恢复）
     */
    initializeAuth() {
      if (process.server) return

      try {
        // 优先从localStorage获取，然后从sessionStorage获取
        const token = localStorage.getItem('token') || sessionStorage.getItem('token')
        const userInfo = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo')

        if (token && userInfo) {
          this.token = token
          this.user = JSON.parse(userInfo)
          this.isLoggedIn = true
        }
      } catch (error) {
        console.error('初始化认证状态失败:', error)
        this.clearAuth()
      }
    },

    /**
     * 用户登录
     */
    async login(credentials: LoginRequest, remember = false): Promise<void> {
      try {
        const response = await authApi.login(credentials)
        const { user, token } = response.data

        // 更新store状态
        this.user = user
        this.token = token
        this.isLoggedIn = true

        // 保存到本地存储
        const storage = remember ? localStorage : sessionStorage
        storage.setItem('token', token)
        storage.setItem('userInfo', JSON.stringify(user))

        // 如果选择了记住我，清除session存储
        if (remember) {
          sessionStorage.removeItem('token')
          sessionStorage.removeItem('userInfo')
        }
      } catch (error) {
        throw error
      }
    },

    /**
     * 用户注册
     */
    async register(userData: any): Promise<void> {
      try {
        const response = await authApi.register(userData)
        // 注册成功后可以选择自动登录或跳转到登录页
        ElMessage.success('注册成功')
      } catch (error) {
        throw error
      }
    },

    /**
     * 用户退出登录
     */
    async logout(): Promise<void> {
      try {
        // 调用后端注销接口
        await authApi.logout()
      } catch (error) {
        console.error('注销接口调用失败:', error)
      } finally {
        // 无论接口是否成功，都清除本地状态
        this.clearAuth()
      }
    },

    /**
     * 获取当前用户信息
     */
    async fetchCurrentUser(): Promise<void> {
      try {
        const response = await authApi.getCurrentUser()
        this.user = response.data

        // 更新本地存储
        const storage = localStorage.getItem('token') ? localStorage : sessionStorage
        storage.setItem('userInfo', JSON.stringify(this.user))
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 如果获取失败，可能是token过期，清除认证状态
        this.clearAuth()
        throw error
      }
    },

    /**
     * 更新用户信息
     */
    async updateProfile(userData: Partial<User>): Promise<void> {
      try {
        const response = await authApi.updateUser(userData)
        this.user = response.data

        // 更新本地存储
        const storage = localStorage.getItem('token') ? localStorage : sessionStorage
        storage.setItem('userInfo', JSON.stringify(this.user))

        ElMessage.success('个人信息更新成功')
      } catch (error) {
        ElMessage.error('更新失败，请重试')
        throw error
      }
    },

    /**
     * 修改密码
     */
    async changePassword(oldPassword: string, newPassword: string): Promise<void> {
      try {
        await authApi.changePassword({
          oldPassword,
          newPassword
        })
        ElMessage.success('密码修改成功')
      } catch (error) {
        ElMessage.error('密码修改失败')
        throw error
      }
    },

    /**
     * 上传头像
     */
    async uploadAvatar(file: File): Promise<void> {
      try {
        const response = await authApi.uploadAvatar(file)
        
        if (this.user) {
          this.user.avatar = response.data.url

          // 更新本地存储
          const storage = localStorage.getItem('token') ? localStorage : sessionStorage
          storage.setItem('userInfo', JSON.stringify(this.user))
        }

        ElMessage.success('头像上传成功')
      } catch (error) {
        ElMessage.error('头像上传失败')
        throw error
      }
    },

    /**
     * 清除认证状态
     */
    clearAuth(): void {
      this.user = null
      this.token = null
      this.isLoggedIn = false

      // 清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      sessionStorage.removeItem('token')
      sessionStorage.removeItem('userInfo')
    },

    /**
     * 检查登录状态
     */
    checkAuthStatus(): boolean {
      if (process.server) return false
      
      const token = localStorage.getItem('token') || sessionStorage.getItem('token')
      const userInfo = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo')
      
      return !!(token && userInfo)
    },

    /**
     * 刷新token（如果后端支持）
     */
    async refreshToken(): Promise<void> {
      try {
        // 如果后端支持refresh token
        // const response = await authApi.refreshToken()
        // this.token = response.data.token
        // 更新本地存储...
        
        // 目前简单处理：重新获取用户信息验证token有效性
        await this.fetchCurrentUser()
      } catch (error) {
        this.clearAuth()
        throw error
      }
    }
  },

  persist: {
    // 持久化配置（如果使用pinia-plugin-persistedstate）
    key: 'user-store',
    storage: process.client ? localStorage : undefined,
    paths: ['user', 'token', 'isLoggedIn']
  }
})