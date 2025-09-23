import { describe, it, expect, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import CourseCard from '~/components/CourseCard.vue'

// Mock course data
const mockCourse = {
  id: 1,
  title: '前端开发入门课程',
  instructor: '张老师',
  price: 199,
  originalPrice: 299,
  rating: 4.8,
  studentsCount: 1520,
  cover: '/images/course-cover.jpg',
  duration: '20小时',
  level: '初级',
  tags: ['前端', 'Vue.js', 'JavaScript']
}

describe('CourseCard', () => {
  it('renders course information correctly', () => {
    const wrapper = mount(CourseCard, {
      props: {
        course: mockCourse
      }
    })

    // 检查标题
    expect(wrapper.find('.course-title').text()).toBe(mockCourse.title)
    
    // 检查讲师
    expect(wrapper.find('.instructor').text()).toContain(mockCourse.instructor)
    
    // 检查价格
    expect(wrapper.find('.current-price').text()).toContain('¥199')
    expect(wrapper.find('.original-price').text()).toContain('¥299')
    
    // 检查评分
    expect(wrapper.find('.rating-score').text()).toBe('4.8')
    
    // 检查学生数量
    expect(wrapper.find('.students-count').text()).toContain('1520')
  })

  it('handles click event correctly', async () => {
    const mockNavigateTo = vi.fn()
    vi.mocked(await import('#app')).navigateTo = mockNavigateTo

    const wrapper = mount(CourseCard, {
      props: {
        course: mockCourse
      }
    })

    await wrapper.find('.course-card').trigger('click')
    expect(mockNavigateTo).toHaveBeenCalledWith('/course/1')
  })

  it('displays course level correctly', () => {
    const wrapper = mount(CourseCard, {
      props: {
        course: mockCourse
      }
    })

    expect(wrapper.find('.level').text()).toBe('初级')
  })

  it('shows discount badge when original price exists', () => {
    const wrapper = mount(CourseCard, {
      props: {
        course: mockCourse
      }
    })

    const discountBadge = wrapper.find('.discount-badge')
    expect(discountBadge.exists()).toBe(true)
    
    // 计算折扣百分比: (199/299) * 100 ≈ 66.6%，向下取整为66%
    expect(discountBadge.text()).toContain('6.7折')
  })

  it('does not show discount badge when no original price', () => {
    const courseWithoutDiscount = { ...mockCourse }
    delete courseWithoutDiscount.originalPrice

    const wrapper = mount(CourseCard, {
      props: {
        course: courseWithoutDiscount
      }
    })

    expect(wrapper.find('.discount-badge').exists()).toBe(false)
  })

  it('renders tags correctly', () => {
    const wrapper = mount(CourseCard, {
      props: {
        course: mockCourse
      }
    })

    const tags = wrapper.findAll('.tag')
    expect(tags).toHaveLength(3)
    expect(tags[0].text()).toBe('前端')
    expect(tags[1].text()).toBe('Vue.js')
    expect(tags[2].text()).toBe('JavaScript')
  })

  it('displays course duration', () => {
    const wrapper = mount(CourseCard, {
      props: {
        course: mockCourse
      }
    })

    expect(wrapper.find('.duration').text()).toContain('20小时')
  })

  it('renders star rating correctly', () => {
    const wrapper = mount(CourseCard, {
      props: {
        course: mockCourse
      }
    })

    // 检查评分星星数量 (4.8分应该显示4个完整星星和1个半星)
    const stars = wrapper.findAll('.star')
    expect(stars).toHaveLength(5)
    
    // 检查完整星星数量 (前4个应该是满星)
    const fullStars = wrapper.findAll('.star.full')
    expect(fullStars).toHaveLength(4)
  })

  it('handles missing course data gracefully', () => {
    const incompleteEourse = {
      id: 2,
      title: '测试课程',
      instructor: '测试老师',
      price: 99
    }

    const wrapper = mount(CourseCard, {
      props: {
        course: incompleteEourse
      }
    })

    // 应该正常渲染基本信息
    expect(wrapper.find('.course-title').text()).toBe('测试课程')
    expect(wrapper.find('.instructor').text()).toContain('测试老师')
    expect(wrapper.find('.current-price').text()).toContain('¥99')
    
    // 缺失的信息应该有默认处理
    expect(wrapper.find('.students-count').text()).toContain('0')
  })
})