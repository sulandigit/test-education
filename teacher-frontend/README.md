# 讲师端管理系统

一个基于 Vue 3 + Element Plus 的教育管理系统前端应用，专为讲师设计，提供完整的课程管理和学生管理功能。

## 🚀 功能特性

### 📚 课程管理
- ✅ 课程列表展示与分页
- ✅ 新增/编辑/删除课程
- ✅ 课程状态管理（进行中/已完成/暂停）
- ✅ 课程搜索与筛选
- ✅ 课程详细信息管理

### 👥 学生管理  
- ✅ 学生列表展示与分页
- ✅ 新增/编辑/删除学生
- ✅ 学生信息管理（姓名、邮箱、电话等）
- ✅ 学生课程分配
- ✅ 批量操作支持
- ✅ 学生搜索与筛选

### 📊 数据统计
- ✅ 仪表盘概览
- ✅ 课程统计数据
- ✅ 学生统计数据
- ✅ 实时数据展示

### 🎨 界面设计
- ✅ 响应式布局设计
- ✅ 现代化UI界面
- ✅ 暗色主题支持
- ✅ 移动端适配

## 🛠️ 技术栈

- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **UI组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **样式**: SCSS
- **图标**: Element Plus Icons

## 📦 项目结构

```
teacher-frontend/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口
│   ├── assets/            # 静态资源
│   │   └── css/          # 样式文件
│   ├── components/        # 公共组件
│   ├── router/           # 路由配置
│   ├── stores/           # 状态管理
│   ├── utils/            # 工具函数
│   ├── views/            # 页面组件
│   ├── App.vue           # 根组件
│   └── main.js           # 入口文件
├── index.html            # HTML模板
├── package.json          # 项目配置
└── vite.config.js        # Vite配置
```

## 🎯 主要页面

### 1. 登录页面 (`/login`)
- 用户身份验证
- 记住登录状态
- 演示账号支持

### 2. 仪表盘 (`/dashboard`)
- 数据统计概览
- 最近课程展示
- 最近学生信息
- 快速操作入口

### 3. 课程管理 (`/courses`)
- 课程列表管理
- 课程CRUD操作
- 状态管理
- 搜索筛选

### 4. 学生管理 (`/students`)
- 学生列表管理
- 学生CRUD操作
- 课程分配
- 批量操作

## 🚦 快速开始

### 环境要求
- Node.js >= 16.0.0
- npm >= 8.0.0

### 安装依赖
```bash
cd teacher-frontend
npm install
```

### 开发模式
```bash
npm run dev
```

### 构建生产版本
```bash
npm run build
```

### 预览生产版本
```bash
npm run preview
```

## 🔧 配置说明

### API配置
在 `src/utils/request.js` 中配置API基础URL：

```javascript
const request = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080/api',
  timeout: 10000
})
```

### 路由配置
路由配置位于 `src/router/index.js`，包含：
- 登录页面路由
- 主布局路由
- 子页面路由
- 路由守卫

### 状态管理
使用 Pinia 进行状态管理，包含：
- 用户状态 (`stores/user.js`)
- 课程状态 (`stores/course.js`)  
- 学生状态 (`stores/student.js`)
- 统计状态 (`stores/stats.js`)

## 🎨 样式定制

### 主题色彩
```scss
// 主色调
$primary-color: #409eff;
$success-color: #67c23a;
$warning-color: #e6a23c;
$danger-color: #f56c6c;
$info-color: #909399;
```

### 响应式断点
```scss
// 移动端
@media (max-width: 768px) { }

// 小屏移动端  
@media (max-width: 480px) { }
```

## 📱 功能演示

### 登录功能
- 用户名: `teacher`
- 密码: `123456`

### 模拟数据
系统内置了完整的模拟数据，包括：
- 示例课程数据
- 示例学生数据
- 统计数据

## 🔒 安全特性

- JWT Token认证
- 路由权限控制
- 请求拦截处理
- XSS防护
- CSRF防护

## 📈 性能优化

- 路由懒加载
- 组件按需引入
- 图片懒加载
- 虚拟滚动支持
- 缓存策略

## 🌍 国际化

系统支持中文界面，Element Plus组件已配置中文语言包。

## 📄 许可证

MIT License

## 👥 贡献

欢迎提交 Issue 和 Pull Request！

## 📞 联系方式

如有问题或建议，请联系项目维护者。