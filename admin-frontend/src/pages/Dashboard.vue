<template>
  <div class="dashboard">
    <div class="welcome-card">
      <el-card>
        <div class="welcome-content">
          <div class="welcome-text">
            <h2>欢迎回来，{{ authStore.userName }}！</h2>
            <p>今天是 {{ currentDate }}，祝您工作愉快！</p>
          </div>
          <div class="welcome-image">
            <el-icon size="80"><Sunny /></el-icon>
          </div>
        </div>
      </el-card>
    </div>

    <div class="stats-cards">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon user">
                <el-icon size="32"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">用户总数</div>
                <div class="stat-value">{{ stats.userCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon course">
                <el-icon size="32"><Reading /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">课程总数</div>
                <div class="stat-value">{{ stats.courseCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon order">
                <el-icon size="32"><ShoppingCart /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">订单总数</div>
                <div class="stat-value">{{ stats.orderCount }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :sm="12" :md="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-icon revenue">
                <el-icon size="32"><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-title">总收入</div>
                <div class="stat-value">¥{{ stats.revenue }}</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <div class="quick-actions">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>快捷操作</span>
          </div>
        </template>
        
        <el-row :gutter="20">
          <el-col :span="6">
            <el-button type="primary" icon="Plus" @click="quickAction('addUser')">
              新增用户
            </el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="success" icon="Plus" @click="quickAction('addCourse')">
              新增课程
            </el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="warning" icon="View" @click="quickAction('viewOrders')">
              查看订单
            </el-button>
          </el-col>
          <el-col :span="6">
            <el-button type="info" icon="Setting" @click="quickAction('systemSettings')">
              系统设置
            </el-button>
          </el-col>
        </el-row>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

// 统计数据
const stats = ref({
  userCount: 0,
  courseCount: 0,
  orderCount: 0,
  revenue: 0
})

// 当前日期
const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 快捷操作
const quickAction = (action: string) => {
  switch (action) {
    case 'addUser':
      router.push('/system/users')
      break
    case 'addCourse':
      router.push('/course/courses')
      break
    case 'viewOrders':
      router.push('/user/orders')
      break
    case 'systemSettings':
      router.push('/system/settings')
      break
  }
}

// 加载统计数据
const loadStats = async () => {
  // TODO: 调用API获取统计数据
  // 暂时使用模拟数据
  stats.value = {
    userCount: 1234,
    courseCount: 56,
    orderCount: 789,
    revenue: 123456.78
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.welcome-text h2 {
  margin: 0 0 8px 0;
  color: #303133;
  font-size: 24px;
}

.welcome-text p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.welcome-image {
  color: #409eff;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.stat-icon.user {
  background-color: #e1f3ff;
  color: #409eff;
}

.stat-icon.course {
  background-color: #f0f9ff;
  color: #67c23a;
}

.stat-icon.order {
  background-color: #fef0e6;
  color: #e6a23c;
}

.stat-icon.revenue {
  background-color: #fde2e2;
  color: #f56c6c;
}

.stat-info {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.quick-actions .card-header {
  font-weight: 600;
  color: #303133;
}

.quick-actions .el-button {
  width: 100%;
  margin-bottom: 10px;
}

@media (max-width: 768px) {
  .welcome-content {
    flex-direction: column;
    text-align: center;
  }
  
  .welcome-image {
    margin-top: 20px;
  }
  
  .quick-actions .el-col {
    margin-bottom: 10px;
  }
}
</style>