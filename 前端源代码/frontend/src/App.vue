<template>
  <div class="app">
    <!-- 普通页面导航栏 -->
    <header class="header" :class="{ hidden: isHeaderHidden || isRiderAdmin || isShopAdmin || isAuthPage }">
      <div class="container">
        <h1 class="logo">校园外卖</h1>
        <nav class="nav">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link v-if="isLoggedIn && role === 'user'" to="/orders" class="nav-link">我的订单</router-link>
          <router-link v-if="isLoggedIn && role === 'shop'" to="/shop-admin" class="nav-link">商家后台</router-link>
          <router-link v-if="isLoggedIn && role === 'rider'" to="/" class="nav-link">首页</router-link>
          <router-link v-if="isLoggedIn && role === 'rider'" to="/rider-admin" class="nav-link">骑手中心</router-link>
          <div v-if="!isLoggedIn" class="auth-links">
            <router-link to="/login" class="nav-link">登录</router-link>
            <router-link to="/register" class="nav-link">注册</router-link>
          </div>
          <div v-else class="user-info">
            <span class="welcome">欢迎, {{ displayName }}</span>
            <button @click="logout" class="logout-btn">退出</button>
          </div>
        </nav>
      </div>
    </header>
    <main class="main" :class="{ 'shop-page': isShopPage }">
      <div class="container">
        <router-view />
      </div>
    </main>
    <footer class="footer">
      <div class="container">
        <p>© 2026 校园外卖系统</p>
      </div>
    </footer>
    
    <!-- 移动端底部导航栏 -->
    <nav class="mobile-nav" :class="{ hidden: isMobileNavHidden }">
      <router-link to="/" class="mobile-nav-item" :class="{ active: $route.path === '/' }">
        <div class="nav-icon">🏠</div>
        <div class="nav-text">首页</div>
      </router-link>
      <router-link to="/coupons" class="mobile-nav-item" :class="{ active: $route.path === '/coupons' }">
        <div class="nav-icon">🎟️</div>
        <div class="nav-text">抢券</div>
      </router-link>
      <router-link v-if="isLoggedIn && role === 'user'" to="/orders" class="mobile-nav-item" :class="{ active: $route.path === '/orders' }">
        <div class="nav-icon">📋</div>
        <div class="nav-text">订单</div>
      </router-link>
      <router-link v-if="isLoggedIn && role === 'shop'" to="/shop-admin" class="mobile-nav-item" :class="{ active: $route.path === '/shop-admin' }">
        <div class="nav-icon">🏪</div>
        <div class="nav-text">商家</div>
      </router-link>
      <router-link v-if="isLoggedIn && role === 'rider'" to="/rider-admin" class="mobile-nav-item" :class="{ active: $route.path === '/rider-admin' }">
        <div class="nav-icon">🛵</div>
        <div class="nav-text">骑手</div>
      </router-link>
      <router-link v-if="!isLoggedIn" to="/login" class="mobile-nav-item" :class="{ active: $route.path === '/login' }">
        <div class="nav-icon">👤</div>
        <div class="nav-text">登录</div>
      </router-link>
      <router-link v-else to="/profile" class="mobile-nav-item" :class="{ active: $route.path === '/profile' }">
        <div class="nav-icon">👤</div>
        <div class="nav-text">我的</div>
      </router-link>
    </nav>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const isLoggedIn = ref(false)
const role = ref<'user' | 'shop' | 'rider' | 'admin' | null>(null)
const user = ref<any>(null)
const shop = ref<any>(null)
const rider = ref<any>(null)
const admin = ref<any>(null)
const isHeaderHidden = ref(false)
const isRiderAdmin = ref(false)
const isShopPage = ref(false)
const isShopAdmin = ref(false)
const isAuthPage = ref(false)
const isMobileNavHidden = ref(false)
let lastScrollY = 0
const SCROLL_THRESHOLD = 50

const displayName = computed(() => {
  if (role.value === 'user' && user.value) {
    return user.value.name || user.value.username
  }
  if (role.value === 'shop' && shop.value) {
    return shop.value.name
  }
  if (role.value === 'rider' && rider.value) {
    return rider.value.name
  }
  if (role.value === 'admin' && admin.value) {
    return admin.value.username || '管理员'
  }
  return ''
})

const checkLoginStatus = () => {
  const storedRole = localStorage.getItem('role')
  const token = localStorage.getItem('token')
  
  // 如果没有token，视为未登录，清除所有登录状态
  if (!token) {
    localStorage.removeItem('user')
    localStorage.removeItem('shop')
    localStorage.removeItem('rider')
    localStorage.removeItem('admin')
    localStorage.removeItem('role')
    localStorage.removeItem('username')
    isLoggedIn.value = false
    role.value = null
    user.value = null
    shop.value = null
    rider.value = null
    admin.value = null
    return
  }
  
  if (storedRole === 'user') {
    const userData = localStorage.getItem('user')
    if (userData) {
      try {
        user.value = JSON.parse(userData)
      } catch {
        user.value = null
      }
    }
    isLoggedIn.value = true
    role.value = 'user'
  } else if (storedRole === 'shop') {
    const shopData = localStorage.getItem('shop')
    if (shopData) {
      try {
        shop.value = JSON.parse(shopData)
      } catch {
        shop.value = null
      }
    }
    isLoggedIn.value = true
    role.value = 'shop'
  } else if (storedRole === 'rider') {
    const riderData = localStorage.getItem('rider')
    if (riderData) {
      try {
        rider.value = JSON.parse(riderData)
      } catch {
        rider.value = null
      }
    }
    isLoggedIn.value = true
    role.value = 'rider'
  } else if (storedRole === 'admin') {
    const adminData = localStorage.getItem('admin')
    if (adminData) {
      try {
        admin.value = JSON.parse(adminData)
      } catch {
        admin.value = null
      }
    }
    isLoggedIn.value = true
    role.value = 'admin'
  } else {
    const storedUsername = localStorage.getItem('username')
    if (storedUsername) {
      user.value = { username: storedUsername }
      isLoggedIn.value = true
      role.value = 'user'
    } else {
      isLoggedIn.value = false
      role.value = null
      user.value = null
      shop.value = null
      rider.value = null
      admin.value = null
    }
  }
}

const handleScroll = () => {
  const currentScrollY = window.scrollY
  
  // 向下滚动超过阈值时隐藏导航栏
  if (currentScrollY > lastScrollY && currentScrollY > SCROLL_THRESHOLD) {
    isHeaderHidden.value = true
  } else if (currentScrollY < lastScrollY) {
    // 向上滚动时显示导航栏
    isHeaderHidden.value = false
  }
  
  lastScrollY = currentScrollY
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  localStorage.removeItem('shop')
  localStorage.removeItem('rider')
  localStorage.removeItem('admin')
  localStorage.removeItem('role')
  localStorage.removeItem('username')
  isLoggedIn.value = false
  role.value = null
  user.value = null
  shop.value = null
  rider.value = null
  admin.value = null
  router.push('/')
}

onMounted(() => {
  checkLoginStatus()
  
  router.afterEach((to) => {
    checkLoginStatus()
    // 路由切换时重置滚动位置和导航栏状态
    lastScrollY = 0
    isHeaderHidden.value = false
    isMobileNavHidden.value = false
    // 检查是否在骑手中心页面
    isRiderAdmin.value = to.path === '/rider-admin'
    // 检查是否在商家详情页面
    isShopPage.value = to.path.startsWith('/shop/')
    // 检查是否在商家后台页面
    isShopAdmin.value = to.path === '/shop-admin'
    // 检查是否在登录或注册页面
    isAuthPage.value = to.path === '/login' || to.path === '/register'
  })
  
  // 添加滚动事件监听
  window.addEventListener('scroll', handleScroll)
})

// 组件卸载时移除滚动监听
import { onUnmounted } from 'vue'

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
.app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.header {
  background-color: #FFD100;
  color: #333;
  padding: 1rem 0;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  transition: transform 0.3s ease;
}

.header.hidden {
  transform: translateY(-100%);
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
  margin-top: 0.5rem;
}

.back-btn {
  background: none;
  border: none;
  color: #333;
  font-weight: 500;
  font-size: 1rem;
  cursor: pointer;
  margin-right: 1rem;
  padding: 0;
}

.back-btn:hover {
  text-decoration: underline;
}

.nav-link {
  color: #333;
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
  border: 1px solid #333;
  color: #333;
  padding: 0.3rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.logout-btn:hover {
  background-color: rgba(0, 0, 0, 0.1);
}

.main {
  flex: 1;
  padding: calc(3.5rem + 2rem) 0 2rem;
}

.main.shop-page {
  padding: calc(3rem + 2rem) 0 2rem;
}

.footer {
  background-color: #f5f5f5;
  padding: 1rem 0;
  margin-top: 2rem;
  text-align: center;
  font-size: 0.9rem;
  color: #666;
}

/* 移动端底部导航栏 */
.mobile-nav {
  display: none;
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  border-top: 1px solid #e0e0e0;
  padding: 0.5rem 0;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  transition: transform 0.3s ease;
}

.mobile-nav.hidden {
  transform: translateY(100%);
}

.mobile-nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  color: #666;
  padding: 0.3rem 0;
  transition: all 0.3s ease;
  flex: 1;
}

.mobile-nav-item.active {
  color: #FFD100;
}

.mobile-nav-item:hover {
  color: #FFD100;
}

.nav-icon {
  font-size: 1.5rem;
  margin-bottom: 0.2rem;
}

.nav-text {
  font-size: 0.75rem;
  font-weight: 500;
}

/* 移动端适配 */
@media screen and (max-width: 768px) {
  .header {
    padding: 0.8rem 0;
  }
  
  .container {
    padding: 0 0.8rem;
  }
  
  .logo {
    font-size: 1.3rem;
  }
  
  .nav {
    flex-wrap: wrap;
    gap: 0.5rem;
  }
  
  .nav-link {
    font-size: 0.9rem;
    margin-right: 0.8rem;
  }
  
  .user-info {
    gap: 0.6rem;
  }
  
  .welcome {
    font-size: 0.9rem;
    max-width: 120px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .logout-btn {
    padding: 0.3rem 0.6rem;
    font-size: 0.85rem;
  }
  
  .main {
    padding: 1rem 0 4rem;
  }
  
  .footer {
    padding-bottom: 5rem;
  }
  
  /* 显示移动端底部导航栏 */
  .mobile-nav {
    display: flex;
  }
}

@media screen and (max-width: 480px) {
  .container {
    padding: 0 0.6rem;
  }
  
  .logo {
    font-size: 1.2rem;
  }
  
  .nav {
    margin-top: 0.3rem;
  }
  
  .nav-link {
    font-size: 0.85rem;
    margin-right: 0.6rem;
  }
  
  .welcome {
    font-size: 0.85rem;
    max-width: 100px;
  }
  
  .main {
    padding: 0.8rem 0;
  }
}
</style>