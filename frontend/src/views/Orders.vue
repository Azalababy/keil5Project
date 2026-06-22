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
          <div v-if="order.riderAcceptedAt" class="rider-accept-time">骑手接单时间：{{ formatDate(order.riderAcceptedAt) }}</div>
          <div v-if="order.deliveredAt" class="delivered-time">送达时间：{{ formatDate(order.deliveredAt) }}</div>
          <div class="order-address">收货地址：{{ order.address }}</div>
          <div class="order-phone">联系电话：{{ order.phone }}</div>
          <div v-if="order.riderName" class="rider-info">骑手：{{ order.riderName }} {{ order.riderPhone }}</div>
          <div v-if="order.remark" class="order-remark">备注：{{ order.remark }}</div>
        </div>
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <div class="item-info">
              <span class="item-name">{{ item.name }}</span>
              <span v-if="item.remark" class="item-remark">备注：{{ item.remark }}</span>
            </div>
            <span class="item-quantity">×{{ item.quantity }}</span>
            <span class="item-price">¥{{ item.price }}</span>
          </div>
        </div>
        <div class="order-footer">
          <div class="order-total">
            总计：<span class="total-price">¥{{ order.totalPrice }}</span>
          </div>
          <button 
            v-if="order.status === 'delivered' && !order.hasReview" 
            class="view-detail-btn" 
            @click="openOrderDetail(order)"
          >
            评价订单
          </button>
          <button 
            v-else-if="order.status === 'delivered' && order.hasReview" 
            class="view-detail-btn reviewed" 
            @click="openOrderDetail(order)"
          >
            查看评价
          </button>
        </div>
      </div>
      <div v-if="orders.length === 0" class="empty-orders">
        <p>暂无订单</p>
        <router-link to="/" class="go-shopping-btn">去购物</router-link>
      </div>
    </div>

    <!-- 订单详情弹窗 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>订单详情</h3>
          <button class="close-btn" @click="closeModal">×</button>
        </div>
        <div class="modal-body" v-if="selectedOrder">
          <div class="detail-section">
            <h4>订单信息</h4>
            <div class="detail-row">
              <span class="label">订单号：</span>
              <span>{{ selectedOrder.id }}</span>
            </div>
            <div class="detail-row">
              <span class="label">商家：</span>
              <span>{{ selectedOrder.shopName }}</span>
            </div>
            <div class="detail-row">
              <span class="label">订单状态：</span>
              <span :class="selectedOrder.status">{{ getStatusText(selectedOrder.status) }}</span>
            </div>
            <div class="detail-row">
              <span class="label">下单时间：</span>
              <span>{{ formatDate(selectedOrder.createdAt) }}</span>
            </div>
            <div v-if="selectedOrder.riderAcceptedAt" class="detail-row">
              <span class="label">骑手接单时间：</span>
              <span>{{ formatDate(selectedOrder.riderAcceptedAt) }}</span>
            </div>
            <div v-if="selectedOrder.deliveredAt" class="detail-row">
              <span class="label">送达时间：</span>
              <span>{{ formatDate(selectedOrder.deliveredAt) }}</span>
            </div>
            <div class="detail-row">
              <span class="label">收货地址：</span>
              <span>{{ selectedOrder.address }}</span>
            </div>
            <div class="detail-row">
              <span class="label">联系电话：</span>
              <span>{{ selectedOrder.phone }}</span>
            </div>
            <div v-if="selectedOrder.riderName" class="detail-row">
              <span class="label">骑手：</span>
              <span>{{ selectedOrder.riderName }} {{ selectedOrder.riderPhone }}</span>
            </div>
          </div>

          <div class="detail-section">
            <h4>商品列表</h4>
            <div v-for="item in selectedOrder.items" :key="item.id" class="item-row">
              <span class="item-name">{{ item.name }}</span>
              <span class="item-quantity">×{{ item.quantity }}</span>
              <span class="item-price">¥{{ (item.price * item.quantity).toFixed(2) }}</span>
            </div>
            <div class="total-row">
              <span>合计：</span>
              <span class="total-amount">¥{{ selectedOrder.totalPrice }}</span>
            </div>
          </div>

          <!-- 评价区域 -->
          <div v-if="selectedOrder.status === 'delivered'" class="review-section">
            <h4>评价</h4>
            
            <!-- 商家评价 -->
            <div class="rating-section">
              <label>商家评分</label>
              <div class="stars-container">
                <span 
                  v-for="i in 5" 
                  :key="'shop-' + i" 
                  class="star"
                  :class="{ active: shopRating >= i }"
                  @click="setShopRating(i)"
                >★</span>
              </div>
              <textarea 
                v-model="shopComment" 
                class="review-textarea" 
                placeholder="请输入对商家的评价..."
              ></textarea>
            </div>

            <!-- 骑手评价 -->
            <div v-if="selectedOrder.riderId" class="rating-section">
              <label>骑手评分</label>
              <div class="stars-container">
                <span 
                  v-for="i in 5" 
                  :key="'rider-' + i" 
                  class="star"
                  :class="{ active: riderRating >= i }"
                  @click="setRiderRating(i)"
                >★</span>
              </div>
              <textarea 
                v-model="riderComment" 
                class="review-textarea" 
                placeholder="请输入对骑手的评价..."
              ></textarea>
            </div>

            <div v-if="selectedOrder.hasReview" class="existing-review">
              <div v-if="selectedOrder.shopRating" class="review-item">
                <span class="review-label">商家评分：</span>
                <span class="stars">{{ '★'.repeat(selectedOrder.shopRating) }}{{ '☆'.repeat(5 - selectedOrder.shopRating) }}</span>
                <p v-if="selectedOrder.shopComment" class="review-comment">{{ selectedOrder.shopComment }}</p>
              </div>
              <div v-if="selectedOrder.riderRating" class="review-item">
                <span class="review-label">骑手评分：</span>
                <span class="stars">{{ '★'.repeat(selectedOrder.riderRating) }}{{ '☆'.repeat(5 - selectedOrder.riderRating) }}</span>
                <p v-if="selectedOrder.riderComment" class="review-comment">{{ selectedOrder.riderComment }}</p>
              </div>
            </div>

            <button 
              v-if="!selectedOrder.hasReview" 
              class="submit-review-btn" 
              @click="submitReview"
            >
              提交评价
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import axios from 'axios'
import router from '../router'

interface OrderItem {
  id: number
  dishId: number
  quantity: number
  price: number
  name?: string
  remark?: string
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
  remark?: string
  shopName?: string
  items?: OrderItem[]
  riderName?: string
  riderPhone?: string
  riderId?: number
  riderAcceptedAt?: string
  deliveredAt?: string
  hasReview?: boolean
  shopRating?: number
  shopComment?: string
  riderRating?: number
  riderComment?: string
}

const orders = ref<Order[]>([])
const user = ref<any>(null)
const showDetailModal = ref(false)
const selectedOrder = ref<Order | null>(null)
const shopRating = ref(0)
const riderRating = ref(0)
const shopComment = ref('')
const riderComment = ref('')

const fetchOrders = async () => {
  if (!user.value) return
  try {
    const userId = user.value.id
    const response = await axios.get(`/api/orders/user/${userId}`)
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
      
      try {
        const shopResponse = await axios.get(`/api/shops/${order.shopId}`)
        order.shopName = shopResponse.data.name
      } catch (error) {
        order.shopName = '未知商家'
      }

      try {
        const reviewResponse = await axios.get(`/api/reviews/order/${order.id}`)
        if (reviewResponse.data) {
          order.hasReview = true
          order.shopRating = reviewResponse.data.shopRating
          order.shopComment = reviewResponse.data.shopComment
          order.riderRating = reviewResponse.data.riderRating
          order.riderComment = reviewResponse.data.riderComment
        } else {
          order.hasReview = false
        }
      } catch (error) {
        order.hasReview = false
      }
    }
    
    orders.value = orderList.sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
  } catch (error) {
    console.error('Failed to fetch orders:', error)
  }
}

const getStatusText = (status: string) => {
  const statusMap: Record<string, string> = {
    pending: '待接单',
    processing: '处理中',
    accepted: '商家已接单',
    preparing: '制作中',
    ready_for_pickup: '待取餐',
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

const openOrderDetail = (order: Order) => {
  selectedOrder.value = order
  shopRating.value = order.shopRating || 0
  riderRating.value = order.riderRating || 0
  shopComment.value = order.shopComment || ''
  riderComment.value = order.riderComment || ''
  showDetailModal.value = true
}

const closeModal = () => {
  showDetailModal.value = false
  selectedOrder.value = null
  shopRating.value = 0
  riderRating.value = 0
  shopComment.value = ''
  riderComment.value = ''
}

const setShopRating = (rating: number) => {
  shopRating.value = rating
}

const setRiderRating = (rating: number) => {
  riderRating.value = rating
}

const submitReview = async () => {
  if (!selectedOrder.value || !user.value) return
  
  try {
    const reviewData = {
      orderId: selectedOrder.value.id,
      userId: user.value.id,
      shopId: selectedOrder.value.shopId,
      riderId: selectedOrder.value.riderId,
      shopRating: shopRating.value,
      riderRating: riderRating.value,
      shopComment: shopComment.value,
      riderComment: riderComment.value
    }
    
    await axios.post('/api/reviews', reviewData)
    alert('评价成功！')
    
    const orderIndex = orders.value.findIndex(o => o.id === selectedOrder.value?.id)
    if (orderIndex > -1) {
      orders.value[orderIndex].hasReview = true
      orders.value[orderIndex].shopRating = shopRating.value
      orders.value[orderIndex].shopComment = shopComment.value
      orders.value[orderIndex].riderRating = riderRating.value
      orders.value[orderIndex].riderComment = riderComment.value
    }
    
    closeModal()
  } catch (error) {
    console.error('Failed to submit review:', error)
    alert('评价失败，请重试')
  }
}

onMounted(() => {
  const role = localStorage.getItem('role')
  const userData = localStorage.getItem('user')
  
  if (role === 'user' && userData) {
    user.value = JSON.parse(userData)
    fetchOrders()
  } else {
    alert('请先登录！')
    router.push('/login')
  }
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

.order-status.processing,
.order-status.accepted,
.order-status.preparing {
  background-color: #e3f2fd;
  color: #1976d2;
}

.order-status.ready_for_pickup,
.order-status.picked_up,
.order-status.delivering {
  background-color: #fff3e0;
  color: #ff9800;
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

.rider-accept-time,
.delivered-time {
  color: #007bff !important;
}

.rider-info {
  color: #28a745 !important;
}

.order-remark {
  font-style: italic;
  color: #888;
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

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.item-name {
  font-size: 0.95rem;
  color: #333;
}

.item-remark {
  font-size: 0.85rem;
  color: #888;
  margin-top: 0.2rem;
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
  justify-content: space-between;
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

.view-detail-btn {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s ease;
}

.view-detail-btn:hover {
  background-color: #0069d9;
}

.view-detail-btn.reviewed {
  background-color: #6c757d;
}

.view-detail-btn.reviewed:hover {
  background-color: #5a6268;
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

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  border-bottom: 1px solid #e9ecef;
  position: sticky;
  top: 0;
  background-color: white;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #999;
}

.close-btn:hover {
  color: #666;
}

.modal-body {
  padding: 1.5rem;
}

.detail-section {
  margin-bottom: 1.5rem;
}

.detail-section h4 {
  margin: 0 0 1rem 0;
  color: #333;
  border-bottom: 1px solid #f1f3f5;
  padding-bottom: 0.5rem;
}

.detail-row {
  display: flex;
  margin-bottom: 0.5rem;
  font-size: 0.95rem;
}

.detail-row .label {
  color: #888;
  width: 100px;
  flex-shrink: 0;
}

.detail-row span:last-child {
  color: #333;
}

.item-row {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f1f3f5;
}

.item-row:last-child {
  border-bottom: none;
}

.total-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 1rem;
  font-size: 1.1rem;
  font-weight: 500;
}

.total-amount {
  color: #ff6b6b;
  margin-left: 0.5rem;
}

.review-section {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e9ecef;
}

.review-section h4 {
  margin: 0 0 1rem 0;
  color: #333;
}

.rating-section {
  margin-bottom: 1.5rem;
}

.rating-section label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #333;
}

.stars-container {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.star {
  cursor: pointer;
  color: #ddd;
  transition: color 0.2s ease;
}

.star:hover,
.star.active {
  color: #ffc107;
}

.review-textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  min-height: 80px;
  font-size: 0.95rem;
}

.existing-review {
  margin-top: 1rem;
  padding: 1rem;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.review-item {
  margin-bottom: 1rem;
}

.review-item:last-child {
  margin-bottom: 0;
}

.review-label {
  font-weight: 500;
  color: #333;
}

.review-item .stars {
  color: #ffc107;
  margin-left: 0.5rem;
}

.review-comment {
  margin: 0.5rem 0 0 0;
  color: #666;
  font-size: 0.95rem;
}

.submit-review-btn {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  width: 100%;
  transition: background-color 0.3s ease;
}

.submit-review-btn:hover {
  background-color: #218838;
}
</style>
