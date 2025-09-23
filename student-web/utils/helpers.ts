/**
 * 格式化时间（秒）为 MM:SS 或 HH:MM:SS 格式
 */
export function formatTime(seconds: number, includeHours = false): string {
  if (seconds < 0 || !Number.isFinite(seconds)) {
    return includeHours ? '00:00:00' : '00:00'
  }
  
  const totalSeconds = Math.floor(seconds)
  const hours = Math.floor(totalSeconds / 3600)
  const minutes = Math.floor((totalSeconds % 3600) / 60)
  const secs = totalSeconds % 60
  
  if (includeHours || hours > 0) {
    return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
  }
  
  return `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

/**
 * 格式化价格
 */
export function formatPrice(price: number, currency = '¥'): string {
  if (!price || price < 0) {
    return `${currency}0`
  }
  
  return `${currency}${price.toLocaleString()}`
}

/**
 * 验证手机号格式
 */
export function validatePhone(phone: string): boolean {
  if (!phone) return false
  const phoneRegex = /^1[3-9]\d{9}$/
  return phoneRegex.test(phone)
}

/**
 * 验证密码强度
 */
export function validatePassword(password: string, strict = false): boolean {
  if (!password) return false
  
  // 基本长度要求
  if (password.length < 8) return false
  
  if (strict) {
    // 强密码要求：至少包含大写字母、小写字母、数字和特殊字符
    const hasUpper = /[A-Z]/.test(password)
    const hasLower = /[a-z]/.test(password)
    const hasNumber = /\d/.test(password)
    const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(password)
    
    return hasUpper && hasLower && hasNumber && hasSpecial
  }
  
  return true
}

/**
 * 格式化文件大小
 */
export function formatFileSize(bytes: number): string {
  if (!bytes || bytes < 0) return '0 B'
  
  const units = ['B', 'KB', 'MB', 'GB', 'TB']
  let size = bytes
  let unitIndex = 0
  
  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024
    unitIndex++
  }
  
  return `${size.toFixed(1)} ${units[unitIndex]}`
}

/**
 * 防抖函数
 */
export function debounce<T extends (...args: any[]) => any>(
  func: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timeoutId: NodeJS.Timeout | null = null
  
  return (...args: Parameters<T>) => {
    if (timeoutId) {
      clearTimeout(timeoutId)
    }
    
    timeoutId = setTimeout(() => {
      func(...args)
    }, delay)
  }
}

/**
 * 节流函数
 */
export function throttle<T extends (...args: any[]) => any>(
  func: T,
  delay: number
): (...args: Parameters<T>) => void {
  let lastExecTime = 0
  
  return (...args: Parameters<T>) => {
    const now = Date.now()
    
    if (now - lastExecTime >= delay) {
      func(...args)
      lastExecTime = now
    }
  }
}

/**
 * 深拷贝对象
 */
export function deepClone<T>(obj: T, visited = new WeakMap()): T {
  // 处理基本类型
  if (obj === null || typeof obj !== 'object') {
    return obj
  }
  
  // 处理循环引用
  if (visited.has(obj as any)) {
    return visited.get(obj as any)
  }
  
  // 处理日期对象
  if (obj instanceof Date) {
    return new Date(obj.getTime()) as T
  }
  
  // 处理数组
  if (Array.isArray(obj)) {
    const clonedArr: any[] = []
    visited.set(obj as any, clonedArr)
    
    for (let i = 0; i < obj.length; i++) {
      clonedArr[i] = deepClone(obj[i], visited)
    }
    
    return clonedArr as T
  }
  
  // 处理普通对象
  const clonedObj: any = {}
  visited.set(obj as any, clonedObj)
  
  for (const key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      clonedObj[key] = deepClone(obj[key], visited)
    }
  }
  
  return clonedObj
}

/**
 * 生成UUID
 */
export function generateUuid(): string {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
    const r = Math.random() * 16 | 0
    const v = c === 'x' ? r : (r & 0x3 | 0x8)
    return v.toString(16)
  })
}

/**
 * 格式化日期
 */
export function formatDate(date: Date | string | number, format = 'YYYY-MM-DD'): string {
  let dateObj: Date
  
  if (date instanceof Date) {
    dateObj = date
  } else if (typeof date === 'string' || typeof date === 'number') {
    dateObj = new Date(date)
  } else {
    return 'Invalid Date'
  }
  
  if (isNaN(dateObj.getTime())) {
    return 'Invalid Date'
  }
  
  const year = dateObj.getFullYear()
  const month = (dateObj.getMonth() + 1).toString().padStart(2, '0')
  const day = dateObj.getDate().toString().padStart(2, '0')
  const hours = dateObj.getHours().toString().padStart(2, '0')
  const minutes = dateObj.getMinutes().toString().padStart(2, '0')
  const seconds = dateObj.getSeconds().toString().padStart(2, '0')
  
  return format
    .replace('YYYY', year.toString())
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 获取文件扩展名
 */
export function getFileExtension(filename: string): string {
  return filename.split('.').pop()?.toLowerCase() || ''
}

/**
 * 检查是否为有效的URL
 */
export function isValidUrl(url: string): boolean {
  try {
    new URL(url)
    return true
  } catch {
    return false
  }
}

/**
 * 从URL中提取域名
 */
export function extractDomain(url: string): string {
  try {
    return new URL(url).hostname
  } catch {
    return ''
  }
}

/**
 * 转换为友好的错误消息
 */
export function getErrorMessage(error: any): string {
  if (typeof error === 'string') {
    return error
  }
  
  if (error?.message) {
    return error.message
  }
  
  if (error?.data?.message) {
    return error.data.message
  }
  
  return '操作失败，请稍后重试'
}

/**
 * 检查对象是否为空
 */
export function isEmpty(obj: any): boolean {
  if (obj == null) return true
  if (Array.isArray(obj) || typeof obj === 'string') return obj.length === 0
  if (typeof obj === 'object') return Object.keys(obj).length === 0
  return false
}

/**
 * 首字母大写
 */
export function capitalize(str: string): string {
  if (!str) return ''
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase()
}

/**
 * 驼峰转短横线
 */
export function camelToKebab(str: string): string {
  return str.replace(/([a-z0-9]|(?=[A-Z]))([A-Z])/g, '$1-$2').toLowerCase()
}

/**
 * 短横线转驼峰
 */
export function kebabToCamel(str: string): string {
  return str.replace(/-([a-z])/g, (match, letter) => letter.toUpperCase())
}