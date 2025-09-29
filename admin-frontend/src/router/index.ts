import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/pages/Login.vue'),
      meta: {
        title: '登录',
        requiresAuth: false
      }
    },
    {
      path: '/',
      component: () => import('@/layouts/AdminLayout.vue'),
      redirect: '/dashboard',
      meta: {
        requiresAuth: true
      },
      children: [
        {
          path: '/dashboard',
          name: 'Dashboard',
          component: () => import('@/pages/Dashboard.vue'),
          meta: {
            title: '首页',
            requiresAuth: true
          }
        },
        // 系统管理
        {
          path: '/system',
          name: 'System',
          meta: {
            title: '系统管理',
            requiresAuth: true
          },
          children: [
            {
              path: '/system/users',
              name: 'SystemUsers',
              component: () => import('@/pages/system/Users.vue'),
              meta: {
                title: '用户管理',
                requiresAuth: true
              }
            },
            {
              path: '/system/roles',
              name: 'SystemRoles',
              component: () => import('@/pages/system/Roles.vue'),
              meta: {
                title: '角色管理',
                requiresAuth: true
              }
            },
            {
              path: '/system/menus',
              name: 'SystemMenus',
              component: () => import('@/pages/system/Menus.vue'),
              meta: {
                title: '菜单管理',
                requiresAuth: true
              }
            }
          ]
        },
        // 课程管理
        {
          path: '/course',
          name: 'Course',
          meta: {
            title: '课程管理',
            requiresAuth: true
          },
          children: [
            {
              path: '/course/categories',
              name: 'CourseCategories',
              component: () => import('@/pages/course/Categories.vue'),
              meta: {
                title: '分类管理',
                requiresAuth: true
              }
            },
            {
              path: '/course/courses',
              name: 'Courses',
              component: () => import('@/pages/course/Courses.vue'),
              meta: {
                title: '课程管理',
                requiresAuth: true
              }
            },
            {
              path: '/course/zones',
              name: 'CourseZones',
              component: () => import('@/pages/course/Zones.vue'),
              meta: {
                title: '专区管理',
                requiresAuth: true
              }
            }
          ]
        },
        // 用户管理
        {
          path: '/user',
          name: 'User',
          meta: {
            title: '用户管理',
            requiresAuth: true
          },
          children: [
            {
              path: '/user/users',
              name: 'Users',
              component: () => import('@/pages/user/Users.vue'),
              meta: {
                title: '用户列表',
                requiresAuth: true
              }
            }
          ]
        }
      ]
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'NotFound',
      component: () => import('@/pages/NotFound.vue'),
      meta: {
        title: '页面不存在'
      }
    }
  ]
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  // 设置页面标题
  if (to.meta?.title) {
    document.title = `${to.meta.title} - 领课教育系统`
  }
  
  // 检查是否需要认证
  if (to.meta?.requiresAuth) {
    if (!authStore.isLoggedIn) {
      // 未登录，跳转到登录页
      next('/login')
      return
    }
    
    // 如果已登录但没有用户信息，尝试获取
    if (!authStore.userInfo) {
      try {
        await authStore.initUserInfo()
      } catch (error) {
        // 获取用户信息失败，跳转到登录页
        next('/login')
        return
      }
    }
  }
  
  // 如果已登录用户访问登录页，重定向到首页
  if (to.path === '/login' && authStore.isLoggedIn) {
    next('/')
    return
  }
  
  next()
})

export default router