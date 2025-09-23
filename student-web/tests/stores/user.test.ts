import { describe, it, expect, vi, beforeEach } from 'vitest'
import { setActivePinia, createPinia } from 'pinia'
import { useUserStore } from '~/stores/user'
import * as authApi from '~/api/auth'

// Mock API calls
vi.mock('~/api/auth', () => ({
  login: vi.fn(),
  register: vi.fn(),
  sendSmsCode: vi.fn(),
  getCurrentUser: vi.fn(),
  logout: vi.fn(),
}))

// Mock cookie
const mockCookie = {
  value: null
}
vi.mock('#app', async () => {
  const actual = await vi.importActual('#app')
  return {
    ...actual,
    useCookie: vi.fn(() => mockCookie),
    navigateTo: vi.fn(),
  }
})

describe('User Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
    mockCookie.value = null
  })

  it('initializes with default state', () => {
    const userStore = useUserStore()
    
    expect(userStore.currentUser).toBeNull()
    expect(userStore.isLoggedIn).toBe(false)
    expect(userStore.token).toBeNull()
    expect(userStore.loading).toBe(false)
  })

  it('handles successful login', async () => {
    const userStore = useUserStore()
    const mockLoginData = {
      phone: '13800138000',
      password: 'password123'
    }
    const mockResponse = {
      code: 200,
      data: {
        token: 'mock-token-123',
        user: {
          id: 1,
          phone: '13800138000',
          nickname: '测试用户',
          avatar: '/avatar.jpg'
        }
      }
    }

    vi.mocked(authApi.login).mockResolvedValue(mockResponse)

    const result = await userStore.login(mockLoginData)

    expect(authApi.login).toHaveBeenCalledWith(mockLoginData)
    expect(userStore.token).toBe('mock-token-123')
    expect(userStore.currentUser).toEqual(mockResponse.data.user)
    expect(userStore.isLoggedIn).toBe(true)
    expect(result).toBe(true)
  })

  it('handles failed login', async () => {
    const userStore = useUserStore()
    const mockLoginData = {
      phone: '13800138000',
      password: 'wrong-password'
    }

    vi.mocked(authApi.login).mockRejectedValue(new Error('Invalid credentials'))

    const result = await userStore.login(mockLoginData)

    expect(userStore.token).toBeNull()
    expect(userStore.currentUser).toBeNull()
    expect(userStore.isLoggedIn).toBe(false)
    expect(result).toBe(false)
  })

  it('handles successful registration', async () => {
    const userStore = useUserStore()
    const mockRegisterData = {
      phone: '13800138000',
      password: 'password123',
      code: '123456',
      nickname: '新用户'
    }
    const mockResponse = {
      code: 200,
      data: {
        token: 'new-token-456',
        user: {
          id: 2,
          phone: '13800138000',
          nickname: '新用户',
          avatar: null
        }
      }
    }

    vi.mocked(authApi.register).mockResolvedValue(mockResponse)

    const result = await userStore.register(mockRegisterData)

    expect(authApi.register).toHaveBeenCalledWith(mockRegisterData)
    expect(userStore.token).toBe('new-token-456')
    expect(userStore.currentUser).toEqual(mockResponse.data.user)
    expect(userStore.isLoggedIn).toBe(true)
    expect(result).toBe(true)
  })

  it('handles failed registration', async () => {
    const userStore = useUserStore()
    const mockRegisterData = {
      phone: '13800138000',
      password: 'password123',
      code: '123456',
      nickname: '新用户'
    }

    vi.mocked(authApi.register).mockRejectedValue(new Error('Phone already exists'))

    const result = await userStore.register(mockRegisterData)

    expect(userStore.token).toBeNull()
    expect(userStore.currentUser).toBeNull()
    expect(userStore.isLoggedIn).toBe(false)
    expect(result).toBe(false)
  })

  it('sends SMS code successfully', async () => {
    const userStore = useUserStore()
    const phone = '13800138000'
    const mockResponse = {
      code: 200,
      message: 'SMS sent successfully'
    }

    vi.mocked(authApi.sendSmsCode).mockResolvedValue(mockResponse)

    const result = await userStore.sendSmsCode(phone)

    expect(authApi.sendSmsCode).toHaveBeenCalledWith(phone)
    expect(result).toBe(true)
  })

  it('handles SMS code sending failure', async () => {
    const userStore = useUserStore()
    const phone = '13800138000'

    vi.mocked(authApi.sendSmsCode).mockRejectedValue(new Error('SMS service error'))

    const result = await userStore.sendSmsCode(phone)

    expect(result).toBe(false)
  })

  it('fetches current user successfully', async () => {
    const userStore = useUserStore()
    const mockUser = {
      id: 1,
      phone: '13800138000',
      nickname: '测试用户',
      avatar: '/avatar.jpg'
    }
    const mockResponse = {
      code: 200,
      data: mockUser
    }

    vi.mocked(authApi.getCurrentUser).mockResolvedValue(mockResponse)

    await userStore.fetchCurrentUser()

    expect(authApi.getCurrentUser).toHaveBeenCalled()
    expect(userStore.currentUser).toEqual(mockUser)
  })

  it('handles logout correctly', async () => {
    const userStore = useUserStore()
    
    // 先设置登录状态
    userStore.token = 'test-token'
    userStore.currentUser = {
      id: 1,
      phone: '13800138000',
      nickname: '测试用户',
      avatar: '/avatar.jpg'
    }

    vi.mocked(authApi.logout).mockResolvedValue({ code: 200 })

    await userStore.logout()

    expect(authApi.logout).toHaveBeenCalled()
    expect(userStore.token).toBeNull()
    expect(userStore.currentUser).toBeNull()
    expect(userStore.isLoggedIn).toBe(false)
  })

  it('initializes authentication state from cookie', async () => {
    // 模拟cookie中有token
    mockCookie.value = 'existing-token'
    
    const mockUser = {
      id: 1,
      phone: '13800138000',
      nickname: '测试用户',
      avatar: '/avatar.jpg'
    }
    const mockResponse = {
      code: 200,
      data: mockUser
    }

    vi.mocked(authApi.getCurrentUser).mockResolvedValue(mockResponse)

    const userStore = useUserStore()
    await userStore.initializeAuth()

    expect(userStore.token).toBe('existing-token')
    expect(authApi.getCurrentUser).toHaveBeenCalled()
    expect(userStore.currentUser).toEqual(mockUser)
    expect(userStore.isLoggedIn).toBe(true)
  })

  it('handles initialization with invalid token', async () => {
    mockCookie.value = 'invalid-token'
    
    vi.mocked(authApi.getCurrentUser).mockRejectedValue(new Error('Invalid token'))

    const userStore = useUserStore()
    await userStore.initializeAuth()

    expect(userStore.token).toBeNull()
    expect(userStore.currentUser).toBeNull()
    expect(userStore.isLoggedIn).toBe(false)
  })

  it('updates user profile correctly', () => {
    const userStore = useUserStore()
    
    // 设置初始用户
    userStore.currentUser = {
      id: 1,
      phone: '13800138000',
      nickname: '原昵称',
      avatar: '/old-avatar.jpg'
    }

    const updates = {
      nickname: '新昵称',
      avatar: '/new-avatar.jpg'
    }

    userStore.updateProfile(updates)

    expect(userStore.currentUser).toEqual({
      id: 1,
      phone: '13800138000',
      nickname: '新昵称',
      avatar: '/new-avatar.jpg'
    })
  })

  it('validates loading states during async operations', async () => {
    const userStore = useUserStore()
    
    // 模拟慢速API响应
    let resolveLogin: (value: any) => void
    const loginPromise = new Promise(resolve => {
      resolveLogin = resolve
    })
    vi.mocked(authApi.login).mockReturnValue(loginPromise)

    // 开始登录
    const loginCall = userStore.login({
      phone: '13800138000',
      password: 'password123'
    })

    // 检查loading状态
    expect(userStore.loading).toBe(true)

    // 完成登录
    resolveLogin!({
      code: 200,
      data: {
        token: 'test-token',
        user: { id: 1, phone: '13800138000', nickname: '测试' }
      }
    })

    await loginCall

    // 检查loading状态已重置
    expect(userStore.loading).toBe(false)
  })
})