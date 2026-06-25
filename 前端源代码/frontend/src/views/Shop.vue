<template>
  <div class="shop">
    <div v-if="shop" class="shop-header">
      <div class="shop-banner">
        <div class="shop-icon-large">
          <FoodIcon :type="getShopIconType(shop.name)" />
        </div>
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
          <div class="dish-icon-wrapper">
            <FoodIcon :type="getDishIconType(dish.name)" />
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
            <input 
              type="number" 
              v-model.number="item.quantity" 
              @blur="validateQuantity(item)"
              min="1" 
              class="quantity-input"
            />
            <button @click="updateQuantity(item.dishId, item.quantity + 1)">+</button>
          </div>
          <button @click="removeFromCart(item.dishId)" class="remove-item-btn">删除</button>
        </div>
      </div>
      
      <!-- 收货信息 -->
      <div class="cart-info">
        <div class="form-group">
          <label class="form-label">收货地址 <span class="required">*</span></label>
          <textarea v-model="address" class="form-textarea" placeholder="请输入收货地址" required></textarea>
        </div>
        <div class="form-group">
          <label class="form-label">联系电话 <span class="required">*</span></label>
          <input v-model="phone" type="tel" class="form-input" placeholder="请输入联系电话" required />
        </div>
        <div class="form-group">
          <label class="form-label">备注</label>
          <textarea v-model="remark" class="form-textarea" placeholder="请输入备注信息（如：少辣、多加酱等）"></textarea>
        </div>
        <!-- 优惠券选择 -->
        <div class="form-group">
          <label class="form-label">选择优惠券</label>
          <div class="coupon-selector">
            <button 
              class="coupon-option" 
              :class="{ selected: selectedCouponId === null }"
              @click="selectedCouponId = null"
            >
              不使用优惠券
            </button>
            <div v-if="availableCoupons.length > 0">
              <button 
                v-for="coupon in availableCoupons" 
                :key="coupon.id"
                class="coupon-option"
                :class="{ selected: selectedCouponId === coupon.id }"
                @click="selectCoupon(coupon)"
              >
                ¥{{ coupon.value }} {{ coupon.minAmount ? `(满${coupon.minAmount}可用)` : '(无门槛)' }}
              </button>
            </div>
            <div v-else class="no-coupon">暂无可用优惠券</div>
          </div>
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
import FoodIcon from '../components/FoodIcon.vue'
import { getShopIcon, getFoodIcon } from '../utils/imageHelper'

const getShopIconType = (shopName: string) => {
  return getShopIcon(shopName)
}

const getDishIconType = (dishName: string) => {
  return getFoodIcon(dishName)
}

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

interface Coupon {
  id: number
  value: number
  minAmount: number | null
  expireDate: string
  description: string
}

const route = useRoute()
const router = useRouter()
const shopId = computed(() => route.params.id as string)

const shop = ref<Shop | null>(null)
const dishes = ref<Dish[]>([])
const cart = ref<CartItem[]>([])
const userCoupons = ref<Coupon[]>([])
const selectedCouponId = ref<number | null>(null)

// 收货信息
const address = ref('')
const phone = ref('')
const remark = ref('')

const totalPrice = computed(() => {
  return cart.value.reduce((total, item) => total + item.price * item.quantity, 0).toFixed(2)
})

const availableCoupons = computed(() => {
  const total = parseFloat(totalPrice.value)
  return userCoupons.value.filter(coupon => {
    if (coupon.minAmount && total < coupon.minAmount) {
      return false
    }
    return new Date(coupon.expireDate) > new Date()
  })
})

const fetchUserCoupons = async () => {
  const userStr = localStorage.getItem('user')
  if (!userStr) return
  
  const user = JSON.parse(userStr)
  try {
    const response = await axios.get(`/api/coupons/user/${user.id}`)
    const userCouponList = response.data
    
    const couponIds = [...new Set(userCouponList.map((uc: any) => uc.couponId))]
    const couponsResponse = await axios.get('/api/coupons')
    const allCoupons = couponsResponse.data
    
    userCoupons.value = couponIds.map(id => allCoupons.find((c: any) => c.id === id)).filter(Boolean)
  } catch (error) {
    console.error('Failed to fetch user coupons:', error)
  }
}

const selectCoupon = (coupon: Coupon) => {
  selectedCouponId.value = coupon.id
}

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

// 图片加载失败处理
const handleImageError = (event: Event) => {
  const target = event.target as HTMLImageElement
  target.style.display = 'none'
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

const validateQuantity = (item: CartItem) => {
  if (!item.quantity || item.quantity <= 0) {
    item.quantity = 1
  }
}

const removeFromCart = (dishId: number) => {
  cart.value = cart.value.filter(item => item.dishId !== dishId)
}

const placeOrder = async () => {
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    alert('请先登录')
    router.push('/login')
    return
  }
  
  // 验证收货地址必填
  if (!address.value.trim()) {
    alert('请输入收货地址')
    return
  }
  
  // 验证手机号必填
  if (!phone.value.trim()) {
    alert('请输入联系电话')
    return
  }
  
  const user = JSON.parse(userStr)
  const userId = user.id
  
  // 计算实际支付金额（考虑优惠券）
  let finalPrice = Number(totalPrice.value)
  let couponId = null
  if (selectedCouponId.value) {
    const coupon = userCoupons.value.find(c => c.id === selectedCouponId.value)
    if (coupon) {
      finalPrice = Math.max(0, finalPrice - coupon.value)
      couponId = selectedCouponId.value
    }
  }
  
  const orderItems = cart.value.map(item => ({
    dishId: item.dishId,
    quantity: item.quantity,
    price: item.price
  }))

  const orderData = {
    order: {
      userId,
      shopId: Number(shopId.value),
      totalPrice: finalPrice,
      originalPrice: Number(totalPrice.value),
      couponId,
      address: address.value || user.address || '收货地址',
      phone: phone.value || user.phone || '13800138000',
      remark: remark.value
    },
    orderItems
  }

  try {
    const response = await axios.post('/api/orders', orderData)
    // 检查响应状态是否成功（200或201）
    if (response.status === 201 || response.status === 200) {
      if (response.data && response.data.id) {
        // 如果使用了优惠券，标记为已使用
        if (couponId) {
          const userCouponList = await axios.get(`/api/coupons/user/${user.id}`)
          const userCoupon = userCouponList.data.find((uc: any) => uc.couponId === couponId)
          if (userCoupon) {
            await axios.post(`/api/coupons/user-coupon/${userCoupon.id}/use`, { orderId: response.data.id })
          }
        }
        alert('订单提交成功！')
        cart.value = []
        address.value = ''
        phone.value = ''
        remark.value = ''
        selectedCouponId.value = null
        router.push('/orders')
      } else {
        throw new Error('订单数据不完整')
      }
    } else {
      throw new Error(`请求失败，状态码: ${response.status}`)
    }
  } catch (error: any) {
    console.error('Failed to place order:', error)
    // 区分网络错误和业务错误
    const errorMessage = error.response?.data?.message || 
                         error.message || 
                         '订单提交失败，请稍后重试'
    alert(errorMessage)
  }
}

onMounted(() => {
  fetchShop()
  fetchDishes()
  fetchUserCoupons()
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

.shop-icon-large {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FFD100, #FFB800);
}

.shop-icon-large .food-icon {
  width: 150px;
  height: 150px;
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

.dish-icon-wrapper {
  padding: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
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

.cart-item-quantity .quantity-input {
  min-width: 50px;
  max-width: 70px;
  text-align: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 0.3rem 0.5rem;
  font-size: 1rem;
  outline: none;
}

.cart-item-quantity .quantity-input:focus {
  border-color: #ff6b6b;
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

.cart-info {
  background-color: white;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 1rem;
}

.form-group:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: #333;
  font-size: 0.9rem;
}

.required {
  color: #e74c3c;
  margin-left: 4px;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.95rem;
  color: #333;
  box-sizing: border-box;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #ff6b6b;
}

.form-input::placeholder {
  color: #999;
}

.form-textarea {
  width: 100%;
  min-height: 80px;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.95rem;
  color: #333;
  resize: vertical;
  box-sizing: border-box;
  transition: border-color 0.3s ease;
}

.form-textarea:focus {
  outline: none;
  border-color: #ff6b6b;
}

.form-textarea::placeholder {
  color: #999;
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

/* 优惠券选择器样式 */
.coupon-selector {
  display: flex;
  flex-wrap: wrap;
  gap: 0.8rem;
}

.coupon-option {
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
}

.coupon-option:hover {
  border-color: #ff6b6b;
}

.coupon-option.selected {
  background-color: #fff5f5;
  border-color: #ff6b6b;
  color: #ff6b6b;
}

.no-coupon {
  color: #999;
  font-size: 0.9rem;
  padding: 0.5rem 0;
}
</style>
