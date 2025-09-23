import { describe, it, expect } from 'vitest'
import { 
  formatTime, 
  formatPrice, 
  validatePhone, 
  validatePassword, 
  formatFileSize, 
  debounce, 
  throttle,
  deepClone,
  generateUuid,
  formatDate
} from '~/utils/helpers'

describe('Helper Functions', () => {
  describe('formatTime', () => {
    it('formats seconds to MM:SS format', () => {
      expect(formatTime(0)).toBe('00:00')
      expect(formatTime(30)).toBe('00:30')
      expect(formatTime(90)).toBe('01:30')
      expect(formatTime(3661)).toBe('61:01') // 1小时1分1秒
    })

    it('formats seconds to HH:MM:SS format when over 1 hour', () => {
      expect(formatTime(3600, true)).toBe('01:00:00')
      expect(formatTime(3661, true)).toBe('01:01:01')
      expect(formatTime(7323, true)).toBe('02:02:03')
    })

    it('handles edge cases', () => {
      expect(formatTime(-1)).toBe('00:00')
      expect(formatTime(0.5)).toBe('00:00')
      expect(formatTime(59.9)).toBe('00:59')
    })
  })

  describe('formatPrice', () => {
    it('formats price with currency symbol', () => {
      expect(formatPrice(0)).toBe('¥0')
      expect(formatPrice(99)).toBe('¥99')
      expect(formatPrice(199.5)).toBe('¥199.5')
      expect(formatPrice(1999)).toBe('¥1,999')
      expect(formatPrice(12345.67)).toBe('¥12,345.67')
    })

    it('handles different currency symbols', () => {
      expect(formatPrice(99, '$')).toBe('$99')
      expect(formatPrice(99, '€')).toBe('€99')
    })

    it('handles edge cases', () => {
      expect(formatPrice(-1)).toBe('¥0')
      expect(formatPrice(null as any)).toBe('¥0')
      expect(formatPrice(undefined as any)).toBe('¥0')
    })
  })

  describe('validatePhone', () => {
    it('validates correct phone numbers', () => {
      expect(validatePhone('13800138000')).toBe(true)
      expect(validatePhone('15912345678')).toBe(true)
      expect(validatePhone('18888888888')).toBe(true)
    })

    it('rejects invalid phone numbers', () => {
      expect(validatePhone('123')).toBe(false)
      expect(validatePhone('1234567890123')).toBe(false)
      expect(validatePhone('abc12345678')).toBe(false)
      expect(validatePhone('12345678901')).toBe(false) // 11位但不是正确格式
      expect(validatePhone('')).toBe(false)
      expect(validatePhone('10000000000')).toBe(false) // 不以1开头的有效运营商号段
    })
  })

  describe('validatePassword', () => {
    it('validates correct passwords', () => {
      expect(validatePassword('password123')).toBe(true)
      expect(validatePassword('Password123!')).toBe(true)
      expect(validatePassword('12345678')).toBe(true)
    })

    it('rejects invalid passwords', () => {
      expect(validatePassword('123')).toBe(false) // 太短
      expect(validatePassword('123456')).toBe(false) // 太短
      expect(validatePassword('')).toBe(false) // 空字符串
    })

    it('validates password strength requirements', () => {
      // 测试强密码验证
      expect(validatePassword('Password123!', true)).toBe(true)
      expect(validatePassword('password123', true)).toBe(false) // 缺少大写字母
      expect(validatePassword('PASSWORD123', true)).toBe(false) // 缺少小写字母
      expect(validatePassword('Password', true)).toBe(false) // 缺少数字
      expect(validatePassword('Password123', true)).toBe(false) // 缺少特殊字符
    })
  })

  describe('formatFileSize', () => {
    it('formats file sizes correctly', () => {
      expect(formatFileSize(0)).toBe('0 B')
      expect(formatFileSize(500)).toBe('500 B')
      expect(formatFileSize(1024)).toBe('1.0 KB')
      expect(formatFileSize(1536)).toBe('1.5 KB')
      expect(formatFileSize(1048576)).toBe('1.0 MB')
      expect(formatFileSize(1073741824)).toBe('1.0 GB')
    })

    it('handles edge cases', () => {
      expect(formatFileSize(-1)).toBe('0 B')
      expect(formatFileSize(null as any)).toBe('0 B')
      expect(formatFileSize(undefined as any)).toBe('0 B')
    })
  })

  describe('debounce', () => {
    it('debounces function calls', (done) => {
      let callCount = 0
      const fn = debounce(() => {
        callCount++
      }, 100)

      // 快速调用多次
      fn()
      fn()
      fn()

      // 立即检查，应该还没有被调用
      expect(callCount).toBe(0)

      // 等待超过debounce时间
      setTimeout(() => {
        expect(callCount).toBe(1) // 只应该被调用一次
        done()
      }, 150)
    })

    it('handles multiple debounce sequences', (done) => {
      let callCount = 0
      const fn = debounce(() => {
        callCount++
      }, 50)

      // 第一组调用
      fn()
      fn()

      setTimeout(() => {
        // 第二组调用
        fn()
        fn()
      }, 100)

      setTimeout(() => {
        expect(callCount).toBe(2) // 应该被调用两次
        done()
      }, 200)
    })
  })

  describe('throttle', () => {
    it('throttles function calls', (done) => {
      let callCount = 0
      const fn = throttle(() => {
        callCount++
      }, 100)

      // 立即调用应该执行
      fn()
      expect(callCount).toBe(1)

      // 快速连续调用应该被节流
      fn()
      fn()
      fn()
      expect(callCount).toBe(1)

      // 等待超过throttle时间后再调用
      setTimeout(() => {
        fn()
        expect(callCount).toBe(2)
        done()
      }, 150)
    })
  })

  describe('deepClone', () => {
    it('clones primitive values', () => {
      expect(deepClone(123)).toBe(123)
      expect(deepClone('hello')).toBe('hello')
      expect(deepClone(true)).toBe(true)
      expect(deepClone(null)).toBe(null)
      expect(deepClone(undefined)).toBe(undefined)
    })

    it('clones arrays correctly', () => {
      const arr = [1, 2, { a: 3 }]
      const cloned = deepClone(arr)
      
      expect(cloned).toEqual(arr)
      expect(cloned).not.toBe(arr)
      expect(cloned[2]).not.toBe(arr[2])
    })

    it('clones objects correctly', () => {
      const obj = {
        a: 1,
        b: 'hello',
        c: {
          d: [1, 2, 3],
          e: { f: 'nested' }
        }
      }
      const cloned = deepClone(obj)
      
      expect(cloned).toEqual(obj)
      expect(cloned).not.toBe(obj)
      expect(cloned.c).not.toBe(obj.c)
      expect(cloned.c.d).not.toBe(obj.c.d)
      expect(cloned.c.e).not.toBe(obj.c.e)
    })

    it('handles circular references', () => {
      const obj: any = { a: 1 }
      obj.self = obj
      
      const cloned = deepClone(obj)
      expect(cloned.a).toBe(1)
      expect(cloned.self).toBe(cloned)
    })
  })

  describe('generateUuid', () => {
    it('generates unique UUIDs', () => {
      const uuid1 = generateUuid()
      const uuid2 = generateUuid()
      
      expect(uuid1).not.toBe(uuid2)
      expect(uuid1).toMatch(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i)
      expect(uuid2).toMatch(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i)
    })

    it('generates multiple unique UUIDs', () => {
      const uuids = Array.from({ length: 100 }, () => generateUuid())
      const uniqueUuids = new Set(uuids)
      
      expect(uniqueUuids.size).toBe(100) // 所有UUID都应该是唯一的
    })
  })

  describe('formatDate', () => {
    it('formats date correctly', () => {
      const date = new Date('2023-12-25T10:30:00')
      
      expect(formatDate(date)).toBe('2023-12-25')
      expect(formatDate(date, 'YYYY-MM-DD HH:mm')).toBe('2023-12-25 10:30')
      expect(formatDate(date, 'MM/DD/YYYY')).toBe('12/25/2023')
    })

    it('handles different date inputs', () => {
      const timestamp = 1703505000000 // 2023-12-25T10:30:00
      const dateString = '2023-12-25'
      
      expect(formatDate(timestamp)).toBe('2023-12-25')
      expect(formatDate(dateString)).toBe('2023-12-25')
    })

    it('handles invalid dates', () => {
      expect(formatDate('invalid')).toBe('Invalid Date')
      expect(formatDate(null as any)).toBe('Invalid Date')
      expect(formatDate(undefined as any)).toBe('Invalid Date')
    })
  })
})