export default defineNuxtPlugin(async () => {
  const userStore = useUserStore()
  
  // 初始化用户认证状态
  if (process.client) {
    userStore.initializeAuth()
    
    // 如果用户已登录，尝试刷新用户信息
    if (userStore.isLoggedIn) {
      try {
        await userStore.fetchCurrentUser()
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 如果获取用户信息失败，可能是token过期，清除认证状态
        userStore.clearAuth()
      }
    }
  }
})