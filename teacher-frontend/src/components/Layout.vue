<template>
  <div class="layout">
    <!-- 侧边栏 -->
    <el-aside class="sidebar" :width="collapsed ? '64px' : '220px'">
      <div class="logo">
        <h2 v-if="!collapsed">讲师端管理</h2>
        <el-icon v-else size="24"><Reading /></el-icon>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="collapsed"
        :unique-opened="true"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><House /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        
        <el-menu-item index="/courses">
          <el-icon><Reading /></el-icon>
          <template #title>课程管理</template>
        </el-menu-item>
        
        <el-menu-item index="/students">
          <el-icon><User /></el-icon>
          <template #title>学生管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-button
            type="text"
            size="large"
            @click="toggleCollapse"
            class="collapse-btn"
          >
            <el-icon size="18">
              <Expand v-if="collapsed" />
              <Fold v-else />
            </el-icon>
          </el-button>
          
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="$route.meta.title">
              {{ $route.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar size="small" src="/avatar.jpg">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="username">讲师用户</span>
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人资料</el-dropdown-item>
                <el-dropdown-item command="settings">设置</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主要内容 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'Layout',
  setup() {
    const route = useRoute()
    const router = useRouter()
    const collapsed = ref(false)

    const activeMenu = computed(() => route.path)

    const toggleCollapse = () => {
      collapsed.value = !collapsed.value
    }

    const handleCommand = (command) => {
      switch (command) {
        case 'profile':
          ElMessage.info('个人资料')
          break
        case 'settings':
          ElMessage.info('设置')
          break
        case 'logout':
          ElMessage.success('退出登录成功')
          router.push('/login')
          break
      }
    }

    return {
      collapsed,
      activeMenu,
      toggleCollapse,
      handleCommand
    }
  }
}
</script>

<style lang="scss" scoped>
.layout {
  height: 100vh;
  display: flex;
}

.sidebar {
  background-color: #001529;
  transition: width 0.3s;
  
  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #fff;
    font-size: 18px;
    font-weight: bold;
    border-bottom: 1px solid #1f4662;
    
    h2 {
      margin: 0;
      font-size: 16px;
    }
  }
  
  .sidebar-menu {
    border: none;
    background-color: #001529;
    
    :deep(.el-menu-item) {
      color: rgba(255, 255, 255, 0.8);
      
      &:hover {
        background-color: #1890ff;
        color: #fff;
      }
      
      &.is-active {
        background-color: #1890ff;
        color: #fff;
      }
    }
    
    :deep(.el-menu-item-group__title) {
      color: rgba(255, 255, 255, 0.6);
    }
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.header {
  background: #fff;
  border-bottom: 1px solid #e8eaec;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  
  .header-left {
    display: flex;
    align-items: center;
    
    .collapse-btn {
      margin-right: 16px;
    }
  }
  
  .header-right {
    .user-info {
      display: flex;
      align-items: center;
      cursor: pointer;
      
      .username {
        margin: 0 8px;
        color: #666;
      }
    }
  }
}

.main-content {
  flex: 1;
  padding: 20px;
  background-color: #f5f7fa;
  overflow: auto;
}
</style>