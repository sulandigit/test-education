<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <!-- Logo和品牌 -->
        <div class="header-brand">
          <NuxtLink to="/" class="brand-link">
            <img 
              src="/images/logo.png" 
              alt="领课教育" 
              class="brand-logo"
              @error="handleLogoError"
            >
            <span class="brand-text">领课教育</span>
          </NuxtLink>
        </div>

        <!-- 主导航菜单 -->
        <nav class="header-nav hidden-mobile">
          <el-menu
            mode="horizontal"
            :default-active="activeMenu"
            class="nav-menu"
            @select="handleMenuSelect"
          >
            <el-menu-item index="/">首页</el-menu-item>
            <el-menu-item index="/courses">课程中心</el-menu-item>
            <el-sub-menu index="categories">
              <template #title>课程分类</template>
              <el-menu-item
                v-for="category in categories"
                :key="category.id"
                :index="`/courses?category=${category.id}`"
              >
                {{ category.categoryName }}
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item index="/about">关于我们</el-menu-item>
          </el-menu>
        </nav>

        <!-- 搜索框 -->
        <div class="header-search hidden-mobile">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课程..."
            class="search-input"
            @keyup.enter="handleSearch"
          >
            <template #suffix>
              <el-icon class="search-icon" @click="handleSearch">
                <Search />
              </el-icon>
            </template>
          </el-input>
        </div>

        <!-- 用户区域 -->
        <div class="header-user">
          <!-- 未登录状态 -->
          <div v-if="!isLoggedIn" class="user-actions">
            <el-button @click="goToLogin">登录</el-button>
            <el-button type="primary" @click="goToRegister">注册</el-button>
          </div>

          <!-- 已登录状态 -->
          <div v-else class="user-info">
            <!-- 消息通知 -->
            <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="message-badge">
              <el-icon class="message-icon" @click="goToMessages">
                <Bell />
              </el-icon>
            </el-badge>

            <!-- 用户下拉菜单 -->
            <el-dropdown @command="handleUserMenuCommand" trigger="click">
              <div class="user-avatar-wrapper">
                <el-avatar
                  :src="userInfo?.avatar"
                  :size="36"
                  class="user-avatar"
                >
                  <template #error>
                    <el-icon><UserFilled /></el-icon>
                  </template>
                </el-avatar>
                <span class="user-name">{{ userInfo?.nickname || userInfo?.username }}</span>
                <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon>
                    个人中心
                  </el-dropdown-item>
                  <el-dropdown-item command="courses">
                    <el-icon><Reading /></el-icon>
                    我的课程
                  </el-dropdown-item>
                  <el-dropdown-item command="orders">
                    <el-icon><ShoppingCart /></el-icon>
                    我的订单
                  </el-dropdown-item>
                  <el-dropdown-item command="settings">
                    <el-icon><Setting /></el-icon>
                    设置
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>

        <!-- 移动端菜单按钮 -->
        <div class="mobile-menu-btn hidden-desktop">
          <el-icon @click="toggleMobileMenu">
            <Menu />
          </el-icon>
        </div>
      </div>
    </div>

    <!-- 移动端导航菜单 -->
    <div v-if="showMobileMenu" class="mobile-menu">
      <div class="mobile-menu-overlay" @click="closeMobileMenu"></div>
      <div class="mobile-menu-content">
        <div class="mobile-menu-header">
          <span class="menu-title">导航菜单</span>
          <el-icon class="close-btn" @click="closeMobileMenu">
            <Close />
          </el-icon>
        </div>
        <div class="mobile-menu-body">
          <div class="mobile-search">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索课程..."
              @keyup.enter="handleMobileSearch"
            >
              <template #suffix>
                <el-icon @click="handleMobileSearch">
                  <Search />
                </el-icon>
              </template>
            </el-input>
          </div>
          <div class="mobile-nav-items">
            <NuxtLink to="/" class="nav-item" @click="closeMobileMenu">首页</NuxtLink>
            <NuxtLink to="/courses" class="nav-item" @click="closeMobileMenu">课程中心</NuxtLink>
            <div class="nav-item-group">
              <div class="nav-item-title">课程分类</div>
              <NuxtLink
                v-for="category in categories"
                :key="category.id"
                :to="`/courses?category=${category.id}`"
                class="nav-sub-item"
                @click="closeMobileMenu"
              >
                {{ category.categoryName }}
              </NuxtLink>
            </div>
            <NuxtLink to="/about" class="nav-item" @click="closeMobileMenu">关于我们</NuxtLink>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import {
  Search,
  Bell,
  User,
  UserFilled,
  Reading,
  ShoppingCart,
  Setting,
  SwitchButton,
  ArrowDown,
  Menu,
  Close
} from '@element-plus/icons-vue'

// 导入stores（将在后续创建）
// const userStore = useUserStore()
// const { isLoggedIn, userInfo } = storeToRefs(userStore)

// 临时数据，实际应从store获取
const isLoggedIn = ref(false)
const userInfo = ref(null)

// 响应式数据
const searchKeyword = ref('')
const showMobileMenu = ref(false)
const unreadCount = ref(0)
const categories = ref([
  { id: 1, categoryName: 'Web前端' },
  { id: 2, categoryName: 'Java开发' },
  { id: 3, categoryName: 'Python' },
  { id: 4, categoryName: '移动开发' }
])

// 计算当前激活的菜单
const activeMenu = computed(() => {
  const route = useRoute()
  return route.path
})

// 方法
const handleLogoError = (e: Event) => {
  // Logo加载失败时的处理
  const target = e.target as HTMLImageElement
  target.style.display = 'none'
}

const handleMenuSelect = (index: string) => {
  if (index !== useRoute().path) {
    navigateTo(index)
  }
}

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    navigateTo(`/courses?search=${encodeURIComponent(searchKeyword.value)}`)
  }
}

const handleMobileSearch = () => {
  handleSearch()
  closeMobileMenu()
}

const goToLogin = () => {
  navigateTo('/login')
}

const goToRegister = () => {
  navigateTo('/register')
}

const goToMessages = () => {
  navigateTo('/user/messages')
}

const handleUserMenuCommand = (command: string) => {
  switch (command) {
    case 'profile':
      navigateTo('/user')
      break
    case 'courses':
      navigateTo('/user/courses')
      break
    case 'orders':
      navigateTo('/user/orders')
      break
    case 'settings':
      navigateTo('/user/profile')
      break
    case 'logout':
      handleLogout()
      break
  }
}

const handleLogout = async () => {
  try {
    // await userStore.logout()
    ElMessage.success('退出登录成功')
    await navigateTo('/')
  } catch (error) {
    ElMessage.error('退出登录失败')
  }
}

const toggleMobileMenu = () => {
  showMobileMenu.value = !showMobileMenu.value
}

const closeMobileMenu = () => {
  showMobileMenu.value = false
}

// 生命周期
onMounted(async () => {
  // 加载课程分类
  // await loadCategories()
  // 加载未读消息数量
  // await loadUnreadCount()
})
</script>

<style lang="scss" scoped>
.app-header {
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;

  .header-content {
    display: flex;
    align-items: center;
    height: 64px;
    gap: $spacing-lg;
  }

  .header-brand {
    flex-shrink: 0;

    .brand-link {
      display: flex;
      align-items: center;
      gap: $spacing-sm;
      color: $text-primary;
      font-weight: 600;
      font-size: 1.2rem;

      .brand-logo {
        height: 36px;
        width: auto;
      }

      .brand-text {
        color: $primary-color;
      }
    }
  }

  .header-nav {
    flex: 1;

    .nav-menu {
      border: none;
      background: transparent;

      :deep(.el-menu-item) {
        &:hover,
        &.is-active {
          color: $primary-color;
          background: transparent;
          border-bottom-color: $primary-color;
        }
      }

      :deep(.el-sub-menu__title) {
        &:hover {
          color: $primary-color;
          background: transparent;
        }
      }
    }
  }

  .header-search {
    width: 300px;
    flex-shrink: 0;

    .search-input {
      :deep(.el-input__wrapper) {
        border-radius: 20px;
      }
    }

    .search-icon {
      cursor: pointer;
      color: $text-secondary;

      &:hover {
        color: $primary-color;
      }
    }
  }

  .header-user {
    flex-shrink: 0;

    .user-actions {
      display: flex;
      gap: $spacing-sm;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: $spacing-md;

      .message-badge {
        .message-icon {
          font-size: 20px;
          color: $text-secondary;
          cursor: pointer;

          &:hover {
            color: $primary-color;
          }
        }
      }

      .user-avatar-wrapper {
        display: flex;
        align-items: center;
        gap: $spacing-sm;
        cursor: pointer;
        padding: $spacing-xs $spacing-sm;
        border-radius: $border-radius-base;
        transition: background-color 0.3s;

        &:hover {
          background-color: $bg-color;
        }

        .user-name {
          font-weight: 500;
          color: $text-primary;
          max-width: 100px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .dropdown-arrow {
          font-size: 12px;
          color: $text-secondary;
        }
      }
    }
  }

  .mobile-menu-btn {
    font-size: 24px;
    cursor: pointer;
    color: $text-primary;
  }
}

// 移动端菜单
.mobile-menu {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2000;

  .mobile-menu-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
  }

  .mobile-menu-content {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    width: 300px;
    background: white;
    display: flex;
    flex-direction: column;

    .mobile-menu-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: $spacing-md;
      border-bottom: 1px solid $border-base;

      .menu-title {
        font-weight: 600;
        color: $text-primary;
      }

      .close-btn {
        font-size: 20px;
        cursor: pointer;
        color: $text-secondary;
      }
    }

    .mobile-menu-body {
      flex: 1;
      padding: $spacing-md;

      .mobile-search {
        margin-bottom: $spacing-lg;
      }

      .mobile-nav-items {
        .nav-item {
          display: block;
          padding: $spacing-md 0;
          color: $text-primary;
          border-bottom: 1px solid $border-extra-light;
          font-weight: 500;

          &:hover {
            color: $primary-color;
          }
        }

        .nav-item-group {
          .nav-item-title {
            padding: $spacing-md 0;
            font-weight: 600;
            color: $text-primary;
            border-bottom: 1px solid $border-extra-light;
          }

          .nav-sub-item {
            display: block;
            padding: $spacing-sm $spacing-md;
            color: $text-secondary;
            font-size: $font-size-sm;

            &:hover {
              color: $primary-color;
            }
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: $breakpoint-sm) {
  .app-header {
    .header-content {
      gap: $spacing-sm;
    }

    .header-brand {
      .brand-text {
        display: none;
      }
    }
  }
}
</style>