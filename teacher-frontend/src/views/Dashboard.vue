<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <!-- 统计卡片 -->
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon courses">
              <el-icon size="32"><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ dashboardData.totalCourses }}</div>
              <div class="stat-label">总课程数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon students">
              <el-icon size="32"><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ dashboardData.totalStudents }}</div>
              <div class="stat-label">总学生数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon active">
              <el-icon size="32"><VideoPlay /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ dashboardData.activeCourses }}</div>
              <div class="stat-label">进行中课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-icon completed">
              <el-icon size="32"><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ dashboardData.completedCourses }}</div>
              <div class="stat-label">已完成课程</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 最近课程 -->
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近课程</span>
              <el-button type="text" @click="$router.push('/courses')">查看全部</el-button>
            </div>
          </template>
          <div class="recent-list">
            <div
              v-for="course in dashboardData.recentCourses"
              :key="course.id"
              class="recent-item"
            >
              <div class="item-icon">
                <el-icon><Reading /></el-icon>
              </div>
              <div class="item-info">
                <div class="item-title">{{ course.name }}</div>
                <div class="item-desc">{{ course.description }}</div>
              </div>
              <div class="item-meta">
                <el-tag :type="course.status === 'active' ? 'success' : 'info'" size="small">
                  {{ course.status === 'active' ? '进行中' : '已完成' }}
                </el-tag>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 最近学生 -->
      <el-col :xs="24" :lg="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近学生</span>
              <el-button type="text" @click="$router.push('/students')">查看全部</el-button>
            </div>
          </template>
          <div class="recent-list">
            <div
              v-for="student in dashboardData.recentStudents"
              :key="student.id"
              class="recent-item"
            >
              <div class="item-icon">
                <el-avatar size="small" :src="student.avatar">
                  <el-icon><User /></el-icon>
                </el-avatar>
              </div>
              <div class="item-info">
                <div class="item-title">{{ student.name }}</div>
                <div class="item-desc">{{ student.email }}</div>
              </div>
              <div class="item-meta">
                <span class="join-date">{{ student.joinDate }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'Dashboard',
  setup() {
    const dashboardData = ref({
      totalCourses: 0,
      totalStudents: 0,
      activeCourses: 0,
      completedCourses: 0,
      recentCourses: [],
      recentStudents: []
    })

    const loadDashboardData = () => {
      // 模拟数据加载
      dashboardData.value = {
        totalCourses: 12,
        totalStudents: 156,
        activeCourses: 8,
        completedCourses: 4,
        recentCourses: [
          {
            id: 1,
            name: 'Vue.js 3.0 开发实战',
            description: '从零开始学习Vue.js 3.0的现代前端开发',
            status: 'active'
          },
          {
            id: 2,
            name: 'JavaScript 高级程序设计',
            description: '深入理解JavaScript核心概念和高级特性',
            status: 'active'
          },
          {
            id: 3,
            name: 'React Hooks 实战',
            description: '掌握React Hooks的使用技巧',
            status: 'completed'
          }
        ],
        recentStudents: [
          {
            id: 1,
            name: '张三',
            email: 'zhangsan@example.com',
            avatar: '',
            joinDate: '2024-01-15'
          },
          {
            id: 2,
            name: '李四',
            email: 'lisi@example.com',
            avatar: '',
            joinDate: '2024-01-18'
          },
          {
            id: 3,
            name: '王五',
            email: 'wangwu@example.com',
            avatar: '',
            joinDate: '2024-01-20'
          }
        ]
      }
    }

    onMounted(() => {
      loadDashboardData()
    })

    return {
      dashboardData
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  .stat-card {
    :deep(.el-card__body) {
      padding: 20px;
    }
  }

  .stat-item {
    display: flex;
    align-items: center;

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 16px;
      color: #fff;

      &.courses {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      }

      &.students {
        background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
      }

      &.active {
        background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      }

      &.completed {
        background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
      }
    }

    .stat-info {
      flex: 1;

      .stat-number {
        font-size: 28px;
        font-weight: bold;
        color: #2c3e50;
        line-height: 1;
      }

      .stat-label {
        font-size: 14px;
        color: #8492a6;
        margin-top: 4px;
      }
    }
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .recent-list {
    .recent-item {
      display: flex;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid #f0f0f0;

      &:last-child {
        border-bottom: none;
      }

      .item-icon {
        margin-right: 12px;
        
        .el-icon {
          font-size: 20px;
          color: #409eff;
        }
      }

      .item-info {
        flex: 1;

        .item-title {
          font-size: 14px;
          font-weight: 500;
          color: #2c3e50;
          line-height: 1.5;
        }

        .item-desc {
          font-size: 12px;
          color: #8492a6;
          line-height: 1.4;
          margin-top: 2px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }

      .item-meta {
        .join-date {
          font-size: 12px;
          color: #8492a6;
        }
      }
    }
  }
}
</style>