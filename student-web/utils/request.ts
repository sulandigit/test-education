import axios, { type AxiosInstance, type AxiosRequestConfig, type AxiosResponse } from 'axios'
import type { ApiResponse } from '@/types'

// 创建axios实例
const createAxiosInstance = (): AxiosInstance => {
  const config = useRuntimeConfig()
  
  const instance = axios.create({
    baseURL: config.public.apiBase,
    timeout: 10000,
    headers: {
      'Content-Type': 'application/json'
    }
  })

  // 请求拦截器
  instance.interceptors.request.use(
    (config) => {
      // 从本地存储获取token
      const token = process.client ? localStorage.getItem('token') : null
      if (token) {
        config.headers.Authorization = `Bearer ${token}`
      }
      
      // 打印请求信息（开发环境）
      if (process.dev) {
        console.log(`🚀 [${config.method?.toUpperCase()}] ${config.url}`, {
          params: config.params,
          data: config.data
        })
      }
      
      return config
    },
    (error) => {
      console.error('Request error:', error)
      return Promise.reject(error)
    }
  )

  // 响应拦截器
  instance.interceptors.response.use(
    (response: AxiosResponse<ApiResponse>) => {
      const { data } = response
      
      // 打印响应信息（开发环境）
      if (process.dev) {
        console.log(`✅ Response:`, data)
      }

      // 统一处理响应
      if (data.code === 200) {
        return response
      } else if (data.code === 401) {
        // 认证失败，清除token并跳转登录
        if (process.client) {
          localStorage.removeItem('token')
          navigateTo('/login')
        }
        throw new Error(data.msg || '认证失败')
      } else {
        // 其他错误
        throw new Error(data.msg || '请求失败')
      }
    },
    (error) => {
      console.error('Response error:', error)
      
      // 网络错误处理
      if (!error.response) {
        throw new Error('网络连接失败，请检查网络设置')
      }
      
      const { status, data } = error.response
      
      switch (status) {
        case 400:
          throw new Error(data?.msg || '请求参数错误')
        case 401:
          if (process.client) {
            localStorage.removeItem('token')
            navigateTo('/login')
          }
          throw new Error('登录已过期，请重新登录')
        case 403:
          throw new Error('权限不足')
        case 404:
          throw new Error('请求的资源不存在')
        case 429:
          throw new Error('请求过于频繁，请稍后再试')
        case 500:
          throw new Error('服务器内部错误')
        default:
          throw new Error(data?.msg || `请求失败 (${status})`)
      }
    }
  )

  return instance
}

// 导出axios实例
export const http = createAxiosInstance()

// 封装常用请求方法
export const request = {
  get<T = any>(url: string, params?: any): Promise<ApiResponse<T>> {
    return http.get(url, { params }).then(res => res.data)
  },

  post<T = any>(url: string, data?: any): Promise<ApiResponse<T>> {
    return http.post(url, data).then(res => res.data)
  },

  put<T = any>(url: string, data?: any): Promise<ApiResponse<T>> {
    return http.put(url, data).then(res => res.data)
  },

  delete<T = any>(url: string, params?: any): Promise<ApiResponse<T>> {
    return http.delete(url, { params }).then(res => res.data)
  },

  upload<T = any>(url: string, formData: FormData): Promise<ApiResponse<T>> {
    return http.post(url, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    }).then(res => res.data)
  }
}

export default request