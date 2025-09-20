# 领课教育系统 - 后台管理页面

基于 Vue 3 + TypeScript + Element Plus 开发的现代化后台管理系统。

## 功能特性

### 🔐 登录认证
- 手机号密码登录
- JWT Token 认证
- 自动刷新用户信息

### 👥 系统管理
- **用户管理**: 系统管理员的增删改查
- **角色管理**: 角色权限配置
- **菜单管理**: 动态菜单配置

### 📚 课程管理
- **分类管理**: 课程分类的树形管理
- **课程管理**: 课程信息完整管理
- **专区管理**: 课程专区配置

### 👤 用户管理
- **用户列表**: 前台用户信息管理
- **用户详情**: 完整的用户信息展示

## 技术栈

- **框架**: Vue 3 + Composition API
- **语言**: TypeScript
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios
- **构建工具**: Vite
- **代码规范**: ESLint + TypeScript ESLint

## 项目结构

```
admin-frontend/
├── src/
│   ├── api/              # API 接口定义
│   │   ├── auth.ts       # 认证相关接口
│   │   ├── course.ts     # 课程相关接口
│   │   ├── system.ts     # 系统管理接口
│   │   └── user.ts       # 用户管理接口
│   ├── components/       # 公共组件
│   │   └── SidebarItem.vue  # 侧边栏菜单项
│   ├── layouts/          # 布局组件
│   │   └── AdminLayout.vue  # 管理后台布局
│   ├── pages/            # 页面组件
│   │   ├── course/       # 课程管理页面
│   │   ├── system/       # 系统管理页面
│   │   ├── user/         # 用户管理页面
│   │   ├── Dashboard.vue # 首页仪表盘
│   │   ├── Login.vue     # 登录页面
│   │   └── NotFound.vue  # 404 页面
│   ├── router/           # 路由配置
│   │   └── index.ts      # 路由定义和守卫
│   ├── stores/           # 状态管理
│   │   └── auth.ts       # 认证状态管理
│   ├── types/            # TypeScript 类型定义
│   │   └── api.ts        # API 相关类型
│   ├── utils/            # 工具函数
│   │   └── request.ts    # HTTP 请求封装
│   ├── App.vue           # 根组件
│   ├── main.ts           # 应用入口
│   └── env.d.ts          # 环境变量类型声明
├── index.html            # HTML 入口文件
├── package.json          # 项目配置
├── tsconfig.json         # TypeScript 配置
└── vite.config.ts        # Vite 构建配置
```

## 开发环境要求

- Node.js >= 16
- npm >= 7 或 yarn >= 1.22

## 安装依赖

```bash
# 使用 npm
npm install

# 或使用 yarn
yarn install
```

## 开发运行

```bash
# 启动开发服务器
npm run dev

# 或
yarn dev
```

访问 `http://localhost:3000` 查看应用。

## 构建生产版本

```bash
# 构建生产版本
npm run build

# 或
yarn build
```

## 代码检查

```bash
# 运行 ESLint 检查
npm run lint

# 或
yarn lint
```

## API 接口配置

项目通过 Vite 代理配置将 `/api` 请求转发到后端服务：

```typescript
// vite.config.ts
server: {
  port: 3000,
  proxy: {
    '/api': {
      target: 'http://localhost:7700', // 后端网关地址
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

## 路由说明

### 公开路由
- `/login` - 登录页面

### 受保护路由（需要登录）
- `/` - 重定向到 `/dashboard`
- `/dashboard` - 首页仪表盘

#### 系统管理
- `/system/users` - 系统用户管理
- `/system/roles` - 角色管理  
- `/system/menus` - 菜单管理

#### 课程管理
- `/course/categories` - 分类管理
- `/course/courses` - 课程管理
- `/course/zones` - 专区管理

#### 用户管理
- `/user/users` - 用户列表

## 状态管理

使用 Pinia 进行状态管理，主要包括：

### 认证状态 (useAuthStore)
- `token` - 用户令牌
- `userInfo` - 当前用户信息  
- `menus` - 用户菜单权限
- `permissions` - 用户权限列表
- `handleLogin()` - 登录方法
- `logout()` - 登出方法
- `hasPermission()` - 权限检查方法

## 组件说明

### 布局组件
- `AdminLayout` - 管理后台主布局，包含侧边栏、顶部导航、面包屑等

### 公共组件  
- `SidebarItem` - 递归渲染的侧边栏菜单项组件

### 页面组件
每个页面组件都包含完整的 CRUD 功能：
- 数据列表展示（支持分页、搜索、排序）
- 新增/编辑对话框
- 删除确认
- 表单验证
- 加载状态处理

## 开发说明

### API 调用
所有 API 调用都通过封装的 `request` 工具进行，支持：
- 自动添加认证 Token
- 统一错误处理  
- 请求/响应拦截
- 自动登出处理

### 权限控制
- 路由级权限：通过路由守卫检查登录状态
- 菜单权限：根据用户菜单数据动态渲染侧边栏
- 按钮权限：可通过 `hasPermission()` 方法检查

### 样式规范
- 使用 Element Plus 主题色彩
- 响应式设计，支持移动端
- 统一的间距和字体规范

## 注意事项

1. **API 对接**: 当前使用模拟数据，实际部署时需要取消注释真实 API 调用
2. **图片上传**: 需要配置真实的文件上传接口
3. **权限验证**: 需要根据实际业务需求调整权限验证逻辑
4. **环境配置**: 可通过环境变量配置不同环境的 API 地址

## 后续优化

- [ ] 添加国际化支持
- [ ] 完善错误边界处理
- [ ] 添加单元测试
- [ ] 优化打包体积
- [ ] 添加 PWA 支持
- [ ] 完善无障碍访问