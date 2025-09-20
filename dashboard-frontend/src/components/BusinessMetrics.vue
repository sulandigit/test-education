<template>
  <div class="dashboard-card">
    <div class="card-title">
      <i class="el-icon-data-line"></i>
      核心业务指标
    </div>
    <div class="card-content">
      <div class="metrics-grid">
        <div class="metric-item">
          <div class="metric-icon">💰</div>
          <div class="metric-info">
            <div class="metric-value">¥{{ formatMoney(data.dailyRevenue) }}</div>
            <div class="metric-label">今日收入</div>
          </div>
        </div>
        
        <div class="metric-item">
          <div class="metric-icon">📊</div>
          <div class="metric-info">
            <div class="metric-value">¥{{ formatMoney(data.monthlyRevenue) }}</div>
            <div class="metric-label">本月收入</div>
          </div>
        </div>
        
        <div class="metric-item">
          <div class="metric-icon">📋</div>
          <div class="metric-info">
            <div class="metric-value">{{ data.dailyOrders || 0 }}</div>
            <div class="metric-label">今日订单</div>
          </div>
        </div>
        
        <div class="metric-item">
          <div class="metric-icon">🎥</div>
          <div class="metric-info">
            <div class="metric-value">{{ formatNumber(data.dailyVideoViews) }}</div>
            <div class="metric-label">视频播放</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BusinessMetrics',
  props: {
    data: {
      type: Object,
      default: () => ({})
    }
  },
  setup() {
    // 格式化金额
    const formatMoney = (amount) => {
      if (!amount) return '0.00'
      return parseFloat(amount).toLocaleString('zh-CN', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
      })
    }

    // 格式化数字
    const formatNumber = (num) => {
      if (!num) return '0'
      if (num >= 10000) {
        return (num / 10000).toFixed(1) + 'W'
      }
      return num.toLocaleString()
    }

    return {
      formatMoney,
      formatNumber
    }
  }
}
</script>

<style scoped>
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  height: 100%;
}

.metric-item {
  display: flex;
  align-items: center;
  padding: 15px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.metric-item:hover {
  background: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 212, 255, 0.2);
}

.metric-icon {
  font-size: 32px;
  margin-right: 15px;
  min-width: 40px;
}

.metric-info {
  flex: 1;
}

.metric-value {
  font-size: 24px;
  font-weight: bold;
  color: #00d4ff;
  margin-bottom: 5px;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

.metric-label {
  font-size: 14px;
  opacity: 0.8;
  color: #ffffff;
}

@media (max-width: 1200px) {
  .metrics-grid {
    grid-template-columns: 1fr;
    gap: 10px;
  }
  
  .metric-value {
    font-size: 20px;
  }
  
  .metric-icon {
    font-size: 24px;
  }
}
</style>