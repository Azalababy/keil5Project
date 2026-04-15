<template>
  <div class="app">
    <header class="header">
      <div class="container">
        <h1 class="logo">校园外卖</h1>
        <nav class="nav">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link to="/orders" class="nav-link">我的订单</router-link>
          <div v-if="!isLoggedIn" class="auth-links">
            <router-link to="/login" class="nav-link">登录</router-link>
            <router-link to="/register" class="nav-link">注册</router-link>
          </div>
          <div v-else class="user-info">
            <span class="welcome">欢迎, {{ username }}</span>
            <button @click="logout" class="logout-btn">退出</button>
          </div>
        </nav>
      </div>
    </header>
    <main class="main">
      <div class="container">
        <router-view />
      </div>
    </main>
    <footer class="footer">
      <div class="container">
        <p>© 2026 校园外卖系统</p>
      </div>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isLoggedIn = ref(false)
const username = ref('')

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  isLoggedIn.value = false
  username.value = ''
  router.push('/login')
}

onMounted(() => {
  const storedUsername = localStorage.getItem('username')
  if (storedUsername) {
    isLoggedIn.value = true
    username.value = storedUsername
  }
})
</script>

<style scoped>
.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  background-color: #ff6b6b;
  color: white;
  padding: 1rem 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.logo {
  font-size: 1.5rem;
  font-weight: bold;
  margin: 0;
}

.nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 1rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  margin-right: 1rem;
  font-weight: 500;
}

.nav-link:hover {
  text-decoration: underline;
}

.auth-links {
  display: flex;
  gap: 1rem;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.welcome {
  font-weight: 500;
}

.logout-btn {
  background: none;
  border: 1px solid white;
  color: white;
  padding: 0.3rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.logout-btn:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.main {
  flex: 1;
  padding: 2rem 0;
}

.footer {
  background-color: #f5f5f5;
  padding: 1rem 0;
  margin-top: 2rem;
  text-align: center;
  font-size: 0.9rem;
  color: #666;
}
</style>
