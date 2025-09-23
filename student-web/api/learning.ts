import request from '@/utils/request'
import type { 
  StudyProgress,
  Course,
  PageResponse, 
  ApiResponse 
} from '@/types'

/**
 * 学习相关API
 */
export const learningApi = {
  /**
   * 获取我的课程列表
   */
  getMyCourses(params: {
    pageCurrent: number
    pageSize: number
    courseName?: string
  }): Promise<ApiResponse<PageResponse<Course>>> {
    return request.post('/course/auth/user/course/page', params)
  },

  /**
   * 获取学习进度
   */
  getStudyProgress(params: {
    courseId?: number
    periodId?: number
  }): Promise<ApiResponse<StudyProgress[]>> {
    return request.post('/course/api/user/study/progress', params)
  },

  /**
   * 更新学习进度
   */
  updateProgress(data: {
    courseId: number
    periodId: number
    studyProgress: number
    studyTime: number
  }): Promise<ApiResponse<void>> {
    return request.post('/course/auth/user/study/progress/update', data)
  },

  /**
   * 获取收藏课程列表
   */
  getCollectedCourses(params: {
    pageCurrent: number
    pageSize: number
  }): Promise<ApiResponse<PageResponse<Course>>> {
    return request.post('/course/auth/user/course/collect/page', params)
  },

  /**
   * 添加课程收藏
   */
  addCourseCollect(courseId: number): Promise<ApiResponse<void>> {
    return request.post('/course/auth/user/course/collect/add', { courseId })
  },

  /**
   * 取消课程收藏
   */
  removeCourseCollect(courseId: number): Promise<ApiResponse<void>> {
    return request.post('/course/auth/user/course/collect/remove', { courseId })
  },

  /**
   * 检查课程收藏状态
   */
  checkCollectStatus(courseId: number): Promise<ApiResponse<{ isCollected: boolean }>> {
    return request.get(`/course/auth/user/course/collect/status/${courseId}`)
  },

  /**
   * 获取学习统计
   */
  getStudyStats(): Promise<ApiResponse<{
    totalCourses: number
    completedCourses: number
    studyTime: number
    studyDays: number
  }>> {
    return request.get('/course/auth/user/study/stats')
  },

  /**
   * 获取学习记录
   */
  getStudyRecords(params: {
    pageCurrent: number
    pageSize: number
    startDate?: string
    endDate?: string
  }): Promise<ApiResponse<PageResponse<{
    courseId: number
    courseName: string
    studyTime: number
    progress: number
    studyDate: string
  }>>> {
    return request.post('/course/auth/user/study/records', params)
  },

  /**
   * 保存学习笔记
   */
  saveNote(data: {
    courseId: number
    periodId: number
    content: string
  }): Promise<ApiResponse<void>> {
    return request.post('/course/auth/user/study/note/save', data)
  },

  /**
   * 获取学习笔记
   */
  getNote(courseId: number, periodId: number): Promise<ApiResponse<{
    content: string
    updateTime: string
  }>> {
    return request.get(`/course/auth/user/study/note/${courseId}/${periodId}`)
  },

  /**
   * 获取学习笔记列表
   */
  getNoteList(params: {
    courseId?: number
    pageCurrent: number
    pageSize: number
  }): Promise<ApiResponse<PageResponse<{
    id: number
    courseId: number
    courseName: string
    periodId: number
    periodName: string
    content: string
    createTime: string
  }>>> {
    return request.post('/course/auth/user/study/notes', params)
  },

  /**
   * 删除学习笔记
   */
  deleteNote(noteId: number): Promise<ApiResponse<void>> {
    return request.delete(`/course/auth/user/study/note/${noteId}`)
  },

  /**
   * 获取学习日历
   */
  getStudyCalendar(params: {
    year: number
    month: number
  }): Promise<ApiResponse<{
    date: string
    studyTime: number
    coursesCount: number
  }[]>> {
    return request.post('/course/auth/user/study/calendar', params)
  },

  /**
   * 获取学习排行榜
   */
  getStudyRanking(params: {
    type: 'time' | 'courses' | 'days' // 按学习时长、课程数、学习天数排行
    period: 'week' | 'month' | 'year' // 时间周期
    limit: number
  }): Promise<ApiResponse<{
    rank: number
    userId: number
    userName: string
    avatar: string
    value: number
    isCurrentUser: boolean
  }[]>> {
    return request.post('/course/auth/user/study/ranking', params)
  },

  /**
   * 获取学习建议
   */
  getStudySuggestions(): Promise<ApiResponse<{
    type: 'continue' | 'new' | 'review'
    title: string
    description: string
    courseId?: number
    courseName?: string
    action: string
  }[]>> {
    return request.get('/course/auth/user/study/suggestions')
  },

  /**
   * 标记课程为已完成
   */
  markCourseCompleted(courseId: number): Promise<ApiResponse<void>> {
    return request.post('/course/auth/user/course/complete', { courseId })
  },

  /**
   * 获取证书信息
   */
  getCertificate(courseId: number): Promise<ApiResponse<{
    certificateId: string
    courseName: string
    completionDate: string
    certificateUrl: string
  }>> {
    return request.get(`/course/auth/user/certificate/${courseId}`)
  },

  /**
   * 生成学习证书
   */
  generateCertificate(courseId: number): Promise<ApiResponse<{
    certificateUrl: string
  }>> {
    return request.post('/course/auth/user/certificate/generate', { courseId })
  }
}