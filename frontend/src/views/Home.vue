<template>
  <div class="home">
    <!-- 横幅区域 -->
    <div class="banner">
      <div class="banner-content">
        <h1>校园美食配送</h1>
        <p>美食一键送达，享受校园生活</p>
        <div class="banner-buttons">
          <router-link to="/" class="btn-primary">立即点餐</router-link>
          <router-link to="/" class="btn-secondary">了解更多</router-link>
        </div>
      </div>
    </div>

    <!-- 特色分类 -->
    <div class="categories">
      <h2 class="section-title">美食分类</h2>
      <div class="categories-container">
        <div v-for="category in categories" :key="category.id" class="category-card">
          <div class="category-icon">{{ category.icon }}</div>
          <span class="category-name">{{ category.name }}</span>
        </div>
      </div>
    </div>

    <!-- 商家列表 -->
    <div class="section">
      <h2 class="section-title">热门商家</h2>
      <div class="shops-container">
        <div v-for="shop in shops" :key="shop.id" class="shop-card">
          <div class="shop-icon-wrapper">
            <FoodIcon :type="getShopIconType(shop.name)" />
          </div>
          <div class="shop-info">
            <h3 class="shop-name">{{ shop.name }}</h3>
            <p class="shop-description">{{ shop.description }}</p>
            <div class="shop-details">
              <span class="shop-address">{{ shop.address }}</span>
              <span class="shop-status" :class="shop.status">
                {{ shop.status === 'open' ? '营业中' : '已关闭' }}
              </span>
            </div>
            <router-link :to="`/shop/${shop.id}`" class="shop-link">查看菜单</router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- 推荐菜品 -->
    <div class="section">
      <h2 class="section-title">推荐菜品</h2>
      <div class="dishes-container">
        <div v-for="dish in recommendedDishes" :key="dish.id" class="dish-card" @click="showDishDetail(dish)">
          <div class="dish-icon-wrapper">
            <FoodIcon :type="getDishIconType(dish.name)" />
          </div>
          <div class="dish-info">
            <h4 class="dish-name">{{ dish.name }}</h4>
            <p class="dish-description">{{ dish.description }}</p>
            <div class="dish-price">¥{{ dish.price }}</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 菜品详情弹窗 -->
    <div v-if="selectedDish" class="dish-modal-overlay" @click="closeDishDetail">
      <div class="dish-modal" @click.stop>
        <div class="dish-modal-header">
          <h3>{{ selectedDish.name }}</h3>
          <button class="close-btn" @click="closeDishDetail">×</button>
        </div>
        <div class="dish-modal-content">
          <div class="dish-modal-image">
            <FoodIcon v-if="!selectedDish.imageUrl || !isValidImageUrl(selectedDish.imageUrl)" :type="getDishIconType(selectedDish.name)" class="dish-icon-large" />
            <img v-else :src="selectedDish.imageUrl" :alt="selectedDish.name" @error="showDishIcon = true" />
          </div>
          <div class="dish-modal-info">
            <p class="dish-modal-description">{{ selectedDish.description }}</p>
            <div class="dish-modal-price">¥{{ selectedDish.price }}</div>
            
            <!-- 品类选择 -->
            <div class="dish-modal-category">
              <h4>品类</h4>
              <select v-model="selectedCategory" class="category-select">
                <option v-for="category in getCategories(selectedDish.name)" :key="category.value" :value="category.value">
                  {{ category.label }}
                </option>
              </select>
            </div>
            
            <!-- 规格选项 -->
            <div class="dish-modal-specs">
              <h4>规格</h4>
              <div class="specs-container">
                <label class="spec-item" v-for="spec in getSpecs(selectedDish.name)" :key="spec.value">
                  <input type="radio" v-model="selectedSpec" :value="spec.value" />
                  <span>{{ spec.label }}</span>
                </label>
              </div>
            </div>
            
            <!-- 数量选择 -->
            <div class="dish-modal-quantity">
              <h4>数量</h4>
              <div class="quantity-control">
                <button @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
                <input 
                  type="number" 
                  v-model.number="quantity" 
                  @input="validateQuantity"
                  min="1"
                  class="quantity-input"
                />
                <button @click="increaseQuantity">+</button>
              </div>
            </div>
            
            <!-- 收货地址 -->
            <div class="dish-modal-address">
              <h4>收货地址</h4>
              <textarea v-model="address" class="address-input" placeholder="请输入收货地址"></textarea>
            </div>
            
            <!-- 联系电话 -->
            <div class="dish-modal-phone">
              <h4>联系电话</h4>
              <input v-model="phone" type="tel" class="phone-input" placeholder="请输入联系电话" />
            </div>
            
            <!-- 备注 -->
            <div class="dish-modal-remark">
              <h4>备注</h4>
              <textarea v-model="remark" class="remark-input" placeholder="请输入备注信息（如：少辣、多加酱等）"></textarea>
            </div>
            
            <div class="dish-modal-actions">
              <button class="add-to-cart-modal-btn" @click="addToCartFromDetail(selectedDish)">加入购物车</button>
              <button class="buy-now-btn" @click="buyNow(selectedDish)">立即购买</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 购买成功提示框 -->
    <div v-if="showSuccessModal" class="success-modal-overlay">
      <div class="success-modal">
        <button class="success-back-btn" @click="handleSuccessClose">←</button>
        <div class="success-content">
          <div class="success-icon">✓</div>
          <h3>购买成功</h3>
          <p>订单已创建，可以在我的订单中查看</p>
        </div>
        <button class="success-confirm-btn" @click="handleSuccessConfirm">确认 ({{ countdown }}秒)</button>
      </div>
    </div>

    <!-- 用户评价 -->
    <div class="section">
      <h2 class="section-title">用户评价</h2>
      <div class="reviews-container">
        <div v-for="review in reviews" :key="review.id" class="review-card">
          <div class="review-header">
            <div class="reviewer-info">
              <div class="reviewer-avatar">{{ review.user.charAt(0) }}</div>
              <div class="reviewer-details">
                <h4 class="reviewer-name">{{ review.user }}</h4>
                <div class="review-rating">
                  <span v-for="i in 5" :key="i" class="star" :class="{ 'active': i <= review.rating }">&#9733;</span>
                </div>
              </div>
            </div>
            <div class="review-date">{{ review.date }}</div>
          </div>
          <div class="review-content">{{ review.content }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'
import FoodIcon from '../components/FoodIcon.vue'
import { getShopIcon, getFoodIcon } from '../utils/imageHelper'

const getShopIconType = (shopName: string) => {
  return getShopIcon(shopName)
}

const getDishIconType = (dishName: string) => {
  return getFoodIcon(dishName)
}

const router = useRouter()
const route = useRoute()

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

interface Category {
  id: number
  name: string
  icon: string
}

interface Dish {
  id: number
  name: string
  description: string
  price: number
  imageUrl: string
  shopId: number
}

interface Review {
  id: number
  user: string
  rating: number
  content: string
  date: string
}

const shops = ref<Shop[]>([])
const categories = ref<Category[]>([
  { id: 1, name: '快餐', icon: '🍔' },
  { id: 2, name: '奶茶', icon: '🥤' },
  { id: 3, name: '烧烤', icon: '🍢' },
  { id: 4, name: '火锅', icon: '🍲' },
  { id: 5, name: '披萨', icon: '🍕' },
  { id: 6, name: '水果', icon: '🍎' }
])

const recommendedDishes = ref<Dish[]>([])

// 从后端获取菜品数据
const fetchDishes = async () => {
  try {
    const response = await axios.get('/api/dishes')
    // 去重：根据菜品名称去重，保留第一个出现的菜品
    const seenNames = new Set<string>()
    const uniqueDishes = response.data.filter((dish: Dish) => {
      if (seenNames.has(dish.name)) {
        return false
      }
      seenNames.add(dish.name)
      return true
    })
    recommendedDishes.value = uniqueDishes.slice(0, 8) // 取前8个不重复的菜品作为推荐
  } catch (error) {
    console.error('Failed to fetch dishes:', error)
    // 如果获取失败，使用默认数据
    recommendedDishes.value = [
      { id: 1, name: '香辣鸡腿堡', description: '鲜嫩多汁的鸡腿肉，搭配香辣酱料', price: 18.9, imageUrl: 'https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300&h=300&fit=crop' },
      { id: 2, name: '珍珠奶茶', description: '香甜可口的珍珠奶茶，口感丝滑', price: 12.0, imageUrl: '' },
      { id: 3, name: '羊肉串', description: '新鲜羊肉，炭火烤制', price: 3.0, imageUrl: '' },
      { id: 4, name: '番茄火锅', description: '浓郁番茄汤底，营养丰富', price: 88.0, imageUrl: 'https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=300&h=300&fit=crop' }
    ]
  }
}

const reviews = ref<Review[]>([
  { id: 1, user: '小明', rating: 5, content: '饭菜很美味，配送速度也很快，下次还会点！', date: '2026-04-25' },
  { id: 2, user: '小红', rating: 4, content: '菜品新鲜，包装完好，味道不错', date: '2026-04-24' },
  { id: 3, user: '小李', rating: 5, content: '服务态度好，餐品质量高，非常满意', date: '2026-04-23' }
])

const selectedDish = ref<Dish | null>(null)
const quantity = ref(1)
const selectedCategory = ref('')
const selectedSpec = ref('')
const remark = ref('')
const address = ref('')
const phone = ref('')
const showSuccessModal = ref(false)
const countdown = ref(3)

// 检查图片URL是否有效（判断是否为完整的HTTP/HTTPS链接）
const isValidImageUrl = (url: string): boolean => {
  if (!url || typeof url !== 'string') return false
  return /^https?:\/\//i.test(url)
}

// 根据菜品名称获取品类列表
const getCategories = (dishName: string) => {
  if (dishName.includes('汉堡')) {
    return [
      { label: '汉堡', value: 'burger' },
      { label: '套餐', value: 'combo' },
      { label: '单品', value: 'single' }
    ]
  } else if (dishName.includes('奶茶')) {
    return [
      { label: '奶茶', value: 'milk_tea' },
      { label: '果茶', value: 'fruit_tea' },
      { label: '咖啡', value: 'coffee' }
    ]
  } else if (dishName.includes('串')) {
    return [
      { label: '烤串', value: 'grilled' },
      { label: '炸串', value: 'fried' },
      { label: '冷串', value: 'cold' }
    ]
  } else if (dishName.includes('火锅')) {
    return [
      { label: '单人锅', value: 'single' },
      { label: '双人锅', value: 'double' },
      { label: '多人锅', value: 'group' }
    ]
  } else {
    return [
      { label: '主食', value: 'main' },
      { label: '配菜', value: 'side' },
      { label: '饮料', value: 'drink' }
    ]
  }
}

// 根据菜品名称获取规格列表
const getSpecs = (dishName: string) => {
  if (dishName.includes('汉堡')) {
    return [
      { label: '香辣', value: 'spicy' },
      { label: '原味', value: 'original' },
      { label: '烧烤', value: 'bbq' },
      { label: '奥尔良', value: 'orleans' }
    ]
  } else if (dishName.includes('奶茶')) {
    return [
      { label: '大杯', value: 'large' },
      { label: '中杯', value: 'medium' },
      { label: '小杯', value: 'small' }
    ]
  } else if (dishName.includes('串')) {
    return [
      { label: '微辣', value: 'mild' },
      { label: '中辣', value: 'medium' },
      { label: '特辣', value: 'spicy' }
    ]
  } else if (dishName.includes('火锅')) {
    return [
      { label: '微辣', value: 'mild' },
      { label: '中辣', value: 'medium' },
      { label: '特辣', value: 'spicy' },
      { label: '清汤', value: 'clear' }
    ]
  } else {
    return [
      { label: '原味', value: 'original' },
      { label: '微辣', value: 'mild' },
      { label: '中辣', value: 'medium' }
    ]
  }
}

const showDishDetail = (dish: Dish) => {
  selectedDish.value = dish
  quantity.value = 1
  const categories = getCategories(dish.name)
  selectedCategory.value = categories[0]?.value || ''
  const specs = getSpecs(dish.name)
  selectedSpec.value = specs[0]?.value || ''
  remark.value = ''
  address.value = ''
  phone.value = ''
}

const closeDishDetail = () => {
  selectedDish.value = null
  quantity.value = 1
  selectedCategory.value = ''
  selectedSpec.value = ''
  remark.value = ''
}

const increaseQuantity = () => {
  quantity.value++
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const validateQuantity = () => {
  if (!quantity.value || quantity.value < 1) {
    quantity.value = 1
  }
}

const addToCartFromDetail = (dish: Dish) => {
  const categoryLabel = getCategories(dish.name).find(c => c.value === selectedCategory.value)?.label || ''
  const specLabel = getSpecs(dish.name).find(s => s.value === selectedSpec.value)?.label || ''
  let info = `${dish.name}`
  if (categoryLabel) info += ` (${categoryLabel})`
  if (specLabel) info += ` - ${specLabel}`
  info += ` × ${quantity.value}`
  if (remark.value) info += `\n备注：${remark.value}`
  alert(`${info} 已加入购物车！`)
  closeDishDetail()
}

const buyNow = async (dish: Dish) => {
  // 验证是否已登录
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    alert('请先登录')
    router.push('/login')
    return
  }
  
  // 验证收货地址和电话
  if (!address.value.trim()) {
    alert('请填写收货地址')
    return
  }
  if (!phone.value.trim()) {
    alert('请填写联系电话')
    return
  }
  
  const categoryLabel = getCategories(dish.name).find(c => c.value === selectedCategory.value)?.label || ''
  const specLabel = getSpecs(dish.name).find(s => s.value === selectedSpec.value)?.label || ''
  const user = JSON.parse(userStr)
  
  // 构建订单数据
  const orderData = {
    order: {
      userId: user.id,
      shopId: dish.shopId,
      totalPrice: dish.price * quantity.value,
      address: address.value,
      phone: phone.value,
      status: 'processing',
      remark: remark.value
    },
    orderItems: [{
      dishId: dish.id,
      quantity: quantity.value,
      price: dish.price,
      category: categoryLabel,
      spec: specLabel
    }]
  }

  try {
    const response = await axios.post('/api/orders', orderData)
    if (response.status === 201) {
      closeDishDetail()
      countdown.value = 3
      showSuccessModal.value = true
      startCountdown()
    }
  } catch (error) {
    console.error('Failed to place order:', error)
    alert('购买失败，请稍后重试')
  }
}

let countdownTimer: ReturnType<typeof setInterval> | null = null

const startCountdown = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
  }
  countdown.value = 3
  countdownTimer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      handleSuccessConfirm()
    }
  }, 1000)
}

const clearCountdown = () => {
  if (countdownTimer) {
    clearInterval(countdownTimer)
    countdownTimer = null
  }
}

const handleSuccessClose = () => {
  clearCountdown()
  showSuccessModal.value = false
}

const handleSuccessConfirm = () => {
  clearCountdown()
  showSuccessModal.value = false
  router.push('/orders')
}

const fetchShops = async () => {
  try {
    const response = await axios.get('/api/shops')
    // 后端返回的是分页数据，需要提取content字段
    shops.value = response.data.content || response.data
  } catch (error) {
    console.error('Failed to fetch shops:', error)
  }
}

onMounted(() => {
  fetchShops()
  fetchDishes()
  
  // 检查URL参数中是否有滚动位置
  if (route.query.scroll) {
    const scrollY = parseInt(route.query.scroll as string) || 0
    // 使用requestAnimationFrame确保DOM已渲染完成
    requestAnimationFrame(() => {
      window.scrollTo({
        top: scrollY,
        behavior: 'smooth'
      })
    })
  }
})
</script>

<style scoped>
.home {
  padding: 0;
  font-family: Arial, sans-serif;
}

/* 横幅样式 */
.banner {
  background: linear-gradient(135deg, #FFD100, #FFB800);
  color: #333;
  padding: 4rem 0;
  text-align: center;
  margin-bottom: 2rem;
}

.banner-content h1 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  font-weight: bold;
}

.banner-content p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.banner-buttons {
  display: flex;
  justify-content: center;
  gap: 1rem;
}

.btn-primary {
  background-color: #FF6B35;
  color: white;
  padding: 0.8rem 1.5rem;
  border-radius: 4px;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background-color: #E55A2B;
  transform: translateY(-2px);
}

.btn-secondary {
  background-color: transparent;
  color: #333;
  padding: 0.8rem 1.5rem;
  border: 2px solid #333;
  border-radius: 4px;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background-color: #333;
  color: #FFD100;
  transform: translateY(-2px);
}

/* 通用部分样式 */
.section {
  padding: 2rem 0;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.8rem;
  margin-bottom: 1.5rem;
  color: #333;
  text-align: center;
}

/* 分类样式 */
.categories {
  padding: 2rem 0;
  background-color: #f8f9fa;
  margin-bottom: 2rem;
}

.categories-container {
  display: flex;
  justify-content: center;
  gap: 2rem;
  flex-wrap: wrap;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1rem;
}

.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 1.5rem;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  min-width: 100px;
}

.category-card:hover {
  transform: translateY(-5px);
}

.category-icon {
  font-size: 2.5rem;
  margin-bottom: 0.5rem;
}

.category-name {
  font-size: 0.9rem;
  color: #333;
  font-weight: 500;
}

/* 商家列表样式 */
.shops-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.shop-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.shop-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.shop-icon-wrapper {
  padding: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

.shop-info {
  padding: 1.2rem;
}

.shop-name {
  font-size: 1.2rem;
  font-weight: bold;
  margin: 0 0 0.5rem 0;
  color: #333;
}

.shop-description {
  font-size: 0.9rem;
  color: #666;
  margin: 0 0 1rem 0;
  line-height: 1.4;
}

.shop-details {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  font-size: 0.85rem;
}

.shop-address {
  color: #666;
  flex: 1;
}

.shop-status {
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  font-weight: 500;
}

.shop-status.open {
  background-color: #e6f7ee;
  color: #28a745;
}

.shop-status.closed {
  background-color: #fff3cd;
  color: #ffc107;
}

.shop-link {
  display: inline-block;
  background-color: #FF6B35;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.3s ease;
}

.shop-link:hover {
  background-color: #E55A2B;
}

/* 推荐菜品样式 */
.dishes-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 1.5rem;
}

.dish-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: transform 0.3s ease;
}

.dish-card:hover {
  transform: translateY(-5px);
}

.dish-icon-wrapper {
  padding: 1rem;
  display: flex;
  justify-content: center;
  align-items: center;
}

.dish-info {
  padding: 1rem;
}

.dish-name {
  font-size: 1.1rem;
  font-weight: bold;
  margin: 0 0 0.5rem 0;
  color: #333;
}

.dish-description {
  font-size: 0.85rem;
  color: #666;
  margin: 0 0 0.8rem 0;
  line-height: 1.3;
}

.dish-price {
  font-size: 1.2rem;
  font-weight: bold;
  color: #FF6B35;
}

/* 用户评价样式 */
.reviews-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.review-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 1.5rem;
  transition: transform 0.3s ease;
}

.review-card:hover {
  transform: translateY(-5px);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.reviewer-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background-color: #ff6b6b;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 1.2rem;
}

.reviewer-name {
  font-size: 1rem;
  font-weight: bold;
  margin: 0 0 0.2rem 0;
  color: #333;
}

.review-rating {
  font-size: 0.9rem;
}

.star {
  color: #ddd;
  margin-right: 2px;
}

.star.active {
  color: #ffc107;
}

.review-date {
  font-size: 0.8rem;
  color: #999;
}

.review-content {
  font-size: 0.95rem;
  color: #666;
  line-height: 1.4;
}

/* 菜品详情弹窗样式 */
.dish-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.dish-modal {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.2);
}

.dish-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

.dish-modal-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background-color: #f8f9fa;
  color: #333;
}

.dish-modal-content {
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.dish-modal-image {
  width: 100%;
  max-height: 300px;
  overflow: hidden;
  border-radius: 8px;
}

.dish-modal-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.dish-icon-large {
  width: 100%;
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 6rem;
}

.dish-modal-info {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.dish-modal-description {
  font-size: 1rem;
  color: #666;
  line-height: 1.5;
  margin: 0;
}

.dish-modal-price {
  font-size: 1.5rem;
  font-weight: bold;
  color: #ff6b6b;
}

.dish-modal-category,
.dish-modal-specs,
.dish-modal-quantity,
.dish-modal-remark {
  margin: 1rem 0;
}

.dish-modal-category h4,
.dish-modal-specs h4,
.dish-modal-quantity h4,
.dish-modal-remark h4 {
  font-size: 1rem;
  font-weight: 500;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.category-select {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  color: #333;
  background-color: white;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.category-select:hover {
  border-color: #ff6b6b;
}

.category-select:focus {
  outline: none;
  border-color: #ff6b6b;
}

.specs-container {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.spec-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.spec-item:hover {
  border-color: #ff6b6b;
}

.spec-item input[type="radio"] {
  accent-color: #ff6b6b;
}

.remark-input {
  width: 100%;
  min-height: 80px;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  color: #333;
  resize: vertical;
  transition: border-color 0.3s ease;
}

.remark-input:hover {
  border-color: #ff6b6b;
}

.remark-input:focus {
  outline: none;
  border-color: #ff6b6b;
}

.remark-input::placeholder {
  color: #999;
}

.address-input {
  width: 100%;
  min-height: 60px;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  color: #333;
  resize: vertical;
  transition: border-color 0.3s ease;
}

.address-input:hover {
  border-color: #ff6b6b;
}

.address-input:focus {
  outline: none;
  border-color: #ff6b6b;
}

.address-input::placeholder {
  color: #999;
}

.phone-input {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  color: #333;
  transition: border-color 0.3s ease;
}

.phone-input:hover {
  border-color: #ff6b6b;
}

.phone-input:focus {
  outline: none;
  border-color: #ff6b6b;
}

.phone-input::placeholder {
  color: #999;
}

.dish-modal-quantity {
  margin: 1rem 0;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 1rem;
  width: fit-content;
}

.quantity-control button {
  width: 36px;
  height: 36px;
  border: 1px solid #ddd;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.quantity-control button:hover:not(:disabled) {
  border-color: #ff6b6b;
  color: #ff6b6b;
}

.quantity-control button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.quantity-control .quantity-input {
  min-width: 60px;
  width: 60px;
  height: 36px;
  text-align: center;
  font-size: 1rem;
  font-weight: 500;
  border: 1px solid #ddd;
  border-radius: 4px;
  outline: none;
  transition: all 0.3s ease;
}

.quantity-control .quantity-input:focus {
  border-color: #ff6b6b;
}

.quantity-control .quantity-input::-webkit-outer-spin-button,
.quantity-control .quantity-input::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.dish-modal-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.add-to-cart-modal-btn {
  flex: 1;
  background-color: #FF6B35;
  color: white;
  border: none;
  padding: 1rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.add-to-cart-modal-btn:hover {
  background-color: #E55A2B;
}

.buy-now-btn {
  flex: 1;
  background-color: #FFD100;
  color: #333;
  border: none;
  padding: 1rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.buy-now-btn:hover {
  background-color: #FFB800;
}

/* 购买成功提示框样式 */
.success-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.success-modal {
  background-color: #FFD100;
  border-radius: 8px;
  width: 90%;
  max-width: 350px;
  padding: 2rem;
  position: relative;
  color: #333;
  text-align: center;
}

.success-back-btn {
  position: absolute;
  top: 1rem;
  left: 1rem;
  background: none;
  border: none;
  color: white;
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0.3rem 0.6rem;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.success-back-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.success-content {
  margin-bottom: 1.5rem;
}

.success-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  background-color: white;
  color: #FF6B35;
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 1rem;
}

.success-content h3 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

.success-content p {
  font-size: 0.95rem;
  opacity: 0.9;
}

.success-confirm-btn {
  position: absolute;
  bottom: 1rem;
  right: 1rem;
  background-color: white;
  color: #FF6B35;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.success-confirm-btn:hover {
  background-color: #f0f0f0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner {
    padding: 2.5rem 0;
  }
  
  .banner-content h1 {
    font-size: 1.8rem;
  }
  
  .banner-content p {
    font-size: 1rem;
    margin-bottom: 1.5rem;
  }
  
  .banner-buttons {
    flex-direction: column;
    align-items: center;
    gap: 0.8rem;
  }
  
  .btn-primary, .btn-secondary {
    padding: 0.7rem 1.2rem;
    font-size: 0.9rem;
  }
  
  .section {
    padding: 0 0.8rem;
    margin-bottom: 1.5rem;
  }
  
  .section-title {
    font-size: 1.4rem;
    margin-bottom: 1rem;
  }
  
  .categories-container {
    gap: 0.8rem;
    padding: 0 0.5rem;
  }
  
  .category-card {
    min-width: 70px;
    padding: 0.8rem;
  }
  
  .category-icon {
    font-size: 1.8rem;
  }
  
  .category-name {
    font-size: 0.85rem;
  }
  
  .shops-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.8rem;
  }
  
  .shop-card {
    border-radius: 6px;
  }
  
  .shop-image {
    height: 120px;
  }
  
  .shop-info {
    padding: 0.8rem;
  }
  
  .shop-name {
    font-size: 1rem;
  }
  
  .shop-description {
    font-size: 0.8rem;
    margin-bottom: 0.6rem;
  }
  
  .shop-details {
    font-size: 0.75rem;
    margin-bottom: 0.6rem;
  }
  
  .shop-link {
    padding: 0.4rem 0.8rem;
    font-size: 0.85rem;
  }
  
  .dishes-container {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.8rem;
  }
  
  .dish-card {
    border-radius: 6px;
  }
  
  .dish-image {
    height: 120px;
  }
  
  .dish-info {
    padding: 0.8rem;
  }
  
  .dish-name {
    font-size: 1rem;
  }
  
  .dish-description {
    font-size: 0.8rem;
    margin-bottom: 0.6rem;
  }
  
  .dish-price {
    font-size: 1.1rem;
  }
  
  .reviews-container {
    grid-template-columns: 1fr;
    gap: 0.8rem;
  }
  
  .review-card {
    padding: 1rem;
  }
  
  .review-header {
    flex-direction: column;
    gap: 0.5rem;
  }
  
  .reviewer-info {
    gap: 0.8rem;
  }
  
  .reviewer-avatar {
    width: 35px;
    height: 35px;
    font-size: 1rem;
  }
  
  .review-content {
    font-size: 0.9rem;
  }
  
  .dish-modal {
    width: 95%;
    margin: 0.5rem;
    max-height: 90vh;
  }
  
  .dish-modal-header {
    padding: 1rem;
  }
  
  .dish-modal-header h3 {
    font-size: 1.1rem;
  }
  
  .dish-modal-content {
    padding: 1rem;
    gap: 1rem;
  }
  
  .dish-modal-image {
    max-height: 200px;
  }
  
  .dish-modal-price {
    font-size: 1.3rem;
  }
  
  .dish-modal-category h4,
  .dish-modal-specs h4,
  .dish-modal-quantity h4,
  .dish-modal-remark h4 {
    font-size: 0.9rem;
  }
  
  .category-select, .remark-input {
    padding: 0.6rem;
    font-size: 0.9rem;
  }
  
  .spec-item {
    padding: 0.4rem 0.8rem;
    font-size: 0.9rem;
  }
  
  .quantity-control {
    gap: 0.6rem;
  }
  
  .quantity-control button {
    width: 32px;
    height: 32px;
    font-size: 1.1rem;
  }
  
  .quantity-control span {
    min-width: 35px;
    font-size: 0.9rem;
  }
  
  .dish-modal-actions {
    flex-direction: column;
    gap: 0.6rem;
  }
  
  .add-to-cart-modal-btn, .buy-now-btn {
    padding: 0.8rem;
    font-size: 0.9rem;
  }
}

@media (max-width: 480px) {
  .banner {
    padding: 2rem 0;
  }
  
  .banner-content h1 {
    font-size: 1.5rem;
  }
  
  .banner-content p {
    font-size: 0.9rem;
  }
  
  .category-card {
    min-width: 65px;
    padding: 0.6rem;
  }
  
  .category-icon {
    font-size: 1.5rem;
  }
  
  .category-name {
    font-size: 0.75rem;
  }
  
  .shops-container {
    grid-template-columns: 1fr;
  }
  
  .dishes-container {
    grid-template-columns: 1fr;
  }
  
  .shop-image, .dish-image {
    height: 140px;
  }
  
  .dish-modal-image {
    max-height: 180px;
  }
}
</style>
