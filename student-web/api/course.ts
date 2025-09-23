import request from '@/utils/request'
import type { 
  Course, 
  CourseChapter,
  CourseComment,
  CourseSearchRequest, 
  PageResponse, 
  ApiResponse 
} from '@/types'

/**
 * 课程相关API
 */
export const courseApi = {
  /**
   * 搜索课程列表
   */
  searchCourses(params: CourseSearchRequest): Promise<ApiResponse<PageResponse<Course>>> {
    return request.post('/course/api/course/search', params)
  },

  /**
   * 获取课程详情
   */
  getCourseDetail(courseId: number): Promise<ApiResponse<Course>> {
    return request.post('/course/api/course/view', { id: courseId })
  },

  /**
   * 获取课程章节列表
   */
  getCourseChapters(courseId: number): Promise<ApiResponse<CourseChapter[]>> {
    return request.get(`/course/api/course/chapters/${courseId}`)
  },

  /**
   * 获取课程评论列表
   */
  getCourseComments(params: {
    courseId: number
    pageCurrent: number
    pageSize: number
  }): Promise<ApiResponse<PageResponse<CourseComment>>> {
    return request.post('/course/api/course/comment', params)
  },

  /**
   * 获取课程分类列表
   */
  getCategories(): Promise<ApiResponse<Array<{
    id: number
    categoryName: string
    categoryDesc: string
    sort: number
  }>>> {
    return request.get('/course/api/course/categories')
  },

  /**
   * 获取热门课程
   */
  getHotCourses(limit = 10): Promise<ApiResponse<Course[]>> {
    return request.get('/course/api/course/hot', { limit })
  },

  /**
   * 获取推荐课程
   */
  getRecommendCourses(limit = 10): Promise<ApiResponse<Course[]>> {
    return request.get('/course/api/course/recommend', { limit })
  },

  /**
   * 获取最新课程
   */
  getLatestCourses(limit = 10): Promise<ApiResponse<Course[]>> {
    return request.get('/course/api/course/latest', { limit })
  },

  /**
   * 获取免费课程
   */
  getFreeCourses(params: {
    pageCurrent: number
    pageSize: number
  }): Promise<ApiResponse<PageResponse<Course>>> {
    return request.post('/course/api/course/free', params)
  }
}

/**
 * 需要认证的课程API
 */
export const authCourseApi = {
  /**
   * 获取学习课程详情（包含视频播放签名等）
   */
  getStudyCourseDetail(courseId: number): Promise<ApiResponse<{
    course: Course
    chapters: CourseChapter[]
    videoSign?: string
    canStudy: boolean
  }>> {
    return request.post('/course/auth/course/view', { id: courseId })
  },

  /**
   * 获取课程学习配置（视频播放签名等）
   */
  getCourseStudyConfig(courseId: number): Promise<ApiResponse<{
    videoSign: string
    videoDomain: string
    videoConfig: any
  }>> {
    return request.post('/course/auth/course/sign', { courseId })
  },

  /**
   * 添加课程评论
   */
  addCourseComment(data: {
    courseId: number
    commentText: string
    commentStar: number
  }): Promise<ApiResponse<void>> {
    return request.post('/course/auth/course/comment/add', data)
  },

  /**
   * 购买课程
   */
  buyCourse(courseId: number): Promise<ApiResponse<{
    orderNo: string
    payUrl?: string
  }>> {
    return request.post('/course/auth/course/buy', { courseId })
  },

  /**
   * 检查课程购买状态
   */
  checkCourseBuyStatus(courseId: number): Promise<ApiResponse<{
    hasBuy: boolean
    canStudy: boolean
    orderNo?: string
  }>> {
    return request.get(`/course/auth/course/buyStatus/${courseId}`)
  }
}