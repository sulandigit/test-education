import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { login, getCurrentUser, getUserMenus, getUserPermissions } from '@/api/auth'
import type { LoginRequest, UserInfo, MenuItem } from '@/types/api'
import router from '@/router'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref<string>(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(null)
  const menus = ref<MenuItem[]>([])
  const permissions = ref<string[]>([])
  const loading = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const userName = computed(() => userInfo.value?.realName || userInfo.value?.nickname || '')

  // 登录
  const handleLogin = async (loginForm: LoginRequest) => {
    try {
      loading.value = true
      const result = await login(loginForm)
      
      token.value = result.token
      userInfo.value = result.adminUser
      
      localStorage.setItem('token', result.token)
      localStorage.setItem('userInfo', JSON.stringify(result.adminUser))
      
      // 获取用户菜单和权限
      await Promise.all([
        loadUserMenus(),
        loadUserPermissions()
      ])
      
      ElMessage.success('登录成功')
      router.push('/')
    } catch (error) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = null
    menus.value = []
    permissions.value = []
    
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    
    router.push('/login')
  }

  // 加载用户菜单
  const loadUserMenus = async () => {
    try {
      const result = await getUserMenus()
      menus.value = result || []
    } catch (error) {
      console.error('获取用户菜单失败:', error)
    }
  }

  // 加载用户权限
  const loadUserPermissions = async () => {
    try {
      const result = await getUserPermissions()
      permissions.value = result || []
    } catch (error) {
      console.error('获取用户权限失败:', error)
    }
  }

  // 检查权限
  const hasPermission = (permission: string): boolean => {
    return permissions.value.includes(permission)
  }

  // 初始化用户信息（从localStorage恢复）
  const initUserInfo = async () => {
    if (token.value) {
      try {
        const savedUserInfo = localStorage.getItem('userInfo')
        if (savedUserInfo) {
          userInfo.value = JSON.parse(savedUserInfo)
        }
        
        // 重新获取用户信息和权限
        await Promise.all([
          getCurrentUser().then(user => userInfo.value = user),
          loadUserMenus(),
          loadUserPermissions()
        ])
      } catch (error) {
        console.error('初始化用户信息失败:', error)
        logout()
      }
    }
  }

  return {
    token,
    userInfo,
    menus,
    permissions,
    loading,
    isLoggedIn,
    userName,
    handleLogin,
    logout,
    loadUserMenus,
    loadUserPermissions,
    hasPermission,
    initUserInfo
  }
})