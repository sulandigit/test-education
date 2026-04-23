<template>
  <div class="dashboard-container">
    <!-- 连接状态指示器 -->
    <div class="connection-status">
      <span class="status-indicator" :class="wsConnected ? 'status-online' : 'status-error'"></span>
      {{ wsConnected ? '已连接' : '连接断开' }}
    </div>

    <div class="dashboard-grid">
      <!-- 头部区域 -->
      <div class="dashboard-header">
        <div class="dashboard-title">
          <img src="/logo.png" alt="Logo" style="height: 40px; margin-right: 15px;" />
          领课教育 - 用户数据展示大屏
        </div>
        <div class="dashboard-time">{{ currentTime }}</div>
      </div>

      <!-- 左侧区域 -->
      <div class="dashboard-left">
        <!-- 用户统计 -->
        <UserStats :data="realTimeData" />
        <!-- 用户地域分布 -->
        <UserLocation :data="realTimeData.userLocations" />
      </div>

      <!-- 中间区域 -->
      <div class="dashboard-center">
        <!-- 核心业务指标 -->
        <BusinessMetrics :data="realTimeData" />
        <!-- 收入趋势图表 -->
        <RevenueTrend :data="realTimeData" />
        <!-- 热门课程排行 -->
        <HotCourses :data="realTimeData.hotCourses" />
      </div>

      <!-- 右侧区域 -->
      <div class="dashboard-right">
        <!-- 系统运行状态 -->
        <SystemStatus :data="realTimeData.systemStatus" />
        <!-- 支付方式分布 -->
        <PaymentTypes :data="realTimeData.paymentTypes" />
      </div>

      <!-- 底部区域 -->
      <div class="dashboard-footer">
        <div>
          数据更新时间: {{ lastUpdateTime }} | 
          当前在线用户: {{ realTimeData.onlineUsers }} | 
          今日新增: {{ realTimeData.dailyNewUsers }} | 
          今日收入: ¥{{ formatMoney(realTimeData.dailyRevenue) }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useStore } from 'vuex'
import dayjs from 'dayjs'
import UserStats from '@/components/UserStats.vue'
import UserLocation from '@/components/UserLocation.vue'
import BusinessMetrics from '@/components/BusinessMetrics.vue'
import RevenueTrend from '@/components/RevenueTrend.vue'
import HotCourses from '@/components/HotCourses.vue'
import SystemStatus from '@/components/SystemStatus.vue'
import PaymentTypes from '@/components/PaymentTypes.vue'
import websocketService from '@/services/websocketService'

export default {
  name: 'Dashboard',
  components: {
    UserStats,
    UserLocation,
    BusinessMetrics,
    RevenueTrend,
    HotCourses,
    SystemStatus,
    PaymentTypes
  },
  setup() {
    const store = useStore()
    const currentTime = ref('')

    // 从store获取数据
    const realTimeData = computed(() => store.getters.getRealTimeData)
    const wsConnected = computed(() => store.getters.isWsConnected)
    const lastUpdateTime = computed(() => {
      const time = store.getters.getLastUpdateTime
      return time ? dayjs(time).format('YYYY-MM-DD HH:mm:ss') : '--'
    })

    // 格式化金额
    const formatMoney = (amount) => {
      if (!amount) return '0.00'
      return parseFloat(amount).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }

    // 更新当前时间
    const updateCurrentTime = () => {
      currentTime.value = dayjs().format('YYYY年MM月DD日 HH:mm:ss')
    }

    // 定时器
    let timeInterval = null

    onMounted(() => {
      // 初始化WebSocket连接
      websocketService.connect()
      
      // 初始化实时数据
      store.dispatch('initRealTimeData')
      
      // 开始时间更新定时器
      updateCurrentTime()
      timeInterval = setInterval(updateCurrentTime, 1000)
    })

    onUnmounted(() => {
      // 断开WebSocket连接
      websocketService.disconnect()
      
      // 清除定时器
      if (timeInterval) {
        clearInterval(timeInterval)
      }
    })

    return {
      realTimeData,
      wsConnected,
      lastUpdateTime,
      currentTime,
      formatMoney
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  width: 100%;
  height: 100vh;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
  position: relative;
  overflow: hidden;
}
</style>