<template>
  <div id="app">
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>

<script setup lang="ts">
// 全局应用配置
useHead({
  title: '领课教育 - 在线学习平台',
  meta: [
    { name: 'description', content: '领课教育系统 - 专业的在线学习平台，提供优质的在线课程' }
  ]
})
</script>

<style lang="scss">
#app {
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

// 全局样式重置
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  line-height: 1.6;
  color: #333;
  background-color: #f8f9fa;
}

a {
  text-decoration: none;
  color: inherit;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 16px;
}
</style>