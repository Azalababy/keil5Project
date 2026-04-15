<template>
  <div class="home">
    <h2 class="page-title">商家列表</h2>
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
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
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

const shops = ref<Shop[]>([])

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
  padding: 1rem 0;
}

.page-title {
  font-size: 1.8rem;
  margin-bottom: 1.5rem;
  color: #333;
}

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
</style>
