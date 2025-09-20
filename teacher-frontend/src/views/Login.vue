<template>
  <div class="login">
    <div class="login-container">
      <div class="login-form">
        <div class="logo-section">
          <el-icon size="48" color="#409eff"><Reading /></el-icon>
          <h1>讲师端管理系统</h1>
          <p>欢迎登录教育管理平台</p>
        </div>

        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="form-content"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>

          <el-form-item>
            <div class="form-options">
              <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
              <el-button type="text" class="forgot-password">忘记密码？</el-button>
            </div>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              style="width: 100%"
              :loading="logging"
              @click="handleLogin"
            >
              登录
            </el-button>
          </el-form-item>
        </el-form>

        <div class="demo-info">
          <el-alert
            title="演示账号"
            type="info"
            :closable="false"
            show-icon
          >
            <template #default>
              <p><strong>用户名：</strong>teacher</p>
              <p><strong>密码：</strong>123456</p>
            </template>
          </el-alert>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const loginFormRef = ref(null)
    const logging = ref(false)

    const loginForm = reactive({
      username: 'teacher',
      password: '123456',
      rememberMe: false
    })

    const loginRules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ]
    }

    const handleLogin = () => {
      loginFormRef.value?.validate((valid) => {
        if (valid) {
          logging.value = true
          // 模拟登录
          setTimeout(() => {
            if (loginForm.username === 'teacher' && loginForm.password === '123456') {
              ElMessage.success('登录成功')
              router.push('/')
            } else {
              ElMessage.error('用户名或密码错误')
            }
            logging.value = false
          }, 1000)
        }
      })
    }

    return {
      loginFormRef,
      logging,
      loginForm,
      loginRules,
      handleLogin
    }
  }
}
</script>

<style lang="scss" scoped>
.login {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 420px;
}

.login-form {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);

  .logo-section {
    text-align: center;
    margin-bottom: 40px;

    h1 {
      margin: 16px 0 8px;
      color: #2c3e50;
      font-size: 28px;
      font-weight: 600;
    }

    p {
      margin: 0;
      color: #8492a6;
      font-size: 14px;
    }
  }

  .form-content {
    .form-options {
      display: flex;
      justify-content: space-between;
      align-items: center;
      width: 100%;

      .forgot-password {
        font-size: 14px;
        padding: 0;
      }
    }
  }

  .demo-info {
    margin-top: 24px;

    :deep(.el-alert__content) {
      p {
        margin: 4px 0;
        font-size: 12px;
      }

      p:last-child {
        margin-bottom: 0;
      }
    }
  }
}

@media (max-width: 480px) {
  .login-form {
    margin: 0 16px;
    padding: 30px 24px;
  }
}
</style>