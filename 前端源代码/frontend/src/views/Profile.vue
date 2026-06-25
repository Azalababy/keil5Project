<template>
  <div class="profile-page">
    <div class="profile-header">
      <div class="avatar">
        <div class="avatar-icon">👤</div>
      </div>
      <div class="user-info">
        <h2 class="username">{{ displayName }}</h2>
        <p class="user-role">{{ roleText }}</p>
      </div>
    </div>

    <div class="profile-menu">
      <div class="menu-section">
        <div class="menu-item" @click="handleMenuClick('orders')">
          <div class="menu-icon">📋</div>
          <div class="menu-text">我的订单</div>
          <div class="menu-arrow">›</div>
        </div>
        <div class="menu-item" @click="handleMenuClick('coupons')">
          <div class="menu-icon">🎟️</div>
          <div class="menu-text">我的优惠券</div>
          <div class="menu-arrow">›</div>
        </div>
      </div>

      <div class="menu-section">
        <div class="menu-item" @click="handleMenuClick('settings')">
          <div class="menu-icon">⚙️</div>
          <div class="menu-text">设置</div>
          <div class="menu-arrow">›</div>
        </div>
        <div class="menu-item" @click="handleMenuClick('about')">
          <div class="menu-icon">ℹ️</div>
          <div class="menu-text">关于我们</div>
          <div class="menu-arrow">›</div>
        </div>
      </div>

      <div class="menu-section">
        <div class="menu-item logout" @click="handleLogout">
          <div class="menu-icon">🚪</div>
          <div class="menu-text">退出登录</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const user = ref<any>(null)
const shop = ref<any>(null)
const rider = ref<any>(null)
const role = ref<'user' | 'shop' | 'rider' | 'admin' | null>(null)

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
  return '未登录'
})

const roleText = computed(() => {
  switch (role.value) {
    case 'user':
      return '普通用户'
    case 'shop':
      return '商家'
    case 'rider':
      return '骑手'
    case 'admin':
      return '管理员'
    default:
      return '未登录'
  }
})

const checkLoginStatus = () => {
  const storedRole = localStorage.getItem('role')
  const token = localStorage.getItem('token')
  
  if (!token) {
    router.push('/login')
    return
  }
  
  role.value = storedRole as any
  
  if (storedRole === 'user') {
    const userData = localStorage.getItem('user')
    if (userData) {
      try {
        user.value = JSON.parse(userData)
      } catch {
        user.value = null
      }
    }
  } else if (storedRole === 'shop') {
    const shopData = localStorage.getItem('shop')
    if (shopData) {
      try {
        shop.value = JSON.parse(shopData)
      } catch {
        shop.value = null
      }
    }
  } else if (storedRole === 'rider') {
    const riderData = localStorage.getItem('rider')
    if (riderData) {
      try {
        rider.value = JSON.parse(riderData)
      } catch {
        rider.value = null
      }
    }
  }
}

const handleMenuClick = (action: string) => {
  switch (action) {
    case 'orders':
      if (role.value === 'user') {
        router.push('/orders')
      } else if (role.value === 'shop') {
        router.push('/shop-admin')
      } else if (role.value === 'rider') {
        router.push('/rider-admin')
      }
      break
    case 'coupons':
      router.push('/coupons')
      break
    case 'settings':
      alert('设置功能开发中...')
      break
    case 'about':
      alert('校园外卖系统 v1.0')
      break
  }
}

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    localStorage.removeItem('shop')
    localStorage.removeItem('rider')
    localStorage.removeItem('admin')
    localStorage.removeItem('role')
    localStorage.removeItem('username')
    router.push('/login')
  }
}

onMounted(() => {
  checkLoginStatus()
})
</script>

<style scoped>
.profile-page {
  padding: 1rem;
  background-color: #f5f5f5;
  min-height: calc(100vh - 4rem);
}

.profile-header {
  background: linear-gradient(135deg, #FFD100 0%, #FFA500 100%);
  padding: 2rem 1.5rem;
  border-radius: 0 0 20px 20px;
  display: flex;
  align-items: center;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 4px 15px rgba(255, 209, 0, 0.3);
}

.avatar {
  width: 70px;
  height: 70px;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
}

.avatar-icon {
  font-size: 2.5rem;
}

.user-info h2 {
  margin: 0 0 0.3rem 0;
  color: #333;
  font-size: 1.4rem;
}

.user-role {
  margin: 0;
  color: rgba(51, 51, 51, 0.8);
  font-size: 0.9rem;
}

.profile-menu {
  padding: 0 0.5rem;
}

.menu-section {
  background-color: #fff;
  border-radius: 12px;
  margin-bottom: 1rem;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.2s;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background-color: #fafafa;
}

.menu-item.logout {
  color: #ff4444;
}

.menu-item.logout:hover {
  background-color: #fff5f5;
}

.menu-icon {
  font-size: 1.5rem;
  margin-right: 1rem;
  width: 30px;
  text-align: center;
}

.menu-text {
  flex: 1;
  font-size: 1rem;
  font-weight: 500;
}

.menu-arrow {
  font-size: 1.5rem;
  color: #ccc;
}

@media screen and (max-width: 480px) {
  .profile-page {
    padding: 0.5rem;
  }

  .profile-header {
    padding: 1.5rem 1rem;
    border-radius: 0 0 15px 15px;
    gap: 1rem;
  }

  .avatar {
    width: 60px;
    height: 60px;
  }

  .avatar-icon {
    font-size: 2rem;
  }

  .user-info h2 {
    font-size: 1.2rem;
  }

  .user-role {
    font-size: 0.85rem;
  }

  .menu-item {
    padding: 0.9rem;
  }

  .menu-icon {
    font-size: 1.3rem;
    margin-right: 0.8rem;
  }

  .menu-text {
    font-size: 0.95rem;
  }
}
</style>