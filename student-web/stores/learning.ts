import { defineStore } from 'pinia'
import type { Course, StudyProgress } from '@/types'

interface LearningState {
  // 当前学习的课程
  currentLearningCourse: Course | null
  
  // 学习进度记录
  studyProgress: Record<string, StudyProgress> // key: courseId-periodId
  
  // 我的课程列表
  myCourses: Course[]
  myCoursesLoading: boolean
  
  // 学习统计
  studyStats: {
    totalCourses: number    // 总课程数
    completedCourses: number // 已完成课程数
    studyTime: number       // 总学习时长（分钟）
    studyDays: number       // 学习天数
  }
  
  // 学习记录
  studyRecords: Array<{
    courseId: number
    courseName: string
    studyTime: number
    progress: number
    lastStudyTime: string
  }>
  
  // 笔记记录
  studyNotes: Record<string, string> // key: courseId-periodId, value: note content
}

export const useLearningStore = defineStore('learning', {
  state: (): LearningState => ({
    currentLearningCourse: null,
    studyProgress: {},
    myCourses: [],
    myCoursesLoading: false,
    studyStats: {
      totalCourses: 0,
      completedCourses: 0,
      studyTime: 0,
      studyDays: 0
    },
    studyRecords: [],
    studyNotes: {}
  }),

  getters: {
    // 课程完成率
    courseCompletionRate: (state) => (courseId: number): number => {
      const courseProgress = Object.entries(state.studyProgress)
        .filter(([key]) => key.startsWith(`${courseId}-`))
        .map(([, progress]) => progress.studyProgress)
      
      if (courseProgress.length === 0) return 0
      
      const totalProgress = courseProgress.reduce((sum, progress) => sum + progress, 0)
      return Math.round(totalProgress / courseProgress.length)
    },

    // 章节完成状态
    isPeriodCompleted: (state) => (courseId: number, periodId: number): boolean => {
      const key = `${courseId}-${periodId}`
      const progress = state.studyProgress[key]
      return progress ? progress.studyProgress >= 90 : false
    },

    // 获取课程学习时长
    getCourseStudyTime: (state) => (courseId: number): number => {
      return Object.entries(state.studyProgress)
        .filter(([key]) => key.startsWith(`${courseId}-`))
        .reduce((total, [, progress]) => total + progress.studyTime, 0)
    },

    // 最近学习的课程
    recentCourses: (state): Course[] => {
      return state.myCourses
        .filter(course => {
          // 过滤出有学习记录的课程
          return Object.keys(state.studyProgress).some(key => 
            key.startsWith(`${course.id}-`)
          )
        })
        .sort((a, b) => {
          // 按最后学习时间排序
          const aLastTime = Math.max(...Object.entries(state.studyProgress)
            .filter(([key]) => key.startsWith(`${a.id}-`))
            .map(([, progress]) => new Date(progress.updateTime).getTime()))
          
          const bLastTime = Math.max(...Object.entries(state.studyProgress)
            .filter(([key]) => key.startsWith(`${b.id}-`))
            .map(([, progress]) => new Date(progress.updateTime).getTime()))
          
          return bLastTime - aLastTime
        })
        .slice(0, 5) // 最近5门课程
    },

    // 学习进度百分比
    overallProgress: (state): number => {
      if (state.studyStats.totalCourses === 0) return 0
      return Math.round((state.studyStats.completedCourses / state.studyStats.totalCourses) * 100)
    }
  },

  actions: {
    /**
     * 设置当前学习课程
     */
    setCurrentLearningCourse(course: Course): void {
      this.currentLearningCourse = course
    },

    /**
     * 更新学习进度
     */
    async updateStudyProgress(params: {
      courseId: number
      periodId: number
      progress: number
      studyTime: number
    }): Promise<void> {
      try {
        const key = `${params.courseId}-${params.periodId}`
        
        // 更新本地状态
        this.studyProgress[key] = {
          courseId: params.courseId,
          periodId: params.periodId,
          studyProgress: params.progress,
          studyTime: params.studyTime,
          updateTime: new Date().toISOString()
        }

        // 调用API保存到服务器
        // await learningApi.updateProgress(params)
        
        // 更新统计信息
        this.updateStudyStats()
      } catch (error) {
        console.error('更新学习进度失败:', error)
      }
    },

    /**
     * 标记章节为已完成
     */
    async markPeriodCompleted(courseId: number, periodId: number): Promise<void> {
      await this.updateStudyProgress({
        courseId,
        periodId,
        progress: 100,
        studyTime: 0 // 这里应该传入实际观看时长
      })
    },

    /**
     * 获取我的课程列表
     */
    async fetchMyCourses(): Promise<void> {
      try {
        this.myCoursesLoading = true
        
        // const response = await learningApi.getMyCourses()
        // this.myCourses = response.data.list
        
        // 临时模拟数据
        this.myCourses = []
        
        // 更新统计
        this.updateStudyStats()
      } catch (error) {
        console.error('获取我的课程失败:', error)
        ElMessage.error('加载我的课程失败')
      } finally {
        this.myCoursesLoading = false
      }
    },

    /**
     * 加载学习进度
     */
    async loadStudyProgress(courseId?: number): Promise<void> {
      try {
        // const response = await learningApi.getStudyProgress({ courseId })
        // const progressList = response.data
        
        // progressList.forEach(progress => {
        //   const key = `${progress.courseId}-${progress.periodId}`
        //   this.studyProgress[key] = progress
        // })
        
        // 临时模拟数据
        this.studyProgress = {}
      } catch (error) {
        console.error('加载学习进度失败:', error)
      }
    },

    /**
     * 更新学习统计
     */
    updateStudyStats(): void {
      // 计算总课程数
      this.studyStats.totalCourses = this.myCourses.length
      
      // 计算已完成课程数
      this.studyStats.completedCourses = this.myCourses.filter(course => {
        const completionRate = this.courseCompletionRate(course.id)
        return completionRate >= 90
      }).length
      
      // 计算总学习时长
      this.studyStats.studyTime = Object.values(this.studyProgress)
        .reduce((total, progress) => total + progress.studyTime, 0)
      
      // 计算学习天数（简单统计有学习记录的天数）
      const studyDates = new Set(
        Object.values(this.studyProgress)
          .map(progress => progress.updateTime.split('T')[0])
      )
      this.studyStats.studyDays = studyDates.size
    },

    /**
     * 保存学习笔记
     */
    async saveStudyNote(courseId: number, periodId: number, content: string): Promise<void> {
      try {
        const key = `${courseId}-${periodId}`
        this.studyNotes[key] = content
        
        // 调用API保存到服务器
        // await learningApi.saveNote({ courseId, periodId, content })
        
        ElMessage.success('笔记保存成功')
      } catch (error) {
        console.error('保存笔记失败:', error)
        ElMessage.error('笔记保存失败')
      }
    },

    /**
     * 获取学习笔记
     */
    async loadStudyNote(courseId: number, periodId: number): Promise<string> {
      try {
        const key = `${courseId}-${periodId}`
        
        // 先从本地获取
        if (this.studyNotes[key]) {
          return this.studyNotes[key]
        }
        
        // 从服务器获取
        // const response = await learningApi.getNote(courseId, periodId)
        // const content = response.data.content
        // this.studyNotes[key] = content
        // return content
        
        return ''
      } catch (error) {
        console.error('获取笔记失败:', error)
        return ''
      }
    },

    /**
     * 获取学习记录
     */
    async fetchStudyRecords(): Promise<void> {
      try {
        // const response = await learningApi.getStudyRecords()
        // this.studyRecords = response.data
        
        // 临时模拟数据
        this.studyRecords = []
      } catch (error) {
        console.error('获取学习记录失败:', error)
      }
    },

    /**
     * 检查课程是否可以继续学习
     */
    canContinueLearning(courseId: number): boolean {
      // 检查是否有学习进度
      const hasProgress = Object.keys(this.studyProgress).some(key => 
        key.startsWith(`${courseId}-`)
      )
      
      // 检查是否在我的课程中
      const isInMyCourses = this.myCourses.some(course => course.id === courseId)
      
      return hasProgress && isInMyCourses
    },

    /**
     * 获取下一个要学习的章节
     */
    getNextPeriodToStudy(courseId: number, chapters: any[]): { chapterIndex: number; periodIndex: number } | null {
      for (let chapterIndex = 0; chapterIndex < chapters.length; chapterIndex++) {
        const chapter = chapters[chapterIndex]
        if (!chapter.periods) continue
        
        for (let periodIndex = 0; periodIndex < chapter.periods.length; periodIndex++) {
          const period = chapter.periods[periodIndex]
          if (!this.isPeriodCompleted(courseId, period.id)) {
            return { chapterIndex, periodIndex }
          }
        }
      }
      
      return null // 全部完成
    },

    /**
     * 清空学习数据
     */
    clearLearningData(): void {
      this.currentLearningCourse = null
      this.studyProgress = {}
      this.myCourses = []
      this.studyRecords = []
      this.studyNotes = {}
      this.studyStats = {
        totalCourses: 0,
        completedCourses: 0,
        studyTime: 0,
        studyDays: 0
      }
    }
  },

  persist: {
    key: 'learning-store',
    storage: process.client ? localStorage : undefined,
    paths: ['studyProgress', 'studyNotes', 'studyStats']
  }
})