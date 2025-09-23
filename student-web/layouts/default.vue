<template>
  <div class="layout-default">
    <!-- 导航栏 -->
    <Header />
    
    <!-- 主要内容区域 -->
    <main class="main-content">
      <slot />
    </main>
    
    <!-- 底部 -->
    <Footer />
  </div>
</template>

<script setup lang="ts">
// 导入组件将在后续创建
</script>

<style lang="scss" scoped>
.layout-default {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding-top: 20px;
  padding-bottom: 40px;
}
</style>