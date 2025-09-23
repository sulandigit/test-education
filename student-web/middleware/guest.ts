/**
 * 游客中间件
 * 只允许未登录用户访问，已登录用户跳转到首页
 */
export default defineNuxtRouteMiddleware((to) => {
  // 检查是否在客户端
  if (process.server) return

  // 检查用户登录状态
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  
  if (token) {
    // 已登录，跳转到首页或指定页面
    const redirect = to.query.redirect as string
    return navigateTo(redirect || '/')
  }
})