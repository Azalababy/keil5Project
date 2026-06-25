<template>
  <div class="shop-admin">
    <div class="admin-header">
      <div class="header-left">
        <h1>商家管理后台</h1>
        <p>欢迎，{{ shop?.name }}</p>
      </div>
      <div class="header-right">
        <button @click="logout" class="logout-btn">退出登录</button>
      </div>
    </div>
    
    <div class="admin-content">
      <div class="sidebar">
        <div 
          v-for="item in menuItems" 
          :key="item.key" 
          class="menu-item"
          :class="{ active: activeMenu === item.key }"
          @click="activeMenu = item.key"
        >
          <span class="menu-icon">{{ item.icon }}</span>
          <span class="menu-text">{{ item.label }}</span>
        </div>
      </div>
      
      <div class="main-content">
        <!-- 数据统计 -->
        <div v-if="activeMenu === 'dashboard'" class="section">
          <div class="section-header">
            <h2>数据概览</h2>
          </div>
          
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon orders-icon">📋</div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalOrders }}</div>
                <div class="stat-label">总订单数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon revenue-icon">💰</div>
              <div class="stat-info">
                <div class="stat-value">¥{{ stats.totalRevenue }}</div>
                <div class="stat-label">总收入</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon dishes-icon">🍔</div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.totalDishes }}</div>
                <div class="stat-label">商品数量</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon pending-icon">⏳</div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.pendingOrders }}</div>
                <div class="stat-label">待处理订单</div>
              </div>
            </div>
          </div>

          <div class="chart-section">
            <h3>订单趋势（近7天）</h3>
            <div class="horizontal-chart">
              <div v-for="(item, index) in orderTrend" :key="index" class="chart-row">
                <span class="chart-label">{{ item.date }}</span>
                <div class="chart-bar-container">
                  <div class="chart-bar" :style="{ width: (item.revenue / maxRevenue * 100) + '%' }">
                    <span class="chart-value">¥{{ item.revenue }} ({{ item.count }}单)</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 商家信息 -->
        <div v-if="activeMenu === 'profile'" class="section">
          <div class="section-header">
            <h2>商家信息</h2>
            <button @click="showEditProfileModal = true" class="add-btn">编辑信息</button>
          </div>
          
          <div class="profile-info">
            <div class="profile-row">
              <label>商家名称</label>
              <span>{{ shop?.name }}</span>
            </div>
            <div class="profile-row">
              <label>联系电话</label>
              <span>{{ shop?.phone }}</span>
            </div>
            <div class="profile-row">
              <label>店铺地址</label>
              <span>{{ shop?.address }}</span>
            </div>
            <div class="profile-row">
              <label>店铺评分</label>
              <span>{{ shop?.rating || '暂无评分' }} ⭐</span>
            </div>
            <div class="profile-row">
              <label>营业状态</label>
              <span :class="['status-badge', shop?.status === 'open' ? 'available' : 'unavailable']">
                {{ shop?.status === 'open' ? '营业中' : '休息中' }}
              </span>
            </div>
            <div class="profile-row">
              <label>配送费</label>
              <span>¥{{ shop?.deliveryFee }}</span>
            </div>
            <div class="profile-row">
              <label>起送价</label>
              <span>¥{{ shop?.minOrder }}</span>
            </div>
            <div class="profile-row">
              <label>店铺描述</label>
              <span>{{ shop?.description || '暂无描述' }}</span>
            </div>
          </div>
        </div>

        <!-- 商品分类管理 -->
        <div v-if="activeMenu === 'categories'" class="section">
          <div class="section-header">
            <h2>商品分类</h2>
            <button @click="showAddCategoryModal = true" class="add-btn">+ 添加分类</button>
          </div>
          
          <div class="categories-list">
            <div v-for="category in categories" :key="category.id" class="category-card">
              <div class="category-info">
                <span class="category-name">{{ category.name }}</span>
                <span class="category-count">{{ getDishCountByCategory(category.id) }} 件商品</span>
              </div>
              <div class="category-actions">
                <button @click="editCategory(category)" class="action-btn edit">编辑</button>
                <button @click="deleteCategory(category.id)" class="action-btn delete">删除</button>
              </div>
            </div>
          </div>
        </div>

        <!-- 商品管理 -->
        <div v-if="activeMenu === 'dishes'" class="section">
          <div class="section-header">
            <h2>商品管理</h2>
            <button @click="showAddDishModal = true" class="add-btn">+ 添加商品</button>
          </div>
          
          <div class="dishes-table">
            <table>
              <thead>
                <tr>
                  <th>商品名称</th>
                  <th>分类</th>
                  <th>描述</th>
                  <th>价格</th>
                  <th>状态</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="dish in dishes" :key="dish.id">
                  <td>{{ dish.name }}</td>
                  <td>{{ getCategoryName(dish.categoryId) }}</td>
                  <td>{{ dish.description }}</td>
                  <td>¥{{ dish.price }}</td>
                  <td>
                    <span :class="['status-badge', dish.status]">
                      {{ dish.status === 'available' ? '上架' : '下架' }}
                    </span>
                  </td>
                  <td>
                    <button @click="toggleDishStatus(dish)" class="action-btn">
                      {{ dish.status === 'available' ? '下架' : '上架' }}
                    </button>
                    <button @click="editDish(dish)" class="action-btn edit">编辑</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        
        <!-- 订单管理 -->
        <div v-if="activeMenu === 'orders'" class="section">
          <div class="section-header">
            <h2>订单管理</h2>
          </div>
          
          <div class="orders-container">
            <div v-for="order in orders" :key="order.id" class="order-card">
              <div class="order-header">
                <div class="order-id">订单号：{{ order.id }}</div>
                <div :class="['order-status', order.status]">{{ getStatusText(order.status) }}</div>
              </div>
              <div class="order-info">
                <div>用户ID：{{ order.userId }}</div>
                <div>下单时间：{{ formatDate(order.createdAt) }}</div>
                <div>收货地址：{{ order.address }}</div>
                <div>联系电话：{{ order.phone }}</div>
                <div v-if="order.riderName" class="rider-info">
                  <span>骑手：{{ order.riderName }} ({{ order.riderPhone }})</span>
                </div>
                <div v-if="order.remark" class="order-remark">
                  <span>备注：{{ order.remark }}</span>
                </div>
              </div>
              <div class="order-items">
                <div v-for="item in order.items" :key="item.id" class="order-item">
                  <span>{{ item.name }}</span>
                  <span>×{{ item.quantity }}</span>
                  <span>¥{{ item.price }}</span>
                </div>
              </div>
              <div class="order-footer">
                <span>总计：¥{{ order.totalPrice }}</span>
                <div class="order-actions">
                  <template v-if="order.status === 'pending'">
                    <button @click="acceptOrder(order.id)" class="update-status-btn accept">
                      接单
                    </button>
                    <button @click="showRejectConfirm(order)" class="update-status-btn reject">
                      拒单
                    </button>
                  </template>
                  <button v-if="order.status === 'accepted'" @click="startPreparing(order.id)" class="update-status-btn prepare">
                    开始制作
                  </button>
                  <button v-if="order.status === 'preparing'" @click="readyForPickup(order.id)" class="update-status-btn ready">
                    完成制作（通知骑手）
                  </button>
                  <button v-if="order.status === 'ready_for_pickup'" class="update-status-btn waiting">
                    等待骑手接单...
                  </button>
                  <button v-if="order.status === 'picked_up' || order.status === 'delivering'" class="update-status-btn delivering">
                    配送中...
                  </button>
                  <button v-if="order.status === 'delivered'" class="update-status-btn delivered">
                    已送达
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 用户评价 -->
        <div v-if="activeMenu === 'reviews'" class="section">
          <div class="section-header">
            <h2>用户评价</h2>
          </div>
          
          <div class="reviews-container">
            <div v-for="review in reviews" :key="review.id" class="review-card">
              <div class="review-header">
                <div class="review-order">订单号：{{ review.orderId }}</div>
                <div class="review-rating">
                  <span class="stars">{{ '★'.repeat(review.shopRating || 0) }}{{ '☆'.repeat(5 - (review.shopRating || 0)) }}</span>
                </div>
              </div>
              <div class="review-content">
                <p v-if="review.shopComment" class="review-comment">{{ review.shopComment }}</p>
                <p v-else class="no-comment">暂无评价内容</p>
              </div>
              <div class="review-footer">
                <span class="review-date">{{ formatDate(review.createdAt) }}</span>
              </div>
            </div>
            <div v-if="reviews.length === 0" class="empty-reviews">
              <p>暂无用户评价</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 添加/编辑商品弹窗 -->
    <div v-if="showAddDishModal" class="modal-overlay" @click.self="closeDishModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingDish ? '编辑商品' : '添加商品' }}</h3>
          <button @click="closeDishModal" class="close-btn">×</button>
        </div>
        <form @submit.prevent="saveDish" class="modal-form">
          <div class="form-group">
            <label>商品名称</label>
            <input v-model="dishForm.name" required />
          </div>
          <div class="form-group">
            <label>所属分类</label>
            <select v-model="dishForm.categoryId" required>
              <option value="">请选择分类</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</option>
            </select>
          </div>
          <div class="form-group">
            <label>商品描述</label>
            <textarea v-model="dishForm.description"></textarea>
          </div>
          <div class="form-group">
            <label>价格</label>
            <input type="number" v-model.number="dishForm.price" required />
          </div>
          <div class="form-group">
            <label>图片URL</label>
            <input v-model="dishForm.imageUrl" />
          </div>
          <div class="form-group">
            <label>状态</label>
            <select v-model="dishForm.status">
              <option value="available">上架</option>
              <option value="unavailable">下架</option>
            </select>
          </div>
          <div class="modal-footer">
            <button type="button" @click="closeDishModal" class="cancel-btn">取消</button>
            <button type="submit" class="submit-btn">保存</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 编辑商家信息弹窗 -->
    <div v-if="showEditProfileModal" class="modal-overlay" @click.self="closeProfileModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>编辑商家信息</h3>
          <button @click="closeProfileModal" class="close-btn">×</button>
        </div>
        <form @submit.prevent="saveProfile" class="modal-form">
          <div class="form-group">
            <label>商家名称</label>
            <input v-model="profileForm.name" required />
          </div>
          <div class="form-group">
            <label>联系电话</label>
            <input v-model="profileForm.phone" required />
          </div>
          <div class="form-group">
            <label>店铺地址</label>
            <textarea v-model="profileForm.address"></textarea>
          </div>
          <div class="form-group">
            <label>配送费</label>
            <input type="number" v-model.number="profileForm.deliveryFee" required />
          </div>
          <div class="form-group">
            <label>起送价</label>
            <input type="number" v-model.number="profileForm.minOrder" required />
          </div>
          <div class="form-group">
            <label>营业状态</label>
            <select v-model="profileForm.status">
              <option value="open">营业中</option>
              <option value="closed">休息中</option>
            </select>
          </div>
          <div class="form-group">
            <label>店铺描述</label>
            <textarea v-model="profileForm.description"></textarea>
          </div>
          <div class="modal-footer">
            <button type="button" @click="closeProfileModal" class="cancel-btn">取消</button>
            <button type="submit" class="submit-btn">保存</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 添加/编辑分类弹窗 -->
    <div v-if="showAddCategoryModal" class="modal-overlay" @click.self="closeCategoryModal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>{{ editingCategory ? '编辑分类' : '添加分类' }}</h3>
          <button @click="closeCategoryModal" class="close-btn">×</button>
        </div>
        <form @submit.prevent="saveCategory" class="modal-form">
          <div class="form-group">
            <label>分类名称</label>
            <input v-model="categoryForm.name" required />
          </div>
          <div class="modal-footer">
            <button type="button" @click="closeCategoryModal" class="cancel-btn">取消</button>
            <button type="submit" class="submit-btn">保存</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 拒单二次确认弹窗 -->
    <div v-if="showRejectModal" class="modal-overlay" @click.self="closeRejectModal">
      <div class="modal-content reject-modal">
        <div class="modal-header">
          <h3>⚠️ 确认拒单</h3>
          <button @click="closeRejectModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <p class="reject-warning">您即将拒绝订单 #{{ rejectOrder?.id }}</p>
          <p class="reject-reason">订单金额：¥{{ rejectOrder?.totalPrice }}</p>
          <p class="reject-confirm">请确认是否确定要拒单？此操作不可恢复！</p>
          <div class="confirm-input">
            <input 
              type="text" 
              v-model="rejectConfirmText" 
              placeholder="请输入'确认拒单'以确认"
              class="confirm-text-input"
            />
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" @click="closeRejectModal" class="cancel-btn">取消</button>
          <button 
            type="button" 
            @click="confirmReject" 
            class="submit-btn reject-confirm"
            :disabled="rejectConfirmText !== '确认拒单'"
          >
            确认拒单
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import router from '../router'

interface Dish {
  id: number
  name: string
  description: string
  price: number
  imageUrl: string
  status: string
  shopId: number
  categoryId: number
}

interface Category {
  id: number
  name: string
  shopId: number
}

interface OrderItem {
  id: number
  dishId: number
  quantity: number
  price: number
  name?: string
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
  remark?: string
  items?: OrderItem[]
}

interface Stats {
  totalOrders: number
  totalRevenue: number
  totalDishes: number
  pendingOrders: number
}

interface OrderTrendItem {
  date: string
  count: number
}

interface Review {
  id: number
  orderId: number
  userId: number
  shopId: number
  shopRating: number
  shopComment: string
  createdAt: string
}

const shop = ref<any>(null)
const activeMenu = ref('dashboard')
const dishes = ref<Dish[]>([])
const categories = ref<Category[]>([])
const orders = ref<Order[]>([])
const reviews = ref<Review[]>([])
const stats = ref<Stats>({
  totalOrders: 0,
  totalRevenue: 0,
  totalDishes: 0,
  pendingOrders: 0
})
const orderTrend = ref<{date: string, count: number, revenue: number}[]>([])

// 弹窗状态
const showAddDishModal = ref(false)
const showEditProfileModal = ref(false)
const showAddCategoryModal = ref(false)
const showRejectModal = ref(false)
const editingDish = ref<Dish | null>(null)
const editingCategory = ref<Category | null>(null)
const rejectOrder = ref<Order | null>(null)
const rejectConfirmText = ref('')

// 表单数据
const dishForm = ref({
  name: '',
  description: '',
  price: 0,
  imageUrl: '',
  status: 'available',
  categoryId: 0
})

const profileForm = ref({
  name: '',
  phone: '',
  address: '',
  deliveryFee: 0,
  minOrder: 0,
  status: 'open',
  description: ''
})

const categoryForm = ref({
  name: ''
})

const menuItems = [
  { key: 'dashboard', label: '数据概览', icon: '📊' },
  { key: 'profile', label: '商家信息', icon: '🏪' },
  { key: 'categories', label: '分类管理', icon: '📁' },
  { key: 'dishes', label: '商品管理', icon: '🍔' },
  { key: 'orders', label: '订单管理', icon: '📋' },
  { key: 'reviews', label: '用户评价', icon: '⭐' }
]

const maxRevenue = computed(() => {
  if (orderTrend.value.length === 0) return 1
  return Math.max(...orderTrend.value.map(item => item.revenue), 1)
})

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待接单',
    accepted: '已接单',
    preparing: '制作中',
    ready_for_pickup: '待骑手取餐',
    picked_up: '骑手已取餐',
    delivering: '配送中',
    delivered: '已送达',
    cancelled: '已取消'
  }
  return statusMap[status] || status
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

const getCategoryName = (categoryId: number) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category?.name || '未分类'
}

const getDishCountByCategory = (categoryId: number) => {
  return dishes.value.filter(d => d.categoryId === categoryId).length
}

const loadShopInfo = () => {
  const shopData = localStorage.getItem('shop')
  if (shopData) {
    shop.value = JSON.parse(shopData)
  } else {
    router.push('/login')
  }
}

const fetchDishes = async () => {
  if (!shop.value) return
  try {
    const response = await axios.get(`/api/dishes/shop/${shop.value.id}`)
    dishes.value = response.data
  } catch (error) {
    console.error('Failed to fetch dishes:', error)
  }
}

const fetchCategories = async () => {
  if (!shop.value) return
  try {
    const response = await axios.get(`/api/categories/shop/${shop.value.id}`)
    categories.value = response.data
  } catch (error) {
    console.error('Failed to fetch categories:', error)
  }
}

const fetchOrders = async () => {
  if (!shop.value) return
  try {
    const response = await axios.get(`/api/orders/shop/${shop.value.id}`)
    const orderList = response.data
    for (const order of orderList) {
      const itemsResponse = await axios.get(`/api/orders/${order.id}/items`)
      order.items = itemsResponse.data
      for (const item of order.items) {
        try {
          const dishResponse = await axios.get(`/api/dishes/${item.dishId}`)
          item.name = dishResponse.data.name
        } catch (error) {
          item.name = '未知菜品'
        }
      }
    }
    // 按创建时间倒序排序，新订单显示在上面
    orderList.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    orders.value = orderList
    updateStats()
  } catch (error) {
    console.error('Failed to fetch orders:', error)
  }
}

const fetchStats = async () => {
  if (!shop.value) return
  try {
    const response = await axios.get(`/api/shops/${shop.value.id}/stats`)
    stats.value = response.data
  } catch (error) {
    console.error('Failed to fetch stats:', error)
  }
}

const fetchOrderTrend = async () => {
  if (!shop.value) return
  try {
    const response = await axios.get(`/api/shops/${shop.value.id}/order-trend`)
    console.log('订单趋势API响应:', response.data)
    // 后端返回的字段是 label、value 和 revenue
    orderTrend.value = response.data.map((item: any) => ({
      date: item.label,
      count: item.value,
      revenue: Number(item.revenue) || 0
    }))
    console.log('转换后的orderTrend:', orderTrend.value)
    console.log('maxRevenue:', maxRevenue.value)
  } catch (error) {
    console.error('Failed to fetch order trend:', error)
    // Mock 数据
    const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    orderTrend.value = days.map(day => ({
      date: day,
      count: Math.floor(Math.random() * 50) + 10,
      revenue: Math.floor(Math.random() * 500) + 100
    }))
  }
}

const updateStats = () => {
  stats.value.totalOrders = orders.value.length
  stats.value.pendingOrders = orders.value.filter(o => o.status === 'pending').length
  stats.value.totalRevenue = orders.value.reduce((sum, o) => sum + o.totalPrice, 0)
  stats.value.totalDishes = dishes.value.length
}

const toggleDishStatus = async (dish: Dish) => {
  try {
    const newStatus = dish.status === 'available' ? 'unavailable' : 'available'
    await axios.put(`/api/dishes/${dish.id}/status`, { status: newStatus })
    dish.status = newStatus
  } catch (error) {
    console.error('Failed to update dish status:', error)
  }
}

const addDish = async () => {
  try {
    const newDish = {
      ...dishForm.value,
      shopId: shop.value.id
    }
    await axios.post('/api/dishes', newDish)
    alert('商品添加成功！')
    closeDishModal()
    fetchDishes()
  } catch (error) {
    console.error('Failed to add dish:', error)
    alert('添加失败，请稍后重试')
  }
}

const editDish = (dish: Dish) => {
  editingDish.value = dish
  dishForm.value = {
    name: dish.name,
    description: dish.description || '',
    price: dish.price,
    imageUrl: dish.imageUrl || '',
    status: dish.status,
    categoryId: dish.categoryId || 0
  }
  showAddDishModal.value = true
}

const updateDish = async () => {
  if (!editingDish.value) return
  try {
    const updatedDish = {
      ...dishForm.value,
      shopId: shop.value.id
    }
    await axios.put(`/api/dishes/${editingDish.value.id}`, updatedDish)
    alert('商品更新成功！')
    closeDishModal()
    fetchDishes()
  } catch (error) {
    console.error('Failed to update dish:', error)
    alert('更新失败，请稍后重试')
  }
}

const saveDish = () => {
  if (editingDish.value) {
    updateDish()
  } else {
    addDish()
  }
}

const closeDishModal = () => {
  showAddDishModal.value = false
  editingDish.value = null
  dishForm.value = {
    name: '',
    description: '',
    price: 0,
    imageUrl: '',
    status: 'available',
    categoryId: 0
  }
}

const closeProfileModal = () => {
  showEditProfileModal.value = false
}

const saveProfile = async () => {
  try {
    await axios.put(`/api/shops/${shop.value.id}`, profileForm.value)
    shop.value = { ...shop.value, ...profileForm.value }
    localStorage.setItem('shop', JSON.stringify(shop.value))
    alert('商家信息更新成功！')
    closeProfileModal()
  } catch (error) {
    console.error('Failed to update profile:', error)
    alert('更新失败，请稍后重试')
  }
}

const editCategory = (category: Category) => {
  editingCategory.value = category
  categoryForm.value = {
    name: category.name
  }
  showAddCategoryModal.value = true
}

const addCategory = async () => {
  try {
    const newCategory = {
      ...categoryForm.value,
      shopId: shop.value.id
    }
    await axios.post('/api/categories', newCategory)
    alert('分类添加成功！')
    closeCategoryModal()
    fetchCategories()
  } catch (error) {
    console.error('Failed to add category:', error)
    alert('添加失败，请稍后重试')
  }
}

const updateCategory = async () => {
  if (!editingCategory.value) return
  try {
    await axios.put(`/api/categories/${editingCategory.value.id}`, categoryForm.value)
    alert('分类更新成功！')
    closeCategoryModal()
    fetchCategories()
  } catch (error) {
    console.error('Failed to update category:', error)
    alert('更新失败，请稍后重试')
  }
}

const deleteCategory = async (categoryId: number) => {
  if (!confirm('确定要删除这个分类吗？该分类下的商品将变为未分类状态。')) return
  try {
    await axios.delete(`/api/categories/${categoryId}`)
    alert('分类删除成功！')
    fetchCategories()
    fetchDishes()
  } catch (error) {
    console.error('Failed to delete category:', error)
    alert('删除失败，请稍后重试')
  }
}

const saveCategory = () => {
  if (editingCategory.value) {
    updateCategory()
  } else {
    addCategory()
  }
}

const closeCategoryModal = () => {
  showAddCategoryModal.value = false
  editingCategory.value = null
  categoryForm.value = {
    name: ''
  }
}

// 商家接单
const acceptOrder = async (orderId: number) => {
  try {
    const response = await axios.put(`/api/orders/${orderId}/accept`)
    if (response.status === 200 && response.data) {
      const order = orders.value.find(o => o.id === orderId)
      if (order) {
        order.status = 'accepted'
      }
      updateStats()
      alert('接单成功！')
    } else {
      throw new Error('接单失败：服务器返回无效数据')
    }
  } catch (error: any) {
    console.error('Failed to accept order:', error)
    const errorMessage = error.response?.data?.message || 
                         error.response?.statusText ||
                         error.message || 
                         '接单失败，请重试'
    alert(`接单失败：${errorMessage}`)
  }
}

// 显示拒单确认弹窗
const showRejectConfirm = (order: Order) => {
  rejectOrder.value = order
  rejectConfirmText.value = ''
  showRejectModal.value = true
}

// 关闭拒单确认弹窗
const closeRejectModal = () => {
  showRejectModal.value = false
  rejectOrder.value = null
  rejectConfirmText.value = ''
}

// 确认拒单
const confirmReject = async () => {
  if (!rejectOrder.value) return
  
  try {
    // 调用后端 API 取消订单
    const response = await axios.put(`/api/orders/${rejectOrder.value.id}/status`, {
      status: 'cancelled'
    })
    
    if (response.status === 200 && response.data) {
      const order = orders.value.find(o => o.id === rejectOrder.value?.id)
      if (order) {
        order.status = 'cancelled'
      }
      updateStats()
      alert('已拒绝订单')
      closeRejectModal()
    } else {
      throw new Error('拒单失败：服务器返回无效数据')
    }
  } catch (error: any) {
    console.error('Failed to reject order:', error)
    const errorMessage = error.response?.data?.message || 
                         error.response?.statusText ||
                         error.message || 
                         '拒单失败，请重试'
    alert(`拒单失败：${errorMessage}`)
  }
}

// 商家开始制作
const startPreparing = async (orderId: number) => {
  try {
    const response = await axios.put(`/api/orders/${orderId}/prepare`)
    if (response.data) {
      const order = orders.value.find(o => o.id === orderId)
      if (order) {
        order.status = 'preparing'
      }
      alert('开始制作！')
    }
  } catch (error) {
    console.error('Failed to start preparing:', error)
    alert('操作失败，请重试')
  }
}

// 商家完成制作（通知骑手）
const readyForPickup = async (orderId: number) => {
  try {
    const response = await axios.put(`/api/orders/${orderId}/ready-for-pickup`)
    if (response.data) {
      const order = orders.value.find(o => o.id === orderId)
      if (order) {
        order.status = 'ready_for_pickup'
      }
      alert('制作完成！骑手将收到通知')
    }
  } catch (error) {
    console.error('Failed to mark as ready:', error)
    alert('操作失败，请重试')
  }
}

const updateOrderStatus = async (orderId: number, status: string) => {
  try {
    await axios.put(`/api/orders/${orderId}/status`, { status })
    const order = orders.value.find(o => o.id === orderId)
    if (order) {
      order.status = status
    }
    updateStats()
  } catch (error) {
    console.error('Failed to update order status:', error)
  }
}

const logout = () => {
  localStorage.removeItem('shop')
  localStorage.removeItem('role')
  router.push('/login')
}

const initProfileForm = () => {
  if (shop.value) {
    profileForm.value = {
      name: shop.value.name || '',
      phone: shop.value.phone || '',
      address: shop.value.address || '',
      deliveryFee: shop.value.deliveryFee || 0,
      minOrder: shop.value.minOrder || 0,
      status: shop.value.status || 'open',
      description: shop.value.description || ''
    }
  }
}

const fetchReviews = async () => {
  if (!shop.value) return
  try {
    const response = await axios.get(`/api/reviews/shop/${shop.value.id}`)
    reviews.value = response.data.sort((a: Review, b: Review) => 
      new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    )
  } catch (error) {
    console.error('Failed to fetch reviews:', error)
  }
}

onMounted(() => {
  loadShopInfo()
  if (shop.value) {
    initProfileForm()
    fetchDishes()
    fetchCategories()
    fetchOrders()
    fetchStats()
    fetchOrderTrend()
    fetchReviews()
  }
})
</script>

<style scoped>
.shop-admin {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 5rem;
}

.admin-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1.2rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left h1 {
  margin: 0 0 0.3rem 0;
  font-size: 1.5rem;
}

.header-left p {
  margin: 0;
  opacity: 0.9;
}

.logout-btn {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  padding: 0.6rem 1.2rem;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.admin-content {
  display: flex;
  min-height: calc(100vh - 80px);
}

.sidebar {
  width: 220px;
  background: white;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.05);
  padding: 1.5rem 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 1rem 1.5rem;
  cursor: pointer;
  transition: background 0.3s ease;
}

.menu-item:hover {
  background: #f8f9fa;
}

.menu-item.active {
  background: #e8eaf0;
  color: #667eea;
  font-weight: 500;
}

.menu-icon {
  margin-right: 0.8rem;
  font-size: 1.2rem;
}

.menu-text {
  font-size: 0.95rem;
}

.main-content {
  flex: 1;
  padding: 2rem;
}

.section {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 1.5rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.section-header h2 {
  margin: 0;
  font-size: 1.3rem;
}

.add-btn {
  background: #667eea;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.add-btn:hover {
  background: #5a6fd6;
}

/* 数据统计 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

.orders-icon {
  background: #e3f2fd;
}

.revenue-icon {
  background: #e8f5e9;
}

.dishes-icon {
  background: #fff3e0;
}

.pending-icon {
  background: #fce4ec;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 0.9rem;
  color: #666;
  margin-top: 0.2rem;
}

.chart-section {
  margin-top: 1.5rem;
}

.chart-section h3 {
  margin-bottom: 1rem;
  font-size: 1.1rem;
  color: #555;
}

.horizontal-chart {
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 8px;
}

.chart-row {
  display: flex;
  align-items: center;
  margin-bottom: 0.75rem;
}

.chart-row:last-child {
  margin-bottom: 0;
}

.chart-label {
  width: 40px;
  font-size: 0.85rem;
  color: #666;
  text-align: right;
  margin-right: 1rem;
}

.chart-bar-container {
  flex: 1;
  height: 30px;
  background: #e9ecef;
  border-radius: 6px;
  overflow: hidden;
  position: relative;
}

.chart-bar {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 6px;
  display: flex;
  align-items: center;
  padding-left: 10px;
  transition: width 0.5s ease;
  min-width: 60px;
}

.chart-value {
  font-size: 0.8rem;
  color: white;
  font-weight: 500;
  white-space: nowrap;
}

/* 商家信息 */
.profile-info {
  max-width: 600px;
}

.profile-row {
  display: flex;
  justify-content: space-between;
  padding: 1rem;
  border-bottom: 1px solid #f1f3f5;
  align-items: center;
}

.profile-row:last-child {
  border-bottom: none;
}

.profile-row label {
  font-weight: 500;
  color: #666;
  min-width: 100px;
}

.profile-row span {
  color: #333;
}

/* 分类管理 */
.categories-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}

.category-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-info {
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
}

.category-name {
  font-weight: 500;
  font-size: 1rem;
}

.category-count {
  font-size: 0.85rem;
  color: #666;
}

/* 商品管理 */
.dishes-table {
  overflow-x: auto;
}

.dishes-table table {
  width: 100%;
  border-collapse: collapse;
}

.dishes-table th,
.dishes-table td {
  padding: 0.8rem;
  text-align: left;
  border-bottom: 1px solid #f1f3f5;
}

.dishes-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #666;
}

.status-badge {
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
}

.status-badge.available {
  background: #e6f7ee;
  color: #28a745;
}

.status-badge.unavailable {
  background: #fef0f0;
  color: #dc3545;
}

.action-btn {
  background: #667eea;
  color: white;
  border: none;
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
  margin-right: 0.5rem;
  transition: background 0.3s ease;
}

.action-btn:hover {
  background: #5a6fd6;
}

.action-btn.edit {
  background: #ffc107;
}

.action-btn.edit:hover {
  background: #e6ae00;
}

.action-btn.delete {
  background: #dc3545;
}

.action-btn.delete:hover {
  background: #c82333;
}

/* 订单管理 */
.orders-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.order-card {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
}

.order-id {
  font-weight: 500;
}

.order-status {
  padding: 0.3rem 0.8rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 500;
}

.order-status.pending {
  background: #fff3cd;
  color: #ffc107;
}

.order-status.accepted {
  background: #e3f2fd;
  color: #1976d2;
}

.order-status.preparing {
  background: #fff3e0;
  color: #ff9800;
}

.order-status.ready_for_pickup {
  background: #f3e5f5;
  color: #7b1fa2;
}

.order-status.picked_up {
  background: #e8eaf6;
  color: #3f51b5;
}

.order-status.delivering {
  background: #e1f5fe;
  color: #0288d1;
}

.order-status.delivered {
  background: #e6f7ee;
  color: #28a745;
}

.order-info {
  padding: 1rem;
  border-bottom: 1px solid #e9ecef;
}

.order-info > div {
  margin-bottom: 0.4rem;
  font-size: 0.9rem;
  color: #555;
}

.rider-info {
  color: #2196f3 !important;
  font-weight: 500;
}

.order-remark {
  color: #ff6b6b !important;
  font-style: italic;
}

.order-items {
  padding: 1rem;
  border-bottom: 1px solid #e9ecef;
}

.order-item {
  display: flex;
  justify-content: space-between;
  padding: 0.4rem 0;
  font-size: 0.9rem;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  font-weight: 500;
}

.order-actions {
  display: flex;
  gap: 0.5rem;
}

.update-status-btn {
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background 0.3s ease;
}

.update-status-btn.accept {
  background: #28a745;
  color: white;
}

.update-status-btn.accept:hover {
  background: #218838;
}

.update-status-btn.reject {
  background: #dc3545;
  color: white;
  margin-left: 0.5rem;
}

.update-status-btn.reject:hover {
  background: #c82333;
}

.update-status-btn.prepare {
  background: #ff9800;
  color: white;
}

.update-status-btn.prepare:hover {
  background: #f57c00;
}

.update-status-btn.ready {
  background: #7b1fa2;
  color: white;
}

.update-status-btn.ready:hover {
  background: #6a1b9a;
}

.update-status-btn.waiting {
  background: #e0e0e0;
  color: #666;
  cursor: not-allowed;
}

.update-status-btn.delivering {
  background: #0288d1;
  color: white;
  cursor: not-allowed;
}

.update-status-btn.delivered {
  background: #e8e8e8;
  color: #999;
  cursor: not-allowed;
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 480px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.2rem 1.5rem;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h3 {
  margin: 0;
  font-size: 1.1rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
}

.modal-form {
  padding: 1.5rem;
}

.modal-form .form-group {
  margin-bottom: 1rem;
}

.modal-form .form-group label {
  display: block;
  margin-bottom: 0.4rem;
  font-weight: 500;
}

.modal-form .form-group input,
.modal-form .form-group textarea,
.modal-form .form-group select {
  width: 100%;
  padding: 0.7rem;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 0.95rem;
  box-sizing: border-box;
}

.modal-form .form-group textarea {
  min-height: 80px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.8rem;
  padding: 1rem 1.5rem;
  border-top: 1px solid #e9ecef;
}

.cancel-btn {
  background: #f5f5f5;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.cancel-btn:hover {
  background: #e8e8e8;
}

.submit-btn {
  background: #667eea;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.3s ease;
}

.submit-btn:hover {
  background: #5a6fd6;
}

/* 拒单弹窗特殊样式 */
.reject-modal {
  max-width: 420px;
}

.reject-modal .modal-body {
  padding: 1.5rem;
}

.reject-warning {
  color: #dc3545;
  font-weight: 600;
  font-size: 1.1rem;
  margin-bottom: 0.8rem;
}

.reject-reason {
  color: #666;
  margin-bottom: 1rem;
}

.reject-confirm {
  color: #333;
  margin-bottom: 1rem;
  font-size: 0.95rem;
}

.confirm-input {
  margin-top: 1rem;
}

.confirm-text-input {
  width: 100%;
  padding: 0.8rem;
  border: 2px solid #dc3545;
  border-radius: 6px;
  font-size: 1rem;
  box-sizing: border-box;
}

.confirm-text-input:focus {
  outline: none;
  border-color: #c82333;
}

.submit-btn.reject-confirm {
  background: #dc3545;
}

.submit-btn.reject-confirm:hover:not(:disabled) {
  background: #c82333;
}
</style>