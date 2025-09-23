<template>
  <div class="course-center">
    <div class="container">
      <!-- 页面标题和搜索 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">课程中心</h1>
          <p class="page-subtitle">发现更多优质课程，提升技能水平</p>
        </div>
        <div class="search-section">
          <el-input
            v-model="searchParams.courseName"
            placeholder="搜索课程名称、讲师或关键词..."
            class="search-input"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prepend>
              <el-select 
                v-model="searchParams.categoryId" 
                placeholder="分类"
                clearable
                style="width: 120px"
              >
                <el-option
                  v-for="category in categories"
                  :key="category.id"
                  :label="category.categoryName"
                  :value="category.id"
                />
              </el-select>
            </template>
            <template #append>
              <el-button :icon="Search" @click="handleSearch" />
            </template>
          </el-input>
        </div>
      </div>

      <!-- 筛选工具栏 -->
      <div class="filter-toolbar">
        <div class="filter-left">
          <!-- 课程类型筛选 -->
          <div class="filter-group">
            <span class="filter-label">课程类型：</span>
            <el-radio-group v-model="searchParams.isFree" @change="handleSearch">
              <el-radio-button :label="undefined">全部</el-radio-button>
              <el-radio-button :label="1">免费</el-radio-button>
              <el-radio-button :label="0">付费</el-radio-button>
            </el-radio-group>
          </div>

          <!-- 排序方式 -->
          <div class="filter-group">
            <span class="filter-label">排序：</span>
            <el-select 
              v-model="searchParams.orderBy" 
              placeholder="选择排序方式"
              @change="handleSearch"
              style="width: 150px"
            >
              <el-option label="最新发布" value="createTime" />
              <el-option label="最热门" value="courseSale" />
              <el-option label="价格从低到高" value="rulingPrice_asc" />
              <el-option label="价格从高到低" value="rulingPrice_desc" />
            </el-select>
          </div>
        </div>

        <div class="filter-right">
          <!-- 视图切换 -->
          <div class="view-toggle">
            <el-radio-group v-model="viewMode" size="small">
              <el-radio-button label="grid">
                <el-icon><Grid /></el-icon>
              </el-radio-button>
              <el-radio-button label="list">
                <el-icon><List /></el-icon>
              </el-radio-button>
            </el-radio-group>
          </div>

          <!-- 结果统计 -->
          <div class="result-count">
            共找到 <strong>{{ totalCount }}</strong> 门课程
          </div>
        </div>
      </div>

      <!-- 课程列表 -->
      <div class="course-content">
        <!-- 侧边栏分类 -->
        <div class="sidebar">
          <div class="category-sidebar">
            <h3 class="sidebar-title">课程分类</h3>
            <div class="category-tree">
              <div 
                v-for="category in categories"
                :key="category.id"
                class="category-item"
                :class="{ 'is-active': searchParams.categoryId === category.id }"
                @click="selectCategory(category.id)"
              >
                <span class="category-name">{{ category.categoryName }}</span>
                <span class="course-count">({{ category.courseCount || 0 }})</span>
              </div>
            </div>
          </div>

          <!-- 热门标签 -->
          <div class="tags-sidebar">
            <h3 class="sidebar-title">热门标签</h3>
            <div class="tags-list">
              <el-tag
                v-for="tag in hotTags"
                :key="tag"
                class="tag-item"
                @click="searchByTag(tag)"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 主内容区域 -->
        <div class="main-content">
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-skeleton :rows="6" animated />
          </div>

          <!-- 课程列表 -->
          <div v-else-if="courseList.length > 0" class="course-list">
            <!-- 网格视图 -->
            <div 
              v-if="viewMode === 'grid'" 
              class="course-grid"
            >
              <CourseCard
                v-for="course in courseList"
                :key="course.id"
                :course="course"
                @click="gotoCourseDetail"
                @collect="handleCollect"
                @share="handleShare"
                @buy="handleBuy"
                @learn="handleLearn"
              />
            </div>

            <!-- 列表视图 -->
            <div 
              v-else 
              class="course-list-view"
            >
              <CourseCard
                v-for="course in courseList"
                :key="course.id"
                :course="course"
                class="list-view"
                @click="gotoCourseDetail"
                @collect="handleCollect"
                @share="handleShare"
                @buy="handleBuy"
                @learn="handleLearn"
              />
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else class="empty-state">
            <el-empty 
              description="暂无相关课程" 
              :image-size="200"
            >
              <el-button type="primary" @click="clearFilters">
                清除筛选条件
              </el-button>
            </el-empty>
          </div>

          <!-- 分页 -->
          <div v-if="totalCount > 0" class="pagination-wrapper">
            <el-pagination
              v-model:current-page="searchParams.pageCurrent"
              v-model:page-size="searchParams.pageSize"
              :page-sizes="[12, 24, 36, 48]"
              :total="totalCount"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handlePageSizeChange"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Search, Grid, List } from '@element-plus/icons-vue'
import type { Course, CourseSearchRequest } from '@/types'
import { courseApi } from '@/api/course'

// 页面元数据
definePageMeta({
  title: '课程中心 - 领课教育',
  description: '发现更多优质课程，提升技能水平'
})

// 响应式数据
const loading = ref(false)
const viewMode = ref<'grid' | 'list'>('grid')

const searchParams = ref<CourseSearchRequest>({
  pageCurrent: 1,
  pageSize: 12,
  courseName: '',
  categoryId: undefined,
  isFree: undefined,
  orderBy: 'createTime'
})

const courseList = ref<Course[]>([])
const totalCount = ref(0)
const categories = ref([])
const hotTags = ref(['Vue.js', 'React', 'Node.js', '微信小程序', 'Python', 'Java', 'SpringBoot', 'MySQL'])

// 方法
const loadCourseList = async () => {
  try {
    loading.value = true
    const response = await courseApi.searchCourses(searchParams.value)
    
    courseList.value = response.data.list
    totalCount.value = response.data.totalCount
  } catch (error) {
    ElMessage.error('加载课程列表失败')
    console.error('Load course list error:', error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const response = await courseApi.getCategories()
    categories.value = response.data
  } catch (error) {
    console.error('Load categories error:', error)
  }
}

const handleSearch = () => {
  searchParams.value.pageCurrent = 1
  loadCourseList()
}

const selectCategory = (categoryId: number) => {
  if (searchParams.value.categoryId === categoryId) {
    searchParams.value.categoryId = undefined
  } else {
    searchParams.value.categoryId = categoryId
  }
  handleSearch()
}

const searchByTag = (tag: string) => {
  searchParams.value.courseName = tag
  handleSearch()
}

const clearFilters = () => {
  searchParams.value = {
    pageCurrent: 1,
    pageSize: 12,
    courseName: '',
    categoryId: undefined,
    isFree: undefined,
    orderBy: 'createTime'
  }
  loadCourseList()
}

const handlePageChange = (page: number) => {
  searchParams.value.pageCurrent = page
  loadCourseList()
  // 滚动到顶部
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

const handlePageSizeChange = (pageSize: number) => {
  searchParams.value.pageSize = pageSize
  searchParams.value.pageCurrent = 1
  loadCourseList()
}

const gotoCourseDetail = (course: Course) => {
  navigateTo(`/course/${course.id}`)
}

const handleCollect = (courseId: number, isCollected: boolean) => {
  ElMessage.success(isCollected ? '收藏成功' : '取消收藏成功')
}

const handleShare = (course: Course) => {
  const shareUrl = `${window.location.origin}/course/${course.id}`
  navigator.clipboard.writeText(shareUrl)
  ElMessage.success('课程链接已复制到剪贴板')
}

const handleBuy = (course: Course) => {
  // 跳转到课程详情页的购买区域
  navigateTo(`/course/${course.id}?action=buy`)
}

const handleLearn = (course: Course) => {
  navigateTo(`/learn/${course.id}`)
}

// 生命周期
onMounted(async () => {
  // 从URL查询参数初始化搜索条件
  const route = useRoute()
  if (route.query.search) {
    searchParams.value.courseName = route.query.search as string
  }
  if (route.query.category) {
    searchParams.value.categoryId = Number(route.query.category)
  }

  // 加载数据
  await Promise.all([
    loadCategories(),
    loadCourseList()
  ])
})

// 监听路由查询参数变化
watch(() => useRoute().query, (newQuery) => {
  if (newQuery.search !== searchParams.value.courseName) {
    searchParams.value.courseName = (newQuery.search as string) || ''
    handleSearch()
  }
}, { deep: true })
</script>

<style lang="scss" scoped>
.course-center {
  padding: $spacing-lg 0;
  min-height: 100vh;

  .page-header {
    text-align: center;
    margin-bottom: $spacing-xl;

    .header-content {
      margin-bottom: $spacing-lg;

      .page-title {
        font-size: 2.5rem;
        font-weight: 700;
        color: $text-primary;
        margin-bottom: $spacing-sm;
      }

      .page-subtitle {
        font-size: 1.1rem;
        color: $text-secondary;
      }
    }

    .search-section {
      max-width: 600px;
      margin: 0 auto;

      .search-input {
        :deep(.el-input-group__prepend) {
          background: white;
        }
      }
    }
  }

  .filter-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: white;
    padding: $spacing-md;
    border-radius: $border-radius-base;
    box-shadow: $box-shadow-base;
    margin-bottom: $spacing-lg;

    .filter-left {
      display: flex;
      align-items: center;
      gap: $spacing-xl;

      .filter-group {
        display: flex;
        align-items: center;
        gap: $spacing-sm;

        .filter-label {
          color: $text-regular;
          font-weight: 500;
          white-space: nowrap;
        }
      }
    }

    .filter-right {
      display: flex;
      align-items: center;
      gap: $spacing-md;

      .result-count {
        color: $text-secondary;
        font-size: $font-size-sm;
      }
    }
  }

  .course-content {
    display: flex;
    gap: $spacing-xl;

    .sidebar {
      width: 250px;
      flex-shrink: 0;

      .category-sidebar,
      .tags-sidebar {
        background: white;
        border-radius: $border-radius-base;
        box-shadow: $box-shadow-base;
        padding: $spacing-lg;
        margin-bottom: $spacing-lg;

        .sidebar-title {
          font-size: $font-size-lg;
          font-weight: 600;
          color: $text-primary;
          margin-bottom: $spacing-md;
          padding-bottom: $spacing-sm;
          border-bottom: 2px solid $primary-color;
        }
      }

      .category-tree {
        .category-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: $spacing-sm;
          cursor: pointer;
          border-radius: $border-radius-base;
          transition: all 0.3s ease;

          &:hover {
            background: $bg-color;
          }

          &.is-active {
            background: $primary-color;
            color: white;
          }

          .category-name {
            font-weight: 500;
          }

          .course-count {
            font-size: $font-size-xs;
            color: $text-placeholder;
          }

          &.is-active .course-count {
            color: rgba(255, 255, 255, 0.8);
          }
        }
      }

      .tags-list {
        display: flex;
        flex-wrap: wrap;
        gap: $spacing-sm;

        .tag-item {
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            background: $primary-color;
            color: white;
          }
        }
      }
    }

    .main-content {
      flex: 1;

      .loading-container {
        background: white;
        border-radius: $border-radius-base;
        padding: $spacing-lg;
      }

      .course-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        gap: $spacing-lg;
        margin-bottom: $spacing-xl;
      }

      .course-list-view {
        display: flex;
        flex-direction: column;
        gap: $spacing-md;
        margin-bottom: $spacing-xl;
      }

      .empty-state {
        background: white;
        border-radius: $border-radius-base;
        padding: $spacing-xl;
        text-align: center;
      }

      .pagination-wrapper {
        display: flex;
        justify-content: center;
        padding: $spacing-lg 0;
      }
    }
  }
}

// 响应式设计
@media (max-width: $breakpoint-lg) {
  .course-center {
    .course-content {
      flex-direction: column;

      .sidebar {
        width: 100%;
        display: flex;
        gap: $spacing-md;
        overflow-x: auto;

        .category-sidebar,
        .tags-sidebar {
          min-width: 250px;
          margin-bottom: 0;
        }
      }
    }
  }
}

@media (max-width: $breakpoint-md) {
  .course-center {
    .filter-toolbar {
      flex-direction: column;
      gap: $spacing-md;
      align-items: stretch;

      .filter-left {
        flex-direction: column;
        gap: $spacing-md;
      }

      .filter-right {
        justify-content: space-between;
      }
    }

    .course-content {
      .main-content {
        .course-grid {
          grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        }
      }
    }
  }
}

@media (max-width: $breakpoint-sm) {
  .course-center {
    padding: $spacing-md 0;

    .page-header {
      .header-content {
        .page-title {
          font-size: 2rem;
        }
      }

      .search-section {
        .search-input {
          :deep(.el-input-group__prepend) {
            display: none;
          }
        }
      }
    }

    .course-content {
      .sidebar {
        flex-direction: column;

        .category-sidebar,
        .tags-sidebar {
          min-width: auto;
        }
      }

      .main-content {
        .course-grid {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}
</style>