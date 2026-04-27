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
          <div class="shop-image">
            <img :src="shop.imageUrl || 'https://via.placeholder.com/300x200'" :alt="shop.name" />
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
          <div class="dish-image">
            <img :src="dish.imageUrl || 'https://via.placeholder.com/200x200'" :alt="dish.name" />
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
            <img :src="selectedDish.imageUrl || 'https://via.placeholder.com/400x300'" :alt="selectedDish.name" />
          </div>
          <div class="dish-modal-info">
            <p class="dish-modal-description">{{ selectedDish.description }}</p>
            <div class="dish-modal-price">¥{{ selectedDish.price }}</div>
            
            <!-- 风味选项 -->
            <div class="dish-modal-options" v-if="selectedDish.name.includes('汉堡')">
              <h4>风味选择</h4>
              <div class="options-container">
                <label class="option-item" v-for="option in flavorOptions" :key="option.value">
                  <input type="radio" v-model="selectedFlavor" :value="option.value" />
                  <span>{{ option.label }}</span>
                </label>
              </div>
            </div>
            
            <!-- 数量选择 -->
            <div class="dish-modal-quantity">
              <h4>数量</h4>
              <div class="quantity-control">
                <button @click="decreaseQuantity" :disabled="quantity <= 1">-</button>
                <span>{{ quantity }}</span>
                <button @click="increaseQuantity">+</button>
              </div>
            </div>
            
            <div class="dish-modal-actions">
              <button class="add-to-cart-modal-btn" @click="addToCartFromDetail(selectedDish)">加入购物车</button>
              <button class="buy-now-btn" @click="buyNow(selectedDish)">立即购买</button>
            </div>
          </div>
        </div>
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
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

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

const recommendedDishes = ref<Dish[]>([
  { id: 1, name: '香辣鸡腿堡', description: '鲜嫩多汁的鸡腿肉，搭配香辣酱料', price: 18.9, imageUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=delicious%20spicy%20chicken%20burger&image_size=square' },
  { id: 2, name: '珍珠奶茶', description: '香甜可口的珍珠奶茶，口感丝滑', price: 12.0, imageUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=tasty%20bubble%20milk%20tea&image_size=square' },
  { id: 3, name: '羊肉串', description: '新鲜羊肉，炭火烤制', price: 3.0, imageUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=delicious%20lamb%20skewers&image_size=square' },
  { id: 4, name: '番茄火锅', description: '浓郁番茄汤底，营养丰富', price: 88.0, imageUrl: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=tomato%20hot%20pot&image_size=square' }
])

const reviews = ref<Review[]>([
  { id: 1, user: '小明', rating: 5, content: '饭菜很美味，配送速度也很快，下次还会点！', date: '2026-04-25' },
  { id: 2, user: '小红', rating: 4, content: '菜品新鲜，包装完好，味道不错', date: '2026-04-24' },
  { id: 3, user: '小李', rating: 5, content: '服务态度好，餐品质量高，非常满意', date: '2026-04-23' }
])

const selectedDish = ref<Dish | null>(null)
const quantity = ref(1)
const selectedFlavor = ref('spicy')

const flavorOptions = [
  { label: '香辣', value: 'spicy' },
  { label: '原味', value: 'original' },
  { label: '烧烤', value: 'bbq' }
]

const showDishDetail = (dish: Dish) => {
  selectedDish.value = dish
  quantity.value = 1
  selectedFlavor.value = 'spicy'
}

const closeDishDetail = () => {
  selectedDish.value = null
  quantity.value = 1
  selectedFlavor.value = 'spicy'
}

const increaseQuantity = () => {
  quantity.value++
}

const decreaseQuantity = () => {
  if (quantity.value > 1) {
    quantity.value--
  }
}

const addToCartFromDetail = (dish: Dish) => {
  // 这里可以实现加入购物车的逻辑
  alert(`${dish.name} (${flavorOptions.find(f => f.value === selectedFlavor.value)?.label}) × ${quantity.value} 已加入购物车！`)
  closeDishDetail()
}

const buyNow = async (dish: Dish) => {
  // 构建订单数据
  const orderData = {
    order: {
      userId: 1,
      shopId: 1, // 假设商家ID为1
      totalPrice: dish.price * quantity.value,
      address: '收货地址',
      phone: '13800138000',
      status: 'pending'
    },
    orderItems: [{
      dishId: dish.id,
      quantity: quantity.value,
      price: dish.price
    }]
  }

  try {
    // 调用后端API创建订单
    const response = await axios.post('/api/orders', orderData)
    if (response.status === 201) {
      alert('购买成功！订单已创建，可以在我的订单中查看。')
      closeDishDetail()
      // 跳转到订单页面
      router.push('/orders')
    }
  } catch (error) {
    console.error('Failed to place order:', error)
    alert('购买失败，请稍后重试')
  }
}

const fetchShops = async () => {
  try {
    const response = await axios.get('/api/shops')
    shops.value = response.data
  } catch (error) {
    console.error('Failed to fetch shops:', error)
  }
}

onMounted(() => {
  fetchShops()
})
</script>

<style scoped>
.home {
  padding: 0;
  font-family: Arial, sans-serif;
}

/* 横幅样式 */
.banner {
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  color: white;
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
  background-color: white;
  color: #ff6b6b;
  padding: 0.8rem 1.5rem;
  border-radius: 4px;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s ease;
}

.btn-primary:hover {
  background-color: #f8f9fa;
  transform: translateY(-2px);
}

.btn-secondary {
  background-color: transparent;
  color: white;
  padding: 0.8rem 1.5rem;
  border: 2px solid white;
  border-radius: 4px;
  text-decoration: none;
  font-weight: bold;
  transition: all 0.3s ease;
}

.btn-secondary:hover {
  background-color: white;
  color: #ff6b6b;
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

.shop-image {
  height: 200px;
  overflow: hidden;
}

.shop-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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
  background-color: #ff6b6b;
  color: white;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: background-color 0.3s ease;
}

.shop-link:hover {
  background-color: #ee5a52;
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

.dish-image {
  height: 180px;
  overflow: hidden;
}

.dish-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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
  color: #ff6b6b;
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

.dish-modal-options {
  margin: 1rem 0;
}

.dish-modal-options h4,
.dish-modal-quantity h4 {
  font-size: 1rem;
  font-weight: 500;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.options-container {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.option-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 1rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.option-item:hover {
  border-color: #ff6b6b;
}

.option-item input[type="radio"] {
  accent-color: #ff6b6b;
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

.quantity-control span {
  min-width: 40px;
  text-align: center;
  font-size: 1rem;
  font-weight: 500;
}

.dish-modal-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1rem;
}

.add-to-cart-modal-btn {
  flex: 1;
  background-color: #ff6b6b;
  color: white;
  border: none;
  padding: 1rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.add-to-cart-modal-btn:hover {
  background-color: #ee5a52;
}

.buy-now-btn {
  flex: 1;
  background-color: #28a745;
  color: white;
  border: none;
  padding: 1rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.buy-now-btn:hover {
  background-color: #218838;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .banner-content h1 {
    font-size: 2rem;
  }
  
  .banner-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .categories-container {
    gap: 1rem;
  }
  
  .category-card {
    min-width: 80px;
    padding: 1rem;
  }
  
  .category-icon {
    font-size: 2rem;
  }
  
  .dish-modal {
    width: 95%;
    margin: 1rem;
  }
  
  .dish-modal-actions {
    flex-direction: column;
  }
}
</style>
