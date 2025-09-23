import request from '@/utils/request'
import type { 
  Order,
  PageResponse, 
  ApiResponse 
} from '@/types'

/**
 * 订单相关API
 */
export const orderApi = {
  /**
   * 获取订单列表
   */
  getOrderList(params: {
    pageCurrent: number
    pageSize: number
    orderStatus?: number
    orderNo?: string
    startDate?: string
    endDate?: string
  }): Promise<ApiResponse<PageResponse<Order>>> {
    return request.post('/user/auth/order/info/page', params)
  },

  /**
   * 获取订单详情
   */
  getOrderDetail(orderNo: string): Promise<ApiResponse<Order & {
    courseInfo: {
      courseName: string
      courseLogo: string
      teacherName: string
    }
    payInfo?: {
      payType: string
      payTime: string
      payAmount: number
      payStatus: number
    }
  }>> {
    return request.get(`/user/auth/order/info/view/${orderNo}`)
  },

  /**
   * 创建订单
   */
  createOrder(data: {
    courseId: number
    payType?: string // 支付方式：alipay, wechat
  }): Promise<ApiResponse<{
    orderNo: string
    payUrl?: string
    qrCode?: string
  }>> {
    return request.post('/user/auth/order/create', data)
  },

  /**
   * 取消订单
   */
  cancelOrder(orderNo: string): Promise<ApiResponse<void>> {
    return request.post('/user/auth/order/cancel', { orderNo })
  },

  /**
   * 删除订单
   */
  deleteOrder(orderNo: string): Promise<ApiResponse<void>> {
    return request.delete(`/user/auth/order/${orderNo}`)
  },

  /**
   * 申请退款
   */
  requestRefund(data: {
    orderNo: string
    refundReason: string
    refundAmount?: number
  }): Promise<ApiResponse<{
    refundNo: string
  }>> {
    return request.post('/user/auth/order/refund/apply', data)
  },

  /**
   * 获取退款详情
   */
  getRefundDetail(refundNo: string): Promise<ApiResponse<{
    refundNo: string
    orderNo: string
    refundAmount: number
    refundReason: string
    refundStatus: number
    refundTime?: string
    rejectReason?: string
    createTime: string
  }>> {
    return request.get(`/user/auth/order/refund/${refundNo}`)
  },

  /**
   * 获取支付方式列表
   */
  getPayMethods(): Promise<ApiResponse<{
    type: string
    name: string
    icon: string
    enabled: boolean
    description?: string
  }[]>> {
    return request.get('/user/api/pay/methods')
  },

  /**
   * 发起支付
   */
  createPayment(data: {
    orderNo: string
    payType: string
    returnUrl?: string
  }): Promise<ApiResponse<{
    payUrl?: string
    qrCode?: string
    payParams?: any
  }>> {
    return request.post('/user/auth/pay/create', data)
  },

  /**
   * 查询支付状态
   */
  checkPaymentStatus(orderNo: string): Promise<ApiResponse<{
    payStatus: number
    payTime?: string
    payAmount?: number
  }>> {
    return request.get(`/user/auth/pay/status/${orderNo}`)
  },

  /**
   * 获取订单统计
   */
  getOrderStats(): Promise<ApiResponse<{
    totalOrders: number
    paidOrders: number
    totalAmount: number
    refundOrders: number
  }>> {
    return request.get('/user/auth/order/stats')
  },

  /**
   * 获取购买记录（用于课程推荐）
   */
  getPurchaseHistory(): Promise<ApiResponse<{
    courseId: number
    courseName: string
    categoryId: number
    categoryName: string
    purchaseTime: string
  }[]>> {
    return request.get('/user/auth/order/purchase/history')
  },

  /**
   * 验证优惠券
   */
  validateCoupon(data: {
    couponCode: string
    courseId: number
  }): Promise<ApiResponse<{
    valid: boolean
    discount: number
    discountType: 'percent' | 'amount'
    minAmount?: number
    maxDiscount?: number
  }>> {
    return request.post('/user/auth/coupon/validate', data)
  },

  /**
   * 获取可用优惠券
   */
  getAvailableCoupons(courseId: number): Promise<ApiResponse<{
    id: number
    name: string
    code: string
    discount: number
    discountType: 'percent' | 'amount'
    minAmount?: number
    maxDiscount?: number
    expireTime: string
  }[]>> {
    return request.get(`/user/auth/coupon/available/${courseId}`)
  },

  /**
   * 应用优惠券
   */
  applyCoupon(data: {
    orderNo: string
    couponCode: string
  }): Promise<ApiResponse<{
    discountAmount: number
    finalAmount: number
  }>> {
    return request.post('/user/auth/order/coupon/apply', data)
  },

  /**
   * 移除优惠券
   */
  removeCoupon(orderNo: string): Promise<ApiResponse<void>> {
    return request.post('/user/auth/order/coupon/remove', { orderNo })
  },

  /**
   * 获取发票信息
   */
  getInvoiceInfo(orderNo: string): Promise<ApiResponse<{
    invoiceTitle: string
    taxNumber?: string
    invoiceType: 'personal' | 'company'
    invoiceStatus: number
    invoiceUrl?: string
  }>> {
    return request.get(`/user/auth/order/invoice/${orderNo}`)
  },

  /**
   * 申请发票
   */
  requestInvoice(data: {
    orderNo: string
    invoiceTitle: string
    taxNumber?: string
    invoiceType: 'personal' | 'company'
    email: string
  }): Promise<ApiResponse<void>> {
    return request.post('/user/auth/order/invoice/apply', data)
  }
}