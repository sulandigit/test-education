<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <!-- 头部 -->
        <div class="login-header">
          <div class="brand">
            <img src="/images/logo.png" alt="领课教育" class="logo" @error="handleLogoError">
            <h1>领课教育</h1>
          </div>
          <p class="subtitle">专业的在线学习平台</p>
        </div>

        <!-- 登录表单 -->
        <div class="login-form">
          <el-tabs v-model="activeTab" class="login-tabs">
            <!-- 账号密码登录 -->
            <el-tab-pane label="密码登录" name="password">
              <el-form
                ref="passwordFormRef"
                :model="passwordForm"
                :rules="passwordRules"
                @submit.prevent="handlePasswordLogin"
              >
                <el-form-item prop="username">
                  <el-input
                    v-model="passwordForm.username"
                    placeholder="请输入用户名/手机号/邮箱"
                    size="large"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><User /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item prop="password">
                  <el-input
                    v-model="passwordForm.password"
                    type="password"
                    placeholder="请输入密码"
                    size="large"
                    show-password
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Lock /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item>
                  <div class="form-options">
                    <el-checkbox v-model="rememberMe">记住我</el-checkbox>
                    <el-button text @click="showForgotPassword">忘记密码？</el-button>
                  </div>
                </el-form-item>

                <el-form-item>
                  <el-button
                    type="primary"
                    size="large"
                    :loading="loading"
                    @click="handlePasswordLogin"
                    block
                  >
                    登录
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>

            <!-- 短信验证码登录 -->
            <el-tab-pane label="验证码登录" name="sms">
              <el-form
                ref="smsFormRef"
                :model="smsForm"
                :rules="smsRules"
                @submit.prevent="handleSmsLogin"
              >
                <el-form-item prop="mobile">
                  <el-input
                    v-model="smsForm.mobile"
                    placeholder="请输入手机号"
                    size="large"
                    clearable
                  >
                    <template #prefix>
                      <el-icon><Iphone /></el-icon>
                    </template>
                  </el-input>
                </el-form-item>

                <el-form-item prop="code">
                  <div class="code-input-group">
                    <el-input
                      v-model="smsForm.code"
                      placeholder="请输入验证码"
                      size="large"
                      clearable
                    >
                      <template #prefix>
                        <el-icon><ChatDotSquare /></el-icon>
                      </template>
                    </el-input>
                    <el-button
                      :disabled="codeCountdown > 0 || !isValidMobile(smsForm.mobile)"
                      :loading="sendingCode"
                      @click="sendSmsCode"
                      class="code-btn"
                    >
                      {{ codeCountdown > 0 ? `${codeCountdown}s后重发` : '发送验证码' }}
                    </el-button>
                  </div>
                </el-form-item>

                <el-form-item>
                  <el-button
                    type="primary"
                    size="large"
                    :loading="loading"
                    @click="handleSmsLogin"
                    block
                  >
                    登录
                  </el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>

          <!-- 其他登录方式 -->
          <div class="other-login">
            <div class="divider">
              <span>其他登录方式</span>
            </div>
            <div class="social-login">
              <el-button circle size="large" @click="loginWithWechat">
                <el-icon><ChatDotRound /></el-icon>
              </el-button>
              <el-button circle size="large" @click="loginWithQQ">
                <el-icon><UserFilled /></el-icon>
              </el-button>
            </div>
          </div>

          <!-- 注册链接 -->
          <div class="register-link">
            <span>还没有账号？</span>
            <NuxtLink to="/register" class="link">立即注册</NuxtLink>
          </div>
        </div>
      </div>

      <!-- 背景装饰 -->
      <div class="login-bg">
        <div class="bg-shape shape-1"></div>
        <div class="bg-shape shape-2"></div>
        <div class="bg-shape shape-3"></div>
      </div>
    </div>

    <!-- 忘记密码对话框 -->
    <el-dialog
      v-model="showForgotDialog"
      title="忘记密码"
      width="400px"
      :close-on-click-modal="false"
    >
      <el-form
        ref="forgotFormRef"
        :model="forgotForm"
        :rules="forgotRules"
      >
        <el-form-item prop="mobile">
          <el-input
            v-model="forgotForm.mobile"
            placeholder="请输入注册手机号"
            size="large"
          >
            <template #prefix>
              <el-icon><Iphone /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="code">
          <div class="code-input-group">
            <el-input
              v-model="forgotForm.code"
              placeholder="请输入验证码"
              size="large"
            >
              <template #prefix>
                <el-icon><ChatDotSquare /></el-icon>
              </template>
            </el-input>
            <el-button
              :disabled="forgotCodeCountdown > 0 || !isValidMobile(forgotForm.mobile)"
              :loading="sendingForgotCode"
              @click="sendForgotCode"
              class="code-btn"
            >
              {{ forgotCodeCountdown > 0 ? `${forgotCodeCountdown}s后重发` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>

        <el-form-item prop="newPassword">
          <el-input
            v-model="forgotForm.newPassword"
            type="password"
            placeholder="请输入新密码"
            size="large"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="showForgotDialog = false">取消</el-button>
          <el-button
            type="primary"
            :loading="resetLoading"
            @click="handleResetPassword"
          >
            重置密码
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {
  User,
  Lock,
  Iphone,
  ChatDotSquare,
  ChatDotRound,
  UserFilled
} from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { LoginRequest } from '@/types'
import { authApi } from '@/api/auth'

// 页面元数据
definePageMeta({
  title: '用户登录 - 领课教育',
  layout: false
})

// 响应式数据
const loading = ref(false)
const sendingCode = ref(false)
const sendingForgotCode = ref(false)
const resetLoading = ref(false)
const activeTab = ref('password')
const rememberMe = ref(false)
const codeCountdown = ref(0)
const forgotCodeCountdown = ref(0)
const showForgotDialog = ref(false)

// 表单引用
const passwordFormRef = ref<FormInstance>()
const smsFormRef = ref<FormInstance>()
const forgotFormRef = ref<FormInstance>()

// 密码登录表单
const passwordForm = ref({
  username: '',
  password: ''
})

// 短信登录表单
const smsForm = ref({
  mobile: '',
  code: ''
})

// 忘记密码表单
const forgotForm = ref({
  mobile: '',
  code: '',
  newPassword: ''
})

// 表单验证规则
const passwordRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 50, message: '用户名长度为2-50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ]
}

const smsRules: FormRules = {
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ]
}

const forgotRules: FormRules = {
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ]
}

// 方法
const isValidMobile = (mobile: string): boolean => {
  return /^1[3-9]\d{9}$/.test(mobile)
}

const handlePasswordLogin = async () => {
  if (!passwordFormRef.value) return

  try {
    await passwordFormRef.value.validate()
    loading.value = true

    const loginData: LoginRequest = {
      username: passwordForm.value.username,
      password: passwordForm.value.password
    }

    const response = await authApi.login(loginData)

    // 保存登录信息
    if (rememberMe.value) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('userInfo', JSON.stringify(response.data.user))
    } else {
      sessionStorage.setItem('token', response.data.token)
      sessionStorage.setItem('userInfo', JSON.stringify(response.data.user))
    }

    ElMessage.success('登录成功')

    // 跳转到目标页面或首页
    const route = useRoute()
    const redirect = route.query.redirect as string
    await navigateTo(redirect || '/')
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '登录失败')
  } finally {
    loading.value = false
  }
}

const handleSmsLogin = async () => {
  if (!smsFormRef.value) return

  try {
    await smsFormRef.value.validate()
    loading.value = true

    // 实现短信验证码登录逻辑
    ElMessage.success('短信登录功能开发中...')
  } catch (error) {
    ElMessage.error('登录失败')
  } finally {
    loading.value = false
  }
}

const sendSmsCode = async () => {
  if (!isValidMobile(smsForm.value.mobile)) {
    ElMessage.error('请输入正确的手机号')
    return
  }

  try {
    sendingCode.value = true
    await authApi.sendSmsCode(smsForm.value.mobile)
    
    ElMessage.success('验证码已发送')
    startCountdown()
  } catch (error) {
    ElMessage.error('发送验证码失败')
  } finally {
    sendingCode.value = false
  }
}

const sendForgotCode = async () => {
  if (!isValidMobile(forgotForm.value.mobile)) {
    ElMessage.error('请输入正确的手机号')
    return
  }

  try {
    sendingForgotCode.value = true
    await authApi.sendSmsCode(forgotForm.value.mobile)
    
    ElMessage.success('验证码已发送')
    startForgotCountdown()
  } catch (error) {
    ElMessage.error('发送验证码失败')
  } finally {
    sendingForgotCode.value = false
  }
}

const startCountdown = () => {
  codeCountdown.value = 60
  const timer = setInterval(() => {
    codeCountdown.value--
    if (codeCountdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

const startForgotCountdown = () => {
  forgotCodeCountdown.value = 60
  const timer = setInterval(() => {
    forgotCodeCountdown.value--
    if (forgotCodeCountdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

const showForgotPassword = () => {
  showForgotDialog.value = true
}

const handleResetPassword = async () => {
  if (!forgotFormRef.value) return

  try {
    await forgotFormRef.value.validate()
    resetLoading.value = true

    await authApi.resetPasswordByMobile({
      mobile: forgotForm.value.mobile,
      code: forgotForm.value.code,
      newPassword: forgotForm.value.newPassword
    })

    ElMessage.success('密码重置成功')
    showForgotDialog.value = false
    
    // 清空忘记密码表单
    forgotForm.value = {
      mobile: '',
      code: '',
      newPassword: ''
    }
  } catch (error) {
    ElMessage.error('密码重置失败')
  } finally {
    resetLoading.value = false
  }
}

const loginWithWechat = () => {
  ElMessage.info('微信登录功能开发中...')
}

const loginWithQQ = () => {
  ElMessage.info('QQ登录功能开发中...')
}

const handleLogoError = (e: Event) => {
  const target = e.target as HTMLImageElement
  target.style.display = 'none'
}

// 页面离开时清理定时器
onUnmounted(() => {
  codeCountdown.value = 0
  forgotCodeCountdown.value = 0
})
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-lg;
  position: relative;
  overflow: hidden;

  .login-container {
    position: relative;
    z-index: 1;
  }

  .login-card {
    width: 100%;
    max-width: 400px;
    background: white;
    border-radius: $border-radius-large;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    overflow: hidden;
  }

  .login-header {
    text-align: center;
    padding: $spacing-xl $spacing-lg $spacing-lg;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;

    .brand {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: $spacing-sm;
      margin-bottom: $spacing-sm;

      .logo {
        height: 40px;
        width: auto;
      }

      h1 {
        font-size: 1.5rem;
        font-weight: 600;
        margin: 0;
      }
    }

    .subtitle {
      font-size: $font-size-sm;
      opacity: 0.9;
      margin: 0;
    }
  }

  .login-form {
    padding: $spacing-xl $spacing-lg;

    .login-tabs {
      margin-bottom: $spacing-lg;

      :deep(.el-tabs__header) {
        margin-bottom: $spacing-lg;
      }

      :deep(.el-tabs__nav-wrap::after) {
        display: none;
      }
    }

    .form-options {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;
    }

    .code-input-group {
      display: flex;
      gap: $spacing-sm;

      .el-input {
        flex: 1;
      }

      .code-btn {
        white-space: nowrap;
        min-width: 100px;
      }
    }

    .other-login {
      margin-top: $spacing-xl;

      .divider {
        position: relative;
        text-align: center;
        margin-bottom: $spacing-lg;

        &::before {
          content: '';
          position: absolute;
          top: 50%;
          left: 0;
          right: 0;
          height: 1px;
          background: $border-base;
        }

        span {
          background: white;
          padding: 0 $spacing-md;
          color: $text-secondary;
          font-size: $font-size-sm;
        }
      }

      .social-login {
        display: flex;
        justify-content: center;
        gap: $spacing-lg;

        .el-button {
          width: 50px;
          height: 50px;
          border-radius: 50%;
          border: 1px solid $border-light;

          &:hover {
            border-color: $primary-color;
            color: $primary-color;
          }
        }
      }
    }

    .register-link {
      text-align: center;
      margin-top: $spacing-lg;
      color: $text-secondary;
      font-size: $font-size-sm;

      .link {
        color: $primary-color;
        text-decoration: none;
        font-weight: 500;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }

  .login-bg {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 0;
    pointer-events: none;

    .bg-shape {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      animation: float 6s ease-in-out infinite;

      &.shape-1 {
        width: 200px;
        height: 200px;
        top: 10%;
        left: -5%;
        animation-delay: 0s;
      }

      &.shape-2 {
        width: 150px;
        height: 150px;
        top: 60%;
        right: -5%;
        animation-delay: 2s;
      }

      &.shape-3 {
        width: 100px;
        height: 100px;
        bottom: 20%;
        left: 10%;
        animation-delay: 4s;
      }
    }
  }

  .dialog-footer {
    text-align: right;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-20px);
  }
}

// 响应式设计
@media (max-width: $breakpoint-sm) {
  .login-page {
    padding: $spacing-md;

    .login-card {
      max-width: 100%;
    }

    .login-form {
      padding: $spacing-lg $spacing-md;

      .code-input-group {
        flex-direction: column;

        .code-btn {
          min-width: auto;
        }
      }
    }
  }
}
</style>