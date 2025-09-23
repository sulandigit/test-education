<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <!-- 头部 -->
        <div class="register-header">
          <div class="brand">
            <img src="/images/logo.png" alt="领课教育" class="logo" @error="handleLogoError">
            <h1>加入领课教育</h1>
          </div>
          <p class="subtitle">开启您的在线学习之旅</p>
        </div>

        <!-- 注册表单 -->
        <div class="register-form">
          <el-form
            ref="registerFormRef"
            :model="registerForm"
            :rules="registerRules"
            @submit.prevent="handleRegister"
          >
            <el-form-item prop="username">
              <el-input
                v-model="registerForm.username"
                placeholder="请输入用户名"
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><User /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="nickname">
              <el-input
                v-model="registerForm.nickname"
                placeholder="请输入昵称"
                size="large"
                clearable
              >
                <template #prefix>
                  <el-icon><Avatar /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="mobile">
              <el-input
                v-model="registerForm.mobile"
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
                  v-model="registerForm.code"
                  placeholder="请输入验证码"
                  size="large"
                  clearable
                >
                  <template #prefix>
                    <el-icon><ChatDotSquare /></el-icon>
                  </template>
                </el-input>
                <el-button
                  :disabled="codeCountdown > 0 || !isValidMobile(registerForm.mobile)"
                  :loading="sendingCode"
                  @click="sendSmsCode"
                  class="code-btn"
                >
                  {{ codeCountdown > 0 ? `${codeCountdown}s后重发` : '发送验证码' }}
                </el-button>
              </div>
            </el-form-item>

            <el-form-item prop="password">
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="请设置密码"
                size="large"
                show-password
                clearable
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="confirmPassword">
              <el-input
                v-model="registerForm.confirmPassword"
                type="password"
                placeholder="请确认密码"
                size="large"
                show-password
                clearable
              >
                <template #prefix>
                  <el-icon><Lock /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <el-form-item prop="agreement">
              <el-checkbox v-model="registerForm.agreement">
                我已阅读并同意
                <el-button text @click="showUserAgreement">《用户协议》</el-button>
                和
                <el-button text @click="showPrivacyPolicy">《隐私政策》</el-button>
              </el-checkbox>
            </el-form-item>

            <el-form-item>
              <el-button
                type="primary"
                size="large"
                :loading="loading"
                @click="handleRegister"
                block
              >
                立即注册
              </el-button>
            </el-form-item>
          </el-form>

          <!-- 其他注册方式 -->
          <div class="other-register">
            <div class="divider">
              <span>快速注册</span>
            </div>
            <div class="social-register">
              <el-button circle size="large" @click="registerWithWechat">
                <el-icon><ChatDotRound /></el-icon>
              </el-button>
              <el-button circle size="large" @click="registerWithQQ">
                <el-icon><UserFilled /></el-icon>
              </el-button>
            </div>
          </div>

          <!-- 登录链接 -->
          <div class="login-link">
            <span>已有账号？</span>
            <NuxtLink to="/login" class="link">立即登录</NuxtLink>
          </div>
        </div>
      </div>

      <!-- 背景装饰 -->
      <div class="register-bg">
        <div class="bg-shape shape-1"></div>
        <div class="bg-shape shape-2"></div>
        <div class="bg-shape shape-3"></div>
        <div class="bg-shape shape-4"></div>
      </div>
    </div>

    <!-- 协议对话框 -->
    <el-dialog
      v-model="showAgreementDialog"
      :title="dialogTitle"
      width="60%"
      :close-on-click-modal="false"
    >
      <div class="agreement-content">
        <div v-if="currentAgreement === 'user'" v-html="userAgreementContent"></div>
        <div v-else v-html="privacyPolicyContent"></div>
      </div>
      <template #footer>
        <el-button @click="showAgreementDialog = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {
  User,
  Avatar,
  Iphone,
  ChatDotSquare,
  Lock,
  ChatDotRound,
  UserFilled
} from '@element-plus/icons-vue'
import type { FormInstance, FormRules } from 'element-plus'
import type { RegisterRequest } from '@/types'
import { authApi } from '@/api/auth'

// 页面元数据
definePageMeta({
  title: '用户注册 - 领课教育',
  layout: false,
  middleware: 'guest'
})

// 响应式数据
const loading = ref(false)
const sendingCode = ref(false)
const codeCountdown = ref(0)
const showAgreementDialog = ref(false)
const currentAgreement = ref<'user' | 'privacy'>('user')

// 表单引用
const registerFormRef = ref<FormInstance>()

// 注册表单
const registerForm = ref({
  username: '',
  nickname: '',
  mobile: '',
  code: '',
  password: '',
  confirmPassword: '',
  agreement: false
})

// 表单验证规则
const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 10, message: '昵称长度为2-10个字符', trigger: 'blur' }
  ],
  mobile: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请设置密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' },
    { 
      pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d@$!%*?&]{6,}$/,
      message: '密码至少包含一个大写字母、一个小写字母和一个数字',
      trigger: 'blur'
    }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== registerForm.value.password) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ],
  agreement: [
    {
      validator: (rule, value, callback) => {
        if (!value) {
          callback(new Error('请阅读并同意用户协议和隐私政策'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  return currentAgreement.value === 'user' ? '用户协议' : '隐私政策'
})

const userAgreementContent = ref(`
  <h3>用户协议</h3>
  <p>欢迎使用领课教育平台服务！</p>
  <h4>1. 服务条款的确认和接纳</h4>
  <p>领课教育平台各项电子服务的所有权和运作权归领课教育所有。用户同意所有注册协议条款并完成注册程序，才能成为领课教育的正式用户。</p>
  <h4>2. 用户账号</h4>
  <p>用户有义务保证密码和账号的安全，用户利用该密码和账号所进行的一切活动引起的任何损失或损害，由用户自行承担全部责任。</p>
  <h4>3. 隐私保护</h4>
  <p>领课教育非常重视对用户个人隐私的保护，在未获得许可之前，不会将用户的任何信息提供给第三方。</p>
  <h4>4. 用户行为</h4>
  <p>用户在使用领课教育服务过程中，必须遵循国家相关法律法规，不得利用领课教育服务进行任何违法或不当行为。</p>
`)

const privacyPolicyContent = ref(`
  <h3>隐私政策</h3>
  <p>本隐私政策描述了当您使用我们的服务时，我们如何收集、使用和共享您的信息。</p>
  <h4>1. 信息收集</h4>
  <p>我们收集您在注册账户、使用服务时提供的信息，包括但不限于姓名、手机号、邮箱地址等。</p>
  <h4>2. 信息使用</h4>
  <p>我们使用收集的信息来提供、维护和改进我们的服务，与您沟通，以及确保服务的安全性。</p>
  <h4>3. 信息共享</h4>
  <p>我们不会将您的个人信息出售、出租或共享给第三方，除非获得您的明确同意或法律要求。</p>
  <h4>4. 信息安全</h4>
  <p>我们采取合理的安全措施来保护您的个人信息免受未经授权的访问、使用或披露。</p>
`)

// 方法
const isValidMobile = (mobile: string): boolean => {
  return /^1[3-9]\d{9}$/.test(mobile)
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  try {
    await registerFormRef.value.validate()
    loading.value = true

    const registerData: RegisterRequest = {
      username: registerForm.value.username,
      password: registerForm.value.password,
      nickname: registerForm.value.nickname,
      mobile: registerForm.value.mobile,
      code: registerForm.value.code
    }

    await authApi.register(registerData)

    ElMessage.success('注册成功！请登录您的账户')
    
    // 跳转到登录页面
    const route = useRoute()
    const redirect = route.query.redirect as string
    await navigateTo(`/login${redirect ? `?redirect=${redirect}` : ''}`)
  } catch (error) {
    ElMessage.error(error instanceof Error ? error.message : '注册失败')
  } finally {
    loading.value = false
  }
}

const sendSmsCode = async () => {
  if (!isValidMobile(registerForm.value.mobile)) {
    ElMessage.error('请输入正确的手机号')
    return
  }

  try {
    sendingCode.value = true
    await authApi.sendSmsCode(registerForm.value.mobile)
    
    ElMessage.success('验证码已发送')
    startCountdown()
  } catch (error) {
    ElMessage.error('发送验证码失败')
  } finally {
    sendingCode.value = false
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

const showUserAgreement = () => {
  currentAgreement.value = 'user'
  showAgreementDialog.value = true
}

const showPrivacyPolicy = () => {
  currentAgreement.value = 'privacy'
  showAgreementDialog.value = true
}

const registerWithWechat = () => {
  ElMessage.info('微信注册功能开发中...')
}

const registerWithQQ = () => {
  ElMessage.info('QQ注册功能开发中...')
}

const handleLogoError = (e: Event) => {
  const target = e.target as HTMLImageElement
  target.style.display = 'none'
}

// 页面离开时清理定时器
onUnmounted(() => {
  codeCountdown.value = 0
})
</script>

<style lang="scss" scoped>
.register-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: $spacing-lg;
  position: relative;
  overflow: hidden;

  .register-container {
    position: relative;
    z-index: 1;
  }

  .register-card {
    width: 100%;
    max-width: 480px;
    background: white;
    border-radius: $border-radius-large;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    overflow: hidden;
  }

  .register-header {
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

  .register-form {
    padding: $spacing-xl $spacing-lg;

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

    .other-register {
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

      .social-register {
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

    .login-link {
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

  .register-bg {
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
      animation: float 8s ease-in-out infinite;

      &.shape-1 {
        width: 150px;
        height: 150px;
        top: 15%;
        left: -5%;
        animation-delay: 0s;
      }

      &.shape-2 {
        width: 100px;
        height: 100px;
        top: 70%;
        right: -3%;
        animation-delay: 2s;
      }

      &.shape-3 {
        width: 80px;
        height: 80px;
        bottom: 30%;
        left: 15%;
        animation-delay: 4s;
      }

      &.shape-4 {
        width: 120px;
        height: 120px;
        top: 50%;
        right: 20%;
        animation-delay: 6s;
      }
    }
  }

  .agreement-content {
    max-height: 400px;
    overflow-y: auto;
    padding: $spacing-md;
    font-size: $font-size-sm;
    line-height: 1.6;

    h3 {
      color: $primary-color;
      margin-bottom: $spacing-md;
    }

    h4 {
      color: $text-primary;
      margin: $spacing-md 0 $spacing-sm;
    }

    p {
      color: $text-regular;
      margin-bottom: $spacing-sm;
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  33% {
    transform: translateY(-10px) rotate(120deg);
  }
  66% {
    transform: translateY(10px) rotate(240deg);
  }
}

// 响应式设计
@media (max-width: $breakpoint-sm) {
  .register-page {
    padding: $spacing-md;

    .register-card {
      max-width: 100%;
    }

    .register-form {
      padding: $spacing-lg $spacing-md;

      .code-input-group {
        flex-direction: column;

        .code-btn {
          min-width: auto;
        }
      }
    }

    .agreement-content {
      max-height: 300px;
    }
  }
}
</style>