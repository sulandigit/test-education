<template>
  <div class="learning-page">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-overlay">
      <el-loading-service />
    </div>

    <!-- 学习界面 -->
    <div v-else-if="courseData" class="learning-container">
      <!-- 视频播放区域 -->
      <div class="video-section">
        <VideoPlayer
          :video-url="currentVideoUrl"
          :video-poster="courseData.course.courseLogo"
          :chapters="videoChapters"
          @play="onVideoPlay"
          @pause="onVideoPause"
          @timeupdate="onTimeUpdate"
          @ended="onVideoEnded"
          @chapterChange="onChapterChange"
        />

        <!-- 视频信息栏 -->
        <div class="video-info">
          <div class="video-title">
            <h2>{{ currentPeriod?.periodName || courseData.course.courseName }}</h2>
            <div class="video-meta">
              <span>第{{ currentChapterIndex + 1 }}章</span>
              <span>第{{ currentPeriodIndex + 1 }}节</span>
              <span v-if="currentPeriod?.videoLength">
                时长：{{ formatDuration(currentPeriod.videoLength) }}
              </span>
            </div>
          </div>

          <div class="video-actions">
            <el-button 
              :icon="isCollected ? StarFilled : Star"
              :class="{ 'is-collected': isCollected }"
              @click="toggleCollect"
            >
              {{ isCollected ? '已收藏' : '收藏' }}
            </el-button>
            <el-button :icon="Share" @click="shareCourse">
              分享
            </el-button>
            <el-button @click="showNotes = !showNotes">
              <el-icon><EditPen /></el-icon>
              笔记
            </el-button>
          </div>
        </div>
      </div>

      <!-- 侧边栏 -->
      <div class="sidebar" :class="{ 'is-collapsed': sidebarCollapsed }">
        <!-- 侧边栏切换按钮 -->
        <div class="sidebar-toggle" @click="toggleSidebar">
          <el-icon>
            <ArrowLeft v-if="!sidebarCollapsed" />
            <ArrowRight v-else />
          </el-icon>
        </div>

        <!-- 课程信息 -->
        <div class="course-header">
          <div class="course-cover">
            <img 
              :src="courseData.course.courseLogo || '/images/default-course-cover.png'" 
              :alt="courseData.course.courseName"
              @error="handleImageError"
            >
          </div>
          <div class="course-info">
            <h3 class="course-title">{{ courseData.course.courseName }}</h3>
            <div class="course-progress">
              <el-progress 
                :percentage="studyProgress" 
                :stroke-width="6"
                :show-text="false"
              />
              <span class="progress-text">{{ studyProgress }}% 完成</span>
            </div>
          </div>
        </div>

        <!-- 课程目录 -->
        <div class="course-catalog">
          <div class="catalog-header">
            <h4>课程目录</h4>
            <el-button 
              text 
              size="small" 
              @click="expandAll = !expandAll"
            >
              {{ expandAll ? '收起全部' : '展开全部' }}
            </el-button>
          </div>

          <div class="catalog-content">
            <div 
              v-for="(chapter, chapterIndex) in courseData.chapters"
              :key="chapter.id"
              class="chapter-group"
            >
              <div 
                class="chapter-title"
                @click="toggleChapter(chapterIndex)"
              >
                <el-icon class="expand-icon" :class="{ 'is-expanded': expandedChapters.includes(chapterIndex) }">
                  <ArrowRight />
                </el-icon>
                <span>第{{ chapterIndex + 1 }}章 {{ chapter.chapterName }}</span>
                <span class="chapter-progress">{{ getChapterProgress(chapterIndex) }}%</span>
              </div>

              <div 
                v-show="expandedChapters.includes(chapterIndex) || expandAll"
                class="periods-list"
              >
                <div 
                  v-for="(period, periodIndex) in chapter.periods"
                  :key="period.id"
                  class="period-item"
                  :class="{ 
                    'is-current': chapterIndex === currentChapterIndex && periodIndex === currentPeriodIndex,
                    'is-completed': isPeriodCompleted(chapterIndex, periodIndex)
                  }"
                  @click="selectPeriod(chapterIndex, periodIndex)"
                >
                  <div class="period-status">
                    <el-icon v-if="isPeriodCompleted(chapterIndex, periodIndex)" class="completed-icon">
                      <Check />
                    </el-icon>
                    <el-icon v-else-if="chapterIndex === currentChapterIndex && periodIndex === currentPeriodIndex" class="playing-icon">
                      <VideoPlay />
                    </el-icon>
                    <span v-else class="period-number">{{ periodIndex + 1 }}</span>
                  </div>
                  <div class="period-info">
                    <span class="period-name">{{ period.periodName }}</span>
                    <span v-if="period.videoLength" class="period-duration">
                      {{ formatDuration(period.videoLength) }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 学习笔记面板 -->
      <div v-if="showNotes" class="notes-panel">
        <div class="notes-header">
          <h4>学习笔记</h4>
          <el-button text @click="showNotes = false">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        <div class="notes-content">
          <el-input
            v-model="currentNote"
            type="textarea"
            :rows="10"
            placeholder="记录你的学习心得..."
            @blur="saveNote"
          />
        </div>
        <div class="notes-actions">
          <el-button type="primary" @click="saveNote">保存笔记</el-button>
        </div>
      </div>
    </div>

    <!-- 错误状态 -->
    <div v-else class="error-state">
      <el-result
        icon="error"
        title="无法加载课程"
        sub-title="抱歉，无法加载该课程内容"
      >
        <template #extra>
          <el-button type="primary" @click="$router.back()">返回上页</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  Star,
  StarFilled,
  Share,
  EditPen,
  ArrowLeft,
  ArrowRight,
  Check,
  VideoPlay,
  Close
} from '@element-plus/icons-vue'
import type { Course, CourseChapter, CoursePeriod } from '@/types'
import { authCourseApi } from '@/api/course'

// 页面元数据
definePageMeta({
  title: '课程学习 - 领课教育',
  middleware: 'auth', // 需要登录
  layout: false // 使用全屏布局
})

// 路由参数
const route = useRoute()
const courseId = Number(route.params.id)

// 响应式数据
const loading = ref(true)
const sidebarCollapsed = ref(false)
const showNotes = ref(false)
const expandAll = ref(false)
const isCollected = ref(false)

const courseData = ref<{
  course: Course
  chapters: CourseChapter[]
  videoSign?: string
  canStudy: boolean
} | null>(null)

const currentChapterIndex = ref(0)
const currentPeriodIndex = ref(0)
const currentVideoUrl = ref('')
const currentNote = ref('')

const studyProgress = ref(0)
const studyRecords = ref<Record<string, { progress: number; completed: boolean }>>({})
const expandedChapters = ref<number[]>([0])

// 计算属性
const currentPeriod = computed(() => {
  const chapter = courseData.value?.chapters[currentChapterIndex.value]
  return chapter?.periods?.[currentPeriodIndex.value]
})

const videoChapters = computed(() => {
  if (!courseData.value?.chapters) return []
  
  return courseData.value.chapters.map((chapter, chapterIndex) => ({
    id: chapter.id,
    title: chapter.chapterName,
    startTime: 0, // 实际应该计算累计时间
    duration: chapter.periods?.reduce((total, period) => total + (period.videoLength || 0), 0) || 0
  }))
})

// 方法
const loadCourseData = async () => {
  try {
    loading.value = true
    const response = await authCourseApi.getStudyCourseDetail(courseId)
    courseData.value = response.data

    if (!courseData.value.canStudy) {
      ElMessage.error('您还没有购买此课程，无法学习')
      navigateTo(`/course/${courseId}`)
      return
    }

    // 设置页面标题
    useHead({
      title: `${courseData.value.course.courseName} - 课程学习`
    })

    // 初始化第一个视频
    initializeFirstVideo()
  } catch (error) {
    console.error('Load course data error:', error)
    ElMessage.error('加载课程数据失败')
  } finally {
    loading.value = false
  }
}

const initializeFirstVideo = () => {
  if (courseData.value?.chapters.length) {
    const firstChapter = courseData.value.chapters[0]
    if (firstChapter.periods?.length) {
      selectPeriod(0, 0)
    }
  }
}

const selectPeriod = async (chapterIndex: number, periodIndex: number) => {
  currentChapterIndex.value = chapterIndex
  currentPeriodIndex.value = periodIndex

  const period = courseData.value?.chapters[chapterIndex]?.periods?.[periodIndex]
  if (period?.videoUrl) {
    currentVideoUrl.value = period.videoUrl
  }

  // 加载该节课的笔记
  await loadPeriodNote()
}

const toggleChapter = (index: number) => {
  const pos = expandedChapters.value.indexOf(index)
  if (pos > -1) {
    expandedChapters.value.splice(pos, 1)
  } else {
    expandedChapters.value.push(index)
  }
}

const toggleSidebar = () => {
  sidebarCollapsed.value = !sidebarCollapsed.value
}

const isPeriodCompleted = (chapterIndex: number, periodIndex: number): boolean => {
  const key = `${chapterIndex}-${periodIndex}`
  return studyRecords.value[key]?.completed || false
}

const getChapterProgress = (chapterIndex: number): number => {
  const chapter = courseData.value?.chapters[chapterIndex]
  if (!chapter?.periods) return 0

  const completedCount = chapter.periods.filter((_, periodIndex) => 
    isPeriodCompleted(chapterIndex, periodIndex)
  ).length

  return Math.round((completedCount / chapter.periods.length) * 100)
}

const updateStudyProgress = () => {
  if (!courseData.value?.chapters) return

  let totalPeriods = 0
  let completedPeriods = 0

  courseData.value.chapters.forEach((chapter, chapterIndex) => {
    if (chapter.periods) {
      totalPeriods += chapter.periods.length
      completedPeriods += chapter.periods.filter((_, periodIndex) => 
        isPeriodCompleted(chapterIndex, periodIndex)
      ).length
    }
  })

  studyProgress.value = totalPeriods > 0 ? Math.round((completedPeriods / totalPeriods) * 100) : 0
}

const markPeriodCompleted = (chapterIndex: number, periodIndex: number) => {
  const key = `${chapterIndex}-${periodIndex}`
  studyRecords.value[key] = {
    progress: 100,
    completed: true
  }
  updateStudyProgress()
  
  // 保存学习进度到服务器
  saveLearningProgress()
}

const saveLearningProgress = async () => {
  try {
    // await learningApi.saveProgress({
    //   courseId,
    //   periodId: currentPeriod.value?.id,
    //   progress: studyProgress.value
    // })
  } catch (error) {
    console.error('Save progress error:', error)
  }
}

const loadPeriodNote = async () => {
  try {
    // const response = await learningApi.getPeriodNote(courseId, currentPeriod.value?.id)
    // currentNote.value = response.data.content
    currentNote.value = '' // 临时空白
  } catch (error) {
    console.error('Load note error:', error)
  }
}

const saveNote = async () => {
  try {
    // await learningApi.savePeriodNote({
    //   courseId,
    //   periodId: currentPeriod.value?.id,
    //   content: currentNote.value
    // })
    ElMessage.success('笔记保存成功')
  } catch (error) {
    ElMessage.error('笔记保存失败')
  }
}

const toggleCollect = async () => {
  try {
    isCollected.value = !isCollected.value
    ElMessage.success(isCollected.value ? '收藏成功' : '取消收藏成功')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const shareCourse = () => {
  const shareUrl = `${window.location.origin}/course/${courseId}`
  navigator.clipboard.writeText(shareUrl)
  ElMessage.success('分享链接已复制')
}

const handleImageError = (e: Event) => {
  const target = e.target as HTMLImageElement
  target.src = '/images/default-course-cover.png'
}

const formatDuration = (seconds: number): string => {
  const minutes = Math.floor(seconds / 60)
  return `${minutes}分钟`
}

// 视频播放器事件处理
const onVideoPlay = () => {
  console.log('Video started playing')
}

const onVideoPause = () => {
  console.log('Video paused')
}

const onTimeUpdate = (currentTime: number) => {
  // 记录观看进度
  const key = `${currentChapterIndex.value}-${currentPeriodIndex.value}`
  const progress = currentPeriod.value ? (currentTime / (currentPeriod.value.videoLength || 1)) * 100 : 0
  
  studyRecords.value[key] = {
    progress: Math.min(progress, 100),
    completed: progress >= 90 // 观看90%视为完成
  }
}

const onVideoEnded = () => {
  // 标记当前节课为已完成
  markPeriodCompleted(currentChapterIndex.value, currentPeriodIndex.value)
  
  // 自动播放下一节课
  const nextPeriod = getNextPeriod()
  if (nextPeriod) {
    selectPeriod(nextPeriod.chapterIndex, nextPeriod.periodIndex)
  }
}

const onChapterChange = (chapterIndex: number) => {
  if (chapterIndex !== currentChapterIndex.value) {
    selectPeriod(chapterIndex, 0)
  }
}

const getNextPeriod = () => {
  const currentChapter = courseData.value?.chapters[currentChapterIndex.value]
  if (!currentChapter) return null

  // 检查当前章节是否还有下一节课
  if (currentPeriodIndex.value < (currentChapter.periods?.length || 0) - 1) {
    return {
      chapterIndex: currentChapterIndex.value,
      periodIndex: currentPeriodIndex.value + 1
    }
  }

  // 检查是否有下一章节
  if (currentChapterIndex.value < (courseData.value?.chapters.length || 0) - 1) {
    return {
      chapterIndex: currentChapterIndex.value + 1,
      periodIndex: 0
    }
  }

  return null
}

// 生命周期
onMounted(async () => {
  await loadCourseData()
})

// 页面离开时保存学习进度
onBeforeUnmount(() => {
  saveLearningProgress()
})
</script>

<style lang="scss" scoped>
.learning-page {
  height: 100vh;
  background: #000;
  position: relative;
  overflow: hidden;

  .loading-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.8);
    z-index: 1000;
  }

  .learning-container {
    display: flex;
    height: 100vh;

    .video-section {
      flex: 1;
      display: flex;
      flex-direction: column;
      background: #000;

      .video-info {
        background: rgba(0, 0, 0, 0.9);
        color: white;
        padding: $spacing-md $spacing-lg;
        display: flex;
        justify-content: space-between;
        align-items: center;

        .video-title {
          h2 {
            margin: 0 0 $spacing-xs 0;
            font-size: $font-size-lg;
          }

          .video-meta {
            display: flex;
            gap: $spacing-md;
            font-size: $font-size-sm;
            color: rgba(255, 255, 255, 0.7);
          }
        }

        .video-actions {
          display: flex;
          gap: $spacing-sm;

          .is-collected {
            color: $warning-color;
          }
        }
      }
    }

    .sidebar {
      width: 360px;
      background: white;
      display: flex;
      flex-direction: column;
      transition: all 0.3s ease;
      position: relative;

      &.is-collapsed {
        width: 0;
        overflow: hidden;
      }

      .sidebar-toggle {
        position: absolute;
        left: -40px;
        top: 50%;
        transform: translateY(-50%);
        width: 40px;
        height: 60px;
        background: rgba(255, 255, 255, 0.9);
        display: flex;
        align-items: center;
        justify-content: center;
        cursor: pointer;
        border-radius: 8px 0 0 8px;
        z-index: 10;

        &:hover {
          background: white;
        }
      }

      .course-header {
        padding: $spacing-lg;
        border-bottom: 1px solid $border-base;

        .course-cover {
          img {
            width: 100%;
            height: 120px;
            object-fit: cover;
            border-radius: $border-radius-base;
            margin-bottom: $spacing-md;
          }
        }

        .course-title {
          font-size: $font-size-md;
          margin-bottom: $spacing-sm;
          line-height: 1.4;
        }

        .course-progress {
          .progress-text {
            font-size: $font-size-sm;
            color: $text-secondary;
            margin-top: $spacing-xs;
            display: block;
          }
        }
      }

      .course-catalog {
        flex: 1;
        overflow-y: auto;

        .catalog-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: $spacing-md $spacing-lg;
          border-bottom: 1px solid $border-extra-light;

          h4 {
            margin: 0;
            font-size: $font-size-md;
          }
        }

        .catalog-content {
          .chapter-group {
            .chapter-title {
              display: flex;
              align-items: center;
              gap: $spacing-sm;
              padding: $spacing-md $spacing-lg;
              cursor: pointer;
              background: $bg-color;
              border-bottom: 1px solid $border-extra-light;
              font-weight: 500;

              &:hover {
                background: darken($bg-color, 5%);
              }

              .expand-icon {
                transition: transform 0.3s ease;

                &.is-expanded {
                  transform: rotate(90deg);
                }
              }

              .chapter-progress {
                margin-left: auto;
                font-size: $font-size-xs;
                color: $text-secondary;
              }
            }

            .periods-list {
              .period-item {
                display: flex;
                align-items: center;
                gap: $spacing-sm;
                padding: $spacing-sm $spacing-lg $spacing-sm calc(#{$spacing-lg} + 24px);
                cursor: pointer;
                border-bottom: 1px solid $border-extra-light;
                transition: all 0.3s ease;

                &:hover {
                  background: $bg-color;
                }

                &.is-current {
                  background: $primary-color;
                  color: white;
                }

                &.is-completed {
                  .period-info .period-name {
                    color: $success-color;
                  }
                }

                .period-status {
                  width: 20px;
                  height: 20px;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  flex-shrink: 0;

                  .completed-icon {
                    color: $success-color;
                  }

                  .playing-icon {
                    color: $primary-color;
                  }

                  .period-number {
                    font-size: $font-size-xs;
                    color: $text-secondary;
                  }
                }

                .period-info {
                  flex: 1;
                  min-width: 0;

                  .period-name {
                    display: block;
                    font-size: $font-size-sm;
                    line-height: 1.4;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                  }

                  .period-duration {
                    font-size: $font-size-xs;
                    color: $text-placeholder;
                  }
                }

                &.is-current .period-status .period-number,
                &.is-current .period-info .period-duration {
                  color: rgba(255, 255, 255, 0.7);
                }
              }
            }
          }
        }
      }
    }

    .notes-panel {
      position: absolute;
      right: 0;
      top: 0;
      bottom: 0;
      width: 400px;
      background: white;
      border-left: 1px solid $border-base;
      display: flex;
      flex-direction: column;
      z-index: 100;

      .notes-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: $spacing-md $spacing-lg;
        border-bottom: 1px solid $border-base;

        h4 {
          margin: 0;
        }
      }

      .notes-content {
        flex: 1;
        padding: $spacing-lg;

        :deep(.el-textarea__inner) {
          resize: none;
          border: none;
          box-shadow: none;

          &:focus {
            box-shadow: none;
          }
        }
      }

      .notes-actions {
        padding: $spacing-md $spacing-lg;
        border-top: 1px solid $border-base;
        text-align: right;
      }
    }
  }

  .error-state {
    height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
    background: white;
  }
}

// 响应式设计
@media (max-width: $breakpoint-md) {
  .learning-page .learning-container {
    .sidebar {
      position: absolute;
      right: 0;
      top: 0;
      bottom: 0;
      z-index: 200;
      box-shadow: $box-shadow-dark;

      &.is-collapsed {
        width: 0;
      }
    }

    .notes-panel {
      width: 100%;
    }
  }
}
</style>