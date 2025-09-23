<template>
  <div class="course-detail">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <div class="container">
        <el-skeleton :rows="8" animated />
      </div>
    </div>

    <!-- 课程详情内容 -->
    <div v-else-if="courseDetail" class="course-content">
      <!-- 课程头部信息 -->
      <div class="course-header">
        <div class="container">
          <div class="header-content">
            <div class="course-info">
              <nav class="breadcrumb">
                <el-breadcrumb separator=">">
                  <el-breadcrumb-item><NuxtLink to="/">首页</NuxtLink></el-breadcrumb-item>
                  <el-breadcrumb-item><NuxtLink to="/courses">课程中心</NuxtLink></el-breadcrumb-item>
                  <el-breadcrumb-item>{{ courseDetail.courseName }}</el-breadcrumb-item>
                </el-breadcrumb>
              </nav>

              <h1 class="course-title">{{ courseDetail.courseName }}</h1>
              <p class="course-subtitle">{{ courseDetail.courseDesc }}</p>

              <div class="course-meta">
                <div class="meta-item">
                  <el-icon><User /></el-icon>
                  <span>讲师：{{ courseDetail.teacherName }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><FolderOpened /></el-icon>
                  <span>分类：{{ courseDetail.categoryName }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><Clock /></el-icon>
                  <span>时长：{{ courseDuration }}</span>
                </div>
                <div class="meta-item">
                  <el-icon><UserFilled /></el-icon>
                  <span>{{ formatNumber(courseDetail.courseSale || 0) }}人学习</span>
                </div>
              </div>
            </div>

            <div class="course-cover">
              <div class="cover-image">
                <img 
                  :src="courseDetail.courseLogo || '/images/default-course-cover.png'" 
                  :alt="courseDetail.courseName"
                  @error="handleImageError"
                >
                <div class="play-button" @click="startLearning">
                  <el-icon class="play-icon"><VideoPlay /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 购买卡片 -->
      <div class="purchase-section">
        <div class="container">
          <div class="purchase-card">
            <div class="price-info">
              <div v-if="courseDetail.isFree === 1" class="price-free">
                <span class="price-label">免费学习</span>
              </div>
              <div v-else class="price-paid">
                <span class="current-price">¥{{ formatPrice(courseDetail.rulingPrice) }}</span>
                <span v-if="originalPrice > courseDetail.rulingPrice" class="original-price">
                  ¥{{ formatPrice(originalPrice) }}
                </span>
              </div>
            </div>

            <div class="purchase-actions">
              <el-button 
                v-if="courseDetail.isFree === 1 || hasBought"
                type="primary" 
                size="large"
                :loading="actionLoading"
                @click="startLearning"
              >
                {{ hasBought ? '继续学习' : '开始学习' }}
              </el-button>
              <el-button 
                v-else
                type="primary" 
                size="large"
                :loading="actionLoading"
                @click="buyCourse"
              >
                立即购买
              </el-button>

              <el-button 
                :icon="isCollected ? StarFilled : Star"
                :class="{ 'is-collected': isCollected }"
                @click="toggleCollect"
              >
                {{ isCollected ? '已收藏' : '收藏课程' }}
              </el-button>
              <el-button 
                :icon="Share"
                @click="shareCourse"
              >
                分享课程
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 课程详细信息 -->
      <div class="course-tabs">
        <div class="container">
          <el-tabs v-model="activeTab">
            <el-tab-pane label="课程介绍" name="intro">
              <div class="course-intro">
                <h3>课程简介</h3>
                <div class="intro-content" v-html="courseDetail.courseDesc || '暂无课程介绍'"></div>
              </div>
            </el-tab-pane>
            <el-tab-pane label="课程目录" name="chapters">
              <div class="course-chapters">
                <h3>课程目录</h3>
                <p>共 {{ courseChapters.length }} 个章节</p>
                <!-- 章节列表将在后续完善 -->
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-state">
      <div class="container">
        <el-result
          icon="error"
          title="课程不存在"
          sub-title="抱歉，您访问的课程不存在或已下架"
        >
          <template #extra>
            <el-button type="primary" @click="$router.back()">返回上页</el-button>
            <el-button @click="$router.push('/courses')">浏览课程</el-button>
          
<script setup lang="ts">
import {
  User,
  UserFilled,
  FolderOpened,
  Clock,
  VideoPlay,
  Star,
  StarFilled,
  Share
} from '@element-plus/icons-vue'
import type { Course, CourseChapter } from '@/types'
import { courseApi } from '@/api/course'

// 页面元数据
definePageMeta({
  title: '课程详情 - 领课教育'
})

// 路由参数
const route = useRoute()
const courseId = Number(route.params.id)

// 响应式数据
const loading = ref(true)
const actionLoading = ref(false)
const activeTab = ref('intro')

const courseDetail = ref<Course | null>(null)
const courseChapters = ref<CourseChapter[]>([])
const hasBought = ref(false)
const isCollected = ref(false)

// 模拟数据
const courseDuration = ref('15小时30分钟')

// 计算属性
const originalPrice = computed(() => courseDetail.value ? courseDetail.value.rulingPrice * 1.2 : 0)

// 方法
const loadCourseDetail = async () => {
  try {
    loading.value = true
    const response = await courseApi.getCourseDetail(courseId)
    courseDetail.value = response.data
    
    // 设置页面标题
    useHead({
      title: `${courseDetail.value.courseName} - 领课教育`
    })
  } catch (error) {
    console.error('Load course detail error:', error)
    ElMessage.error('加载课程详情失败')
  } finally {
    loading.value = false
  }
}

const loadCourseChapters = async () => {
  try {
    const response = await courseApi.getCourseChapters(courseId)
    courseChapters.value = response.data
  } catch (error) {
    console.error('Load course chapters error:', error)
  }
}

const startLearning = () => {
  if (courseDetail.value?.isFree === 1 || hasBought.value) {
    navigateTo(`/learn/${courseId}`)
  } else {
    buyCourse()
  }
}

const buyCourse = async () => {
  try {
    actionLoading.value = true
    ElMessage.success('跳转到支付页面...')
    // 实际应该跳转到支付页面
  } catch (error) {
    ElMessage.error('购买失败，请重试')
  } finally {
    actionLoading.value = false
  }
}

const toggleCollect = async () => {
  try {
    isCollected.value = !isCollected.value
    ElMessage.success(isCollected.value ? '收藏成功' : '取消收藏成功')
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

const shareCourse = () => {
  const shareUrl = window.location.href
  navigator.clipboard.writeText(shareUrl)
  ElMessage.success('课程链接已复制到剪贴板')
}

const handleImageError = (e: Event) => {
  const target = e.target as HTMLImageElement
  target.src = '/images/default-course-cover.png'
}

const formatNumber = (num: number): string => {
  if (num >= 10000) {
    return `${(num / 10000).toFixed(1)}万`
  }
  return num.toString()
}

const formatPrice = (price: number): string => {
  return price.toFixed(2)
}

// 生命周期
onMounted(async () => {
  await loadCourseDetail()
  if (courseDetail.value) {
    await loadCourseChapters()
  }
})

<style lang="scss" scoped>
.course-detail {
  .loading-container {
    padding: $spacing-xl 0;
  }

  .course-header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: $spacing-xl 0;

    .header-content {
      display: grid;
      grid-template-columns: 2fr 1fr;
      gap: $spacing-xl;
      align-items: start;
    }

    .breadcrumb {
      margin-bottom: $spacing-md;

      :deep(.el-breadcrumb__inner) {
        color: rgba(255, 255, 255, 0.8);

        &.is-link {
          color: white;
        }
      }
    }

    .course-title {
      font-size: 2.5rem;
      font-weight: 700;
      margin-bottom: $spacing-sm;
      line-height: 1.2;
    }

    .course-subtitle {
      font-size: 1.2rem;
      opacity: 0.9;
      margin-bottom: $spacing-lg;
      line-height: 1.5;
    }

    .course-meta {
      display: flex;
      flex-wrap: wrap;
      gap: $spacing-lg;
      margin-bottom: $spacing-md;

      .meta-item {
        display: flex;
        align-items: center;
        gap: $spacing-xs;
        color: rgba(255, 255, 255, 0.9);

        .el-icon {
          font-size: 16px;
        }
      }
    }

    .course-cover {
      .cover-image {
        position: relative;
        border-radius: $border-radius-large;
        overflow: hidden;
        box-shadow: $box-shadow-dark;

        img {
          width: 100%;
          height: 200px;
          object-fit: cover;
        }

        .play-button {
          position: absolute;
          top: 50%;
          left: 50%;
          transform: translate(-50%, -50%);
          width: 60px;
          height: 60px;
          background: rgba(0, 0, 0, 0.8);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            background: rgba(0, 0, 0, 0.9);
            transform: translate(-50%, -50%) scale(1.1);
          }

          .play-icon {
            font-size: 24px;
            color: white;
            margin-left: 4px;
          }
        }
      }
    }
  }

  .purchase-section {
    background: white;
    padding: $spacing-lg 0;
    box-shadow: $box-shadow-base;

    .purchase-card {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: $spacing-lg;
      border-radius: $border-radius-base;
      background: $bg-color;

      .price-info {
        .price-free {
          .price-label {
            font-size: 1.5rem;
            font-weight: 600;
            color: $success-color;
          }
        }

        .price-paid {
          display: flex;
          align-items: baseline;
          gap: $spacing-sm;

          .current-price {
            font-size: 2rem;
            font-weight: 700;
            color: $danger-color;
          }

          .original-price {
            font-size: $font-size-lg;
            color: $text-placeholder;
            text-decoration: line-through;
          }
        }
      }

      .purchase-actions {
        display: flex;
        gap: $spacing-sm;

        .is-collected {
          color: $warning-color;
        }
      }
    }
  }

  .course-tabs {
    padding: $spacing-xl 0;

    :deep(.el-tabs) {
      background: white;
      border-radius: $border-radius-base;
      box-shadow: $box-shadow-base;
      padding: $spacing-lg;

      .el-tabs__header {
        margin-bottom: $spacing-lg;
      }
    }

    .course-intro,
    .course-chapters {
      h3 {
        font-size: $font-size-lg;
        color: $text-primary;
        margin-bottom: $spacing-md;
        padding-bottom: $spacing-sm;
        border-bottom: 2px solid $primary-color;
      }

      .intro-content {
        line-height: 1.8;
        color: $text-regular;
      }
    }
  }

  .error-state {
    padding: $spacing-xl 0;
  }
}

// 响应式设计
@media (max-width: $breakpoint-md) {
  .course-detail {
    .course-header {
      .header-content {
        grid-template-columns: 1fr;
        gap: $spacing-lg;
      }

      .course-title {
        font-size: 2rem;
      }
    }

    .purchase-section {
      .purchase-card {
        flex-direction: column;
        gap: $spacing-md;
        align-items: stretch;

        .purchase-actions {
          justify-content: center;
        }
      }
    }
  }
}

@media (max-width: $breakpoint-sm) {
  .course-detail {
    .course-header {
      .course-meta {
        flex-direction: column;
        gap: $spacing-sm;
      }
    }

    .purchase-section {
      .purchase-card {
        .purchase-actions {
          flex-direction: column;
        }
      }
    }
  }
}
</style>
        </el-result>
      </div>
    </div>
  </div>
</template>