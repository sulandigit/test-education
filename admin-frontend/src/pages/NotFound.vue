<template>
  <div class="not-found">
    <div class="not-found-content">
      <div class="not-found-image">
        <el-icon size="120"><Warning /></el-icon>
      </div>
      <h1 class="not-found-title">404</h1>
      <p class="not-found-message">抱歉，您访问的页面不存在</p>
      <div class="not-found-actions">
        <el-button type="primary" @click="goHome">
          返回首页
        </el-button>
        <el-button @click="goBack">
          返回上页
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goHome = () => {
  router.push('/')
}

const goBack = () => {
  router.go(-1)
}
</script>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #f0f2f5;
}

.not-found-content {
  text-align: center;
  padding: 40px;
}

.not-found-image {
  color: #409eff;
  margin-bottom: 20px;
}

.not-found-title {
  font-size: 72px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 16px 0;
}

.not-found-message {
  font-size: 16px;
  color: #606266;
  margin: 0 0 32px 0;
}

.not-found-actions .el-button {
  margin: 0 8px;
}
</style>