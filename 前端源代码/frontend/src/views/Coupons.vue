<template>
  <div class="coupons-page">
    <div class="coupons-header">
      <h1>🎟️ 每日抢券</h1>
      <p>每天限量发放，先到先得</p>
    </div>

    <div class="coupons-container">
      <div v-for="coupon in coupons" :key="coupon.id" class="coupon-card">
        <div class="coupon-left">
          <div class="coupon-value">
            <span class="value-symbol">¥</span>
            <span class="value-amount">{{ coupon.value }}</span>
          </div>
          <div v-if="coupon.minAmount" class="coupon-condition">
            满{{ coupon.minAmount }}可用
          </div>
          <div v-else class="coupon-condition">
            无门槛
          </div>
        </div>
        <div class="coupon-divider">
          <div class="divider-circle"></div>
          <div class="divider-circle"></div>
          <div class="divider-circle"></div>
        </div>
        <div class="coupon-right">
          <div class="coupon-desc">{{ coupon.description }}</div>
          <div class="coupon-expire">有效期至：{{ formatDate(coupon.expireDate) }}</div>
          <div class="coupon-stock">剩余：{{ coupon.remainingQuantity }}张</div>
          <button 
            class="grab-btn" 
            :class="{ disabled: coupon.remainingQuantity <= 0 }"
            @click="grabCoupon(coupon)"
            :disabled="coupon.remainingQuantity <= 0 || isGrabbing"
          >
            {{ coupon.remainingQuantity <= 0 ? '已抢光' : '立即领取' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 我的优惠券 -->
    <div class="my-coupons-section">
      <h2>我的优惠券</h2>
      <div v-if="userCoupons.length === 0" class="empty-state">
        <div class="empty-icon">🎫</div>
        <p>暂无优惠券，快去领取吧！</p>
      </div>
      <div v-else class="user-coupons-container">
        <div v-for="userCoupon in userCoupons" :key="userCoupon.id" class="user-coupon-card">
          <div class="user-coupon-left">
            <div class="user-coupon-value">
              <span class="value-symbol">¥</span>
              <span class="value-amount">{{ getCouponValue(userCoupon.couponId) }}</span>
            </div>
            <div v-if="getCouponMinAmount(userCoupon.couponId)" class="user-coupon-condition">
              满{{ getCouponMinAmount(userCoupon.couponId) }}可用
            </div>
            <div v-else class="user-coupon-condition">
              无门槛
            </div>
          </div>
          <div class="user-coupon-divider">
            <div class="divider-circle"></div>
            <div class="divider-circle"></div>
            <div class="divider-circle"></div>
          </div>
          <div class="user-coupon-right">
            <div class="user-coupon-desc">{{ getCouponDesc(userCoupon.couponId) }}</div>
            <div class="user-coupon-expire">有效期至：{{ formatDate(getCouponExpireDate(userCoupon.couponId)) }}</div>
            <div class="user-coupon-status unused">未使用</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 提示弹窗 -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-icon">{{ modalIcon }}</div>
        <div class="modal-message">{{ modalMessage }}</div>
        <button class="modal-close-btn" @click="closeModal">确定</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

interface Coupon {
  id: number
  value: number
  minAmount: number | null
  expireDate: string
  description: string
  totalQuantity: number
  remainingQuantity: number
  status: string
}

interface UserCoupon {
  id: number
  userId: number
  couponId: number
  status: string
  orderId: number | null
  createdAt: string
}

const coupons = ref<Coupon[]>([])
const userCoupons = ref<UserCoupon[]>([])
const isGrabbing = ref(false)
const showModal = ref(false)
const modalIcon = ref('')
const modalMessage = ref('')

const formatDate = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

const getCouponValue = (couponId: number) => {
  const coupon = coupons.value.find(c => c.id === couponId)
  return coupon?.value || 0
}

const getCouponMinAmount = (couponId: number) => {
  const coupon = coupons.value.find(c => c.id === couponId)
  return coupon?.minAmount || null
}

const getCouponDesc = (couponId: number) => {
  const coupon = coupons.value.find(c => c.id === couponId)
  return coupon?.description || ''
}

const getCouponExpireDate = (couponId: number) => {
  const coupon = coupons.value.find(c => c.id === couponId)
  return coupon?.expireDate || ''
}

const fetchCoupons = async () => {
  try {
    const response = await axios.get('/api/coupons')
    coupons.value = response.data
  } catch (error) {
    console.error('Failed to fetch coupons:', error)
  }
}

const fetchUserCoupons = async () => {
  const userStr = localStorage.getItem('user')
  if (!userStr) return
  
  const user = JSON.parse(userStr)
  try {
    const response = await axios.get(`/api/coupons/user/${user.id}`)
    userCoupons.value = response.data
  } catch (error) {
    console.error('Failed to fetch user coupons:', error)
  }
}

const grabCoupon = async (coupon: Coupon) => {
  const userStr = localStorage.getItem('user')
  if (!userStr) {
    showModalMessage('⚠️', '请先登录')
    setTimeout(() => {
      router.push('/login')
    }, 1500)
    return
  }

  const user = JSON.parse(userStr)
  
  if (userCoupons.value.some(uc => uc.couponId === coupon.id)) {
    showModalMessage('⚠️', '您已经领取过该优惠券')
    return
  }

  isGrabbing.value = true
  
  try {
    const response = await axios.post(`/api/coupons/${coupon.id}/grab`, { userId: user.id })
    
    if (response.data.success) {
      showModalMessage('🎉', '领取成功！')
      coupon.remainingQuantity--
      fetchUserCoupons()
    } else {
      showModalMessage('⚠️', response.data.message)
    }
  } catch (error: any) {
    showModalMessage('⚠️', error.response?.data?.message || '领取失败')
  } finally {
    isGrabbing.value = false
  }
}

const showModalMessage = (icon: string, message: string) => {
  modalIcon.value = icon
  modalMessage.value = message
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
}

onMounted(() => {
  fetchCoupons()
  fetchUserCoupons()
})
</script>

<style scoped>
.coupons-page {
  min-height: 100vh;
  background-color: #f8f9fa;
  padding: 2rem 1rem 6rem;
}

.coupons-header {
  text-align: center;
  margin-bottom: 2rem;
}

.coupons-header h1 {
  font-size: 2rem;
  color: #ff6b6b;
  margin-bottom: 0.5rem;
}

.coupons-header p {
  color: #666;
  font-size: 1rem;
}

.coupons-container {
  max-width: 600px;
  margin: 0 auto 2rem;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.coupon-card {
  display: flex;
  background: linear-gradient(135deg, #fff9f0, #fff5e6);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.coupon-left {
  flex: 1;
  padding: 1.5rem;
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.coupon-value {
  display: flex;
  align-items: baseline;
  margin-bottom: 0.5rem;
}

.value-symbol {
  font-size: 1.2rem;
  font-weight: bold;
}

.value-amount {
  font-size: 3rem;
  font-weight: bold;
}

.coupon-condition {
  font-size: 0.9rem;
  opacity: 0.9;
}

.coupon-divider {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 0.5rem;
  padding: 0 0.5rem;
}

.divider-circle {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background-color: #f8f9fa;
}

.coupon-right {
  flex: 2;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.coupon-desc {
  font-size: 1.1rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 0.5rem;
}

.coupon-expire {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 0.3rem;
}

.coupon-stock {
  font-size: 0.85rem;
  color: #ff6b6b;
  margin-bottom: 1rem;
}

.grab-btn {
  align-self: flex-start;
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  color: white;
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.grab-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.4);
}

.grab-btn.disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

/* 我的优惠券 */
.my-coupons-section {
  max-width: 600px;
  margin: 0 auto;
}

.my-coupons-section h2 {
  font-size: 1.5rem;
  color: #333;
  margin-bottom: 1rem;
  padding-left: 0.5rem;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  background-color: white;
  border-radius: 12px;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.empty-state p {
  color: #666;
}

.user-coupons-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.user-coupon-card {
  display: flex;
  background: linear-gradient(135deg, #f0f8ff, #e6f3ff);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.user-coupon-left {
  flex: 1;
  padding: 1.5rem;
  background: linear-gradient(135deg, #4dabf7, #339af0);
  color: white;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
}

.user-coupon-value {
  display: flex;
  align-items: baseline;
  margin-bottom: 0.5rem;
}

.user-coupon-condition {
  font-size: 0.9rem;
  opacity: 0.9;
}

.user-coupon-divider {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 0.5rem;
  padding: 0 0.5rem;
}

.user-coupon-right {
  flex: 2;
  padding: 1.5rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.user-coupon-desc {
  font-size: 1.1rem;
  font-weight: bold;
  color: #333;
  margin-bottom: 0.5rem;
}

.user-coupon-expire {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 0.3rem;
}

.user-coupon-status {
  align-self: flex-start;
  padding: 0.2rem 0.8rem;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.user-coupon-status.unused {
  background-color: #e6f7ff;
  color: #1890ff;
}

.user-coupon-status.used {
  background-color: #f6ffed;
  color: #52c41a;
}

.user-coupon-status.expired {
  background-color: #fff2e8;
  color: #fa8c16;
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
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 12px;
  padding: 2rem;
  text-align: center;
  max-width: 300px;
  width: 90%;
}

.modal-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.modal-message {
  font-size: 1.1rem;
  color: #333;
  margin-bottom: 1.5rem;
}

.modal-close-btn {
  background-color: #ff6b6b;
  color: white;
  border: none;
  padding: 0.6rem 2rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.modal-close-btn:hover {
  background-color: #ee5a52;
}

/* 响应式 */
@media (max-width: 480px) {
  .coupons-page {
    padding: 1rem 0.5rem;
  }

  .coupons-header h1 {
    font-size: 1.6rem;
  }

  .coupon-card, .user-coupon-card {
    flex-direction: column;
  }

  .coupon-divider, .user-coupon-divider {
    flex-direction: row;
    padding: 0.5rem 0;
    justify-content: space-around;
  }

  .coupon-left, .user-coupon-left {
    padding: 1rem;
  }

  .coupon-right, .user-coupon-right {
    padding: 1rem;
  }

  .value-amount {
    font-size: 2.5rem;
  }

  .grab-btn {
    align-self: stretch;
  }
}
</style>