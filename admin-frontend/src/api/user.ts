import request from '@/utils/request'
import type { PageData } from '@/types/api'

// 用户相关类型
export interface User {
  id?: number
  mobile: string
  nickname: string
  userHead: string
  userAge: number
  userSex: number
  remarkCounts: number
  courseCounts: number
  statusId: number
  gmtCreate?: string
  gmtModified?: string
}

export interface UserPageReq {
  pageCurrent: number
  pageSize: number
  mobile?: string
  nickname?: string
  statusId?: number
}

export interface UserSaveReq {
  mobile: string
  nickname: string
  userHead?: string
  userAge: number
  userSex: number
  statusId: number
}

export interface UserUpdateReq {
  id: number
  mobile: string
  nickname: string
  userHead?: string
  userAge: number
  userSex: number
  statusId: number
}

// 用户API
export const getUserPage = (data: UserPageReq) => {
  return request.post<PageData<User>>('/user/admin/users/page', data)
}

export const saveUser = (data: UserSaveReq) => {
  return request.post<string>('/user/admin/users/save', data)
}

export const updateUser = (data: UserUpdateReq) => {
  return request.put<string>('/user/admin/users/edit', data)
}

export const deleteUser = (id: number) => {
  return request.delete<string>(`/user/admin/users/delete?id=${id}`)
}

export const getUserById = (id: number) => {
  return request.get<User>(`/user/admin/users/view?id=${id}`)
}