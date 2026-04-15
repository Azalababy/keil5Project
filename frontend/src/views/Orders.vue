<template>
  <div class="orders">
    <h2 class="page-title">我的订单</h2>
    <div class="orders-container">
      <div v-for="order in orders" :key="order.id" class="order-card">
        <div class="order-header">
          <div class="order-id">订单号：{{ order.id }}</div>
          <div class="order-status" :class="order.status">
            {{ getStatusText(order.status) }}
          </div>
        </div>
        <div class="order-info">
          <div class="order-shop">商家：{{ order.shopName || '未知商家' }}</div>
          <div class="order-time">下单时间：{{ formatDate(order.createdAt) }}</div>
          <div class="order-address">收货地址：{{ order.address }}</div>
          <div class="order-phone">联系电话：{{ order.phone }}</div>
        </div>
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <span class="item-name">{{ item.name }}</span>
            <span class="item-quantity">×{{ item.quantity }}</span>
            <span class="item-price">¥{{ item.price }}</span>
          </div>
        </div>
        <div class="order-footer">
          <div class="order-total">
            总计：<span class="total-price">¥{{ order.totalPrice }}</span>
          </div>
        </div>
      </div>
      <div v-if="orders.length === 0" class="empty-orders">
        <p>暂无订单</p>
        <router-link to="/" class="go-shopping-btn">去购物</router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'

interface OrderItem {
  id: number
  dishId: number
  quantity: number
  price: number
  name?: string // 前端添加的字段，用于显示菜品名称
}

interface Order {
  id: number
  userId: number
  shopId: number
  totalPrice: number
  address: string
  phone: string
  status: string
  createdAt: string
  updatedAt: string
  shopName?: string // 前端添加的字段，用于显示商家名称
  items?: OrderItem[] // 前端添加的字段，用于显示订单详情
}

const orders = ref<Order[]>([])

const fetchOrders = async () => {
  try {
    const userId = 1 // 简化处理，实际应该从登录状态获取
    const response = await axios.get(`/api/orders/user/${userId}`)
    const orderList = response.data
    
    // 为每个订单获取订单详情
    for (const order of orderList) {
      const itemsResponse = await axios.get(`/api/orders/${order.id}/items`)
      order.items = itemsResponse.data
      
      // 为每个订单详情获取菜品名称
      for (const item of order.items) {
        try {
          const dishResponse = await axios.get(`/api/dishes/${item.dishId}`)
          item.name = dishResponse.data.name
        } catch (error) {
          console.error(`Failed to fetch dish ${item.dishId}:`, error)
          item.name = '未知菜品'
        }
      }
      
      // 获取商家名称
      try {
        const shopResponse = await axios.get(`/api/shops/${order.shopId}`)
        order.shopName = shopResponse.data.name
      } catch (error) {
        console.error(`Failed to fetch shop ${order.shopId}:`, error)
        order.shopName = '未知商家'
      }
    }
    
    orders.value = orderList
  } catch (error) {
    console.error('Failed to fetch orders:', error)
  }
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待处理',
    processing: '处理中',
    delivered: '已送达',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.orders {
  padding: 1rem 0;
}

.page-title {
  font-size: 1.8rem;
  margin-bottom: 1.5rem;
  color: #333;
}

.orders-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.order-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.order-id {
  font-weight: 500;
  color: #333;
}

.order-status {
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-weight: 500;
  font-size: 0.9rem;
}

.order-status.pending {
  background-color: #fff3cd;
  color: #ffc107;
}

.order-status.processing {
  background-color: #e3f2fd;
  color: #1976d2;
}

.order-status.delivered {
  background-color: #e6f7ee;
  color: #28a745;
}

.order-status.cancelled {
  background-color: #f8d7da;
  color: #dc3545;
}

.order-info {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

.order-info > div {
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
  color: #555;
}

.order-items {
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f1f3f5;
}

.order-item:last-child {
  border-bottom: none;
}

.item-name {
  flex: 1;
  font-size: 0.95rem;
  color: #333;
}

.item-quantity {
  margin: 0 1rem;
  font-size: 0.9rem;
  color: #666;
}

.item-price {
  font-weight: 500;
  color: #333;
}

.order-footer {
  padding: 1rem 1.5rem;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.order-total {
  font-size: 1.1rem;
  font-weight: 500;
  color: #333;
}

.total-price {
  color: #ff6b6b;
  font-size: 1.2rem;
  margin-left: 0.5rem;
}

.empty-orders {
  text-align: center;
  padding: 3rem 0;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.empty-orders p {
  font-size: 1.1rem;
  color: #666;
  margin-bottom: 1.5rem;
}

.go-shopping-btn {
  display: inline-block;
  background-color: #ff6b6b;
  color: white;
  padding: 0.8rem 1.5rem;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.3s ease;
}

.go-shopping-btn:hover {
  background-color: #ee5a52;
}
</style>
