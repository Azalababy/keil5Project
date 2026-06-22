<template>
  <div class="register">
    <div class="register-container">
      <!-- 注册类型切换 -->
      <div class="register-tabs">
        <div 
          class="tab" 
          :class="{ active: registerType === 'user' }"
          @click="registerType = 'user'"
        >
          用户注册
        </div>
        <div 
          class="tab" 
          :class="{ active: registerType === 'shop' }"
          @click="registerType = 'shop'"
        >
          商家注册
        </div>
        <div 
          class="tab" 
          :class="{ active: registerType === 'rider' }"
          @click="registerType = 'rider'"
        >
          骑手注册
        </div>
      </div>

      <h2 class="register-title">{{ getRegisterTitle() }}</h2>
      
      <form @submit.prevent="handleRegister" class="register-form">
        <!-- 用户注册字段 -->
        <template v-if="registerType === 'user'">
          <div class="form-group">
            <label for="username" class="form-label">用户名</label>
            <input 
              type="text" 
              id="username" 
              v-model="form.username" 
              class="form-input" 
              placeholder="请输入用户名"
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
          <div class="form-group">
            <label for="name" class="form-label">姓名</label>
            <input 
              type="text" 
              id="name" 
              v-model="form.name" 
              class="form-input" 
              placeholder="请输入姓名"
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
              placeholder="请输入手机号"
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
              placeholder="请输入收货地址"
              required
            />
          </div>
        </template>
        
        <!-- 商家注册字段 -->
        <template v-else-if="registerType === 'shop'">
          <div class="form-group">
            <label for="shopName" class="form-label">商家名称</label>
            <input 
              type="text" 
              id="shopName" 
              v-model="form.shopName" 
              class="form-input" 
              placeholder="请输入商家名称"
              required
            />
          </div>
          <div class="form-group">
            <label for="shopPhone" class="form-label">手机号</label>
            <input 
              type="tel" 
              id="shopPhone" 
              v-model="form.shopPhone" 
              class="form-input" 
              placeholder="请输入手机号"
              required
            />
          </div>
          <div class="form-group">
            <label for="shopPassword" class="form-label">密码</label>
            <input 
              type="password" 
              id="shopPassword" 
              v-model="form.shopPassword" 
              class="form-input" 
              placeholder="请输入密码"
              required
            />
          </div>
          <div class="form-group">
            <label for="shopAddress" class="form-label">店铺地址</label>
            <input 
              type="text" 
              id="shopAddress" 
              v-model="form.shopAddress" 
              class="form-input" 
              placeholder="请输入店铺地址"
              required
            />
          </div>
          <div class="form-group">
            <label for="shopDescription" class="form-label">店铺描述</label>
            <textarea 
              id="shopDescription" 
              v-model="form.shopDescription" 
              class="form-input" 
              placeholder="请输入店铺描述"
              rows="3"
            ></textarea>
          </div>
        </template>
        
        <!-- 骑手注册字段 -->
        <template v-else>
          <div class="form-group">
            <label for="riderName" class="form-label">骑手姓名</label>
            <input 
              type="text" 
              id="riderName" 
              v-model="form.riderName" 
              class="form-input" 
              placeholder="请输入骑手姓名"
              required
            />
          </div>
          <div class="form-group">
            <label for="riderPhone" class="form-label">手机号</label>
            <input 
              type="tel" 
              id="riderPhone" 
              v-model="form.riderPhone" 
              class="form-input" 
              placeholder="请输入手机号"
              required
            />
          </div>
          <div class="form-group">
            <label for="riderPassword" class="form-label">密码</label>
            <input 
              type="password" 
              id="riderPassword" 
              v-model="form.riderPassword" 
              class="form-input" 
              placeholder="请输入密码"
              required
            />
          </div>
          <div class="form-group">
            <label for="riderIdCard" class="form-label">身份证号</label>
            <input 
              type="text" 
              id="riderIdCard" 
              v-model="form.riderIdCard" 
              class="form-input" 
              placeholder="请输入身份证号"
              required
            />
          </div>
        </template>
        
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
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const isLoading = ref(false)
const registerType = ref<'user' | 'shop' | 'rider'>('user')

onMounted(() => {
  const type = route.query.type as string
  if (type === 'shop') {
    registerType.value = 'shop'
  } else if (type === 'rider') {
    registerType.value = 'rider'
  }
})

const getRegisterTitle = () => {
  switch (registerType.value) {
    case 'user': return '用户注册'
    case 'shop': return '商家注册'
    case 'rider': return '骑手注册'
    default: return '注册'
  }
}
const form = ref({
  // 用户注册字段
  username: '',
  password: '',
  name: '',
  phone: '',
  address: '',
  // 商家注册字段
  shopName: '',
  shopPhone: '',
  shopPassword: '',
  shopAddress: '',
  shopDescription: '',
  // 骑手注册字段
  riderName: '',
  riderPhone: '',
  riderPassword: '',
  riderIdCard: ''
})

const handleRegister = async () => {
  isLoading.value = true
  try {
    if (registerType.value === 'user') {
      // 用户注册
      const userData = {
        username: form.value.username,
        password: form.value.password,
        name: form.value.name,
        phone: form.value.phone,
        address: form.value.address
      }
      const response = await axios.post('/api/users/register', userData)
      if (response.data.success) {
        alert('注册成功，请登录')
        router.push('/login')
      } else {
        alert(response.data.message || '注册失败，请稍后重试')
      }
    } else if (registerType.value === 'shop') {
      // 商家注册
      const shopData = {
        name: form.value.shopName,
        phone: form.value.shopPhone,
        password: form.value.shopPassword,
        address: form.value.shopAddress,
        description: form.value.shopDescription
      }
      const response = await axios.post('/api/shops', shopData)
      if (response.status === 201) {
        alert('商家注册成功，请登录')
        router.push('/login')
      }
    } else {
      // 骑手注册
      const riderData = {
        name: form.value.riderName,
        phone: form.value.riderPhone,
        password: form.value.riderPassword,
        idCard: form.value.riderIdCard
      }
      const response = await axios.post('/api/riders', riderData)
      if (response.status === 201) {
        alert('骑手注册成功，请登录')
        router.push('/login')
      }
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
  max-width: 420px;
}

.register-tabs {
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
  box-sizing: border-box;
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