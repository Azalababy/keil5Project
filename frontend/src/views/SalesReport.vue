<template>
  <div class="sales-report">
    <div class="report-header">
      <h1>销售额报表</h1>
    </div>

    <div class="filter-section">
      <div class="filter-group">
        <label>年份</label>
        <select v-model="filters.year" @change="fetchReport">
          <option value="">全部年份</option>
          <option v-for="year in availableYears" :key="year" :value="year">{{ year }}年</option>
        </select>
      </div>
      <div class="filter-group">
        <label>月份</label>
        <select v-model="filters.month" @change="fetchReport">
          <option value="">全部月份</option>
          <option v-for="month in 12" :key="month" :value="month">{{ month }}月</option>
        </select>
      </div>
      <button @click="fetchReport" class="search-btn">查询</button>
    </div>

    <div class="report-content">
      <!-- 月度详情 -->
      <div v-if="reportData.month" class="detail-section">
        <div class="detail-card">
          <h3>{{ reportData.year }}年{{ reportData.month }}月销售详情</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <div class="detail-value">¥{{ formatNumber(reportData.sales) }}</div>
              <div class="detail-label">销售额</div>
            </div>
            <div class="detail-item">
              <div class="detail-value">{{ reportData.orders }}</div>
              <div class="detail-label">订单数</div>
            </div>
            <div class="detail-item">
              <div class="detail-value">¥{{ formatNumber(reportData.lastYearSales) }}</div>
              <div class="detail-label">去年同期</div>
            </div>
            <div class="detail-item">
              <div :class="['detail-value', reportData.salesGrowth >= 0 ? 'positive' : 'negative']">
                {{ reportData.salesGrowth >= 0 ? '+' : '' }}{{ reportData.salesGrowth.toFixed(2) }}%
              </div>
              <div class="detail-label">同比增长</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 年度汇总 -->
      <div v-else class="summary-section">
        <div class="summary-header">
          <h3>{{ reportData.year }}年销售汇总</h3>
          <div class="summary-total">
            <span class="total-label">年度总计</span>
            <span class="total-value">¥{{ formatNumber(reportData.totalSales) }}</span>
          </div>
        </div>
        
        <div class="monthly-table">
          <table>
            <thead>
              <tr>
                <th>月份</th>
                <th>销售额</th>
                <th>订单数</th>
                <th>占比</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="item in reportData.monthlyData" :key="item.month">
                <td>{{ item.month }}月</td>
                <td>¥{{ formatNumber(item.sales) }}</td>
                <td>{{ item.orders }}</td>
                <td>
                  <div class="percentage-bar">
                    <div 
                      class="percentage-fill" 
                      :style="{ width: getPercentage(item.sales) + '%' }"
                    ></div>
                    <span class="percentage-text">{{ getPercentage(item.sales).toFixed(1) }}%</span>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import axios from 'axios'

interface ReportData {
  year?: number
  month?: number
  sales?: number | string
  orders?: number
  lastYearSales?: number | string
  salesGrowth?: number
  totalSales?: number | string
  totalOrders?: number
  monthlyData?: Array<{
    month: number
    sales: number | string
    orders: number
  }>
}

const filters = reactive({
  year: '',
  month: ''
})

const reportData = ref<ReportData>({})

const availableYears = computed(() => {
  const currentYear = new Date().getFullYear()
  return Array.from({ length: 5 }, (_, i) => currentYear - i)
})

const formatNumber = (num: number | string | undefined) => {
  if (!num) return '0'
  return Number(num).toLocaleString('zh-CN')
}

const getPercentage = (sales: number | string) => {
  if (!reportData.value.totalSales) return 0
  return (Number(sales) / Number(reportData.value.totalSales)) * 100
}

const fetchReport = async () => {
  const params: Record<string, string> = {}
  if (filters.year) params.year = filters.year
  if (filters.month) params.month = filters.month

  try {
    const response = await axios.get('/api/reports/sales', { params })
    reportData.value = response.data
  } catch (error) {
    console.error('Failed to fetch sales report:', error)
  }
}

onMounted(() => {
  fetchReport()
})
</script>

<style scoped>
.sales-report {
  padding: 2rem;
  max-width: 1000px;
  margin: 0 auto;
}

.report-header h1 {
  font-size: 1.8rem;
  color: #333;
  margin: 0 0 1.5rem 0;
}

.filter-section {
  display: flex;
  gap: 1rem;
  margin-bottom: 1.5rem;
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-group label {
  font-size: 0.95rem;
  color: #666;
  font-weight: 500;
}

.filter-group select {
  padding: 0.6rem 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 0.95rem;
  min-width: 120px;
}

.search-btn {
  background: #667eea;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: background 0.3s ease;
}

.search-btn:hover {
  background: #5a6fd6;
}

.report-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 1.5rem;
}

.detail-card h3 {
  font-size: 1.2rem;
  color: #333;
  margin: 0 0 1rem 0;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 1rem;
}

.detail-item {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 1rem;
  text-align: center;
}

.detail-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #333;
  margin-bottom: 0.3rem;
}

.detail-value.positive {
  color: #28a745;
}

.detail-value.negative {
  color: #dc3545;
}

.detail-label {
  font-size: 0.85rem;
  color: #888;
}

.summary-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.summary-header h3 {
  font-size: 1.2rem;
  color: #333;
  margin: 0;
}

.summary-total {
  display: flex;
  align-items: baseline;
  gap: 0.5rem;
}

.total-label {
  font-size: 0.9rem;
  color: #888;
}

.total-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: #667eea;
}

.monthly-table {
  overflow-x: auto;
}

.monthly-table table {
  width: 100%;
  border-collapse: collapse;
}

.monthly-table th,
.monthly-table td {
  padding: 0.8rem;
  text-align: left;
  border-bottom: 1px solid #f1f3f5;
}

.monthly-table th {
  background: #f8f9fa;
  font-weight: 600;
  color: #666;
}

.percentage-bar {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.percentage-fill {
  height: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 6px;
  min-width: 20px;
  transition: width 0.3s ease;
}

.percentage-text {
  font-size: 0.85rem;
  color: #666;
  min-width: 40px;
  text-align: right;
}
</style>