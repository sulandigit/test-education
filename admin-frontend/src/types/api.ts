// 通用响应类型
export interface ApiResponse<T = any> {
  success: boolean
  code: number
  msg: string
  data: T
}

// 分页数据类型
export interface PageData<T = any> {
  list: T[]
  total: number
  current: number
  size: number
}

// 登录请求
export interface LoginRequest {
  mobile: string
  password: string
}

// 登录响应
export interface LoginResponse {
  token: string
  adminUser: {
    id: number
    mobile: string
    realName: string
    nickname: string
    remark: string
    statusId: number
    sortNo: number
    gmtCreate: string
    gmtModified: string
  }
}

// 用户信息
export interface UserInfo {
  id: number
  mobile: string
  realName: string
  nickname: string
  remark: string
  statusId: number
  sortNo: number
  gmtCreate: string
  gmtModified: string
}

// 菜单项
export interface MenuItem {
  id: number
  parentId: number
  menuNme: string
  menuUrl: string
  authValue: string
  icon: string
  remarks: string
  statusId: number
  sortNo: number
  isShow: number
  targetId: number
  children?: MenuItem[]
}