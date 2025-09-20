import request from '@/utils/request'

// 课程相关API
export const courseApi = {
  // 获取课程列表
  getCourseList(params) {
    return request({
      url: '/courses',
      method: 'get',
      params
    })
  },

  // 获取课程详情
  getCourseDetail(id) {
    return request({
      url: `/courses/${id}`,
      method: 'get'
    })
  },

  // 创建课程
  createCourse(data) {
    return request({
      url: '/courses',
      method: 'post',
      data
    })
  },

  // 更新课程
  updateCourse(id, data) {
    return request({
      url: `/courses/${id}`,
      method: 'put',
      data
    })
  },

  // 删除课程
  deleteCourse(id) {
    return request({
      url: `/courses/${id}`,
      method: 'delete'
    })
  },

  // 批量删除课程
  batchDeleteCourses(ids) {
    return request({
      url: '/courses/batch-delete',
      method: 'post',
      data: { ids }
    })
  },

  // 更新课程状态
  updateCourseStatus(id, status) {
    return request({
      url: `/courses/${id}/status`,
      method: 'patch',
      data: { status }
    })
  }
}

// 学生相关API
export const studentApi = {
  // 获取学生列表
  getStudentList(params) {
    return request({
      url: '/students',
      method: 'get',
      params
    })
  },

  // 获取学生详情
  getStudentDetail(id) {
    return request({
      url: `/students/${id}`,
      method: 'get'
    })
  },

  // 创建学生
  createStudent(data) {
    return request({
      url: '/students',
      method: 'post',
      data
    })
  },

  // 更新学生
  updateStudent(id, data) {
    return request({
      url: `/students/${id}`,
      method: 'put',
      data
    })
  },

  // 删除学生
  deleteStudent(id) {
    return request({
      url: `/students/${id}`,
      method: 'delete'
    })
  },

  // 批量删除学生
  batchDeleteStudents(ids) {
    return request({
      url: '/students/batch-delete',
      method: 'post',
      data: { ids }
    })
  },

  // 为学生分配课程
  assignCourses(studentId, courseIds) {
    return request({
      url: `/students/${studentId}/courses`,
      method: 'post',
      data: { courseIds }
    })
  },

  // 批量导入学生
  batchImportStudents(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/students/batch-import',
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

// 认证相关API
export const authApi = {
  // 用户登录
  login(data) {
    return request({
      url: '/auth/login',
      method: 'post',
      data
    })
  },

  // 用户登出
  logout() {
    return request({
      url: '/auth/logout',
      method: 'post'
    })
  },

  // 获取用户信息
  getUserInfo() {
    return request({
      url: '/auth/user-info',
      method: 'get'
    })
  },

  // 刷新token
  refreshToken() {
    return request({
      url: '/auth/refresh-token',
      method: 'post'
    })
  }
}

// 统计相关API
export const statsApi = {
  // 获取仪表盘统计数据
  getDashboardStats() {
    return request({
      url: '/stats/dashboard',
      method: 'get'
    })
  },

  // 获取课程统计
  getCourseStats(params) {
    return request({
      url: '/stats/courses',
      method: 'get',
      params
    })
  },

  // 获取学生统计
  getStudentStats(params) {
    return request({
      url: '/stats/students',
      method: 'get',
      params
    })
  }
}