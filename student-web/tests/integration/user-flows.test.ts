import { describe, it, expect, vi, beforeEach } from 'vitest'
import { mount, flushPromises } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '~/stores/user'
import { useCourseStore } from '~/stores/course'
import { useLearningStore } from '~/stores/learning'
import * as authApi from '~/api/auth'
import * as courseApi from '~/api/course'
import * as learningApi from '~/api/learning'

// Mock all APIs
vi.mock('~/api/auth')
vi.mock('~/api/course')
vi.mock('~/api/learning')

// Mock Nuxt runtime
vi.mock('#app', () => ({
  $fetch: vi.fn(),
  useRuntimeConfig: () => ({
    public: { apiBase: 'http://localhost:3001' }
  }),
  navigateTo: vi.fn(),
  useCookie: vi.fn(() => ({ value: null })),
  useNuxtApp: () => ({ $router: { push: vi.fn() } })
}))

// Mock Element Plus messages
vi.mock('element-plus', () => ({
  ElMessage: {
    success: vi.fn(),
    error: vi.fn(),
    warning: vi.fn(),
    info: vi.fn(),
  },
  ElMessageBox: {
    confirm: vi.fn().mockResolvedValue('confirm'),
    alert: vi.fn(),
  },
}))

describe('Integration Tests - Complete User Flows', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  describe('New User Registration and First Course Purchase Flow', () => {
    it('should complete full user onboarding flow', async () => {
      const userStore = useUserStore()
      const courseStore = useCourseStore()
      
      // Step 1: Send SMS verification code
      vi.mocked(authApi.sendSmsCode).mockResolvedValue({
        code: 200,
        message: 'Verification code sent successfully'
      })

      const smsResult = await userStore.sendSmsCode('13800138000')
      expect(smsResult).toBe(true)
      expect(authApi.sendSmsCode).toHaveBeenCalledWith('13800138000')

      // Step 2: Register new user
      const mockUser = {
        id: 1,
        phone: '13800138000',
        nickname: '新用户',
        avatar: null
      }

      vi.mocked(authApi.register).mockResolvedValue({
        code: 200,
        data: {
          token: 'new-user-token',
          user: mockUser
        }
      })

      const registerResult = await userStore.register({
        phone: '13800138000',
        password: 'password123',
        code: '123456',
        nickname: '新用户'
      })

      expect(registerResult).toBe(true)
      expect(userStore.isLoggedIn).toBe(true)
      expect(userStore.currentUser).toEqual(mockUser)

      // Step 3: Browse and search courses
      const mockCourses = [
        {
          id: 1,
          title: 'Vue.js 入门课程',
          instructor: '张老师',
          price: 199,
          originalPrice: 299,
          rating: 4.8,
          studentsCount: 1520,
          category: 'frontend',
          cover: '/course1.jpg'
        }
      ]

      vi.mocked(courseApi.searchCourses).mockResolvedValue({
        code: 200,
        data: {
          courses: mockCourses,
          total: 1,
          page: 1,
          pageSize: 10,
          totalPages: 1
        }
      })

      await courseStore.searchCourses({
        query: 'Vue',
        category: 'frontend',
        page: 1,
        pageSize: 10
      })

      expect(courseStore.courses).toEqual(mockCourses)
      expect(courseStore.searchQuery).toBe('Vue')

      // Step 4: View course details
      const mockCourseDetail = {
        ...mockCourses[0],
        description: '这是一门优秀的Vue.js入门课程',
        chapters: [
          { id: 1, title: '课程介绍', duration: 300, startTime: 0 },
          { id: 2, title: 'Vue基础', duration: 900, startTime: 300 }
        ],
        comments: [
          { id: 1, user: '学员A', content: '很好的课程', rating: 5 }
        ]
      }

      vi.mocked(courseApi.getCourseDetail).mockResolvedValue({
        code: 200,
        data: mockCourseDetail
      })

      await courseStore.fetchCourseDetail(1)

      expect(courseStore.currentCourse).toEqual(mockCourseDetail)

      // Verify complete flow state
      expect(userStore.isLoggedIn).toBe(true)
      expect(userStore.currentUser?.nickname).toBe('新用户')
      expect(courseStore.currentCourse?.title).toBe('Vue.js 入门课程')
    })

    it('should handle registration flow with validation errors', async () => {
      const userStore = useUserStore()
      
      // Step 1: Invalid phone number
      expect(() => userStore.sendSmsCode('123')).rejects.toThrow()

      // Step 2: SMS sending failure
      vi.mocked(authApi.sendSmsCode).mockRejectedValue(new Error('SMS service unavailable'))
      
      const smsResult = await userStore.sendSmsCode('13800138000')
      expect(smsResult).toBe(false)

      // Step 3: Registration with invalid code
      vi.mocked(authApi.register).mockRejectedValue(new Error('Invalid verification code'))
      
      const registerResult = await userStore.register({
        phone: '13800138000',
        password: 'password123',
        code: '000000',
        nickname: '用户'
      })

      expect(registerResult).toBe(false)
      expect(userStore.isLoggedIn).toBe(false)
    })
  })

  describe('Returning User Login and Course Learning Flow', () => {
    it('should complete returning user learning flow', async () => {
      const userStore = useUserStore()
      const courseStore = useCourseStore()
      const learningStore = useLearningStore()

      // Step 1: User login
      const mockUser = {
        id: 1,
        phone: '13800138000',
        nickname: '老用户',
        avatar: '/avatar.jpg'
      }

      vi.mocked(authApi.login).mockResolvedValue({
        code: 200,
        data: {
          token: 'returning-user-token',
          user: mockUser
        }
      })

      const loginResult = await userStore.login({
        phone: '13800138000',
        password: 'password123'
      })

      expect(loginResult).toBe(true)
      expect(userStore.currentUser).toEqual(mockUser)

      // Step 2: Load user's enrolled courses
      const mockEnrolledCourses = [
        {
          id: 1,
          title: 'Vue.js 入门课程',
          instructor: '张老师',
          progress: 60,
          lastWatchedChapter: 2,
          enrolledAt: '2023-12-01'
        },
        {
          id: 2,
          title: 'React 进阶课程',
          instructor: '李老师',
          progress: 25,
          lastWatchedChapter: 1,
          enrolledAt: '2023-12-10'
        }
      ]

      vi.mocked(learningApi.getMyCourses).mockResolvedValue({
        code: 200,
        data: mockEnrolledCourses
      })

      await learningStore.fetchMyCourses()

      expect(learningStore.myCourses).toEqual(mockEnrolledCourses)

      // Step 3: Continue learning from where left off
      const courseId = 1
      const mockLearningProgress = {
        courseId: 1,
        currentChapter: 2,
        currentTime: 450,
        totalWatchTime: 1800,
        completedChapters: [1],
        watchHistory: [
          { chapterId: 1, watchTime: 300, completedAt: '2023-12-02' },
          { chapterId: 2, watchTime: 450, lastWatchedAt: '2023-12-03' }
        ]
      }

      vi.mocked(learningApi.getLearningProgress).mockResolvedValue({
        code: 200,
        data: mockLearningProgress
      })

      await learningStore.fetchLearningProgress(courseId)

      expect(learningStore.currentProgress).toEqual(mockLearningProgress)

      // Step 4: Update learning progress during video watching
      const progressUpdate = {
        chapterId: 2,
        currentTime: 600,
        duration: 900
      }

      vi.mocked(learningApi.updateLearningProgress).mockResolvedValue({
        code: 200,
        message: 'Progress updated'
      })

      await learningStore.updateProgress(courseId, progressUpdate)

      expect(learningApi.updateLearningProgress).toHaveBeenCalledWith(courseId, progressUpdate)

      // Verify learning state
      expect(learningStore.myCourses).toHaveLength(2)
      expect(learningStore.currentProgress?.currentChapter).toBe(2)
    })

    it('should handle learning progress synchronization', async () => {
      const learningStore = useLearningStore()

      // Mock multiple progress updates in quick succession
      const updates = [
        { chapterId: 1, currentTime: 100, duration: 900 },
        { chapterId: 1, currentTime: 200, duration: 900 },
        { chapterId: 1, currentTime: 300, duration: 900 }
      ]

      vi.mocked(learningApi.updateLearningProgress).mockResolvedValue({
        code: 200,
        message: 'Progress updated'
      })

      // Send multiple updates
      const updatePromises = updates.map(update => 
        learningStore.updateProgress(1, update)
      )

      await Promise.all(updatePromises)

      // Should have called API for each update
      expect(learningApi.updateLearningProgress).toHaveBeenCalledTimes(3)
    })
  })

  describe('Course Discovery and Enrollment Flow', () => {
    it('should complete course discovery and enrollment', async () => {
      const userStore = useUserStore()
      const courseStore = useCourseStore()

      // Step 1: User is already logged in
      userStore.token = 'existing-token'
      userStore.currentUser = {
        id: 1,
        phone: '13800138000',
        nickname: '学习者',
        avatar: '/avatar.jpg'
      }

      // Step 2: Browse featured courses on homepage
      const mockFeaturedCourses = [
        {
          id: 1,
          title: '热门前端课程',
          instructor: '名师A',
          price: 299,
          rating: 4.9,
          studentsCount: 5000,
          category: 'frontend'
        },
        {
          id: 2,
          title: '热门后端课程',
          instructor: '名师B',
          price: 399,
          rating: 4.8,
          studentsCount: 3000,
          category: 'backend'
        }
      ]

      vi.mocked(courseApi.getFeaturedCourses).mockResolvedValue({
        code: 200,
        data: mockFeaturedCourses
      })

      await courseStore.fetchFeaturedCourses()

      expect(courseStore.featuredCourses).toEqual(mockFeaturedCourses)

      // Step 3: Search for specific topics
      const mockSearchResults = [
        {
          id: 3,
          title: 'TypeScript 高级应用',
          instructor: '专家C',
          price: 199,
          rating: 4.7,
          studentsCount: 1200,
          category: 'frontend'
        }
      ]

      vi.mocked(courseApi.searchCourses).mockResolvedValue({
        code: 200,
        data: {
          courses: mockSearchResults,
          total: 1,
          page: 1,
          pageSize: 10,
          totalPages: 1
        }
      })

      await courseStore.searchCourses({
        query: 'TypeScript',
        category: 'frontend',
        page: 1,
        pageSize: 10
      })

      expect(courseStore.courses).toEqual(mockSearchResults)

      // Step 4: View detailed course information
      const mockCourseDetail = {
        ...mockSearchResults[0],
        description: 'TypeScript的高级特性和实战应用',
        content: '详细的课程内容介绍...',
        chapters: [
          { id: 1, title: 'TypeScript基础回顾', duration: 600 },
          { id: 2, title: '高级类型系统', duration: 900 },
          { id: 3, title: '装饰器和元数据', duration: 1200 }
        ],
        comments: [
          {
            id: 1,
            user: '学员A',
            content: '讲解非常详细，受益匪浅',
            rating: 5,
            createdAt: '2023-12-01'
          }
        ],
        prerequisites: ['JavaScript基础', 'ES6+语法'],
        learningGoals: ['掌握TypeScript高级特性', '能够在项目中灵活运用']
      }

      vi.mocked(courseApi.getCourseDetail).mockResolvedValue({
        code: 200,
        data: mockCourseDetail
      })

      await courseStore.fetchCourseDetail(3)

      expect(courseStore.currentCourse).toEqual(mockCourseDetail)

      // Verify complete discovery flow
      expect(courseStore.featuredCourses).toHaveLength(2)
      expect(courseStore.courses).toHaveLength(1)
      expect(courseStore.currentCourse?.title).toBe('TypeScript 高级应用')
    })

    it('should handle course filtering and pagination', async () => {
      const courseStore = useCourseStore()

      // Step 1: Load course categories
      const mockCategories = [
        { id: 1, name: '前端开发', slug: 'frontend', count: 150 },
        { id: 2, name: '后端开发', slug: 'backend', count: 120 },
        { id: 3, name: '移动开发', slug: 'mobile', count: 80 }
      ]

      vi.mocked(courseApi.getCourseCategories).mockResolvedValue({
        code: 200,
        data: mockCategories
      })

      await courseStore.fetchCategories()

      expect(courseStore.categories).toEqual(mockCategories)

      // Step 2: Filter by category and paginate
      const mockFilteredCourses = Array.from({ length: 20 }, (_, i) => ({
        id: i + 1,
        title: `前端课程 ${i + 1}`,
        instructor: `讲师${i + 1}`,
        price: 199 + i * 10,
        rating: 4.5 + Math.random() * 0.5,
        studentsCount: 1000 + i * 100,
        category: 'frontend'
      }))

      // First page
      vi.mocked(courseApi.searchCourses).mockResolvedValue({
        code: 200,
        data: {
          courses: mockFilteredCourses.slice(0, 10),
          total: 20,
          page: 1,
          pageSize: 10,
          totalPages: 2
        }
      })

      await courseStore.searchCourses({
        query: '',
        category: 'frontend',
        page: 1,
        pageSize: 10
      })

      expect(courseStore.courses).toHaveLength(10)
      expect(courseStore.totalPages).toBe(2)
      expect(courseStore.currentPage).toBe(1)

      // Second page
      vi.mocked(courseApi.searchCourses).mockResolvedValue({
        code: 200,
        data: {
          courses: mockFilteredCourses.slice(10, 20),
          total: 20,
          page: 2,
          pageSize: 10,
          totalPages: 2
        }
      })

      await courseStore.searchCourses({
        query: '',
        category: 'frontend',
        page: 2,
        pageSize: 10
      })

      expect(courseStore.courses).toHaveLength(10)
      expect(courseStore.currentPage).toBe(2)
    })
  })

  describe('Error Handling and Recovery Flow', () => {
    it('should handle network errors gracefully', async () => {
      const userStore = useUserStore()
      const courseStore = useCourseStore()

      // Simulate network error during login
      vi.mocked(authApi.login).mockRejectedValue(new Error('Network error'))

      const loginResult = await userStore.login({
        phone: '13800138000',
        password: 'password123'
      })

      expect(loginResult).toBe(false)
      expect(userStore.isLoggedIn).toBe(false)

      // Should still be able to browse courses as guest
      vi.mocked(courseApi.searchCourses).mockResolvedValue({
        code: 200,
        data: {
          courses: [
            { id: 1, title: '免费课程', price: 0, instructor: '讲师' }
          ],
          total: 1,
          page: 1,
          pageSize: 10,
          totalPages: 1
        }
      })

      await courseStore.searchCourses({
        query: '免费',
        category: '',
        page: 1,
        pageSize: 10
      })

      expect(courseStore.courses).toHaveLength(1)
    })

    it('should handle session expiration during usage', async () => {
      const userStore = useUserStore()
      const learningStore = useLearningStore()

      // User starts logged in
      userStore.token = 'expired-token'
      userStore.currentUser = {
        id: 1,
        phone: '13800138000',
        nickname: '用户',
        avatar: null
      }

      // API call fails due to expired token
      vi.mocked(learningApi.getMyCourses).mockRejectedValue(new Error('Token expired'))

      await learningStore.fetchMyCourses()

      // Should clear user data and redirect to login
      expect(learningStore.myCourses).toEqual([])
    })
  })
})