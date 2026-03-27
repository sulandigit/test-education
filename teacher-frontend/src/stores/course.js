import { defineStore } from 'pinia'
import { ref } from 'vue'
import { courseApi } from '@/api'

export const useCourseStore = defineStore('course', () => {
  // 状态
  const courses = ref([])
  const loading = ref(false)
  const total = ref(0)

  // 获取课程列表
  const getCourseList = async (params = {}) => {
    loading.value = true
    try {
      const response = await courseApi.getCourseList(params)
      courses.value = response.data.list || response.data
      total.value = response.data.total || courses.value.length
      return response
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 创建课程
  const createCourse = async (courseData) => {
    try {
      const response = await courseApi.createCourse(courseData)
      // 重新获取列表或直接添加到本地状态
      courses.value.unshift(response.data)
      total.value += 1
      return response
    } catch (error) {
      throw error
    }
  }

  // 更新课程
  const updateCourse = async (id, courseData) => {
    try {
      const response = await courseApi.updateCourse(id, courseData)
      // 更新本地状态
      const index = courses.value.findIndex(course => course.id === id)
      if (index > -1) {
        courses.value[index] = { ...courses.value[index], ...response.data }
      }
      return response
    } catch (error) {
      throw error
    }
  }

  // 删除课程
  const deleteCourse = async (id) => {
    try {
      const response = await courseApi.deleteCourse(id)
      // 从本地状态中移除
      const index = courses.value.findIndex(course => course.id === id)
      if (index > -1) {
        courses.value.splice(index, 1)
        total.value -= 1
      }
      return response
    } catch (error) {
      throw error
    }
  }

  // 批量删除课程
  const batchDeleteCourses = async (ids) => {
    try {
      const response = await courseApi.batchDeleteCourses(ids)
      // 从本地状态中移除
      courses.value = courses.value.filter(course => !ids.includes(course.id))
      total.value -= ids.length
      return response
    } catch (error) {
      throw error
    }
  }

  // 更新课程状态
  const updateCourseStatus = async (id, status) => {
    try {
      const response = await courseApi.updateCourseStatus(id, status)
      // 更新本地状态
      const index = courses.value.findIndex(course => course.id === id)
      if (index > -1) {
        courses.value[index].status = status
      }
      return response
    } catch (error) {
      throw error
    }
  }

  // 根据ID获取课程
  const getCourseById = (id) => {
    return courses.value.find(course => course.id === id)
  }

  // 重置状态
  const resetState = () => {
    courses.value = []
    loading.value = false
    total.value = 0
  }

  return {
    // 状态
    courses,
    loading,
    total,
    
    // 方法
    getCourseList,
    createCourse,
    updateCourse,
    deleteCourse,
    batchDeleteCourses,
    updateCourseStatus,
    getCourseById,
    resetState
  }
})