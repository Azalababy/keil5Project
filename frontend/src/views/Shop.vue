<template>
  <div class="shop">
    <div v-if="shop" class="shop-header">
      <div class="shop-banner">
        <img :src="shop.imageUrl || 'https://via.placeholder.com/1200x400'" :alt="shop.name" />
        <div class="shop-info-overlay">
          <h2 class="shop-name">{{ shop.name }}</h2>
          <p class="shop-description">{{ shop.description }}</p>
          <div class="shop-meta">
            <span class="shop-address">{{ shop.address }}</span>
            <span class="shop-phone">{{ shop.phone }}</span>
            <span class="shop-hours">{{ shop.openingHours }}</span>
            <span class="shop-status" :class="shop.status">
              {{ shop.status === 'open' ? '营业中' : '已关闭' }}
            </span>
          </div>
        </div>
      </div>
    </div>

    <div class="dishes-section">
      <h3 class="section-title">菜单</h3>
      <div class="dishes-container">
        <div v-for="dish in dishes" :key="dish.id" class="dish-card">
          <div class="dish-image">
            <img :src="dish.imageUrl || 'https://via.placeholder.com/200x200'" :alt="dish.name" />
          </div>
          <div class="dish-info">
            <h4 class="dish-name">{{ dish.name }}</h4>
            <p class="dish-description">{{ dish.description }}</p>
            <div class="dish-price">¥{{ dish.price }}</div>
            <div class="dish-actions">
              <button 
                class="add-to-cart-btn" 
                @click="addToCart(dish)"
                :disabled="dish.status !== 'available'"
              >
                {{ dish.status === 'available' ? '加入购物车' : '暂不可用' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div v-if="cart.length > 0" class="cart-section">
      <h3 class="section-title">购物车</h3>
      <div class="cart-items">
        <div v-for="item in cart" :key="item.dishId" class="cart-item">
          <div class="cart-item-info">
            <span class="cart-item-name">{{ item.name }}</span>
            <span class="cart-item-price">¥{{ item.price }}</span>
          </div>
          <div class="cart-item-quantity">
            <button @click="updateQuantity(item.dishId, item.quantity - 1)" :disabled="item.quantity <= 1">-</button>
            <span>{{ item.quantity }}</span>
            <button @click="updateQuantity(item.dishId, item.quantity + 1)">+</button>
          </div>
          <button @click="removeFromCart(item.dishId)" class="remove-item-btn">删除</button>
        </div>
      </div>
      <div class="cart-total">
        <span>总计：</span>
        <span class="total-price">¥{{ totalPrice }}</span>
      </div>
      <button @click="placeOrder" class="place-order-btn">提交订单</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

interface Shop {
  id: number
  name: string
  description: string
  address: string
  phone: string
  openingHours: string
  imageUrl: string
  status: string
  createdAt: string
  updatedAt: string
}

interface Dish {
  id: number
  shopId: number
  name: string
  description: string
  price: number
  imageUrl: string
  status: string
  createdAt: string
  updatedAt: string
}

interface CartItem {
  dishId: number
  name: string
  price: number
  quantity: number
}

const route = useRoute()
const router = useRouter()
const shopId = computed(() => route.params.id as string)

const shop = ref<Shop | null>(null)
const dishes = ref<Dish[]>([])
const cart = ref<CartItem[]>([])

const totalPrice = computed(() => {
  return cart.value.reduce((total, item) => total + item.price * item.quantity, 0).toFixed(2)
})

const fetchShop = async () => {
  try {
    const response = await axios.get(`/api/shops/${shopId.value}`)
    shop.value = response.data
  } catch (error) {
    console.error('Failed to fetch shop:', error)
  }
}

const fetchDishes = async () => {
  try {
    const response = await axios.get(`/api/dishes/shop/${shopId.value}`)
    dishes.value = response.data
  } catch (error) {
    console.error('Failed to fetch dishes:', error)
  }
}

const addToCart = (dish: Dish) => {
  const existingItem = cart.value.find(item => item.dishId === dish.id)
  if (existingItem) {
    existingItem.quantity++
  } else {
    cart.value.push({
      dishId: dish.id,
      name: dish.name,
      price: dish.price,
      quantity: 1
    })
  }
}

const updateQuantity = (dishId: number, quantity: number) => {
  if (quantity <= 0) return
  const item = cart.value.find(item => item.dishId === dishId)
  if (item) {
    item.quantity = quantity
  }
}

const removeFromCart = (dishId: number) => {
  cart.value = cart.value.filter(item => item.dishId !== dishId)
}

const placeOrder = async () => {
  const userId = 1 // 简化处理，实际应该从登录状态获取
  const orderItems = cart.value.map(item => ({
    dishId: item.dishId,
    quantity: item.quantity,
    price: item.price
  }))

  const orderData = {
    order: {
      userId,
      shopId: Number(shopId.value),
      totalPrice: Number(totalPrice.value),
      address: '收货地址', // 实际应该从用户信息获取
      phone: '13800138000' // 实际应该从用户信息获取
    },
    orderItems
  }

  try {
    const response = await axios.post('/api/orders', orderData)
    if (response.status === 201) {
      alert('订单提交成功！')
      cart.value = []
      router.push('/orders')
    }
  } catch (error) {
    console.error('Failed to place order:', error)
    alert('订单提交失败，请稍后重试')
  }
}

onMounted(() => {
  fetchShop()
  fetchDishes()
})
</script>

<style scoped>
.shop {
  padding: 1rem 0;
}

.shop-header {
  margin-bottom: 2rem;
}

.shop-banner {
  position: relative;
  height: 400px;
  overflow: hidden;
  border-radius: 8px;
}

.shop-banner img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.shop-info-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.8), transparent);
  color: white;
  padding: 2rem;
}

.shop-name {
  font-size: 2rem;
  font-weight: bold;
  margin: 0 0 0.5rem 0;
}

.shop-description {
  font-size: 1rem;
  margin: 0 0 1rem 0;
  opacity: 0.9;
}

.shop-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  font-size: 0.9rem;
  opacity: 0.8;
}

.shop-status {
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  font-weight: 500;
}

.shop-status.open {
  background-color: rgba(40, 167, 69, 0.2);
  color: #28a745;
}

.shop-status.closed {
  background-color: rgba(255, 193, 7, 0.2);
  color: #ffc107;
}

.dishes-section {
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
  color: #333;
}

.dishes-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.dish-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.dish-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.dish-image {
  height: 200px;
  overflow: hidden;
}

.dish-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dish-info {
  padding: 1.2rem;
}

.dish-name {
  font-size: 1.1rem;
  font-weight: bold;
  margin: 0 0 0.5rem 0;
  color: #333;
}

.dish-description {
  font-size: 0.9rem;
  color: #666;
  margin: 0 0 1rem 0;
  line-height: 1.4;
}

.dish-price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #ff6b6b;
  margin-bottom: 1rem;
}

.dish-actions {
  display: flex;
  justify-content: flex-end;
}

.add-to-cart-btn {
  background-color: #ff6b6b;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.add-to-cart-btn:hover {
  background-color: #ee5a52;
}

.add-to-cart-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.cart-section {
  background-color: #f9f9f9;
  border-radius: 8px;
  padding: 1.5rem;
  margin-top: 2rem;
}

.cart-items {
  margin-bottom: 1.5rem;
}

.cart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background-color: white;
  border-radius: 4px;
  margin-bottom: 0.8rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.cart-item-info {
  flex: 1;
}

.cart-item-name {
  font-weight: 500;
  margin-right: 1rem;
}

.cart-item-price {
  color: #ff6b6b;
  font-weight: 500;
}

.cart-item-quantity {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  margin: 0 1rem;
}

.cart-item-quantity button {
  width: 28px;
  height: 28px;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  display: flex;
  align-items: center;
  justify-content: center;
}

.cart-item-quantity button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.cart-item-quantity span {
  min-width: 30px;
  text-align: center;
}

.remove-item-btn {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 0.3rem 0.8rem;
  border-radius: 4px;
  font-size: 0.9rem;
  cursor: pointer;
}

.cart-total {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #ddd;
}

.total-price {
  color: #ff6b6b;
  font-size: 1.4rem;
}

.place-order-btn {
  width: 100%;
  background-color: #28a745;
  color: white;
  border: none;
  padding: 1rem;
  border-radius: 4px;
  font-size: 1.1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.place-order-btn:hover {
  background-color: #218838;
}
</style>
