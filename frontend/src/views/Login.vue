<template>
  <div class="login">
    <div class="login-container">
      <!-- 登录类型切换 -->
      <div class="login-tabs">
        <div 
          class="tab" 
          :class="{ active: loginType === 'user' }"
          @click="loginType = 'user'"
        >
          用户登录
        </div>
        <div 
          class="tab" 
          :class="{ active: loginType === 'shop' }"
          @click="loginType = 'shop'"
        >
          商家登录
        </div>
        <div 
          class="tab" 
          :class="{ active: loginType === 'rider' }"
          @click="loginType = 'rider'"
        >
          骑手登录
        </div>
      </div>

      <h2 class="login-title">{{ getLoginTitle() }}</h2>
      
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="account" class="form-label">{{ loginType === 'user' ? '用户名' : '手机号' }}</label>
          <input 
            type="text" 
            id="account" 
            v-model="form.account" 
            class="form-input" 
            :placeholder="loginType === 'user' ? '请输入用户名' : '请输入手机号'"
            required
          />
        </div>
        <div class="form-group">
          <label for="password" class="form-label">密码</label>
          <input 
            type="password" 
            id="password" 
            v-model="form.password" 
            class="form-input" 
            placeholder="请输入密码"
            required
          />
        </div>
        <button type="submit" class="login-btn" :disabled="isLoading">
          {{ isLoading ? '登录中...' : '登录' }}
        </button>
        <p class="register-link">
          还没有账号？<router-link :to="{ path: '/register', query: { type: loginType } }">立即注册</router-link>
        </p>
      </form>
    </div>
    
    <!-- 登录成功提示框 -->
    <div v-if="showSuccessModal" class="success-modal-overlay">
      <div class="success-modal">
        <div class="success-content">
          <div class="success-icon">✓</div>
          <h3>登录成功</h3>
          <p>即将跳转到首页...</p>
        </div>
        <button class="success-confirm-btn" @click="handleSuccessConfirm">确认 ({{ countdown }}秒)</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const isLoading = ref(false)
const loginType = ref<'user' | 'shop' | 'rider'>('user')
const form = ref({
  account: '',
  password: ''
})
const showSuccessModal = ref(false)
const countdown = ref(3)
let countdownTimer: ReturnType<typeof setInterval> | null = null

const getLoginTitle = () => {
  switch (loginType.value) {
    case 'user': return '用户登录'
    case 'shop': return '商家登录'
    case 'rider': return '骑手登录'
    default: return '登录'
  }
}

const startCountdown = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
  countdown.value = 3
  countdownTimer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      handleSuccessConfirm()
    }
  }, 1000)
}

const clearCountdown = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

const handleSuccessConfirm = () => {
  clearCountdown()
  showSuccessModal.value = false
  if (loginType.value === 'user') {
    router.push('/')
  } else if (loginType.value === 'shop') {
    router.push('/shop-admin')
  } else {
    router.push('/rider-admin')
  }
}

const handleLogin = async () => {
  isLoading.value = true
  try {
    if (loginType.value === 'user') {
      // 用户登录
      const response = await axios.post('/api/users/login', {
        username: form.value.account,
        password: form.value.password
      })
      
      if (response.data.success) {
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('user', JSON.stringify(response.data.user))
        localStorage.setItem('role', 'user')
        localStorage.setItem('loginTime', String(Date.now()))
        showSuccessModal.value = true
        startCountdown()
      } else {
        alert(response.data.message || '用户名或密码错误')
      }
    } else if (loginType.value === 'shop') {
      // 商家登录
      const response = await axios.post('/api/shops/login', {
        phone: form.value.account,
        password: form.value.password
      })
      
      if (response.data.success) {
        localStorage.setItem('token', 'mock-token')
        localStorage.setItem('shop', JSON.stringify(response.data.shop))
        localStorage.setItem('role', 'shop')
        localStorage.setItem('loginTime', String(Date.now()))
        showSuccessModal.value = true
        startCountdown()
      } else {
        alert(response.data.message)
      }
    } else {
      // 骑手登录
      const response = await axios.post('/api/riders/login', {
        phone: form.value.account,
        password: form.value.password
      })
      
      if (response.data.success) {
        localStorage.setItem('token', 'mock-token')
        localStorage.setItem('rider', JSON.stringify(response.data.rider))
        localStorage.setItem('role', 'rider')
        localStorage.setItem('loginTime', String(Date.now()))
        showSuccessModal.value = true
        startCountdown()
      } else {
        alert(response.data.message)
      }
    }
  } catch (error: any) {
    console.error('Login failed:', error)
    if (error.response && error.response.data && error.response.data.message) {
      alert(error.response.data.message)
    } else {
      alert('登录失败，请稍后重试')
    }
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.login {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.login-container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 2rem;
  width: 100%;
  max-width: 420px;
}

.login-tabs {
  display: flex;
  margin-bottom: 1.5rem;
  background: #f5f5f5;
  border-radius: 6px;
  padding: 4px;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 0.7rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.3s ease;
  color: #666;
}

.tab.active {
  background: white;
  color: #ff6b6b;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.tab:hover {
  color: #ff6b6b;
}

.login-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  text-align: center;
  color: #333;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.form-label {
  font-size: 0.9rem;
  font-weight: 500;
  color: #555;
}

.form-input {
  padding: 0.8rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #ff6b6b;
  box-shadow: 0 0 0 2px rgba(255, 107, 107, 0.1);
}

.login-btn {
  background-color: #ff6b6b;
  color: white;
  border: none;
  padding: 0.8rem;
  border-radius: 4px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 0.5rem;
}

.login-btn:hover {
  background-color: #ee5a52;
}

.login-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.register-link {
  text-align: center;
  font-size: 0.9rem;
  color: #666;
  margin-top: 1rem;
}

.register-link a {
  color: #ff6b6b;
  text-decoration: none;
  font-weight: 500;
}

.register-link a:hover {
  text-decoration: underline;
}

/* 成功提示框样式 */
.success-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.success-modal {
  background-color: #4a90d9;
  border-radius: 12px;
  padding: 2rem;
  width: 100%;
  max-width: 360px;
  text-align: center;
  color: white;
  position: relative;
}

.success-content {
  margin-bottom: 1.5rem;
}

.success-icon {
  width: 60px;
  height: 60px;
  background-color: white;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto 1rem;
  font-size: 2.5rem;
  color: #4a90d9;
}

.success-content h3 {
  margin: 0 0 0.5rem;
  font-size: 1.5rem;
}

.success-content p {
  margin: 0;
  opacity: 0.9;
}

.success-confirm-btn {
  background-color: white;
  color: #4a90d9;
  border: none;
  padding: 0.75rem 2rem;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.success-confirm-btn:hover {
  background-color: #f0f0f0;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .login-content {
    padding: 1rem;
    min-height: 60vh;
  }
  
  .login-container {
    padding: 1.2rem;
    max-width: 100%;
    margin: 0 0.5rem;
  }
  
  .login-title {
    font-size: 1.3rem;
    margin-bottom: 1rem;
  }
  
  .tab {
    padding: 0.5rem;
    font-size: 0.9rem;
  }
  
  .form-input {
    padding: 0.7rem;
    font-size: 0.9rem;
  }
  
  .login-btn {
    padding: 0.7rem;
    font-size: 0.95rem;
  }
  
  .register-link {
    font-size: 0.85rem;
    margin-top: 0.8rem;
  }
}

@media screen and (max-width: 480px) {
  .login-content {
    padding: 0.5rem;
  }
  
  .login-container {
    padding: 1rem;
  }
  
  .login-title {
    font-size: 1.2rem;
  }
  
  .tab {
    padding: 0.4rem;
    font-size: 0.85rem;
  }
  
  .form-input {
    padding: 0.6rem;
    font-size: 0.85rem;
  }
  
  .login-btn {
    padding: 0.65rem;
    font-size: 0.9rem;
  }
}
</style>