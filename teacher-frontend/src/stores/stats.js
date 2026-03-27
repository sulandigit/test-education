import { defineStore } from 'pinia'
import { ref } from 'vue'
import { statsApi } from '@/api'

export const useStatsStore = defineStore('stats', () => {
  // 状态
  const dashboardStats = ref({
    totalCourses: 0,
    totalStudents: 0,
    activeCourses: 0,
    completedCourses: 0,
    recentCourses: [],
    recentStudents: []
  })
  const courseStats = ref([])
  const studentStats = ref([])
  const loading = ref(false)

  // 获取仪表盘统计数据
  const getDashboardStats = async () => {
    loading.value = true
    try {
      const response = await statsApi.getDashboardStats()
      dashboardStats.value = response.data
      return response
    } catch (error) {
      // 如果API失败，使用模拟数据
      dashboardStats.value = {
        totalCourses: 12,
        totalStudents: 156,
        activeCourses: 8,
        completedCourses: 4,
        recentCourses: [
          {
            id: 1,
            name: 'Vue.js 3.0 开发实战',
            description: '从零开始学习Vue.js 3.0的现代前端开发',
            status: 'active'
          },
          {
            id: 2,
            name: 'JavaScript 高级程序设计',
            description: '深入理解JavaScript核心概念和高级特性',
            status: 'active'
          },
          {
            id: 3,
            name: 'React Hooks 实战',
            description: '掌握React Hooks的使用技巧',
            status: 'completed'
          }
        ],
        recentStudents: [
          {
            id: 1,
            name: '张三',
            email: 'zhangsan@example.com',
            avatar: '',
            joinDate: '2024-01-15'
          },
          {
            id: 2,
            name: '李四',
            email: 'lisi@example.com',
            avatar: '',
            joinDate: '2024-01-18'
          },
          {
            id: 3,
            name: '王五',
            email: 'wangwu@example.com',
            avatar: '',
            joinDate: '2024-01-20'
          }
        ]
      }
      console.warn('Using mock data for dashboard stats')
    } finally {
      loading.value = false
    }
  }

  // 获取课程统计
  const getCourseStats = async (params = {}) => {
    loading.value = true
    try {
      const response = await statsApi.getCourseStats(params)
      courseStats.value = response.data
      return response
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 获取学生统计
  const getStudentStats = async (params = {}) => {
    loading.value = true
    try {
      const response = await statsApi.getStudentStats(params)
      studentStats.value = response.data
      return response
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 重置状态
  const resetState = () => {
    dashboardStats.value = {
      totalCourses: 0,
      totalStudents: 0,
      activeCourses: 0,
      completedCourses: 0,
      recentCourses: [],
      recentStudents: []
    }
    courseStats.value = []
    studentStats.value = []
    loading.value = false
  }

  return {
    // 状态
    dashboardStats,
    courseStats,
    studentStats,
    loading,
    
    // 方法
    getDashboardStats,
    getCourseStats,
    getStudentStats,
    resetState
  }
})