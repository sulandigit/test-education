import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest'
import { mount } from '@vue/test-utils'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '~/stores/user'
import { useCourseStore } from '~/stores/course'
import * as authApi from '~/api/auth'
import * as courseApi from '~/api/course'

// Mock APIs
vi.mock('~/api/auth')
vi.mock('~/api/course')

// Mock Nuxt runtime
vi.mock('#app', () => ({
  $fetch: vi.fn(),
  useRuntimeConfig: () => ({
    public: {
      apiBase: 'http://localhost:3001'
    }
  }),
  navigateTo: vi.fn(),
  useCookie: vi.fn(() => ({ value: null })),
}))

describe('Integration Tests - User Authentication Flow', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('should handle complete login flow', async () => {
    const userStore = useUserStore()
    
    // Mock successful login
    const mockLoginResponse = {
      code: 200,
      data: {
        token: 'test-token-123',
        user: {
          id: 1,
          phone: '13800138000',
          nickname: '测试用户',
          avatar: '/avatar.jpg'
        }
      }
    }
    
    vi.mocked(authApi.login).mockResolvedValue(mockLoginResponse)

    const result = await userStore.login({
      phone: '13800138000',
      password: 'password123'
    })

    expect(result).toBe(true)
    expect(userStore.isLoggedIn).toBe(true)
    expect(userStore.currentUser).toEqual(mockLoginResponse.data.user)
    expect(userStore.token).toBe('test-token-123')
  })

  it('should handle login failure and error states', async () => {
    const userStore = useUserStore()
    
    vi.mocked(authApi.login).mockRejectedValue(new Error('Invalid credentials'))

    const result = await userStore.login({
      phone: '13800138000',
      password: 'wrong-password'
    })

    expect(result).toBe(false)
    expect(userStore.isLoggedIn).toBe(false)
    expect(userStore.currentUser).toBeNull()
    expect(userStore.token).toBeNull()
  })

  it('should handle registration flow with SMS verification', async () => {
    const userStore = useUserStore()
    
    // Step 1: Send SMS code
    vi.mocked(authApi.sendSmsCode).mockResolvedValue({
      code: 200,
      message: 'SMS sent successfully'
    })

    const smsResult = await userStore.sendSmsCode('13800138000')
    expect(smsResult).toBe(true)

    // Step 2: Register with code
    const mockRegisterResponse = {
      code: 200,
      data: {
        token: 'new-token-456',
        user: {
          id: 2,
          phone: '13800138000',
          nickname: '新用户',
          avatar: null
        }
      }
    }

    vi.mocked(authApi.register).mockResolvedValue(mockRegisterResponse)

    const registerResult = await userStore.register({
      phone: '13800138000',
      password: 'newpassword123',
      code: '123456',
      nickname: '新用户'
    })

    expect(registerResult).toBe(true)
    expect(userStore.isLoggedIn).toBe(true)
    expect(userStore.currentUser).toEqual(mockRegisterResponse.data.user)
  })

  it('should handle session restoration from token', async () => {
    const userStore = useUserStore()
    
    // Mock cookie with existing token
    const mockCookie = { value: 'existing-token' }
    vi.mocked(await import('#app')).useCookie = vi.fn(() => mockCookie)
    
    const mockUserResponse = {
      code: 200,
      data: {
        id: 1,
        phone: '13800138000',
        nickname: '测试用户',
        avatar: '/avatar.jpg'
      }
    }

    vi.mocked(authApi.getCurrentUser).mockResolvedValue(mockUserResponse)

    await userStore.initializeAuth()

    expect(userStore.token).toBe('existing-token')
    expect(userStore.isLoggedIn).toBe(true)
    expect(userStore.currentUser).toEqual(mockUserResponse.data)
  })

  it('should handle logout and clear all user data', async () => {
    const userStore = useUserStore()
    
    // Set initial logged-in state
    userStore.token = 'test-token'
    userStore.currentUser = {
      id: 1,
      phone: '13800138000',
      nickname: '测试用户',
      avatar: '/avatar.jpg'
    }

    vi.mocked(authApi.logout).mockResolvedValue({ code: 200 })

    await userStore.logout()

    expect(userStore.token).toBeNull()
    expect(userStore.currentUser).toBeNull()
    expect(userStore.isLoggedIn).toBe(false)
  })
})

describe('Integration Tests - Course Management Flow', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('should handle course search and filtering', async () => {
    const courseStore = useCourseStore()
    
    const mockCourses = [
      {
        id: 1,
        title: 'Vue.js基础课程',
        instructor: '张老师',
        price: 199,
        category: 'frontend',
        rating: 4.8,
        studentsCount: 1520
      },
      {
        id: 2,
        title: 'React进阶课程',
        instructor: '李老师',
        price: 299,
        category: 'frontend',
        rating: 4.9,
        studentsCount: 2300
      }
    ]

    const mockSearchResponse = {
      code: 200,
      data: {
        courses: mockCourses,
        total: 2,
        page: 1,
        pageSize: 10,
        totalPages: 1
      }
    }

    vi.mocked(courseApi.searchCourses).mockResolvedValue(mockSearchResponse)

    await courseStore.searchCourses({
      query: 'Vue',
      category: 'frontend',
      page: 1,
      pageSize: 10
    })

    expect(courseStore.courses).toEqual(mockCourses)
    expect(courseStore.searchQuery).toBe('Vue')
    expect(courseStore.selectedCategory).toBe('frontend')
    expect(courseStore.totalPages).toBe(1)
  })

  it('should handle course detail fetching', async () => {
    const courseStore = useCourseStore()
    
    const mockCourseDetail = {
      id: 1,
      title: 'Vue.js基础课程',
      instructor: '张老师',
      price: 199,
      description: '这是一门Vue.js基础课程',
      chapters: [
        { id: 1, title: '课程介绍', duration: 300 },
        { id: 2, title: '基础语法', duration: 600 }
      ],
      comments: []
    }

    const mockDetailResponse = {
      code: 200,
      data: mockCourseDetail
    }

    vi.mocked(courseApi.getCourseDetail).mockResolvedValue(mockDetailResponse)

    await courseStore.fetchCourseDetail(1)

    expect(courseStore.currentCourse).toEqual(mockCourseDetail)
  })

  it('should handle categories loading', async () => {
    const courseStore = useCourseStore()
    
    const mockCategories = [
      { id: 1, name: '前端开发', slug: 'frontend', count: 150 },
      { id: 2, name: '后端开发', slug: 'backend', count: 120 }
    ]

    const mockCategoriesResponse = {
      code: 200,
      data: mockCategories
    }

    vi.mocked(courseApi.getCourseCategories).mockResolvedValue(mockCategoriesResponse)

    await courseStore.fetchCategories()

    expect(courseStore.categories).toEqual(mockCategories)
  })

  it('should handle featured courses loading', async () => {
    const courseStore = useCourseStore()
    
    const mockFeaturedCourses = [
      {
        id: 1,
        title: '热门课程1',
        instructor: '名师A',
        price: 199,
        rating: 4.9
      },
      {
        id: 2,
        title: '热门课程2',
        instructor: '名师B',
        price: 299,
        rating: 4.8
      }
    ]

    const mockFeaturedResponse = {
      code: 200,
      data: mockFeaturedCourses
    }

    vi.mocked(courseApi.getFeaturedCourses).mockResolvedValue(mockFeaturedResponse)

    await courseStore.fetchFeaturedCourses()

    expect(courseStore.featuredCourses).toEqual(mockFeaturedCourses)
  })
})

describe('Integration Tests - Complete User Journey', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('should handle complete user journey from registration to course purchase', async () => {
    const userStore = useUserStore()
    const courseStore = useCourseStore()

    // Step 1: User registration
    vi.mocked(authApi.sendSmsCode).mockResolvedValue({ code: 200, message: 'SMS sent' })
    vi.mocked(authApi.register).mockResolvedValue({
      code: 200,
      data: {
        token: 'new-user-token',
        user: { id: 1, phone: '13800138000', nickname: '新用户', avatar: null }
      }
    })

    await userStore.sendSmsCode('13800138000')
    const registerResult = await userStore.register({
      phone: '13800138000',
      password: 'password123',
      code: '123456',
      nickname: '新用户'
    })

    expect(registerResult).toBe(true)
    expect(userStore.isLoggedIn).toBe(true)

    // Step 2: Browse courses
    vi.mocked(courseApi.searchCourses).mockResolvedValue({
      code: 200,
      data: {
        courses: [
          { id: 1, title: 'Vue.js课程', price: 199, instructor: '张老师' }
        ],
        total: 1,
        page: 1,
        pageSize: 10,
        totalPages: 1
      }
    })

    await courseStore.searchCourses({
      query: 'Vue',
      category: '',
      page: 1,
      pageSize: 10
    })

    expect(courseStore.courses).toHaveLength(1)

    // Step 3: View course details
    vi.mocked(courseApi.getCourseDetail).mockResolvedValue({
      code: 200,
      data: {
        id: 1,
        title: 'Vue.js课程',
        price: 199,
        instructor: '张老师',
        description: '课程描述',
        chapters: [
          { id: 1, title: '第一章', duration: 300 }
        ]
      }
    })

    await courseStore.fetchCourseDetail(1)

    expect(courseStore.currentCourse).toBeTruthy()
    expect(courseStore.currentCourse?.title).toBe('Vue.js课程')

    // Verify the complete state
    expect(userStore.isLoggedIn).toBe(true)
    expect(courseStore.currentCourse?.id).toBe(1)
  })

  it('should handle error states in user journey', async () => {
    const userStore = useUserStore()
    const courseStore = useCourseStore()

    // Step 1: Failed login
    vi.mocked(authApi.login).mockRejectedValue(new Error('Invalid credentials'))
    
    const loginResult = await userStore.login({
      phone: '13800138000',
      password: 'wrong-password'
    })

    expect(loginResult).toBe(false)
    expect(userStore.isLoggedIn).toBe(false)

    // Step 2: Course search should still work for anonymous users
    vi.mocked(courseApi.searchCourses).mockResolvedValue({
      code: 200,
      data: {
        courses: [
          { id: 1, title: 'Public Course', price: 0, instructor: '公开讲师' }
        ],
        total: 1,
        page: 1,
        pageSize: 10,
        totalPages: 1
      }
    })

    await courseStore.searchCourses({
      query: '',
      category: '',
      page: 1,
      pageSize: 10
    })

    expect(courseStore.courses).toHaveLength(1)

    // Step 3: Course detail should fail for protected content
    vi.mocked(courseApi.getCourseDetail).mockRejectedValue(new Error('Authentication required'))

    await courseStore.fetchCourseDetail(1)

    expect(courseStore.currentCourse).toBeNull()
  })
})