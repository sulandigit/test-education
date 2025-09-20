<template>
  <div class="dashboard-card">
    <div class="card-title">
      <i class="el-icon-user"></i>
      用户实时统计
    </div>
    <div class="card-content">
      <div class="stats-grid">
        <div class="stat-item">
          <div class="metric-value">{{ data.onlineUsers || 0 }}</div>
          <div class="metric-label">当前在线</div>
          <div class="metric-change positive">
            <i class="el-icon-top"></i> 实时更新
          </div>
        </div>
        
        <div class="stat-item">
          <div class="metric-value">{{ data.dailyNewUsers || 0 }}</div>
          <div class="metric-label">今日新增</div>
          <div class="metric-change" :class="dailyGrowthClass">
            <i :class="dailyGrowthIcon"></i> {{ dailyGrowthText }}
          </div>
        </div>
        
        <div class="stat-item">
          <div class="metric-value">{{ formatNumber(data.totalUsers) }}</div>
          <div class="metric-label">用户总数</div>
          <div class="metric-change">
            累计注册用户
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'

export default {
  name: 'UserStats',
  props: {
    data: {
      type: Object,
      default: () => ({})
    }
  },
  setup(props) {
    // 格式化数字
    const formatNumber = (num) => {
      if (!num) return '0'
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + 'W'
      }
      return num.toLocaleString()
    }

    // 计算日增长率
    const dailyGrowthClass = computed(() => {
      const growth = props.data.dailyNewUsers || 0
      return growth > 0 ? 'positive' : 'neutral'
    })

    const dailyGrowthIcon = computed(() => {
      const growth = props.data.dailyNewUsers || 0
      return growth > 0 ? 'el-icon-top' : 'el-icon-minus'
    })

    const dailyGrowthText = computed(() => {
      const growth = props.data.dailyNewUsers || 0
      return growth > 0 ? `+${growth}` : '暂无新增'
    })

    return {
      formatNumber,
      dailyGrowthClass,
      dailyGrowthIcon,
      dailyGrowthText
    }
  }
}
</script>

<style scoped>
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  height: 100%;
}

.stat-item {
  text-align: center;
  padding: 10px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.metric-value {
  font-size: 32px;
  font-weight: bold;
  color: #00d4ff;
  margin-bottom: 8px;
  text-shadow: 0 0 15px rgba(0, 212, 255, 0.5);
}

.metric-label {
  font-size: 14px;
  opacity: 0.8;
  margin-bottom: 5px;
}

.metric-change {
  font-size: 12px;
}

.metric-change.positive {
  color: #00ff00;
}

.metric-change.neutral {
  color: #ffaa00;
}

@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }
  
  .metric-value {
    font-size: 24px;
  }
}
</style>