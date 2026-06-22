<template>
  <div 
    class="food-icon" 
    :style="{ backgroundColor: iconData.bg }"
    :title="iconData.label"
  >
    <span class="icon-emoji">{{ iconData.icon }}</span>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  type: string
}>()

const iconMap: Record<string, { bg: string; icon: string; label: string }> = {
  // 中式菜品 - 肉类
  'braised-pork': { bg: '#B71C1C', icon: '🍖', label: '红烧肉' },
  'kung-pao': { bg: '#FF8F00', icon: '🥜', label: '宫保鸡丁' },
  'fish-fragrance': { bg: '#FF6F00', icon: '🐟', label: '鱼香肉丝' },
  'sweet-sour-ribs': { bg: '#E65100', icon: '🍖', label: '糖醋排骨' },
  'mapo-tofu': { bg: '#4CAF50', icon: '🧈', label: '麻婆豆腐' },
  'boiled-fish': { bg: '#0288D1', icon: '🐟', label: '水煮鱼' },
  'twice-cooked': { bg: '#BF360C', icon: '🥓', label: '回锅肉' },
  'shredded-pork': { bg: '#FF7043', icon: '🥩', label: '肉丝' },
  
  // 鸡肉类
  'yellow-chicken': { bg: '#FFD100', icon: '🍗', label: '黄焖鸡' },
  'crispy-chicken': { bg: '#FF9800', icon: '🍗', label: '脆皮鸡' },
  'roast-chicken': { bg: '#E91E63', icon: '🍗', label: '烤鸡' },
  'fried-chicken': { bg: '#FFA726', icon: '🍗', label: '炸鸡' },
  
  // 面食类 - 细分类
  'noodle': { bg: '#E55A2B', icon: '🍝', label: '面食' },
  'lanzhou-noodle': { bg: '#9C27B0', icon: '🍜', label: '兰州拉面' },
  'hot-dry-noodle': { bg: '#607D8B', icon: '🍜', label: '热干面' },
  'hot-sour-noodle': { bg: '#F44336', icon: '🍜', label: '酸辣粉' },
  'snail-noodle': { bg: '#FF5722', icon: '🍜', label: '螺蛳粉' },
  'fish-noodle': { bg: '#4ECDC4', icon: '🐟', label: '鱼粉' },
  'clam-noodle': { bg: '#00BCD4', icon: '🦪', label: '花甲粉' },
  'rice-noodle': { bg: '#689F38', icon: '🍜', label: '米粉' },
  
  // 重庆小面细分
  'chongqing-noodle': { bg: '#E53935', icon: '🍜', label: '重庆小面' },
  'spicy-noodle': { bg: '#D32F2F', icon: '🌶️', label: '麻辣小面' },
  'clear-noodle': { bg: '#0288D1', icon: '🥣', label: '清汤小面' },
  'za-jiang-noodle': { bg: '#6D4C41', icon: '🍜', label: '杂酱小面' },
  
  // 米饭类
  'rice': { bg: '#8BC34A', icon: '🍚', label: '米饭' },
  'fried-rice': { bg: '#F9A825', icon: '🍛', label: '炒饭' },
  
  // 火锅麻辣烫类
  'spicy-soup': { bg: '#FF6B35', icon: '🍲', label: '麻辣烫' },
  'hotpot': { bg: '#C62828', icon: '🍲', label: '火锅' },
  
  // 烧烤类
  'bbq': { bg: '#BF360C', icon: '🍢', label: '烧烤' },
  'kebab': { bg: '#795548', icon: '🍢', label: '烤串' },
  
  // 鱼类
  'fish': { bg: '#0288D1', icon: '🐟', label: '鱼' },
  
  // 饺子包子类
  'dumpling': { bg: '#2196F3', icon: '🥟', label: '饺子' },
  'bun': { bg: '#FFCA28', icon: '🥟', label: '包子' },
  
  // 小吃炒菜类
  'snack': { bg: '#FF5252', icon: '🍪', label: '小吃' },
  'dish': { bg: '#7C4DFF', icon: '🍳', label: '炒菜' },
  'sichuan': { bg: '#DC143C', icon: '🌶️', label: '川菜' },
  
  // 汤粥类
  'porridge': { bg: '#FFEB3B', icon: '🥣', label: '粥' },
  'soup': { bg: '#4FC3F7', icon: '🥣', label: '汤' },
  
  // 饮品甜品
  'dessert': { bg: '#FF4081', icon: '🍰', label: '甜品' },
  'fruit': { bg: '#4CAF50', icon: '🍎', label: '水果' },
  'milk-tea': { bg: '#D4AC0D', icon: '🥤', label: '奶茶' },
  'drink': { bg: '#03A9F4', icon: '🧋', label: '饮品' },
  
  // 西餐
  'burger': { bg: '#FF7043', icon: '🍔', label: '汉堡' },
  'pizza': { bg: '#EF6C00', icon: '🍕', label: '披萨' },
  'sushi': { bg: '#009688', icon: '🍣', label: '寿司' },
  'bento': { bg: '#FFC107', icon: '🍱', label: '便当' },
  
  // 海鲜
  'seafood': { bg: '#00BCD4', icon: '🦐', label: '海鲜' },
  
  // 蔬菜类
  'vegetable': { bg: '#4CAF50', icon: '🥬', label: '蔬菜' },
  
  // 鸡蛋类
  'egg': { bg: '#FFEB3B', icon: '🥚', label: '鸡蛋' },
  
  'default': { bg: '#9E9E9E', icon: '🍽️', label: '美食' }
}

const iconData = computed(() => {
  return iconMap[props.type] || iconMap['default']
})
</script>

<style scoped>
.food-icon {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.food-icon:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.icon-emoji {
  font-size: 32px;
}
</style>