import request from '@/utils/request'
import type { 
  User, 
  LoginRequest, 
  RegisterRequest, 
  ApiResponse 
} from '@/types'

/**
 * 用户认证相关API
 */
export const authApi = {
  /**
   * 用户登录
   */
  login(data: LoginRequest): Promise<ApiResponse<{ user: User; token: string }>> {
    return request.post('/user/api/users/login', data)
  },

  /**
   * 用户注册
   */
  register(data: RegisterRequest): Promise<ApiResponse<User>> {
    return request.post('/user/api/users/register', data)
  },

  /**
   * 发送短信验证码
   */
  sendSmsCode(mobile: string): Promise<ApiResponse<void>> {
    return request.post('/user/api/users/sendCode', { mobile })
  },

  /**
   * 获取当前用户信息
   */
  getCurrentUser(): Promise<ApiResponse<User>> {
    return request.get('/user/auth/users/view')
  },

  /**
   * 更新用户信息
   */
  updateUser(data: Partial<User>): Promise<ApiResponse<User>> {
    return request.post('/user/auth/users/edit', data)
  },

  /**
   * 用户退出登录
   */
  logout(): Promise<ApiResponse<void>> {
    return request.post('/user/auth/users/logout')
  },

  /**
   * 修改密码
   */
  changePassword(data: {
    oldPassword: string
    newPassword: string
  }): Promise<ApiResponse<void>> {
    return request.post('/user/auth/users/password', data)
  },

  /**
   * 忘记密码 - 通过手机号重置
   */
  resetPasswordByMobile(data: {
    mobile: string
    code: string
    newPassword: string
  }): Promise<ApiResponse<void>> {
    return request.post('/user/api/users/resetPassword', data)
  },

  /**
   * 绑定手机号
   */
  bindMobile(data: {
    mobile: string
    code: string
  }): Promise<ApiResponse<void>> {
    return request.post('/user/auth/users/bindMobile', data)
  },

  /**
   * 上传头像
   */
  uploadAvatar(file: File): Promise<ApiResponse<{ url: string }>> {
    const formData = new FormData()
    formData.append('file', file)
    return request.upload('/user/auth/users/avatar', formData)
  }
}