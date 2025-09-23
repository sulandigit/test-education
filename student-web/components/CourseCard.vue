<template>
  <div class="course-card" @click="handleClick">
    <!-- 课程封面 -->
    <div class="course-cover">
      <img 
        :src="course.courseLogo || defaultCover" 
        :alt="course.courseName"
        class="cover-image"
        @error="handleImageError"
      >
      
      <!-- 免费标签 -->
      <div v-if="course.isFree === 1" class="free-badge">
        免费
      </div>
      
      <!-- 折扣标签 -->
      <div v-else-if="discountPercent > 0" class="discount-badge">
        {{ discountPercent }}折
      </div>

      <!-- 悬浮操作 -->
      <div class="course-actions">
        <el-button 
          circle 
          :icon="isCollected ? StarFilled : Star"
          :class="{ 'is-collected': isCollected }"
          @click.stop="toggleCollect"
        />
        <el-button 
          circle 
          :icon="Share"
          @click.stop="handleShare"
        />
      </div>
    </div>

    <!-- 课程信息 -->
    <div class="course-info">
      <!-- 课程标题 -->
      <h3 class="course-title" :title="course.courseName">
        {{ course.courseName }}
      </h3>

      <!-- 课程描述 -->
      <p class="course-desc" :title="course.courseDesc">
        {{ course.courseDesc }}
      </p>

      <!-- 讲师信息 -->
      <div class="course-teacher">
        <el-avatar 
          :src="teacherAvatar" 
          :size="24"
          class="teacher-avatar"
        >
          <template #error>
            <el-icon><UserFilled /></el-icon>
          </template>
        </el-avatar>
        <span class="teacher-name">{{ course.teacherName }}</span>
      </div>

      <!-- 统计信息 -->
      <div class="course-stats">
        <div class="stat-item">
          <el-icon><User /></el-icon>
          <span>{{ formatNumber(course.courseSale || 0) }}</span>
        </div>
        <div class="stat-item">
          <el-icon><Clock /></el-icon>
          <span>{{ courseDuration }}</span>
        </div>
        <div class="stat-item" v-if="course.rating">
          <el-rate
            v-model="course.rating"
            disabled
            show-score
            text-color="#ff9900"
            score-template="{value}"
            :max="5"
            size="small"
          />
        </div>
      </div>

      <!-- 价格信息 -->
      <div class="course-price">
        <div v-if="course.isFree === 1" class="price-free">
          免费
        </div>
        <div v-else class="price-paid">
          <span class="current-price">¥{{ formatPrice(course.rulingPrice) }}</span>
          <span v-if="originalPrice > course.rulingPrice" class="original-price">
            ¥{{ formatPrice(originalPrice) }}
          </span>
        </div>
      </div>

      <!-- 课程标签 -->
      <div class="course-tags" v-if="courseTags.length > 0">
        <el-tag
          v-for="tag in courseTags"
          :key="tag"
          size="small"
          type="info"
          effect="plain"
        >
          {{ tag }}
        </el-tag>
      </div>
    </div>

    <!-- 悬浮时的操作栏 -->
    <div class="course-hover-actions">
      <el-button 
        v-if="course.isFree === 1 || hasBought"
        type="primary" 
        size="small"
        @click.stop="startLearning"
      >
        {{ hasBought ? '继续学习' : '开始学习' }}
      </el-button>
      <el-button 
        v-else
        type="primary" 
        size="small"
        @click.stop="buyCourse"
      >
        立即购买
      </el-button>
      <el-button 
        size="small"
        @click.stop="viewDetail"
      >
        查看详情
      </el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import {
  Star,
  StarFilled,
  Share,
  UserFilled,
  User,
  Clock
} from '@element-plus/icons-vue'
import type { Course } from '@/types'

// Props
interface Props {
  course: Course
  showActions?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  showActions: true
})

// Emits
const emit = defineEmits<{
  click: [course: Course]
  collect: [courseId: number, isCollected: boolean]
  share: [course: Course]
  buy: [course: Course]
  learn: [course: Course]
}>()

// 响应式数据
const isCollected = ref(false)
const hasBought = ref(false)
const defaultCover = '/images/default-course-cover.png'
const teacherAvatar = ref('')

// 计算属性
const originalPrice = computed(() => {
  // 假设原价比现价高20%用于展示折扣效果
  return props.course.rulingPrice * 1.2
})

const discountPercent = computed(() => {
  if (props.course.isFree === 1) return 0
  const discount = Math.round((props.course.rulingPrice / originalPrice.value) * 10)
  return discount < 10 ? 10 - discount : 0
})

const courseDuration = computed(() => {
  // 假设课程时长数据，实际应从course对象获取
  const hours = Math.floor(Math.random() * 20) + 5
  return `${hours}小时`
})

const courseTags = computed(() => {
  // 根据课程信息生成标签
  const tags = []
  if (props.course.isFree === 1) tags.push('免费')
  if (props.course.courseSale && props.course.courseSale > 1000) tags.push('热门')
  if (props.course.categoryName) tags.push(props.course.categoryName)
  return tags.slice(0, 3) // 最多显示3个标签
})

// 方法
const handleClick = () => {
  emit('click', props.course)
}

const handleImageError = (e: Event) => {
  const target = e.target as HTMLImageElement
  target.src = defaultCover
}

const toggleCollect = async () => {
  try {
    const newCollectedState = !isCollected.value
    // 调用收藏API
    // await courseApi.toggleCollect(props.course.id, newCollectedState)
    isCollected.value = newCollectedState
    emit('collect', props.course.id, newCollectedState)
    
    ElMessage.success(newCollectedState ? '收藏成功' : '取消收藏成功')
  } catch (error) {
    ElMessage.error('操作失败，请重试')
  }
}

const handleShare = () => {
  emit('share', props.course)
  // 可以实现分享功能
  ElMessage.success('分享链接已复制到剪贴板')
}

const startLearning = () => {
  emit('learn', props.course)
}

const buyCourse = () => {
  emit('buy', props.course)
}

const viewDetail = () => {
  navigateTo(`/course/${props.course.id}`)
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
  // 检查收藏状态和购买状态
  await checkCourseStatus()
})

const checkCourseStatus = async () => {
  try {
    // 检查是否已收藏
    // const collectResponse = await courseApi.checkCollectStatus(props.course.id)
    // isCollected.value = collectResponse.data.isCollected

    // 检查是否已购买
    // const buyResponse = await courseApi.checkBuyStatus(props.course.id)
    // hasBought.value = buyResponse.data.hasBought
  } catch (error) {
    console.error('检查课程状态失败:', error)
  }
}
</script>

<style lang="scss" scoped>
.course-card {
  background: white;
  border-radius: $border-radius-large;
  overflow: hidden;
  box-shadow: $box-shadow-base;
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;

  &:hover {
    transform: translateY(-4px);
    box-shadow: $box-shadow-light;

    .course-actions {
      opacity: 1;
    }

    .course-hover-actions {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .course-cover {
    position: relative;
    width: 100%;
    height: 180px;
    overflow: hidden;

    .cover-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.3s ease;

      &:hover {
        transform: scale(1.05);
      }
    }

    .free-badge,
    .discount-badge {
      position: absolute;
      top: $spacing-sm;
      left: $spacing-sm;
      padding: 4px 8px;
      border-radius: $border-radius-base;
      font-size: $font-size-xs;
      font-weight: 600;
      color: white;
    }

    .free-badge {
      background: $success-color;
    }

    .discount-badge {
      background: $danger-color;
    }

    .course-actions {
      position: absolute;
      top: $spacing-sm;
      right: $spacing-sm;
      display: flex;
      flex-direction: column;
      gap: $spacing-xs;
      opacity: 0;
      transition: opacity 0.3s ease;

      .el-button {
        background: rgba(255, 255, 255, 0.9);
        border: none;
        box-shadow: $box-shadow-base;

        &:hover {
          background: white;
        }

        &.is-collected {
          color: $warning-color;
        }
      }
    }
  }

  .course-info {
    padding: $spacing-md;

    .course-title {
      font-size: $font-size-md;
      font-weight: 600;
      color: $text-primary;
      margin-bottom: $spacing-sm;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      line-height: 1.4;
      height: 2.8em;
    }

    .course-desc {
      color: $text-secondary;
      font-size: $font-size-sm;
      margin-bottom: $spacing-md;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      line-height: 1.5;
      height: 3em;
    }

    .course-teacher {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      margin-bottom: $spacing-md;

      .teacher-name {
        color: $text-regular;
        font-size: $font-size-sm;
      }
    }

    .course-stats {
      display: flex;
      align-items: center;
      gap: $spacing-md;
      margin-bottom: $spacing-md;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 4px;
        color: $text-secondary;
        font-size: $font-size-xs;

        .el-icon {
          font-size: 14px;
        }
      }
    }

    .course-price {
      margin-bottom: $spacing-sm;

      .price-free {
        color: $success-color;
        font-weight: 600;
        font-size: $font-size-lg;
      }

      .price-paid {
        display: flex;
        align-items: baseline;
        gap: $spacing-sm;

        .current-price {
          color: $danger-color;
          font-weight: 600;
          font-size: $font-size-lg;
        }

        .original-price {
          color: $text-placeholder;
          font-size: $font-size-sm;
          text-decoration: line-through;
        }
      }
    }

    .course-tags {
      display: flex;
      flex-wrap: wrap;
      gap: $spacing-xs;
    }
  }

  .course-hover-actions {
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    background: white;
    padding: $spacing-md;
    display: flex;
    gap: $spacing-sm;
    opacity: 0;
    transform: translateY(100%);
    transition: all 0.3s ease;
    border-top: 1px solid $border-extra-light;

    .el-button {
      flex: 1;
    }
  }
}

// 列表视图样式
.course-card.list-view {
  display: flex;
  height: 150px;

  .course-cover {
    width: 200px;
    height: 100%;
    flex-shrink: 0;
  }

  .course-info {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
  }

  .course-hover-actions {
    position: static;
    opacity: 1;
    transform: none;
    background: transparent;
    border: none;
    padding: 0;
    margin-top: auto;
  }
}

// 小尺寸卡片
.course-card.compact {
  .course-cover {
    height: 120px;
  }

  .course-info {
    padding: $spacing-sm;

    .course-title {
      font-size: $font-size-sm;
      -webkit-line-clamp: 1;
      height: 1.4em;
    }

    .course-desc {
      -webkit-line-clamp: 1;
      height: 1.5em;
    }
  }
}
</style>