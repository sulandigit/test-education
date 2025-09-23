# 开发指南

## 环境要求

- Node.js 16.x 或更高版本
- npm 或 yarn 包管理器
- 现代浏览器（Chrome、Firefox、Safari、Edge）

## 快速开始

### 1. 安装依赖

```bash
cd student-web
npm install
# 或
yarn install
```

### 2. 启动开发服务器

```bash
npm run dev
# 或
yarn dev
```

访问 http://localhost:3000 查看应用

### 3. 构建生产版本

```bash
npm run build
# 或
yarn build
```

### 4. 预览生产版本

```bash
npm run preview
# 或
yarn preview
```

## 开发规范

### 组件开发

1. **组件命名**: 使用 PascalCase，如 `CourseCard.vue`
2. **文件组织**: 相关文件放在同一目录下
3. **TypeScript**: 所有组件使用 `<script setup lang="ts">`
4. **样式**: 使用 scoped 样式，避免全局样式污染

### 代码风格

1. **缩进**: 使用 2 个空格
2. **引号**: 使用单引号
3. **分号**: 每行末尾加分号
4. **命名**: 变量使用 camelCase，常量使用 UPPER_CASE

### Git 提交规范

```
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 代码重构
test: 测试相关
chore: 构建过程或辅助工具的变动
```

## 目录说明

```
├── api/          # API 接口
├── assets/       # 静态资源
├── components/   # 公共组件
├── layouts/      # 布局模板
├── pages/        # 页面组件
├── plugins/      # 插件
├── store/        # 状态管理
├── types/        # 类型定义
├── utils/        # 工具函数
└── middleware/   # 中间件
```

## 常用命令

```bash
# 开发
npm run dev

# 构建
npm run build

# 代码检查
npm run lint

# 类型检查
npm run type-check

# 测试
npm run test
```

## API 集成

### 请求示例

```typescript
import { authApi } from '@/api/auth'

// 登录
const loginUser = async (credentials: LoginRequest) => {
  try {
    const response = await authApi.login(credentials)
    return response.data
  } catch (error) {
    console.error('登录失败:', error)
    throw error
  }
}
```

### 错误处理

```typescript
try {
  const data = await api.getData()
  // 处理数据
} catch (error) {
  if (error.message.includes('401')) {
    // 未授权，跳转登录
    navigateTo('/login')
  } else {
    // 其他错误
    ElMessage.error(error.message)
  }
}
```

## 状态管理

### Store 使用示例

```typescript
// 定义 store
export const useUserStore = defineStore('user', () => {
  const user = ref<User | null>(null)
  const isLoggedIn = computed(() => !!user.value)
  
  const login = async (credentials: LoginRequest) => {
    const response = await authApi.login(credentials)
    user.value = response.data.user
    localStorage.setItem('token', response.data.token)
  }
  
  return { user, isLoggedIn, login }
})

// 在组件中使用
const userStore = useUserStore()
const { user, isLoggedIn } = storeToRefs(userStore)
```

## 路由配置

### 路由守卫

```typescript
// middleware/auth.ts
export default defineNuxtRouteMiddleware((to) => {
  const userStore = useUserStore()
  
  if (!userStore.isLoggedIn) {
    return navigateTo('/login')
  }
})
```

### 页面使用中间件

```vue
<script setup lang="ts">
definePageMeta({
  middleware: 'auth'
})
</script>
```

## 样式开发

### 使用设计系统变量

```scss
.my-component {
  color: $primary-color;
  padding: $spacing-md;
  border-radius: $border-radius-base;
  box-shadow: $box-shadow-base;
}
```

### 响应式设计

```scss
.responsive-component {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: $spacing-lg;
  
  @media (max-width: $breakpoint-md) {
    grid-template-columns: repeat(2, 1fr);
  }
  
  @media (max-width: $breakpoint-sm) {
    grid-template-columns: 1fr;
  }
}
```

## 性能优化

### 懒加载组件

```vue
<script setup lang="ts">
// 懒加载组件
const LazyComponent = defineAsyncComponent(() => import('@/components/Heavy.vue'))
</script>
```

### 图片优化

```vue
<template>
  <img 
    :src="imageUrl" 
    :alt="imageAlt"
    loading="lazy"
    @error="handleImageError"
  >
</template>
```

## 调试技巧

### Vue DevTools
安装 Vue DevTools 浏览器扩展进行组件调试

### 网络请求调试
在浏览器开发者工具的 Network 面板查看 API 请求

### 控制台输出
```typescript
// 开发环境下的调试输出
if (process.dev) {
  console.log('Debug:', data)
}
```

## 部署注意事项

1. **环境变量**: 确保生产环境变量正确配置
2. **API 地址**: 更新生产环境的 API 基础地址
3. **静态资源**: 配置 CDN 加速静态资源
4. **缓存策略**: 设置合适的缓存头
5. **GZIP 压缩**: 启用服务器压缩

## 常见问题

### Q: 开发服务器启动失败
A: 检查 Node.js 版本，确保在 16.x 以上

### Q: API 请求失败
A: 检查后端服务是否启动，网络代理是否配置正确

### Q: 样式不生效
A: 检查 SCSS 语法，确保变量导入正确

### Q: 组件不更新
A: 检查响应式数据定义，使用 ref() 或 reactive()

## 参考资料

- [Vue 3 官方文档](https://vuejs.org/)
- [Nuxt 3 官方文档](https://nuxt.com/)
- [Element Plus 文档](https://element-plus.org/)
- [Pinia 文档](https://pinia.vuejs.org/)
- [TypeScript 文档](https://www.typescriptlang.org/)