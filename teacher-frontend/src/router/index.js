import { createRouter, createWebHistory } from 'vue-router'
import Layout from '@/components/Layout.vue'

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'House' }
      },
      {
        path: 'courses',
        name: 'Courses',
        component: () => import('@/views/CourseManagement.vue'),
        meta: { title: '课程管理', icon: 'Reading' }
      },
      {
        path: 'students',
        name: 'Students',
        component: () => import('@/views/StudentManagement.vue'),
        meta: { title: '学生管理', icon: 'User' }
      }
    ]
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 讲师端管理系统` : '讲师端管理系统'
  next()
})

export default router