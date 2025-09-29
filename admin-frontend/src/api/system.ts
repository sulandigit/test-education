import request from '@/utils/request'
import type { PageData } from '@/types/api'

// 系统用户相关类型
export interface SysUser {
  id?: number
  mobile: string
  realName: string
  nickname: string
  remark: string
  statusId: number
  sortNo: number
  gmtCreate?: string
  gmtModified?: string
  roleNames?: string[]
}

export interface SysUserPageReq {
  pageCurrent: number
  pageSize: number
  mobile?: string
  realName?: string
}

export interface SysUserSaveReq {
  mobile: string
  realName: string
  nickname: string
  remark?: string
  statusId: number
  sortNo: number
  password: string
}

export interface SysUserUpdateReq {
  id: number
  mobile: string
  realName: string
  nickname: string
  remark?: string
  statusId: number
  sortNo: number
}

// 系统用户API
export const getSysUserPage = (data: SysUserPageReq) => {
  return request.post<PageData<SysUser>>('/system/admin/sys/user/page', data)
}

export const saveSysUser = (data: SysUserSaveReq) => {
  return request.post<string>('/system/admin/sys/user/save', data)
}

export const updateSysUser = (data: SysUserUpdateReq) => {
  return request.post<string>('/system/admin/sys/user/edit', data)
}

export const deleteSysUser = (id: number) => {
  return request.post<string>('/system/admin/sys/user/delete', { id })
}

export const getSysUserById = (id: number) => {
  return request.post<SysUser>('/system/admin/sys/user/view', { id })
}