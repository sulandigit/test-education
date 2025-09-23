// 用户相关类型
export interface User {
  id: number
  username: string
  nickname: string
  avatar?: string
  email?: string
  mobile?: string
  status: number
  createTime: string
  updateTime: string
}

export interface LoginRequest {
  username: string
  password: string
  code?: string
}

export interface RegisterRequest {
  username: string
  password: string
  nickname: string
  mobile: string
  code: string
}

// 课程相关类型
export interface Course {
  id: number
  courseName: string
  courseDesc: string
  courseLogo: string
  teacherId: number
  teacherName: string
  categoryId: number
  categoryName: string
  isFree: number
  rulingPrice: number
  courseSale: number
  courseStatus: number
  courseSort: number
  createTime: string
  updateTime: string
}

export interface CourseChapter {
  id: number
  courseId: number
  chapterName: string
  chapterDesc?: string
  chapterSort: number
  periods: CoursePeriod[]
}

export interface CoursePeriod {
  id: number
  chapterId: number
  periodName: string
  periodDesc?: string
  periodSort: number
  videoUrl?: string
  videoLength?: number
  isFree: number
}

// 订单相关类型
export interface Order {
  id: number
  orderNo: string
  userId: number
  courseId: number
  courseName: string
  rulingPrice: number
  orderStatus: number
  payTime?: string
  createTime: string
}

// API响应类型
export interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
  timestamp?: number
}

export interface PageResponse<T = any> {
  list: T[]
  totalCount: number
  totalPage: number
  pageCurrent: number
  pageSize: number
}

// 分页请求类型
export interface PageRequest {
  pageCurrent: number
  pageSize: number
}

// 搜索请求类型
export interface CourseSearchRequest extends PageRequest {
  courseName?: string
  categoryId?: number
  isFree?: number
  orderBy?: string
}

// 学习进度类型
export interface StudyProgress {
  courseId: number
  periodId: number
  studyProgress: number
  studyTime: number
  updateTime: string
}

// 课程评价类型
export interface CourseComment {
  id: number
  courseId: number
  userId: number
  userName: string
  userAvatar?: string
  commentText: string
  commentStar: number
  createTime: string
}

// 系统配置类型
export interface SysConfig {
  websiteName: string
  websiteLogo: string
  websiteDesc: string
  websiteKeywords: string
  websiteIcp: string
}

// 轮播图类型
export interface Carousel {
  id: number
  carouselTitle: string
  carouselUrl: string
  carouselTarget: string
  carouselSort: number
  statusId: number
}

// 友情链接类型
export interface Link {
  id: number
  linkName: string
  linkUrl: string
  linkTarget: string
  linkSort: number
  statusId: number
}