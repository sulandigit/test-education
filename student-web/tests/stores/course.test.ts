import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useCourseStore } from '~/stores/course'
import * as courseApi from '~/api/course'

// Mock API calls
vi.mock('~/api/course', () => ({
  searchCourses: vi.fn(),
  getCourseDetail: vi.fn(),
  getCourseCategories: vi.fn(),
  getFeaturedCourses: vi.fn(),
  getRecommendedCourses: vi.fn(),
}))

// Mock courses data
const mockCourses = [
  {
    id: 1,
    title: '前端开发入门',
    instructor: '张老师',
    price: 199,
    originalPrice: 299,
    rating: 4.8,
    studentsCount: 1520,
    cover: '/course1.jpg',
    category: 'frontend'
  },
  {
    id: 2,
    title: 'Vue.js实战课程',
    instructor: '李老师',
    price: 299,
    rating: 4.9,
    studentsCount: 2300,
    cover: '/course2.jpg',
    category: 'frontend'
  }
]

const mockCourseDetail = {
  id: 1,
  title: '前端开发入门',
  instructor: '张老师',
  price: 199,
  originalPrice: 299,
  rating: 4.8,
  studentsCount: 1520,
  cover: '/course1.jpg',
  description: '这是一门前端开发入门课程',
  content: '课程内容详情...',
  chapters: [
    { id: 1, title: '课程介绍', duration: 300 },
    { id: 2, title: '基础知识', duration: 600 }
  ],
  comments: [
    { id: 1, user: '学员A', content: '很好的课程', rating: 5 }
  ]
}

const mockCategories = [
  { id: 1, name: '前端开发', slug: 'frontend', count: 150 },
  { id: 2, name: '后端开发', slug: 'backend', count: 120 },
  { id: 3, name: '移动开发', slug: 'mobile', count: 80 }
]

describe('Course Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('initializes with default state', () => {
    const courseStore = useCourseStore()
    
    expect(courseStore.courses).toEqual([])
    expect(courseStore.currentCourse).toBeNull()
    expect(courseStore.categories).toEqual([])
    expect(courseStore.featuredCourses).toEqual([])
    expect(courseStore.loading).toBe(false)
    expect(courseStore.searchQuery).toBe('')
    expect(courseStore.selectedCategory).toBe('')
    expect(courseStore.currentPage).toBe(1)
    expect(courseStore.totalPages).toBe(0)
  })

  it('searches courses successfully', async () => {
    const courseStore = useCourseStore()
    const searchParams = {
      query: 'Vue',
      category: 'frontend',
      page: 1,
      pageSize: 10
    }
    const mockResponse = {
      code: 200,
      data: {
        courses: mockCourses,
        total: 2,
        page: 1,
        pageSize: 10,
        totalPages: 1
      }
    }

    vi.mocked(courseApi.searchCourses).mockResolvedValue(mockResponse)

    await courseStore.searchCourses(searchParams)

    expect(courseApi.searchCourses).toHaveBeenCalledWith(searchParams)
    expect(courseStore.courses).toEqual(mockCourses)
    expect(courseStore.searchQuery).toBe('Vue')
    expect(courseStore.selectedCategory).toBe('frontend')
    expect(courseStore.currentPage).toBe(1)
    expect(courseStore.totalPages).toBe(1)
    expect(courseStore.loading).toBe(false)
  })

  it('handles search courses failure', async () => {
    const courseStore = useCourseStore()
    const searchParams = {
      query: 'test',
      category: '',
      page: 1,
      pageSize: 10
    }

    vi.mocked(courseApi.searchCourses).mockRejectedValue(new Error('Search failed'))

    await courseStore.searchCourses(searchParams)

    expect(courseStore.courses).toEqual([])
    expect(courseStore.loading).toBe(false)
  })

  it('fetches course detail successfully', async () => {
    const courseStore = useCourseStore()
    const courseId = 1
    const mockResponse = {
      code: 200,
      data: mockCourseDetail
    }

    vi.mocked(courseApi.getCourseDetail).mockResolvedValue(mockResponse)

    await courseStore.fetchCourseDetail(courseId)

    expect(courseApi.getCourseDetail).toHaveBeenCalledWith(courseId)
    expect(courseStore.currentCourse).toEqual(mockCourseDetail)
  })

  it('handles course detail fetch failure', async () => {
    const courseStore = useCourseStore()
    const courseId = 1

    vi.mocked(courseApi.getCourseDetail).mockRejectedValue(new Error('Course not found'))

    await courseStore.fetchCourseDetail(courseId)

    expect(courseStore.currentCourse).toBeNull()
  })

  it('fetches categories successfully', async () => {
    const courseStore = useCourseStore()
    const mockResponse = {
      code: 200,
      data: mockCategories
    }

    vi.mocked(courseApi.getCourseCategories).mockResolvedValue(mockResponse)

    await courseStore.fetchCategories()

    expect(courseApi.getCourseCategories).toHaveBeenCalled()
    expect(courseStore.categories).toEqual(mockCategories)
  })

  it('fetches featured courses successfully', async () => {
    const courseStore = useCourseStore()
    const mockResponse = {
      code: 200,
      data: mockCourses
    }

    vi.mocked(courseApi.getFeaturedCourses).mockResolvedValue(mockResponse)

    await courseStore.fetchFeaturedCourses()

    expect(courseApi.getFeaturedCourses).toHaveBeenCalled()
    expect(courseStore.featuredCourses).toEqual(mockCourses)
  })

  it('resets search filters correctly', () => {
    const courseStore = useCourseStore()
    
    // 设置一些搜索状态
    courseStore.searchQuery = 'Vue'
    courseStore.selectedCategory = 'frontend'
    courseStore.currentPage = 3
    courseStore.courses = mockCourses

    courseStore.resetFilters()

    expect(courseStore.searchQuery).toBe('')
    expect(courseStore.selectedCategory).toBe('')
    expect(courseStore.currentPage).toBe(1)
    expect(courseStore.courses).toEqual([])
  })

  it('updates search query correctly', () => {
    const courseStore = useCourseStore()
    
    const newQuery = 'React'
    courseStore.setSearchQuery(newQuery)

    expect(courseStore.searchQuery).toBe(newQuery)
  })

  it('updates selected category correctly', () => {
    const courseStore = useCourseStore()
    
    const newCategory = 'backend'
    courseStore.setSelectedCategory(newCategory)

    expect(courseStore.selectedCategory).toBe(newCategory)
  })

  it('updates current page correctly', () => {
    const courseStore = useCourseStore()
    
    const newPage = 3
    courseStore.setCurrentPage(newPage)

    expect(courseStore.currentPage).toBe(newPage)
  })

  it('handles loading states during async operations', async () => {
    const courseStore = useCourseStore()
    
    // 模拟慢速API响应
    let resolveSearch: (value: any) => void
    const searchPromise = new Promise(resolve => {
      resolveSearch = resolve
    })
    vi.mocked(courseApi.searchCourses).mockReturnValue(searchPromise)

    // 开始搜索
    const searchCall = courseStore.searchCourses({
      query: 'test',
      category: '',
      page: 1,
      pageSize: 10
    })

    // 检查loading状态
    expect(courseStore.loading).toBe(true)

    // 完成搜索
    resolveSearch!({
      code: 200,
      data: {
        courses: mockCourses,
        total: 2,
        page: 1,
        pageSize: 10,
        totalPages: 1
      }
    })

    await searchCall

    // 检查loading状态已重置
    expect(courseStore.loading).toBe(false)
  })

  it('finds course by id correctly', () => {
    const courseStore = useCourseStore()
    
    // 设置课程列表
    courseStore.courses = mockCourses

    const foundCourse = courseStore.getCourseById(2)
    expect(foundCourse).toEqual(mockCourses[1])

    const notFoundCourse = courseStore.getCourseById(999)
    expect(notFoundCourse).toBeUndefined()
  })

  it('filters courses by category correctly', () => {
    const courseStore = useCourseStore()
    
    // 设置课程列表，包含不同分类
    const mixedCourses = [
      ...mockCourses,
      {
        id: 3,
        title: 'Python后端开发',
        instructor: '王老师',
        price: 399,
        rating: 4.7,
        studentsCount: 800,
        cover: '/course3.jpg',
        category: 'backend'
      }
    ]
    courseStore.courses = mixedCourses

    const frontendCourses = courseStore.getCoursesByCategory('frontend')
    expect(frontendCourses).toHaveLength(2)
    expect(frontendCourses.every(course => course.category === 'frontend')).toBe(true)

    const backendCourses = courseStore.getCoursesByCategory('backend')
    expect(backendCourses).toHaveLength(1)
    expect(backendCourses[0].category).toBe('backend')
  })

  it('validates course search parameters', () => {
    const courseStore = useCourseStore()
    
    // 测试无效页码处理
    courseStore.setCurrentPage(-1)
    expect(courseStore.currentPage).toBe(1)

    courseStore.setCurrentPage(0)
    expect(courseStore.currentPage).toBe(1)

    // 测试有效页码
    courseStore.setCurrentPage(5)
    expect(courseStore.currentPage).toBe(5)
  })

  it('handles empty search results correctly', async () => {
    const courseStore = useCourseStore()
    const searchParams = {
      query: 'nonexistent',
      category: '',
      page: 1,
      pageSize: 10
    }
    const mockResponse = {
      code: 200,
      data: {
        courses: [],
        total: 0,
        page: 1,
        pageSize: 10,
        totalPages: 0
      }
    }

    vi.mocked(courseApi.searchCourses).mockResolvedValue(mockResponse)

    await courseStore.searchCourses(searchParams)

    expect(courseStore.courses).toEqual([])
    expect(courseStore.totalPages).toBe(0)
  })
})