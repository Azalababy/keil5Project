<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1>数据仪表盘</h1>
      <p class="date">{{ currentDate }}</p>
    </div>
    
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon users-icon">👥</div>
        <div class="stat-info">
          <div class="stat-value">{{ dashboardData.totalUsers }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon shops-icon">🏪</div>
        <div class="stat-info">
          <div class="stat-value">{{ dashboardData.totalShops }}</div>
          <div class="stat-label">总商家数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon riders-icon">🚴</div>
        <div class="stat-info">
          <div class="stat-value">{{ dashboardData.totalRiders }}</div>
          <div class="stat-label">总骑手数</div>
        </div>
      </div>
      
      <div class="stat-card">
        <div class="stat-icon orders-icon">📋</div>
        <div class="stat-info">
          <div class="stat-value">{{ dashboardData.totalOrders }}</div>
          <div class="stat-label">总订单数</div>
        </div>
      </div>
      
      <div class="stat-card sales-card">
        <div class="stat-icon">💰</div>
        <div class="stat-info">
          <div class="stat-value">¥{{ formatNumber(dashboardData.totalSales) }}</div>
          <div class="stat-label">总销售额</div>
        </div>
      </div>
      
      <div class="stat-card monthly-card">
        <div class="stat-icon">📈</div>
        <div class="stat-info">
          <div class="stat-value">¥{{ formatNumber(dashboardData.monthlySales) }}</div>
          <div class="stat-label">本月销售额</div>
        </div>
      </div>
    </div>

    <div class="charts-section">
      <div class="chart-card">
        <div class="chart-header">
          <h2>销售趋势</h2>
          <div class="chart-legend">
            <span class="legend-item"><span class="legend-color"></span> 销售额</span>
          </div>
        </div>
        <div class="sales-chart">
          <div class="chart-bars">
            <div 
              v-for="item in salesTrend" 
              :key="item.monthLabel"
              class="bar-item"
            >
              <div 
                class="bar" 
                :style="{ height: getBarHeight(item.sales) + '%' }"
              >
                <div class="bar-tooltip">¥{{ formatNumber(item.sales) }}</div>
              </div>
              <div class="bar-label">{{ item.monthLabel }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

interface DashboardData {
  totalUsers: number
  totalShops: number
  totalRiders: number
  totalOrders: number
  totalSales: number | string
  monthlySales: number | string
  monthlyOrders: number
}

interface TrendItem {
  year: number
  month: number
  monthLabel: string
  sales: number | string
  orders: number
}

const dashboardData = ref<DashboardData>({
  totalUsers: 0,
  totalShops: 0,
  totalRiders: 0,
  totalOrders: 0,
  totalSales: 0,
  monthlySales: 0,
  monthlyOrders: 0
})

const salesTrend = ref<TrendItem[]>([])

const currentDate = computed(() => {
  const now = new Date()
  return now.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

const formatNumber = (num: number | string) => {
  return Number(num).toLocaleString('zh-CN')
}

const getBarHeight = (sales: number | string) => {
  const maxSales = Math.max(...salesTrend.value.map(item => Number(item.sales)))
  if (maxSales === 0) return 0
  return (Number(sales) / maxSales) * 100
}

const fetchDashboardData = async () => {
  try {
    const response = await axios.get('/api/reports/dashboard')
    dashboardData.value = response.data
  } catch (error) {
    console.error('Failed to fetch dashboard data:', error)
  }
}

const fetchSalesTrend = async () => {
  try {
    const response = await axios.get('/api/reports/sales/trend?months=6')
    salesTrend.value = response.data
  } catch (error) {
    console.error('Failed to fetch sales trend:', error)
  }
}

onMounted(() => {
  fetchDashboardData()
  fetchSalesTrend()
})
</script>

<style scoped>
.dashboard {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.dashboard-header {
  margin-bottom: 2rem;
}

.dashboard-header h1 {
  font-size: 2rem;
  color: #333;
  margin: 0 0 0.5rem 0;
}

.dashboard-header .date {
  color: #888;
  font-size: 0.95rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  font-size: 2.5rem;
  margin-right: 1rem;
  padding: 1rem;
  border-radius: 12px;
  background: #f8f9fa;
}

.users-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.shops-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
}

.riders-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
}

.orders-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.sales-card .stat-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: white;
}

.monthly-card .stat-icon {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 0.2rem;
}

.stat-label {
  font-size: 0.9rem;
  color: #888;
}

.charts-section {
  display: grid;
  grid-template-columns: 1fr;
  gap: 1.5rem;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.chart-header h2 {
  font-size: 1.3rem;
  color: #333;
  margin: 0;
}

.chart-legend {
  display: flex;
  gap: 1rem;
}

.legend-item {
  display: flex;
  align-items: center;
  font-size: 0.9rem;
  color: #666;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  margin-right: 0.5rem;
}

.sales-chart {
  height: 300px;
}

.chart-bars {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  height: 100%;
  padding-bottom: 2rem;
}

.bar-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  height: 100%;
}

.bar {
  width: 60%;
  max-width: 50px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 6px 6px 0 0;
  position: relative;
  min-height: 10px;
  transition: height 0.3s ease;
}

.bar:hover {
  opacity: 0.8;
}

.bar-tooltip {
  position: absolute;
  top: -40px;
  left: 50%;
  transform: translateX(-50%);
  background: #333;
  color: white;
  padding: 0.4rem 0.8rem;
  border-radius: 4px;
  font-size: 0.85rem;
  white-space: nowrap;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.bar:hover .bar-tooltip {
  opacity: 1;
}

.bar-label {
  margin-top: 0.8rem;
  font-size: 0.8rem;
  color: #888;
  text-align: center;
}
</style>