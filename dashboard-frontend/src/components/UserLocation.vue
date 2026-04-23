<template>
  <div class="dashboard-card">
    <div class="card-title">
      <i class="el-icon-location"></i>
      用户地域分布
    </div>
    <div class="card-content">
      <div class="chart-container" ref="chartRef"></div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

export default {
  name: 'UserLocation',
  props: {
    data: {
      type: Array,
      default: () => []
    }
  },
  setup(props) {
    const chartRef = ref(null)
    let chart = null

    // 初始化图表
    const initChart = () => {
      if (!chartRef.value) return
      
      chart = echarts.init(chartRef.value)
      updateChart()
    }

    // 更新图表数据
    const updateChart = () => {
      if (!chart) return

      const chartData = props.data.map(item => ({
        name: item.locationName,
        value: item.userCount
      }))

      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)',
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: '#00d4ff',
          textStyle: {
            color: '#ffffff'
          }
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          top: 'center',
          textStyle: {
            color: '#ffffff',
            fontSize: 12
          },
          itemWidth: 10,
          itemHeight: 10
        },
        series: [
          {
            name: '用户分布',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['60%', '50%'],
            avoidLabelOverlap: false,
            itemStyle: {
              borderRadius: 8,
              borderColor: '#ffffff',
              borderWidth: 2
            },
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '16',
                fontWeight: 'bold',
                color: '#ffffff'
              },
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 212, 255, 0.5)'
              }
            },
            labelLine: {
              show: false
            },
            data: chartData,
            color: [
              '#00d4ff',
              '#5b63d3',
              '#ff6b9d',
              '#ffa726',
              '#66bb6a',
              '#ab47bc',
              '#26c6da',
              '#ef5350'
            ]
          }
        ]
      }

      chart.setOption(option)
    }

    // 监听数据变化
    watch(() => props.data, () => {
      updateChart()
    }, { deep: true })

    // 窗口大小变化时重新渲染
    const handleResize = () => {
      if (chart) {
        chart.resize()
      }
    }

    onMounted(() => {
      nextTick(() => {
        initChart()
        window.addEventListener('resize', handleResize)
      })
    })

    onUnmounted(() => {
      if (chart) {
        chart.dispose()
      }
      window.removeEventListener('resize', handleResize)
    })

    return {
      chartRef
    }
  }
}
</script>

<style scoped>
.chart-container {
  width: 100%;
  height: 250px;
}
</style>