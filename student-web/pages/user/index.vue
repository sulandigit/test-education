<template>
  <div class="user-center">
    <div class="container">
      <!-- 用户信息卡片 -->
      <div class="user-profile-card">
        <div class="profile-header">
          <div class="avatar-section">
            <el-avatar :size="80" :src="userInfo?.avatar">
              <template #error>
                <el-icon><UserFilled /></el-icon>
              </template>
            </el-avatar>
            <el-button class="upload-btn" @click="showAvatarUpload">
              <el-icon><Camera /></el-icon>
            </el-button>
          </div>
          <div class="user-info">
            <h2>{{ userInfo?.nickname || userInfo?.username }}</h2>
            <p class="user-desc">{{ userInfo?.email || '暂无邮箱' }}</p>
            <div class="user-stats">
              <div class="stat-item">
                <div class="stat-number">{{ studyStats.totalCourses }}</div>
                <div class="stat-label">学习课程</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ studyStats.studyDays }}</div>
                <div class="stat-label">学习天数</div>
              </div>
              <div class="stat-item">
                <div class="stat-number">{{ formatStudyTime(studyStats.studyTime) }}</div>
                <div class="stat-label">学习时长</div>
              </div>
            </div>
          </div>
        </div>
        <div class="profile-actions">
          <el-button type="primary" @click="gotoProfileSettings">
            <el-icon><Setting /></el-icon>
            编辑资料
          </el-button>
        </div>
      </div>

      <!-- 学习进度概览 -->
      <div class="study-overview">
        <div class="overview-header">
          <h3>学习进度</h3>
          <div class="progress-ring">
            <el-progress
              type="circle"
              :percentage="overallProgress"
              :width="100"
              :stroke-width="8"
            >
              <template #default="{ percentage }">
                <span class="progress-text">{{ percentage }}%</span>
              </template>
            </el-progress>
          </div>
        </div>
        <div class="overview-stats">
          <div class="overview-item">
            <div class="item-icon completed">
              <el-icon><Check /></el-icon>
            </div>
            <div class="item-info">
              <div class="item-number">{{ studyStats.completedCourses }}</div>
              <div class="item-label">已完成</div>
            </div>
          </div>
          <div class="overview-item">
            <div class="item-icon learning">
              <el-icon><VideoPlay /></el-icon>
            </div>
            <div class="item-info">
              <div class="item-number">{{ studyStats.totalCourses - studyStats.completedCourses }}</div>
              <div class="item-label">学习中</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷功能 -->
      <div class="quick-actions">
        <div class="action-grid">
          <div class="action-item" @click="gotoMyCourses">
            <div class="action-icon">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="action-text">我的课程</div>
          </div>
          <div class="action-item" @click="gotoMyOrders">
            <div class="action-icon">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="action-text">我的订单</div>
          </div>
          <div class="action-item" @click="gotoCollections">
            <div class="action-icon">
              <el-icon><Star /></el-icon>
            </div>
            <div class="action-text">我的收藏</div>
          </div>
          <div class="action-item" @click="gotoNotes">
            <div class="action-icon">
              <el-icon><EditPen /></el-icon>
            </div>
            <div class="action-text">学习笔记</div>
          </div>
        </div>
      </div>

      <!-- 最近学习 -->
      <div class="recent-study" v-if="recentCourses.length > 0">
        <div class="section-header">
          <h3>最近学习</h3>
          <el-button text @click="gotoMyCourses">查看全部</el-button>
        </div>
        <div class="recent-courses">
          <div 
            v-for="course in recentCourses"
            :key="course.id"
            class="recent-course-item"
            @click="continueLearning(course)"
          >
            <div class="course-cover">
              <img 
                :src="course.courseLogo || '/images/default-course-cover.png'" 
                :alt="course.courseName"
                @error="handleImageError"
              >
              <div class="play-overlay">
                <el-icon><VideoPlay /></el-icon>
              </div>
            </div>
            <div class="course-info">
              <h4>{{ course.courseName }}</h4>
              <div class="course-progress">
                <el-progress 
                  :percentage="getCourseProgress(course.id)" 
                  :stroke-width="4"
                  :show-text="false"
                />
                <span class="progress-text">{{ getCourseProgress(course.id) }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 学习日历 -->
      <div class="study-calendar">
        <div class="section-header">
          <h3>学习日历</h3>
          <div class="calendar-controls">
            <el-button-group size="small">
              <el-button @click="previousMonth">
                <el-icon><ArrowLeft /></el-icon>
              </el-button>
              <el-button>{{ currentMonthText }}</el-button>
              <el-button @click="nextMonth">
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </el-button-group>
          </div>
        </div>
        <div class="calendar-grid">
          <div class="calendar-header">
            <div v-for="day in weekDays" :key="day" class="day-header">{{ day }}</div>
          </div>
          <div class="calendar-body">
            <div 
              v-for="date in calendarDates"
              :key="date.date"
              class="calendar-day"
              :class="{
                'other-month': !date.isCurrentMonth,
                'has-study': date.hasStudy,
                'today': date.isToday
              }"
            >
              <div class="day-number">{{ date.day }}</div>
              <div v-if="date.hasStudy" class="study-indicator">
                <div class="study-dot" :style="{ opacity: date.studyIntensity }"></div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 头像上传对话框 -->
    <el-dialog
      v-model="showAvatarDialog"
      title="上传头像"
      width="400px"
    >
      <el-upload
        class="avatar-uploader"
        :show-file-list="false"
        :on-success="handleAvatarSuccess"
        :before-upload="beforeAvatarUpload"
        action="/api/upload/avatar"
        :headers="uploadHeaders"
      >
        <img v-if="previewAvatar" :src="previewAvatar" class="avatar-preview">
        <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
      </el-upload>
      <template #footer>
        <el-button @click="showAvatarDialog = false">取消</el-button>
        <el-button type="primary" @click="saveAvatar">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {
  UserFilled,
  Camera,
  Setting,
  Check,
  VideoPlay,
  Reading,
  ShoppingCart,
  Star,
  EditPen,
  ArrowLeft,
  ArrowRight,
  Plus
} from '@element-plus/icons-vue'

// 页面元数据
definePageMeta({
  title: '个人中心 - 领课教育',
  middleware: 'auth'
})

// Store
const userStore = useUserStore()
const learningStore = useLearningStore()

const { userInfo } = storeToRefs(userStore)
const { studyStats, recentCourses, overallProgress } = storeToRefs(learningStore)

// 响应式数据
const showAvatarDialog = ref(false)
const previewAvatar = ref('')
const currentDate = ref(new Date())

const weekDays = ['日', '一', '二', '三', '四', '五', '六']

// 计算属性
const currentMonthText = computed(() => {
  return `${currentDate.value.getFullYear()}年${currentDate.value.getMonth() + 1}月`
})

const calendarDates = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth()
  const today = new Date()
  
  // 获取当月第一天和最后一天
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  
  // 获取日历开始和结束日期（包含上月和下月的日期）
  const startDate = new Date(firstDay)
  startDate.setDate(startDate.getDate() - firstDay.getDay())
  
  const endDate = new Date(lastDay)
  endDate.setDate(endDate.getDate() + (6 - lastDay.getDay()))
  
  const dates = []
  const current = new Date(startDate)
  
  while (current <= endDate) {
    const dateStr = current.toISOString().split('T')[0]
    dates.push({
      date: dateStr,
      day: current.getDate(),
      isCurrentMonth: current.getMonth() === month,
      isToday: current.toDateString() === today.toDateString(),
      hasStudy: Math.random() > 0.7, // 模拟学习数据
      studyIntensity: Math.random() // 学习强度
    })
    current.setDate(current.getDate() + 1)
  }
  
  return dates
})

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token') || sessionStorage.getItem('token')
  return {
    Authorization: `Bearer ${token}`
  }
})

// 方法
const formatStudyTime = (minutes: number): string => {
  if (minutes < 60) {
    return `${minutes}分钟`
  }
  const hours = Math.floor(minutes / 60)
  return `${hours}小时`
}

const getCourseProgress = (courseId: number): number => {
  return learningStore.courseCompletionRate(courseId)
}

const gotoMyCourses = () => {
  navigateTo('/user/courses')
}

const gotoMyOrders = () => {
  navigateTo('/user/orders')
}

const gotoCollections = () => {
  navigateTo('/user/collections')
}

const gotoNotes = () => {
  navigateTo('/user/notes')
}

const gotoProfileSettings = () => {
  navigateTo('/user/profile')
}

const continueLearning = (course: any) => {
  navigateTo(`/learn/${course.id}`)
}

const showAvatarUpload = () => {
  previewAvatar.value = userInfo.value?.avatar || ''
  showAvatarDialog.value = true
}

const beforeAvatarUpload = (file: File) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
  const isLt2M = file.size / 1024 / 1024 < 2

  if (!isJPG) {
    ElMessage.error('头像图片只能是 JPG/PNG 格式!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleAvatarSuccess = (response: any) => {
  previewAvatar.value = response.data.url
}

const saveAvatar = async () => {
  try {
    if (previewAvatar.value) {
      // 这里应该调用API更新头像
      // await userStore.updateProfile({ avatar: previewAvatar.value })
      ElMessage.success('头像更新成功')
      showAvatarDialog.value = false
    }
  } catch (error) {
    ElMessage.error('头像更新失败')
  }
}

const previousMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() - 1, 1)
}

const nextMonth = () => {
  currentDate.value = new Date(currentDate.value.getFullYear(), currentDate.value.getMonth() + 1, 1)
}

const handleImageError = (e: Event) => {
  const target = e.target as HTMLImageElement
  target.src = '/images/default-course-cover.png'
}

// 生命周期
onMounted(async () => {
  // 加载学习数据
  await Promise.all([
    learningStore.fetchMyCourses(),
    learningStore.loadStudyProgress()
  ])
})
</script>

<style lang="scss" scoped>
.user-center {
  padding: $spacing-lg 0;
  background: $bg-page;
  min-height: 100vh;

  .user-profile-card {
    background: white;
    border-radius: $border-radius-large;
    padding: $spacing-xl;
    margin-bottom: $spacing-lg;
    box-shadow: $box-shadow-base;

    .profile-header {
      display: flex;
      gap: $spacing-xl;
      margin-bottom: $spacing-lg;

      .avatar-section {
        position: relative;
        flex-shrink: 0;

        .upload-btn {
          position: absolute;
          bottom: -5px;
          right: -5px;
          width: 32px;
          height: 32px;
          border-radius: 50%;
          padding: 0;
          border: 2px solid white;
          box-shadow: $box-shadow-base;
        }
      }

      .user-info {
        flex: 1;

        h2 {
          font-size: 1.5rem;
          margin-bottom: $spacing-sm;
          color: $text-primary;
        }

        .user-desc {
          color: $text-secondary;
          margin-bottom: $spacing-lg;
        }

        .user-stats {
          display: flex;
          gap: $spacing-xl;

          .stat-item {
            text-align: center;

            .stat-number {
              font-size: 1.5rem;
              font-weight: 600;
              color: $primary-color;
              margin-bottom: $spacing-xs;
            }

            .stat-label {
              font-size: $font-size-sm;
              color: $text-secondary;
            }
          }
        }
      }
    }

    .profile-actions {
      text-align: right;
    }
  }

  .study-overview {
    background: white;
    border-radius: $border-radius-large;
    padding: $spacing-xl;
    margin-bottom: $spacing-lg;
    box-shadow: $box-shadow-base;

    .overview-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-lg;

      h3 {
        margin: 0;
        color: $text-primary;
      }

      .progress-ring {
        .progress-text {
          font-size: $font-size-lg;
          font-weight: 600;
          color: $primary-color;
        }
      }
    }

    .overview-stats {
      display: flex;
      gap: $spacing-xl;

      .overview-item {
        display: flex;
        align-items: center;
        gap: $spacing-md;

        .item-icon {
          width: 48px;
          height: 48px;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 20px;

          &.completed {
            background: rgba($success-color, 0.1);
            color: $success-color;
          }

          &.learning {
            background: rgba($primary-color, 0.1);
            color: $primary-color;
          }
        }

        .item-info {
          .item-number {
            font-size: 1.2rem;
            font-weight: 600;
            color: $text-primary;
          }

          .item-label {
            font-size: $font-size-sm;
            color: $text-secondary;
          }
        }
      }
    }
  }

  .quick-actions {
    background: white;
    border-radius: $border-radius-large;
    padding: $spacing-xl;
    margin-bottom: $spacing-lg;
    box-shadow: $box-shadow-base;

    .action-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: $spacing-lg;

      .action-item {
        text-align: center;
        padding: $spacing-lg;
        border-radius: $border-radius-base;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          background: $bg-color;
          transform: translateY(-2px);
        }

        .action-icon {
          width: 48px;
          height: 48px;
          background: rgba($primary-color, 0.1);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          margin: 0 auto $spacing-sm;
          font-size: 20px;
          color: $primary-color;
        }

        .action-text {
          font-weight: 500;
          color: $text-primary;
        }
      }
    }
  }

  .recent-study {
    background: white;
    border-radius: $border-radius-large;
    padding: $spacing-xl;
    margin-bottom: $spacing-lg;
    box-shadow: $box-shadow-base;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-lg;

      h3 {
        margin: 0;
        color: $text-primary;
      }
    }

    .recent-courses {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: $spacing-lg;

      .recent-course-item {
        cursor: pointer;
        transition: transform 0.3s ease;

        &:hover {
          transform: translateY(-4px);
        }

        .course-cover {
          position: relative;
          border-radius: $border-radius-base;
          overflow: hidden;
          margin-bottom: $spacing-sm;

          img {
            width: 100%;
            height: 120px;
            object-fit: cover;
          }

          .play-overlay {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 40px;
            height: 40px;
            background: rgba(0, 0, 0, 0.7);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            opacity: 0;
            transition: opacity 0.3s ease;
          }

          &:hover .play-overlay {
            opacity: 1;
          }
        }

        .course-info {
          h4 {
            margin: 0 0 $spacing-sm 0;
            font-size: $font-size-md;
            color: $text-primary;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .course-progress {
            display: flex;
            align-items: center;
            gap: $spacing-sm;

            .progress-text {
              font-size: $font-size-xs;
              color: $text-secondary;
              min-width: 35px;
            }
          }
        }
      }
    }
  }

  .study-calendar {
    background: white;
    border-radius: $border-radius-large;
    padding: $spacing-xl;
    box-shadow: $box-shadow-base;

    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: $spacing-lg;

      h3 {
        margin: 0;
        color: $text-primary;
      }
    }

    .calendar-grid {
      .calendar-header {
        display: grid;
        grid-template-columns: repeat(7, 1fr);
        margin-bottom: $spacing-sm;

        .day-header {
          text-align: center;
          padding: $spacing-sm;
          font-weight: 600;
          color: $text-secondary;
          font-size: $font-size-sm;
        }
      }

      .calendar-body {
        display: grid;
        grid-template-columns: repeat(7, 1fr);
        gap: 1px;
        background: $border-extra-light;

        .calendar-day {
          background: white;
          padding: $spacing-sm;
          min-height: 40px;
          position: relative;
          text-align: center;
          cursor: pointer;
          transition: background-color 0.3s ease;

          &.other-month {
            color: $text-placeholder;
            background: $bg-color;
          }

          &.today {
            background: rgba($primary-color, 0.1);
            color: $primary-color;
            font-weight: 600;
          }

          &.has-study {
            background: rgba($success-color, 0.05);
          }

          &:hover {
            background: rgba($primary-color, 0.1);
          }

          .day-number {
            font-size: $font-size-sm;
          }

          .study-indicator {
            position: absolute;
            bottom: 2px;
            right: 2px;

            .study-dot {
              width: 6px;
              height: 6px;
              background: $success-color;
              border-radius: 50%;
            }
          }
        }
      }
    }
  }

  .avatar-uploader {
    :deep(.el-upload) {
      border: 1px dashed $border-base;
      border-radius: 6px;
      cursor: pointer;
      position: relative;
      overflow: hidden;
      transition: border-color 0.3s ease;

      &:hover {
        border-color: $primary-color;
      }
    }

    .avatar-preview {
      width: 178px;
      height: 178px;
      display: block;
      object-fit: cover;
    }

    .avatar-uploader-icon {
      font-size: 28px;
      color: $text-placeholder;
      width: 178px;
      height: 178px;
      text-align: center;
      line-height: 178px;
    }
  }
}

// 响应式设计
@media (max-width: $breakpoint-lg) {
  .user-center {
    .quick-actions .action-grid {
      grid-template-columns: repeat(2, 1fr);
    }

    .recent-study .recent-courses {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}

@media (max-width: $breakpoint-md) {
  .user-center {
    .user-profile-card .profile-header {
      flex-direction: column;
      text-align: center;
    }

    .study-overview .overview-header {
      flex-direction: column;
      gap: $spacing-lg;
    }

    .study-overview .overview-stats {
      justify-content: center;
    }

    .quick-actions .action-grid {
      grid-template-columns: repeat(2, 1fr);
      gap: $spacing-md;
    }

    .recent-study .recent-courses {
      grid-template-columns: 1fr;
    }
  }
}
</style>