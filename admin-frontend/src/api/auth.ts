import request from '@/utils/request'
import type { LoginRequest, LoginResponse, UserInfo, MenuItem } from '@/types/api'

// 登录
export const login = (data: LoginRequest) => {
  return request.post<LoginResponse>('/system/admin/login/password', data)
}

// 获取当前用户信息
export const getCurrentUser = () => {
  return request.get<UserInfo>('/system/admin/sys/user/current')
}

// 获取用户菜单
export const getUserMenus = () => {
  return request.post<MenuItem[]>('/system/admin/sys/menu/user/list', {})
}

// 获取用户权限列表
export const getUserPermissions = () => {
  return request.get<string[]>('/system/admin/sys/menu/permission/list')
}