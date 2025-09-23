import { defineStore } from 'pinia'
import type { Course, CourseChapter, CourseSearchRequest, PageResponse } from '@/types'
import { courseApi, authCourseApi } from '@/api/course'

interface CourseState {
  // 课程列表
  courseList: Course[]
  totalCount: number
  loading: boolean
  
  // 当前课程详情
  currentCourse: Course | null
  courseChapters: CourseChapter[]
  
  // 搜索和筛选
  searchParams: CourseSearchRequest
  categories: Array<{
    id: number
    categoryName: string
    categoryDesc: string
    courseCount?: number
  }>
  
  // 收藏课程
  collectedCourses: Course[]
  
  // 推荐课程
  recommendCourses: Course[]
  hotCourses: Course[]
  freeCourses: Course[]
}

export const useCourseStore = defineStore('course', {
  state: (): CourseState => ({
    courseList: [],
    totalCount: 0,
    loading: false,
    
    currentCourse: null,
    courseChapters: [],
    
    searchParams: {
      pageCurrent: 1,
      pageSize: 12,
      courseName: '',
      categoryId: undefined,
      isFree: undefined,
      orderBy: 'createTime'
    },
    categories: [],
    
    collectedCourses: [],
    recommendCourses: [],
    hotCourses: [],
    freeCourses: []
  }),

  getters: {
    // 是否有更多课程
    hasMore: (state): boolean => {
      const totalPages = Math.ceil(state.totalCount / state.searchParams.pageSize)
      return state.searchParams.pageCurrent < totalPages
    },

    // 当前课程是否免费
    isCurrentCourseFree: (state): boolean => {
      return state.currentCourse?.isFree === 1
    },

    // 获取指定分类的课程数量
    getCategoryCount: (state) => (categoryId: number): number => {
      const category = state.categories.find(cat => cat.id === categoryId)
      return category?.courseCount || 0
    },

    // 按分类分组的课程
    coursesByCategory: (state) => {
      const grouped: Record<string, Course[]> = {}
      state.courseList.forEach(course => {
        const categoryName = course.categoryName || '其他'
        if (!grouped[categoryName]) {
          grouped[categoryName] = []
        }
        grouped[categoryName].push(course)
      })
      return grouped
    }
  },

  actions: {
    /**
     * 搜索课程列表
     */
    async searchCourses(params?: Partial<CourseSearchRequest>): Promise<void> {
      try {
        this.loading = true
        
        // 更新搜索参数
        if (params) {
          this.searchParams = { ...this.searchParams, ...params }
        }

        const response = await courseApi.searchCourses(this.searchParams)
        
        if (this.searchParams.pageCurrent === 1) {
          // 首页或新搜索，替换列表
          this.courseList = response.data.list
        } else {
          // 加载更多，追加到列表
          this.courseList.push(...response.data.list)
        }
        
        this.totalCount = response.data.totalCount
      } catch (error) {
        console.error('搜索课程失败:', error)
        ElMessage.error('加载课程列表失败')
        throw error
      } finally {
        this.loading = false
      }
    },

    /**
     * 加载更多课程
     */
    async loadMoreCourses(): Promise<void> {
      if (!this.hasMore || this.loading) return
      
      this.searchParams.pageCurrent++
      await this.searchCourses()
    },

    /**
     * 重置搜索条件
     */
    resetSearch(): void {
      this.searchParams = {
        pageCurrent: 1,
        pageSize: 12,
        courseName: '',
        categoryId: undefined,
        isFree: undefined,
        orderBy: 'createTime'
      }
      this.courseList = []
      this.totalCount = 0
    },

    /**
     * 获取课程详情
     */
    async fetchCourseDetail(courseId: number): Promise<Course> {
      try {
        const response = await courseApi.getCourseDetail(courseId)
        this.currentCourse = response.data
        return response.data
      } catch (error) {
        console.error('获取课程详情失败:', error)
        ElMessage.error('加载课程详情失败')
        throw error
      }
    },

    /**
     * 获取课程章节
     */
    async fetchCourseChapters(courseId: number): Promise<CourseChapter[]> {
      try {
        const response = await courseApi.getCourseChapters(courseId)
        this.courseChapters = response.data
        return response.data
      } catch (error) {
        console.error('获取课程章节失败:', error)
        throw error
      }
    },

    /**
     * 获取课程分类
     */
    async fetchCategories(): Promise<void> {
      try {
        const response = await courseApi.getCategories()
        this.categories = response.data
      } catch (error) {
        console.error('获取课程分类失败:', error)
      }
    },

    /**
     * 获取推荐课程
     */
    async fetchRecommendCourses(limit = 10): Promise<void> {
      try {
        const response = await courseApi.getRecommendCourses(limit)
        this.recommendCourses = response.data
      } catch (error) {
        console.error('获取推荐课程失败:', error)
      }
    },

    /**
     * 获取热门课程
     */
    async fetchHotCourses(limit = 10): Promise<void> {
      try {
        const response = await courseApi.getHotCourses(limit)
        this.hotCourses = response.data
      } catch (error) {
        console.error('获取热门课程失败:', error)
      }
    },

    /**
     * 获取免费课程
     */
    async fetchFreeCourses(params: { pageCurrent: number; pageSize: number }): Promise<void> {
      try {
        const response = await courseApi.getFreeCourses(params)
        this.freeCourses = response.data.list
      } catch (error) {
        console.error('获取免费课程失败:', error)
      }
    },

    /**
     * 收藏/取消收藏课程
     */
    async toggleCourseCollect(courseId: number): Promise<boolean> {
      try {
        const course = this.courseList.find(c => c.id === courseId) || this.currentCourse
        if (!course) return false

        const isCollected = this.collectedCourses.some(c => c.id === courseId)
        
        if (isCollected) {
          // 取消收藏
          // await authCourseApi.removeCourseCollect(courseId)
          this.collectedCourses = this.collectedCourses.filter(c => c.id !== courseId)
          return false
        } else {
          // 添加收藏
          // await authCourseApi.addCourseCollect(courseId)
          this.collectedCourses.push(course)
          return true
        }
      } catch (error) {
        console.error('收藏操作失败:', error)
        ElMessage.error('操作失败，请重试')
        throw error
      }
    },

    /**
     * 获取收藏课程列表
     */
    async fetchCollectedCourses(): Promise<void> {
      try {
        // const response = await authCourseApi.getCollectedCourses()
        // this.collectedCourses = response.data.list
        // 临时模拟数据
        this.collectedCourses = []
      } catch (error) {
        console.error('获取收藏课程失败:', error)
      }
    },

    /**
     * 检查课程是否已收藏
     */
    isCourseCollected(courseId: number): boolean {
      return this.collectedCourses.some(c => c.id === courseId)
    },

    /**
     * 按分类搜索课程
     */
    async searchByCategory(categoryId: number): Promise<void> {
      await this.searchCourses({
        categoryId,
        pageCurrent: 1
      })
    },

    /**
     * 按关键词搜索课程
     */
    async searchByKeyword(keyword: string): Promise<void> {
      await this.searchCourses({
        courseName: keyword,
        pageCurrent: 1
      })
    },

    /**
     * 清除当前课程数据
     */
    clearCurrentCourse(): void {
      this.currentCourse = null
      this.courseChapters = []
    },

    /**
     * 获取课程学习配置（需要登录）
     */
    async fetchCourseStudyConfig(courseId: number): Promise<any> {
      try {
        const response = await authCourseApi.getCourseStudyConfig(courseId)
        return response.data
      } catch (error) {
        console.error('获取学习配置失败:', error)
        throw error
      }
    },

    /**
     * 购买课程
     */
    async buyCourse(courseId: number): Promise<{ orderNo: string; payUrl?: string }> {
      try {
        const response = await authCourseApi.buyCourse(courseId)
        return response.data
      } catch (error) {
        console.error('购买课程失败:', error)
        ElMessage.error('购买失败，请重试')
        throw error
      }
    },

    /**
     * 检查课程购买状态
     */
    async checkCourseBuyStatus(courseId: number): Promise<{
      hasBuy: boolean
      canStudy: boolean
      orderNo?: string
    }> {
      try {
        const response = await authCourseApi.checkCourseBuyStatus(courseId)
        return response.data
      } catch (error) {
        console.error('检查购买状态失败:', error)
        return { hasBuy: false, canStudy: false }
      }
    }
  }
})