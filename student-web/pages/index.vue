<template>
  <div class="home-page">
    <div class="container">
      <!-- Hero区域 -->
      <section class="hero-section">
        <div class="hero-content">
          <h1 class="hero-title">领课教育</h1>
          <p class="hero-subtitle">专业的在线学习平台，助您成就梦想</p>
          <div class="hero-actions">
            <el-button type="primary" size="large" @click="goCourseCenter">
              开始学习
            </el-button>
            <el-button size="large" @click="showMore">
              了解更多
            </el-button>
          </div>
        </div>
      </section>

      <!-- 轮播图区域 -->
      <section class="carousel-section">
        <el-carousel height="300px" indicator-position="outside">
          <el-carousel-item v-for="item in carouselList" :key="item.id">
            <div class="carousel-item" :style="{ backgroundImage: `url(${item.imageUrl})` }">
              <div class="carousel-content">
                <h3>{{ item.title }}</h3>
                <p>{{ item.description }}</p>
              </div>
            </div>
          </el-carousel-item>
        </el-carousel>
      </section>

      <!-- 课程推荐区域 -->
      <section class="recommend-section">
        <div class="section-header">
          <h2>推荐课程</h2>
          <p>精选优质课程，助您快速提升</p>
        </div>
        <div class="course-grid">
          <CourseCard 
            v-for="course in recommendCourses" 
            :key="course.id"
            :course="course"
            @click="gotoCourse(course.id)"
          />
        </div>
      </section>

      <!-- 分类导航区域 -->
      <section class="category-section">
        <div class="section-header">
          <h2>课程分类</h2>
          <p>多领域课程，满足不同学习需求</p>
        </div>
        <div class="category-grid">
          <div 
            v-for="category in categories" 
            :key="category.id"
            class="category-item"
            @click="gotoCategory(category.id)"
          >
            <div class="category-icon">
              <i :class="category.icon"></i>
            </div>
            <h3>{{ category.name }}</h3>
            <p>{{ category.description }}</p>
            <span class="course-count">{{ category.courseCount }}门课程</span>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
// 页面元数据
definePageMeta({
  title: '领课教育 - 首页',
  description: '专业的在线学习平台'
})

// 响应式数据
const carouselList = ref([
  {
    id: 1,
    title: '热门课程推荐',
    description: '精选优质课程，助您快速提升技能',
    imageUrl: '/images/carousel-1.jpg'
  },
  {
    id: 2,
    title: '新手入门指南',
    description: '从零基础开始，系统化学习路径',
    imageUrl: '/images/carousel-2.jpg'
  },
  {
    id: 3,
    title: '实战项目训练',
    description: '真实项目经验，提升实战能力',
    imageUrl: '/images/carousel-3.jpg'
  }
])

const recommendCourses = ref([
  // 推荐课程数据将通过API获取
])

const categories = ref([
  {
    id: 1,
    name: 'Web前端',
    description: 'HTML、CSS、JavaScript等前端技术',
    icon: 'el-icon-monitor',
    courseCount: 156
  },
  {
    id: 2,
    name: 'Java开发',
    description: 'Spring、SpringBoot等Java技术栈',
    icon: 'el-icon-cpu',
    courseCount: 203
  },
  {
    id: 3,
    name: 'Python',
    description: 'Python编程及人工智能应用',
    icon: 'el-icon-data-analysis',
    courseCount: 128
  },
  {
    id: 4,
    name: '移动开发',
    description: 'Android、iOS、Flutter等移动端开发',
    icon: 'el-icon-mobile-phone',
    courseCount: 89
  }
])

// 方法
const goCourseCenter = () => {
  navigateTo('/courses')
}

const showMore = () => {
  // 滚动到下方内容
  document.querySelector('.recommend-section')?.scrollIntoView({ behavior: 'smooth' })
}

const gotoCourse = (courseId: number) => {
  navigateTo(`/course/${courseId}`)
}

const gotoCategory = (categoryId: number) => {
  navigateTo(`/courses?category=${categoryId}`)
}

// 生命周期
onMounted(async () => {
  // 加载推荐课程数据
  // await loadRecommendCourses()
})
</script>

<style lang="scss" scoped>
.home-page {
  .hero-section {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 80px 0;
    text-align: center;
    border-radius: $border-radius-large;
    margin-bottom: $spacing-xl;

    .hero-title {
      font-size: 3rem;
      font-weight: 700;
      margin-bottom: $spacing-md;
    }

    .hero-subtitle {
      font-size: 1.2rem;
      margin-bottom: $spacing-xl;
      opacity: 0.9;
    }

    .hero-actions {
      display: flex;
      justify-content: center;
      gap: $spacing-md;
    }
  }

  .carousel-section {
    margin-bottom: $spacing-xl;

    .carousel-item {
      height: 300px;
      background-size: cover;
      background-position: center;
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;

      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background: rgba(0, 0, 0, 0.4);
      }

      .carousel-content {
        position: relative;
        z-index: 1;
        color: white;
        text-align: center;

        h3 {
          font-size: 2rem;
          margin-bottom: $spacing-sm;
        }

        p {
          font-size: 1.1rem;
          opacity: 0.9;
        }
      }
    }
  }

  .section-header {
    text-align: center;
    margin-bottom: $spacing-xl;

    h2 {
      font-size: 2rem;
      color: $text-primary;
      margin-bottom: $spacing-sm;
    }

    p {
      color: $text-secondary;
      font-size: 1rem;
    }
  }

  .course-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: $spacing-lg;
    margin-bottom: $spacing-xl;
  }

  .category-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: $spacing-lg;

    .category-item {
      background: white;
      padding: $spacing-xl;
      border-radius: $border-radius-large;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s ease;
      box-shadow: $box-shadow-base;

      &:hover {
        transform: translateY(-4px);
        box-shadow: $box-shadow-light;
      }

      .category-icon {
        width: 60px;
        height: 60px;
        background: $primary-color;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto $spacing-md;

        i {
          font-size: 24px;
          color: white;
        }
      }

      h3 {
        font-size: 1.2rem;
        color: $text-primary;
        margin-bottom: $spacing-sm;
      }

      p {
        color: $text-secondary;
        margin-bottom: $spacing-md;
        line-height: 1.5;
      }

      .course-count {
        color: $primary-color;
        font-weight: 500;
      }
    }
  }
}

// 移动端响应式
@media (max-width: $breakpoint-sm) {
  .home-page {
    .hero-section {
      padding: 60px 0;

      .hero-title {
        font-size: 2rem;
      }

      .hero-actions {
        flex-direction: column;
        align-items: center;
        gap: $spacing-sm;
      }
    }

    .course-grid {
      grid-template-columns: 1fr;
    }

    .category-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>