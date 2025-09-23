// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  devtools: { enabled: true },
  
  // CSS框架
  css: [
    'element-plus/dist/index.css',
    '@/assets/css/main.scss'
  ],

  // 模块
  modules: [
    '@element-plus/nuxt',
    '@pinia/nuxt'
  ],

  // 运行时配置
  runtimeConfig: {
    // 服务端环境变量
    public: {
      apiBase: process.env.API_BASE_URL || 'http://localhost:8080',
      appTitle: process.env.APP_TITLE || '领课教育',
      videoDomain: process.env.VIDEO_DOMAIN || 'video.domain.com'
    }
  },

  // 页面配置
  app: {
    head: {
      title: '领课教育 - 在线学习平台',
      meta: [
        { charset: 'utf-8' },
        { name: 'viewport', content: 'width=device-width, initial-scale=1' },
        { name: 'description', content: '领课教育系统 - 专业的在线学习平台' }
      ],
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }
      ]
    }
  },

  // Vite配置
  vite: {
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: '@use "@/assets/css/variables.scss" as *;'
        }
      }
    }
  },

  // 服务端渲染
  ssr: true,

  // Element Plus配置
  elementPlus: {
    importStyle: 'scss'
  }
})