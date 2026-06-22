<template>
  <div class="rider-admin">
    <div class="admin-header">
      <h1>🚴 骑手中心</h1>
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </div>

    <div class="admin-content">
      <!-- 骑手信息卡片 -->
      <div class="info-card">
        <div class="rider-avatar">👤</div>
        <div class="rider-info">
          <h2>{{ rider.name }}</h2>
          <p>手机号：{{ rider.phone }}</p>
          <div class="status-badge" :class="{ online: isOnline }">
            {{ isOnline ? '在线' : '离线' }}
          </div>
        </div>
        <button class="status-btn" @click="toggleOnline">
          {{ isOnline ? '下线' : '上线' }}
        </button>
      </div>

      <!-- 统计数据 -->
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-icon">📦</div>
          <div class="stat-content">
            <p class="stat-value">{{ todayOrders }}</p>
            <p class="stat-label">今日订单</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">💰</div>
          <div class="stat-content">
            <p class="stat-value">¥{{ Number(todayEarnings).toFixed(2) }}</p>
            <p class="stat-label">今日收入</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">⏱️</div>
          <div class="stat-content">
            <p class="stat-value">{{ Number(avgDeliveryTime).toFixed(2) }}分钟</p>
            <p class="stat-label">平均配送时长</p>
          </div>
        </div>
        <div class="stat-card">
          <div class="stat-icon">⭐</div>
          <div class="stat-content">
            <p class="stat-value">{{ Number(deliveryRate).toFixed(2) }}%</p>
            <p class="stat-label">配送完成率</p>
          </div>
        </div>
      </div>

      <!-- 当前配送 -->
      <div v-if="currentOrders.length > 0" class="section">
        <h3 class="section-title">🚴 当前配送中 <span class="count">{{ currentOrders.length }}</span></h3>
        <div v-for="order in currentOrders" :key="order.id" class="order-card current">
          <div class="order-header">
            <span class="order-id">订单号：{{ order.id }}</span>
            <span class="order-price">配送费：¥{{ order.deliveryFee }}</span>
            <span class="delivery-time">配送时长：{{ order.deliveryTimeStr || '00:00' }}</span>
          </div>
          <div class="order-body">
            <div class="address-group">
              <p><span class="label">取货地址：</span>{{ order.shopAddress }}</p>
              <p><span class="label">送货地址：</span>{{ order.userAddress }}</p>
              <p><span class="label">联系电话：</span>{{ order.userPhone }}</p>
            </div>
          </div>
          <div class="order-status">
            <div class="status-step" :class="{ active: order.orderStatus === 'picked_up' }">
              <span class="step-icon">✓</span>
              <span>已取货</span>
            </div>
            <div class="status-step" :class="{ active: order.orderStatus === 'delivering' }">
              <span class="step-icon">✓</span>
              <span>配送中</span>
            </div>
            <div class="status-step" :class="{ active: order.orderStatus === 'delivered' }">
              <span class="step-icon">✓</span>
              <span>已送达</span>
            </div>
          </div>
          <button 
            class="action-btn primary" 
            v-if="order.orderStatus !== 'delivered'"
            @click="updateOrderStatus(order)"
          >
            {{ getStatusButtonText(order) }}
          </button>
        </div>
      </div>

      <!-- 待接订单 -->
      <div class="section">
        <h3 class="section-title">📋 待接订单 <span class="count">{{ pendingOrders.length }}</span></h3>
        <div v-if="pendingOrders.length === 0" class="empty-state">
          <p>暂无待接订单</p>
        </div>
        <div v-else class="order-list">
          <div v-for="order in pendingOrders" :key="order.id" class="order-card">
            <div class="order-header">
              <div>
                <span class="order-id">订单号：{{ order.id }}</span>
                <span v-if="order.shopName" class="shop-name">🏪 {{ order.shopName }}</span>
              </div>
              <div class="price-info">
                <span class="order-price">配送费：¥{{ order.deliveryFee }}</span>
                <span v-if="order.totalPrice" class="total-price">订单金额：¥{{ order.totalPrice }}</span>
              </div>
            </div>
            <div class="order-body">
              <!-- 商品列表 -->
              <div v-if="order.orderItems && order.orderItems.length > 0" class="items-section">
                <p class="section-label">📦 商品信息</p>
                <div class="items-list">
                  <div v-for="item in order.orderItems" :key="item.id" class="item-row">
                    <span class="item-name">{{ item.dishName }}</span>
                    <span class="item-quantity">×{{ item.quantity }}</span>
                    <span class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
                  </div>
                </div>
              </div>
              <!-- 地址信息 -->
              <div class="address-group">
                <p><span class="label">📍 取货地址：</span>{{ order.shopAddress || '未知' }}</p>
                <p><span class="label">🏠 送货地址：</span>{{ order.userAddress }}</p>
                <p><span class="label">📞 联系电话：</span>{{ order.phone }}</p>
              </div>
            </div>
            <div class="order-actions">
              <button class="action-btn accept" @click="acceptOrder(order)">接单</button>
              <button class="action-btn reject" @click="showRejectConfirm(order)">拒单</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 历史订单 -->
      <div class="section">
        <h3 class="section-title">📝 历史订单</h3>
        <div v-if="historyOrders.length === 0" class="empty-state">
          <p>暂无历史订单</p>
        </div>
        <div v-else class="order-list">
          <div v-for="order in historyOrders" :key="order.id" class="order-card">
            <div class="order-header">
              <span class="order-id">订单号：{{ order.id }}</span>
              <span class="order-price">¥{{ order.deliveryFee }}</span>
            </div>
            <div class="order-body">
              <div class="address-group">
                <p><span class="label">取货：</span>{{ order.shopAddress }}</p>
                <p><span class="label">送货：</span>{{ order.userAddress }}</p>
                <p><span class="label">配送时长：</span>{{ order.deliveryTime != null && !isNaN(order.deliveryTime) ? Number(order.deliveryTime).toFixed(2) : '0.00' }}分钟</p>
              </div>
            </div>
            <div class="order-status-badge" :class="order.orderStatus">
              {{ getStatusText(order.orderStatus) }}
            </div>
          </div>
        </div>
      </div>

      <!-- 用户评价 -->
      <div class="section">
        <h3 class="section-title">⭐ 用户评价</h3>
        <div v-if="reviews.length === 0" class="empty-state">
          <p>暂无用户评价</p>
        </div>
        <div v-else class="reviews-list">
          <div v-for="review in reviews" :key="review.id" class="review-card">
            <div class="review-header">
              <span class="review-order">订单号：{{ review.orderId }}</span>
              <span class="review-stars">{{ '★'.repeat(review.riderRating) }}{{ '☆'.repeat(5 - review.riderRating) }}</span>
            </div>
            <div class="review-content">
              <p v-if="review.riderComment" class="review-comment">{{ review.riderComment }}</p>
              <p v-else class="no-comment">暂无评价内容</p>
            </div>
            <div class="review-footer">
              <span class="review-date">{{ formatDate(review.createdAt) }}</span>
            </div>
          </div>
        </div>
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
          <p class="reject-reason">配送费：¥{{ rejectOrder?.deliveryFee }}</p>
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
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

interface Rider {
  id: number
  name: string
  phone: string
}

interface OrderItem {
  id: number
  dishName: string
  quantity: number
  price: number
}

interface Order {
  id: number
  deliveryFee: number
  address: string
  phone: string
  expectedTime?: number
  deliveryTime?: number
  deliveryTimeStr?: string  // 格式化的配送时间字符串
  deliverySeconds?: number  // 配送秒数
  status: string
  shopId?: number
  orderItems?: OrderItem[]
  shopAddress?: string
  userAddress?: string
  shopName?: string
  totalPrice?: number
}

interface Review {
  id: number
  orderId: number
  riderId: number
  riderRating: number
  riderComment: string
  createdAt: string
}

const rider = reactive<Rider>({
  id: 0,
  name: '骑手',
  phone: ''
})

const isOnline = ref(true)
const todayOrders = ref(0)
const todayEarnings = ref(0)
const totalOrders = ref(0)
const totalEarnings = ref(0)
const avgDeliveryTime = ref(0)
const deliveryRate = ref(0)
// 使用 Map 存储每个订单的计时器，key 为订单ID
const deliveryTimers = new Map<number, ReturnType<typeof setInterval>>()

const fetchRiderStats = async (riderId: number) => {
  try {
    const response = await axios.get(`/api/riders/${riderId}/stats`)
    todayOrders.value = response.data.todayOrders || 0
    // 保持为数字类型，便于后续计算
    todayEarnings.value = Number(response.data.todayEarnings) || 0
    totalOrders.value = response.data.totalOrders || 0
    totalEarnings.value = Number(response.data.totalEarnings) || 0
    // 使用实际计算的值，不使用默认值
    avgDeliveryTime.value = Number(response.data.avgDeliveryTime) || 0
    deliveryRate.value = Number(response.data.deliveryRate) || 0
  } catch (error) {
    console.error('Failed to fetch rider stats:', error)
  }
}

const currentOrder = ref<Order | null>(null)
const currentOrders = ref<Order[]>([])  // 支持多单配送
const pendingOrders = ref<Order[]>([])
const historyOrders = ref<Order[]>([])
const reviews = ref<Review[]>([])
const showRejectModal = ref(false)
const rejectOrder = ref<Order | null>(null)
const rejectConfirmText = ref('')

// 获取待接单的订单（商家已完成制作，等待骑手取餐）
const fetchPendingOrders = async () => {
  try {
    const response = await axios.get('/api/orders/ready-for-pickup')
    pendingOrders.value = response.data.map((order: any) => ({
      id: order.id,
      deliveryFee: order.totalPrice ? (order.totalPrice * 0.2).toFixed(2) : '5.00',
      address: order.address,
      phone: order.phone,
      status: order.status,
      shopId: order.shopId,
      orderItems: order.orderItems || [],
      shopAddress: order.shopAddress,
      userAddress: order.address,
      shopName: order.shopName,
      totalPrice: order.totalPrice
    }))
  } catch (error) {
    console.error('Failed to fetch pending orders:', error)
  }
}

// 获取骑手的配送中订单
const fetchCurrentOrder = async (riderId: number) => {
  try {
    const response = await axios.get(`/api/orders/rider/${riderId}`)
    const orders = response.data.filter((order: any) => 
      order.status === 'picked_up' || order.status === 'delivering'
    )
    // 支持多单配送，将所有配送中的订单保存到 currentOrders
    currentOrders.value = orders.map((order: any) => ({
      id: order.id,
      deliveryFee: order.totalPrice ? (order.totalPrice * 0.2).toFixed(2) : '5.00',
      address: order.address,
      phone: order.phone,
      status: order.status,
      shopAddress: order.shopAddress,
      userAddress: order.address,
      shopName: order.shopName,
      totalPrice: order.totalPrice,
      orderStatus: order.status,
      userPhone: order.phone,
      deliveryTimeStr: '00:00',  // 初始化配送时间显示
      deliverySeconds: 0         // 初始化配送秒数
    }))
    
    // 为了兼容旧代码，如果有订单则设置第一个为 currentOrder
    if (orders.length > 0) {
      currentOrder.value = currentOrders.value[0]
      // 为每个配送中的订单启动独立计时器
      currentOrders.value.forEach(order => {
        if (order.status === 'delivering') {
          startOrderTimer(order.id)
        }
      })
    } else {
      currentOrder.value = null
    }
  } catch (error) {
    console.error('Failed to fetch current order:', error)
  }
}

// 获取骑手的历史订单
const fetchHistoryOrders = async (riderId: number) => {
  try {
    const response = await axios.get(`/api/orders/rider/${riderId}`)
    historyOrders.value = response.data
      .filter((order: any) => order.status === 'delivered')
      .map((order: any) => ({
        id: order.id,
        deliveryFee: order.totalPrice ? (order.totalPrice * 0.2).toFixed(2) : '5.00',
        address: order.address,
        phone: order.phone,
        status: order.status,
        deliveryTime: order.deliveryTime || 0,
        shopAddress: order.shopAddress || '未知',
        userAddress: order.address || '未知',
        orderStatus: order.status
      }))
  } catch (error) {
    console.error('Failed to fetch history orders:', error)
  }
}

// 获取骑手的评价
const fetchReviews = async (riderId: number) => {
  try {
    const response = await axios.get(`/api/reviews/rider/${riderId}`)
    reviews.value = response.data.sort((a: Review, b: Review) => 
      new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
    )
  } catch (error) {
    console.error('Failed to fetch reviews:', error)
  }
}

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('rider')
  localStorage.removeItem('role')
  router.push('/login')
}

const toggleOnline = () => {
  isOnline.value = !isOnline.value
  if (isOnline.value) {
    alert('已上线，可接收订单')
  } else {
    alert('已下线')
  }
}

const acceptOrder = async (order: Order) => {
  if (!isOnline.value) {
    alert('请先上线')
    return
  }
  
  const confirm = window.confirm(`确定要接此订单吗？配送费: ¥${order.deliveryFee}`)
  if (confirm) {
    try {
      const response = await axios.put(`/api/orders/${order.id}/rider-accept`, {
        riderId: rider.id,
        riderName: rider.name,
        riderPhone: rider.phone
      })
      
      if (response.data) {
        const index = pendingOrders.value.findIndex(o => o.id === order.id)
        if (index > -1) {
          pendingOrders.value.splice(index, 1)
        }
        
        // 将订单添加到当前配送列表
        const newOrder = {
          ...order,
          status: 'picked_up',
          orderStatus: 'picked_up',
          shopAddress: order.shopAddress,
          userAddress: order.userAddress,
          userPhone: order.phone
        }
        currentOrders.value.push(newOrder)
        currentOrder.value = newOrder
        
        alert('接单成功，快去取餐吧！')
      }
    } catch (error) {
      console.error('Failed to accept order:', error)
      alert('接单失败，请重试')
    }
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
      // 从待接单列表中移除
      const index = pendingOrders.value.findIndex(o => o.id === rejectOrder.value?.id)
      if (index > -1) {
        pendingOrders.value.splice(index, 1)
      }
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

// 旧的拒单函数（保留以防兼容）
const rejectOrderOld = (order: Order) => {
  showRejectConfirm(order)
}

// 为指定订单启动独立计时器
const startOrderTimer = (orderId: number) => {
  // 如果该订单已有计时器，先停止
  if (deliveryTimers.has(orderId)) {
    stopOrderTimer(orderId)
  }
  
  const timer = setInterval(() => {
    const order = currentOrders.value.find(o => o.id === orderId)
    if (order) {
      order.deliverySeconds = (order.deliverySeconds || 0) + 1
      const minutes = Math.floor((order.deliverySeconds || 0) / 60)
      const secs = (order.deliverySeconds || 0) % 60
      order.deliveryTimeStr = `${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    } else {
      // 订单已不存在，停止计时器
      stopOrderTimer(orderId)
    }
  }, 1000)
  
  deliveryTimers.set(orderId, timer)
}

// 停止指定订单的计时器
const stopOrderTimer = (orderId: number) => {
  const timer = deliveryTimers.get(orderId)
  if (timer) {
    clearInterval(timer)
    deliveryTimers.delete(orderId)
  }
}

// 停止所有计时器
const stopAllTimers = () => {
  deliveryTimers.forEach((timer) => {
    clearInterval(timer)
  })
  deliveryTimers.clear()
}

const updateOrderStatus = async (order: Order) => {
  if (!order) return
  
  try {
    if (order.status === 'picked_up') {
      // 开始配送
      const response = await axios.put(`/api/orders/${order.id}/deliver`)
      if (response.data) {
        order.status = 'delivering'
        order.orderStatus = 'delivering'
        // 启动该订单的独立计时器
        startOrderTimer(order.id)
      }
    } else if (order.status === 'delivering') {
      // 送达
      const response = await axios.put(`/api/orders/${order.id}/complete`)
      if (response.data) {
        order.status = 'delivered'
        order.orderStatus = 'delivered'
        
        // 停止该订单的计时器
        stopOrderTimer(order.id)
        
        // 从当前配送列表中移除该订单
        const index = currentOrders.value.findIndex(o => o.id === order.id)
        if (index > -1) {
          currentOrders.value.splice(index, 1)
        }
        
        // 如果没有配送中的订单了，清空 currentOrder
        if (currentOrders.value.length === 0) {
          currentOrder.value = null
        }
        
        // 添加到历史订单（使用该订单的配送时长）
        const deliveryMinutes = Math.floor((order.deliverySeconds || 0) / 60) + (order.deliverySeconds || 0) % 60 / 60
        historyOrders.value.unshift({
          ...order,
          deliveryTime: deliveryMinutes
        })
        
        todayOrders.value++
        todayEarnings.value += Number(order.deliveryFee)
        totalOrders.value++
        totalEarnings.value += Number(order.deliveryFee)
        
        alert('配送完成！')
      }
    }
  } catch (error) {
    console.error('Failed to update order status:', error)
    alert('操作失败，请重试')
  }
}

const getStatusButtonText = (order: Order) => {
  if (!order) return ''
  switch (order.status) {
    case 'picked_up': return '开始配送'
    case 'delivering': return '已送达'
    default: return '确认'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'pending': return '待接单'
    case 'accepted': return '商家已接单'
    case 'preparing': return '制作中'
    case 'ready_for_pickup': return '待取餐'
    case 'picked_up': return '已取货'
    case 'delivering': return '配送中'
    case 'delivered': return '已送达'
    case 'cancelled': return '已取消'
    default: return status
  }
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  const riderData = localStorage.getItem('rider')
  if (riderData) {
    const data = JSON.parse(riderData)
    rider.id = data.id
    rider.name = data.name
    rider.phone = data.phone
    // 获取骑手统计数据
    fetchRiderStats(data.id)
    // 获取订单数据
    fetchPendingOrders()
    fetchCurrentOrder(data.id)
    fetchHistoryOrders(data.id)
    // 获取评价数据
    fetchReviews(data.id)
  }
  
  const role = localStorage.getItem('role')
  if (role !== 'rider') {
    router.push('/login')
  }
})

onUnmounted(() => {
  stopAllTimers()
})
</script>

<style scoped>
.rider-admin {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.admin-header {
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  color: white;
  padding: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.admin-header h1 {
  margin: 0;
  font-size: 1.5rem;
}

.logout-btn {
  background-color: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.logout-btn:hover {
  background-color: rgba(255, 255, 255, 0.3);
}

.admin-content {
  padding: 1.5rem;
  max-width: 1200px;
  margin: 0 auto;
}

.info-card {
  background-color: white;
  border-radius: 8px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 1.5rem;
}

.rider-avatar {
  width: 80px;
  height: 80px;
  background-color: #4CAF50;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 2.5rem;
  margin-right: 1.5rem;
}

.rider-info {
  flex: 1;
}

.rider-info h2 {
  margin: 0 0 0.5rem;
  font-size: 1.3rem;
  color: #333;
}

.rider-info p {
  margin: 0 0 0.5rem;
  color: #666;
}

.status-badge {
  display: inline-block;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  background-color: #f5f5f5;
  color: #666;
}

.status-badge.online {
  background-color: #e8f5e9;
  color: #4CAF50;
}

.status-btn {
  background-color: #4CAF50;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.95rem;
}

.status-btn:hover {
  background-color: #43a047;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-card {
  background-color: white;
  border-radius: 8px;
  padding: 1rem;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  font-size: 2rem;
  margin-right: 1rem;
}

.stat-content {
  flex: 1;
}

.stat-value {
  margin: 0;
  font-size: 1.5rem;
  font-weight: bold;
  color: #4CAF50;
}

.stat-label {
  margin: 0;
  font-size: 0.85rem;
  color: #666;
}

.section {
  margin-bottom: 1.5rem;
}

.section-title {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.timer {
  font-family: monospace;
  font-size: 1.5rem;
  font-weight: bold;
  color: #ff6b6b;
}

.count {
  background-color: #ff6b6b;
  color: white;
  font-size: 0.8rem;
  padding: 0.2rem 0.5rem;
  border-radius: 10px;
}

.order-card {
  background-color: white;
  border-radius: 8px;
  padding: 1rem;
  margin-bottom: 1rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.order-card.current {
  border-left: 4px solid #4CAF50;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #f0f0f0;
}

.order-id {
  font-size: 0.9rem;
  color: #666;
  display: block;
  margin-bottom: 0.25rem;
}

.shop-name {
  font-size: 0.95rem;
  color: #333;
  font-weight: 500;
}

.price-info {
  text-align: right;
}

.order-price {
  font-size: 1rem;
  font-weight: bold;
  color: #4CAF50;
  display: block;
}

.total-price {
  font-size: 0.85rem;
  color: #666;
  display: block;
}

.order-body {
  margin-bottom: 1rem;
}

.items-section {
  margin-bottom: 1rem;
  padding: 0.75rem;
  background-color: #fafafa;
  border-radius: 6px;
}

.section-label {
  margin: 0 0 0.5rem 0;
  font-size: 0.85rem;
  font-weight: 500;
  color: #333;
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 0.4rem;
}

.item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 0.9rem;
}

.item-name {
  flex: 1;
  color: #333;
}

.item-quantity {
  color: #999;
  margin: 0 0.5rem;
}

.item-price {
  color: #666;
  font-weight: 500;
}

.address-group {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.address-group p {
  margin: 0;
  font-size: 0.9rem;
  color: #333;
}

.address-group .label {
  color: #999;
}

.order-status {
  display: flex;
  justify-content: space-around;
  padding: 1rem 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 1rem;
}

.status-step {
  text-align: center;
  opacity: 0.5;
}

.status-step.active {
  opacity: 1;
}

.step-icon {
  display: inline-block;
  width: 36px;
  height: 36px;
  line-height: 36px;
  border-radius: 50%;
  background-color: #f5f5f5;
  color: #999;
  font-size: 0.8rem;
  margin-bottom: 0.5rem;
}

.status-step.active .step-icon {
  background-color: #4CAF50;
  color: white;
}

.status-step span:last-child {
  font-size: 0.8rem;
  color: #666;
}

.order-actions {
  display: flex;
  gap: 0.5rem;
}

.action-btn {
  flex: 1;
  padding: 0.75rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
}

.action-btn.primary {
  background-color: #4CAF50;
  color: white;
  width: 100%;
}

.action-btn.primary:hover {
  background-color: #43a047;
}

.action-btn.accept {
  background-color: #4CAF50;
  color: white;
}

.action-btn.accept:hover {
  background-color: #43a047;
}

.action-btn.reject {
  background-color: #f5f5f5;
  color: #666;
}

.action-btn.reject:hover {
  background-color: #e8e8e8;
}

.order-status-badge {
  display: inline-block;
  padding: 0.3rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  background-color: #f5f5f5;
  color: #666;
}

.order-status-badge.delivered {
  background-color: #e8f5e9;
  color: #4CAF50;
}

.empty-state {
  text-align: center;
  padding: 2rem;
  color: #999;
  background-color: white;
  border-radius: 8px;
}

/* 评价样式 */
.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.review-card {
  background-color: white;
  padding: 1rem;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
}

.review-order {
  font-size: 0.9rem;
  color: #666;
}

.review-stars {
  font-size: 1.2rem;
  color: #ffc107;
}

.review-content {
  margin-bottom: 0.75rem;
}

.review-comment {
  font-size: 0.95rem;
  color: #333;
  line-height: 1.5;
}

.no-comment {
  font-size: 0.9rem;
  color: #999;
}

.review-footer {
  text-align: right;
}

.review-date {
  font-size: 0.85rem;
  color: #999;
}

@media screen and (max-width: 768px) {
  .admin-header {
    padding: 1rem;
  }
  
  .admin-header h1 {
    font-size: 1.2rem;
  }
  
  .admin-content {
    padding: 1rem;
  }
  
  .info-card {
    flex-direction: column;
    text-align: center;
  }
  
  .rider-avatar {
    margin-right: 0;
    margin-bottom: 1rem;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .order-status {
    flex-wrap: wrap;
  }
  
  .status-step {
    flex: 1;
    min-width: 80px;
  }
}

/* 拒单弹窗样式 */
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