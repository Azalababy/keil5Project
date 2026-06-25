import { createRouter, createWebHistory } from 'vue-router'
import { checkAndClearExpiredLogin, getCurrentRole } from './utils/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('./views/Home.vue')
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('./views/Login.vue')
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('./views/Register.vue')
    },
    {
      path: '/shop/:id',
      name: 'shop',
      component: () => import('./views/Shop.vue')
    },
    {
      path: '/orders',
      name: 'orders',
      component: () => import('./views/Orders.vue')
    },
    {
      path: '/shop-admin',
      name: 'shop-admin',
      component: () => import('./views/ShopAdmin.vue')
    },
    {
      path: '/rider-admin',
      name: 'rider-admin',
      component: () => import('./views/RiderAdmin.vue')
    },
    {
      path: '/coupons',
      name: 'coupons',
      component: () => import('./views/Coupons.vue')
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('./views/Profile.vue')
    }
  ]
})

// 全局导航守卫 - 检查登录状态过期
router.beforeEach((to, from, next) => {
  checkAndClearExpiredLogin()
  
  const role = getCurrentRole()
  
  // 如果已登录但访问登录页面，跳转到首页
  if (role && to.path === '/login') {
    next('/')
  } else {
    next()
  }
})

export default router
