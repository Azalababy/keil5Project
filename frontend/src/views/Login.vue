<template>
  <div class="login">
    <div class="login-container">
      <h2 class="login-title">用户登录</h2>
      <form @submit.prevent="handleLogin" class="login-form">
        <div class="form-group">
          <label for="username" class="form-label">用户名</label>
          <input 
            type="text" 
            id="username" 
            v-model="form.username" 
            class="form-input" 
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
            required
          />
        </div>
        <button type="submit" class="login-btn" :disabled="isLoading">
          {{ isLoading ? '登录中...' : '登录' }}
        </button>
        <p class="register-link">
          还没有账号？<router-link to="/register">立即注册</router-link>
        </p>
      </form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const isLoading = ref(false)
const form = ref({
  username: '',
  password: ''
})

const handleLogin = async () => {
  isLoading.value = true
  try {
    // 这里简化处理，实际应该调用登录API
    // 暂时使用用户列表API模拟登录
    const response = await axios.get('/api/users')
    const user = response.data.find((u: any) => u.username === form.value.username && u.password === form.value.password)
    
    if (user) {
      localStorage.setItem('token', 'mock-token')
      localStorage.setItem('username', user.name)
      router.push('/')
    } else {
      alert('用户名或密码错误')
    }
  } catch (error) {
    console.error('Login failed:', error)
    alert('登录失败，请稍后重试')
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
  max-width: 400px;
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
</style>
