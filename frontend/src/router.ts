import { createRouter, createWebHistory } from 'vue-router'

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
    }
  ]
})

export default router
