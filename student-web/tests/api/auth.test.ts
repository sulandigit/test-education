import { describe, it, expect, vi, beforeEach } from 'vitest'
import { login, register, sendSmsCode, getCurrentUser, logout } from '~/api/auth'

// Mock $fetch
const mockFetch = vi.fn()
vi.mock('#app', () => ({
  $fetch: mockFetch
}))

describe('Auth API', () => {
  beforeEach(() => {
    vi.clearAllMocks()
  })

  describe('login', () => {
    it('sends correct login request', async () => {
      const loginData = {
        phone: '13800138000',
        password: 'password123'
      }
      const mockResponse = {
        code: 200,
        data: {
          token: 'mock-token',
          user: {
            id: 1,
            phone: '13800138000',
            nickname: '测试用户'
          }
        }
      }

      mockFetch.mockResolvedValue(mockResponse)

      const result = await login(loginData)

      expect(mockFetch).toHaveBeenCalledWith('/api/auth/login', {
        method: 'POST',
        body: loginData
      })
      expect(result).toEqual(mockResponse)
    })

    it('handles login failure', async () => {
      const loginData = {
        phone: '13800138000',
        password: 'wrong-password'
      }

      mockFetch.mockRejectedValue(new Error('Invalid credentials'))

      await expect(login(loginData)).rejects.toThrow('Invalid credentials')
    })
  })

  describe('register', () => {
    it('sends correct register request', async () => {
      const registerData = {
        phone: '13800138000',
        password: 'password123',
        code: '123456',
        nickname: '新用户'
      }
      const mockResponse = {
        code: 200,
        data: {
          token: 'new-token',
          user: {
            id: 2,
            phone: '13800138000',
            nickname: '新用户'
          }
        }
      }

      mockFetch.mockResolvedValue(mockResponse)

      const result = await register(registerData)

      expect(mockFetch).toHaveBeenCalledWith('/api/auth/register', {
        method: 'POST',
        body: registerData
      })
      expect(result).toEqual(mockResponse)
    })

    it('handles register failure', async () => {
      const registerData = {
        phone: '13800138000',
        password: 'password123',
        code: '123456',
        nickname: '新用户'
      }

      mockFetch.mockRejectedValue(new Error('Phone already exists'))

      await expect(register(registerData)).rejects.toThrow('Phone already exists')
    })
  })

  describe('sendSmsCode', () => {
    it('sends SMS code request correctly', async () => {
      const phone = '13800138000'
      const mockResponse = {
        code: 200,
        message: 'SMS code sent successfully'
      }

      mockFetch.mockResolvedValue(mockResponse)

      const result = await sendSmsCode(phone)

      expect(mockFetch).toHaveBeenCalledWith('/api/auth/sms-code', {
        method: 'POST',
        body: { phone }
      })
      expect(result).toEqual(mockResponse)
    })

    it('handles SMS code sending failure', async () => {
      const phone = '13800138000'

      mockFetch.mockRejectedValue(new Error('SMS service unavailable'))

      await expect(sendSmsCode(phone)).rejects.toThrow('SMS service unavailable')
    })

    it('validates phone number format', async () => {
      const invalidPhone = '123'
      
      await expect(sendSmsCode(invalidPhone)).rejects.toThrow()
    })
  })

  describe('getCurrentUser', () => {
    it('fetches current user successfully', async () => {
      const mockResponse = {
        code: 200,
        data: {
          id: 1,
          phone: '13800138000',
          nickname: '测试用户',
          avatar: '/avatar.jpg',
          email: 'test@example.com'
        }
      }

      mockFetch.mockResolvedValue(mockResponse)

      const result = await getCurrentUser()

      expect(mockFetch).toHaveBeenCalledWith('/api/user/profile', {
        method: 'GET'
      })
      expect(result).toEqual(mockResponse)
    })

    it('handles unauthorized access', async () => {
      mockFetch.mockRejectedValue(new Error('Unauthorized'))

      await expect(getCurrentUser()).rejects.toThrow('Unauthorized')
    })
  })

  describe('logout', () => {
    it('sends logout request successfully', async () => {
      const mockResponse = {
        code: 200,
        message: 'Logout successful'
      }

      mockFetch.mockResolvedValue(mockResponse)

      const result = await logout()

      expect(mockFetch).toHaveBeenCalledWith('/api/auth/logout', {
        method: 'POST'
      })
      expect(result).toEqual(mockResponse)
    })

    it('handles logout failure gracefully', async () => {
      mockFetch.mockRejectedValue(new Error('Server error'))

      await expect(logout()).rejects.toThrow('Server error')
    })
  })

  describe('API request validation', () => {
    it('validates required fields for login', async () => {
      // 测试缺少手机号
      await expect(login({ password: 'password123' } as any)).rejects.toThrow()

      // 测试缺少密码
      await expect(login({ phone: '13800138000' } as any)).rejects.toThrow()
    })

    it('validates required fields for register', async () => {
      const baseData = {
        phone: '13800138000',
        password: 'password123',
        code: '123456',
        nickname: '用户'
      }

      // 测试缺少各个字段
      await expect(register({ ...baseData, phone: undefined } as any)).rejects.toThrow()
      await expect(register({ ...baseData, password: undefined } as any)).rejects.toThrow()
      await expect(register({ ...baseData, code: undefined } as any)).rejects.toThrow()
    })

    it('validates phone number format in requests', async () => {
      const invalidPhones = ['123', '1234567890123', 'abc123', '']

      for (const phone of invalidPhones) {
        await expect(login({ phone, password: 'password123' })).rejects.toThrow()
        await expect(sendSmsCode(phone)).rejects.toThrow()
      }
    })

    it('validates password strength', async () => {
      const weakPasswords = ['123', '123456', 'password', 'abc']

      for (const password of weakPasswords) {
        await expect(login({ 
          phone: '13800138000', 
          password 
        })).rejects.toThrow()
      }
    })
  })

  describe('response format validation', () => {
    it('validates login response format', async () => {
      const loginData = {
        phone: '13800138000',
        password: 'password123'
      }

      // 测试无效响应格式
      const invalidResponses = [
        null,
        undefined,
        {},
        { code: 200 }, // 缺少data
        { data: {} }, // 缺少code
        { code: 200, data: {} }, // data格式不正确
      ]

      for (const invalidResponse of invalidResponses) {
        mockFetch.mockResolvedValue(invalidResponse)
        
        await expect(login(loginData)).rejects.toThrow()
      }
    })

    it('validates successful response structure', async () => {
      const loginData = {
        phone: '13800138000',
        password: 'password123'
      }
      const validResponse = {
        code: 200,
        data: {
          token: 'valid-token',
          user: {
            id: 1,
            phone: '13800138000',
            nickname: '用户'
          }
        }
      }

      mockFetch.mockResolvedValue(validResponse)

      const result = await login(loginData)
      expect(result).toEqual(validResponse)
      expect(result.data.token).toBeDefined()
      expect(result.data.user).toBeDefined()
      expect(result.data.user.id).toBeTypeOf('number')
    })
  })
})