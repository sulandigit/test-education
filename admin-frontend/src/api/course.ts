import request from '@/utils/request'
import type { PageData } from '@/types/api'

// 课程分类相关类型
export interface Category {
  id?: number
  categoryName: string
  parentId: number
  categoryLogo: string
  remark: string
  statusId: number
  sortNo: number
  gmtCreate?: string
  gmtModified?: string
  children?: Category[]
}

export interface CategoryPageReq {
  pageCurrent: number
  pageSize: number
  categoryName?: string
}

// 课程相关类型
export interface Course {
  id?: number
  categoryId: number
  categoryName?: string
  courseName: string
  courseLogo: string
  courseIntroduce: string
  teacherName: string
  teacherHead: string
  teacherIntroduce: string
  courseSort: number
  coursePrice: number
  putawayTime: string
  statusId: number
  gmtCreate?: string
  gmtModified?: string
}

export interface CoursePageReq {
  pageCurrent: number
  pageSize: number
  courseName?: string
  categoryId?: number
  statusId?: number
}

// 专区相关类型
export interface Zone {
  id?: number
  zoneName: string
  zoneDesc: string
  statusId: number
  sortNo: number
  gmtCreate?: string
  gmtModified?: string
}

export interface ZonePageReq {
  pageCurrent: number
  pageSize: number
  zoneName?: string
}

// 分类API
export const getCategoryPage = (data: CategoryPageReq) => {
  return request.post<PageData<Category>>('/course/admin/category/page', data)
}

export const getCategoryList = () => {
  return request.post<Category[]>('/course/admin/category/list', {})
}

export const saveCategory = (data: Omit<Category, 'id'>) => {
  return request.post<string>('/course/admin/category/save', data)
}

export const updateCategory = (data: Category) => {
  return request.put<string>('/course/admin/category/edit', data)
}

export const deleteCategory = (id: number) => {
  return request.delete<string>(`/course/admin/category/delete?id=${id}`)
}

export const getCategoryById = (id: number) => {
  return request.get<Category>(`/course/admin/category/view?id=${id}`)
}

// 课程API
export const getCoursePage = (data: CoursePageReq) => {
  return request.post<PageData<Course>>('/course/admin/course/page', data)
}

export const saveCourse = (data: Omit<Course, 'id'>) => {
  return request.post<string>('/course/admin/course/save', data)
}

export const updateCourse = (data: Course) => {
  return request.put<string>('/course/admin/course/edit', data)
}

export const deleteCourse = (id: number) => {
  return request.delete<string>(`/course/admin/course/delete?id=${id}`)
}

export const getCourseById = (id: number) => {
  return request.get<Course>(`/course/admin/course/view?id=${id}`)
}

// 专区API
export const getZonePage = (data: ZonePageReq) => {
  return request.post<PageData<Zone>>('/course/admin/zone/page', data)
}

export const saveZone = (data: Omit<Zone, 'id'>) => {
  return request.post<string>('/course/admin/zone/save', data)
}

export const updateZone = (data: Zone) => {
  return request.put<string>('/course/admin/zone/edit', data)
}

export const deleteZone = (id: number) => {
  return request.delete<string>(`/course/admin/zone/delete?id=${id}`)
}

export const getZoneById = (id: number) => {
  return request.get<Zone>(`/course/admin/zone/view?id=${id}`)
}