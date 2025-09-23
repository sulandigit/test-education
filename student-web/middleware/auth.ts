/**
 * 认证中间件
 * 检查用户是否已登录，未登录则跳转到登录页面
 */
export default defineNuxtRouteMiddleware((to) => {
  // 检查是否在客户端
  if (process.server) return

  // 检查用户登录状态
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  
  if (!token) {
    // 未登录，跳转到登录页面，并记录当前页面用于登录后跳转
    return navigateTo(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
  }

  // 可以在这里进一步验证token的有效性
  try {
    const userInfo = localStorage.getItem('userInfo') || sessionStorage.getItem('userInfo')
    if (!userInfo) {
      throw new Error('用户信息不存在')
    }
    
    // 解析用户信息
    const user = JSON.parse(userInfo)
    if (!user.id) {
      throw new Error('用户信息无效')
    }
  } catch (error) {
    // 用户信息无效，清除token并跳转到登录页
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    sessionStorage.removeItem('token')
    sessionStorage.removeItem('userInfo')
    
    return navigateTo(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
  }
})