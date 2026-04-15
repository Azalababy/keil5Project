<template>
  <div class="register">
    <div class="register-container">
      <h2 class="register-title">用户注册</h2>
      <form @submit.prevent="handleRegister" class="register-form">
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
        <div class="form-group">
          <label for="name" class="form-label">姓名</label>
          <input 
            type="text" 
            id="name" 
            v-model="form.name" 
            class="form-input" 
            required
          />
        </div>
        <div class="form-group">
          <label for="phone" class="form-label">手机号</label>
          <input 
            type="tel" 
            id="phone" 
            v-model="form.phone" 
            class="form-input" 
            required
          />
        </div>
        <div class="form-group">
          <label for="address" class="form-label">地址</label>
          <input 
            type="text" 
            id="address" 
            v-model="form.address" 
            class="form-input" 
            required
          />
        </div>
        <button type="submit" class="register-btn" :disabled="isLoading">
          {{ isLoading ? '注册中...' : '注册' }}
        </button>
        <p class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
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
  password: '',
  name: '',
  phone: '',
  address: ''
})

const handleRegister = async () => {
  isLoading.value = true
  try {
    const response = await axios.post('/api/users', form.value)
    if (response.status === 201) {
      alert('注册成功，请登录')
      router.push('/login')
    }
  } catch (error) {
    console.error('Registration failed:', error)
    alert('注册失败，请稍后重试')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 70vh;
}

.register-container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 2rem;
  width: 100%;
  max-width: 400px;
}

.register-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  text-align: center;
  color: #333;
}

.register-form {
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

.register-btn {
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

.register-btn:hover {
  background-color: #ee5a52;
}

.register-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  font-size: 0.9rem;
  color: #666;
  margin-top: 1rem;
}

.login-link a {
  color: #ff6b6b;
  text-decoration: none;
  font-weight: 500;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>
