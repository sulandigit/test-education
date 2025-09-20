import { defineStore } from 'pinia'
import { ref } from 'vue'
import { studentApi } from '@/api'

export const useStudentStore = defineStore('student', () => {
  // 状态
  const students = ref([])
  const loading = ref(false)
  const total = ref(0)

  // 获取学生列表
  const getStudentList = async (params = {}) => {
    loading.value = true
    try {
      const response = await studentApi.getStudentList(params)
      students.value = response.data.list || response.data
      total.value = response.data.total || students.value.length
      return response
    } catch (error) {
      throw error
    } finally {
      loading.value = false
    }
  }

  // 创建学生
  const createStudent = async (studentData) => {
    try {
      const response = await studentApi.createStudent(studentData)
      // 重新获取列表或直接添加到本地状态
      students.value.unshift(response.data)
      total.value += 1
      return response
    } catch (error) {
      throw error
    }
  }

  // 更新学生
  const updateStudent = async (id, studentData) => {
    try {
      const response = await studentApi.updateStudent(id, studentData)
      // 更新本地状态
      const index = students.value.findIndex(student => student.id === id)
      if (index > -1) {
        students.value[index] = { ...students.value[index], ...response.data }
      }
      return response
    } catch (error) {
      throw error
    }
  }

  // 删除学生
  const deleteStudent = async (id) => {
    try {
      const response = await studentApi.deleteStudent(id)
      // 从本地状态中移除
      const index = students.value.findIndex(student => student.id === id)
      if (index > -1) {
        students.value.splice(index, 1)
        total.value -= 1
      }
      return response
    } catch (error) {
      throw error
    }
  }

  // 批量删除学生
  const batchDeleteStudents = async (ids) => {
    try {
      const response = await studentApi.batchDeleteStudents(ids)
      // 从本地状态中移除
      students.value = students.value.filter(student => !ids.includes(student.id))
      total.value -= ids.length
      return response
    } catch (error) {
      throw error
    }
  }

  // 为学生分配课程
  const assignCourses = async (studentId, courseIds) => {
    try {
      const response = await studentApi.assignCourses(studentId, courseIds)
      // 更新本地状态
      const index = students.value.findIndex(student => student.id === studentId)
      if (index > -1 && response.data.courses) {
        students.value[index].courses = response.data.courses
      }
      return response
    } catch (error) {
      throw error
    }
  }

  // 批量导入学生
  const batchImportStudents = async (file) => {
    try {
      const response = await studentApi.batchImportStudents(file)
      // 重新获取列表
      await getStudentList()
      return response
    } catch (error) {
      throw error
    }
  }

  // 根据ID获取学生
  const getStudentById = (id) => {
    return students.value.find(student => student.id === id)
  }

  // 根据课程ID获取学生列表
  const getStudentsByCourseId = (courseId) => {
    return students.value.filter(student => 
      student.courses && student.courses.some(course => course.id === courseId)
    )
  }

  // 重置状态
  const resetState = () => {
    students.value = []
    loading.value = false
    total.value = 0
  }

  return {
    // 状态
    students,
    loading,
    total,
    
    // 方法
    getStudentList,
    createStudent,
    updateStudent,
    deleteStudent,
    batchDeleteStudents,
    assignCourses,
    batchImportStudents,
    getStudentById,
    getStudentsByCourseId,
    resetState
  }
})