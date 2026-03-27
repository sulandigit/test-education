import { defineStore } from 'pinia'
import { ref } from 'vue'
import { authApi } from '@/api'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref({})
  const isLoggedIn = ref(!!token.value)

  // 登录
  const login = async (loginForm) => {
    try {
      const response = await authApi.login(loginForm)
      token.value = response.data.token
      userInfo.value = response.data.userInfo
      isLoggedIn.value = true
      
      // 保存token到localStorage
      localStorage.setItem('token', token.value)
      
      return response
    } catch (error) {
      throw error
    }
  }

  // 登出
  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      // 清理状态
      token.value = ''
      userInfo.value = {}
      isLoggedIn.value = false
      localStorage.removeItem('token')
    }
  }

  // 获取用户信息
  const getUserInfo = async () => {
    try {
      const response = await authApi.getUserInfo()
      userInfo.value = response.data
      return response
    } catch (error) {
      throw error
    }
  }

  // 重置状态
  const resetState = () => {
    token.value = ''
    userInfo.value = {}
    isLoggedIn.value = false
    localStorage.removeItem('token')
  }

  return {
    // 状态
    token,
    userInfo,
    isLoggedIn,
    
    // 方法
    login,
    logout,
    getUserInfo,
    resetState
  }
})