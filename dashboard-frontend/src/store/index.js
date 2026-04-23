import { createStore } from 'vuex'

export default createStore({
  state: {
    // 实时数据
    realTimeData: {
      timestamp: null,
      onlineUsers: 0,
      dailyNewUsers: 0,
      totalUsers: 0,
      dailyOrders: 0,
      dailyRevenue: 0,
      monthlyRevenue: 0,
      dailyVideoViews: 0,
      hotCourses: [],
      paymentTypes: [],
      userLocations: [],
      systemStatus: {}
    },
    // WebSocket连接状态
    wsConnected: false,
    // 最后更新时间
    lastUpdateTime: null
  },
  mutations: {
    // 更新实时数据
    UPDATE_REALTIME_DATA(state, data) {
      state.realTimeData = { ...state.realTimeData, ...data }
      state.lastUpdateTime = new Date()
    },
    // 更新WebSocket连接状态
    SET_WS_CONNECTED(state, connected) {
      state.wsConnected = connected
    },
    // 更新指标数据
    UPDATE_METRIC(state, { metricType, value }) {
      if (state.realTimeData.hasOwnProperty(metricType)) {
        state.realTimeData[metricType] = value
      }
    }
  },
  actions: {
    // 初始化实时数据
    async initRealTimeData({ commit }) {
      try {
        // 这里可以调用API获取初始数据
        console.log('初始化实时数据')
      } catch (error) {
        console.error('初始化实时数据失败:', error)
      }
    },
    // 更新实时数据
    updateRealTimeData({ commit }, data) {
      commit('UPDATE_REALTIME_DATA', data)
    },
    // 设置WebSocket连接状态
    setWsConnected({ commit }, connected) {
      commit('SET_WS_CONNECTED', connected)
    },
    // 更新指标
    updateMetric({ commit }, payload) {
      commit('UPDATE_METRIC', payload)
    }
  },
  getters: {
    // 获取实时数据
    getRealTimeData: state => state.realTimeData,
    // 获取WebSocket连接状态
    isWsConnected: state => state.wsConnected,
    // 获取最后更新时间
    getLastUpdateTime: state => state.lastUpdateTime
  }
})