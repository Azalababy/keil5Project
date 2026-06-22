// 商家名称到图标类型的映射
const shopIconMap: Record<string, string> = {
  '正宗黄焖鸡': 'yellow-chicken',
  '功夫麻辣烫': 'spicy-soup',
  '沙县小吃': 'snack',
  '五谷渔粉': 'fish-noodle',
  '大米先生': 'rice',
  '脆皮鸡米饭': 'crispy-chicken',
  '酸辣粉': 'hot-sour-noodle',
  '兰州拉面': 'lanzhou-noodle',
  '武汉热干面': 'hot-dry-noodle',
  '奥尔良烤鸡': 'roast-chicken',
  '黄焖鸡': 'yellow-chicken',
  '东北饺子': 'dumpling',
  '花甲粉': 'clam-noodle',
  '螺蛳粉': 'snail-noodle',
  '川菜馆': 'sichuan',
  '粥铺': 'porridge',
  '甜品店': 'dessert',
  '水果摊': 'fruit',
  '寿司店': 'sushi',
  '便当店': 'bento',
  '汉堡店': 'burger',
  '炸鸡店': 'fried-chicken',
  '奶茶店': 'milk-tea',
  '烧烤店': 'bbq',
  '烤鱼店': 'fish',
  '火锅店': 'hotpot',
  '披萨店': 'pizza',
  '正宗千年老店': 'dish',
  '川湘菜馆': 'sichuan',
  '东北饺子馆': 'dumpling',
  '黄焖鸡米饭': 'yellow-chicken',
  '麻辣香锅': 'spicy-soup',
  '重庆小面': 'chongqing-noodle',
  '云南过桥米线': 'rice-noodle',
  '韩式炸鸡': 'fried-chicken',
  '日式寿司': 'sushi',
  '披萨小屋': 'pizza',
  '汉堡王': 'burger',
  '麻辣烫': 'spicy-soup',
  '烧烤摊': 'bbq',
  '奶茶铺': 'milk-tea',
  '水果捞': 'fruit',
  '早餐店': 'porridge',
  '包子铺': 'bun',
  '饺子馆': 'dumpling',
  '面馆': 'noodle',
  '湘菜馆': 'sichuan',
  '粤菜馆': 'dish',
  '东北菜': 'dish',
  '家常菜': 'dish',
  '小吃店': 'snack',
  '快餐店': 'bento',
  '海鲜店': 'seafood',
  '蛋糕店': 'dessert',
  '面包店': 'dessert',
  '咖啡店': 'milk-tea',
  '饮品店': 'drink',
  '水果店': 'fruit',
  '生鲜店': 'fruit',
  '熟食店': 'braised-pork',
  '卤味店': 'braised-pork',
  '冒菜店': 'spicy-soup',
  '干锅店': 'spicy-soup',
  '米线店': 'rice-noodle',
  '米粉店': 'rice-noodle',
  '酸辣粉店': 'hot-sour-noodle',
  '螺蛳粉店': 'snail-noodle',
  '日料店': 'sushi',
  '韩料店': 'bbq',
  '西餐店': 'dish'
}

// 菜品名称到图标类型的映射（精确匹配）
const foodNameMap: Record<string, string> = {
  // 黄焖鸡类
  '黄焖鸡米饭': 'yellow-chicken',
  '黄焖排骨饭': 'yellow-chicken',
  '黄焖牛腩饭': 'yellow-chicken',
  '黄焖豆腐饭': 'yellow-chicken',
  '黄焖土豆饭': 'yellow-chicken',
  
  // 川菜类
  '麻婆豆腐': 'mapo-tofu',
  '宫保鸡丁': 'kung-pao',
  '鱼香肉丝': 'fish-fragrance',
  '回锅肉': 'twice-cooked',
  '水煮肉片': 'boiled-fish',
  
  // 东北菜类
  '猪肉白菜饺': 'dumpling',
  '韭菜鸡蛋饺': 'dumpling',
  '酸菜猪肉饺': 'dumpling',
  '三鲜水饺': 'dumpling',
  '小鸡炖蘑菇': 'dish',
  
  // 沙县小吃
  '蒸饺': 'dumpling',
  '煎饺': 'dumpling',
  '拌面': 'noodle',
  '扁肉': 'noodle',
  '蒸包': 'bun',
  
  // 拉面类
  '牛肉面': 'lanzhou-noodle',
  '羊肉面': 'lanzhou-noodle',
  '肥肠面': 'noodle',
  '清汤面': 'clear-noodle',
  '炒面': 'noodle',
  
  // 米饭类
  '卤肉饭': 'rice',
  '咖喱鸡饭': 'rice',
  '黑椒牛柳饭': 'rice',
  '香菇滑鸡饭': 'rice',
  '红烧排骨饭': 'rice',
  
  // 麻辣香锅类
  '麻辣香锅': 'spicy-soup',
  '麻辣牛肚': 'spicy-soup',
  '麻辣鸭血': 'spicy-soup',
  '麻辣豆腐': 'spicy-soup',
  '麻辣土豆': 'vegetable',
  
  // 重庆小面类 - 细分
  '重庆小面': 'chongqing-noodle',
  '麻辣小面': 'spicy-noodle',
  '清汤小面': 'clear-noodle',
  '杂酱小面': 'za-jiang-noodle',
  '牛肉小面': 'lanzhou-noodle',
  
  // 米线类
  '过桥米线': 'rice-noodle',
  '麻辣米线': 'spicy-noodle',
  '番茄米线': 'rice-noodle',
  '三鲜米线': 'rice-noodle',
  '牛肉米线': 'rice-noodle',
  
  // 炸鸡类
  '脆皮炸鸡': 'crispy-chicken',
  '香辣鸡翅': 'fried-chicken',
  '鸡米花': 'fried-chicken',
  '炸鸡腿': 'fried-chicken',
  '蜜汁烤翅': 'roast-chicken',
  
  // 寿司类
  '三文鱼寿司': 'sushi',
  '金枪鱼寿司': 'sushi',
  '加州卷': 'sushi',
  '鳗鱼寿司': 'sushi',
  '寿司套餐': 'sushi',
  
  // 披萨类
  '芝士披萨': 'pizza',
  '牛肉披萨': 'pizza',
  '水果披萨': 'pizza',
  '海鲜披萨': 'pizza',
  '至尊披萨': 'pizza',
  
  // 汉堡类
  '牛肉汉堡': 'burger',
  '鸡腿汉堡': 'burger',
  '芝士汉堡': 'burger',
  '双层汉堡': 'burger',
  '汉堡套餐': 'burger',
  
  // 麻辣烫类
  '经典麻辣烫': 'spicy-soup',
  '麻辣麻辣烫': 'spicy-soup',
  '清汤麻辣烫': 'spicy-soup',
  '海鲜麻辣烫': 'seafood',
  '蔬菜麻辣烫': 'vegetable',
  
  // 烧烤类
  '羊肉串': 'kebab',
  '牛肉串': 'kebab',
  '烤鸡翅': 'bbq',
  '烤茄子': 'vegetable',
  '烤玉米': 'vegetable',
  
  // 甜品类
  '芒果布丁': 'dessert',
  '提拉米苏': 'dessert',
  '芝士蛋糕': 'dessert',
  '水果沙拉': 'fruit',
  '冰淇淋': 'dessert',
  
  // 奶茶类
  '珍珠奶茶': 'milk-tea',
  '芋圆奶茶': 'milk-tea',
  '水果茶': 'drink',
  '奶盖茶': 'milk-tea',
  '杨枝甘露': 'dessert',
  
  // 水果类
  '西瓜切盒': 'fruit',
  '苹果': 'fruit',
  '香蕉': 'fruit',
  '草莓': 'fruit',
  '水果拼盘': 'fruit',
  
  // 早餐类
  '皮蛋瘦肉粥': 'porridge',
  '小米粥': 'porridge',
  '南瓜粥': 'porridge',
  '豆浆油条': 'porridge',
  '包子': 'bun',
  
  // 便当类
  '鸡腿便当': 'bento',
  '鱼排便当': 'bento',
  '叉烧便当': 'bento',
  '素食便当': 'bento',
  '双拼便当': 'bento',
  
  // 其他菜品
  '红烧肉': 'braised-pork',
  '糖醋排骨': 'sweet-sour-ribs',
  '番茄炒蛋': 'egg',
  '土豆丝': 'vegetable',
  '炒青菜': 'vegetable',
  '酸辣土豆丝': 'vegetable',
  '红烧鱼': 'fish',
  '清蒸鱼': 'fish',
  '炸鸡': 'fried-chicken',
  '烤鸭': 'roast-chicken',
  '鸡蛋面': 'noodle',
  '炒饭': 'fried-rice'
}

// 菜品关键词到图标类型的映射（用于模糊匹配）
const foodKeywordMap: Record<string, string> = {
  // 肉类菜品
  '红烧肉': 'braised-pork',
  '宫保鸡丁': 'kung-pao',
  '鱼香肉丝': 'fish-fragrance',
  '糖醋排骨': 'sweet-sour-ribs',
  '麻婆豆腐': 'mapo-tofu',
  '水煮鱼': 'boiled-fish',
  '水煮肉片': 'boiled-fish',
  '回锅肉': 'twice-cooked',
  '青椒肉丝': 'shredded-pork',
  '黄焖鸡': 'yellow-chicken',
  '黄焖排骨': 'yellow-chicken',
  '黄焖牛腩': 'yellow-chicken',
  '黄焖豆腐': 'yellow-chicken',
  '黄焖土豆': 'yellow-chicken',
  '脆皮鸡': 'crispy-chicken',
  '烤鸡': 'roast-chicken',
  '炸鸡': 'fried-chicken',
  '鸡腿': 'fried-chicken',
  '鸡翅': 'fried-chicken',
  '鸡米花': 'fried-chicken',
  '蜜汁烤翅': 'fried-chicken',
  '奥尔良': 'roast-chicken',
  '卤肉': 'braised-pork',
  '咖喱鸡': 'kung-pao',
  '黑椒牛柳': 'shredded-pork',
  '香菇滑鸡': 'kung-pao',
  '红烧排骨': 'sweet-sour-ribs',
  '叉烧': 'braised-pork',
  '鱼排': 'fish',
  '肥牛': 'hotpot',
  '羊肉': 'hotpot',
  '肥肠': 'noodle',
  '虾滑': 'seafood',
  
  // 主食类
  '米饭': 'rice',
  '炒饭': 'fried-rice',
  '盖饭': 'rice',
  '面条': 'noodle',
  '拉面': 'lanzhou-noodle',
  '热干面': 'hot-dry-noodle',
  '米粉': 'rice-noodle',
  '酸辣粉': 'hot-sour-noodle',
  '螺蛳粉': 'snail-noodle',
  '鱼粉': 'fish-noodle',
  '花甲粉': 'clam-noodle',
  '米线': 'rice-noodle',
  '小面': 'chongqing-noodle',
  '清汤面': 'clear-noodle',
  '杂酱面': 'za-jiang-noodle',
  '炒面': 'noodle',
  '牛肉面': 'lanzhou-noodle',
  '羊肉面': 'lanzhou-noodle',
  '肥肠面': 'noodle',
  '过桥米线': 'rice-noodle',
  '番茄米线': 'rice-noodle',
  '三鲜米线': 'rice-noodle',
  
  // 其他菜品
  '麻辣烫': 'spicy-soup',
  '麻辣香锅': 'spicy-soup',
  '麻辣牛肚': 'spicy-soup',
  '麻辣鸭血': 'spicy-soup',
  '麻辣豆腐': 'mapo-tofu',
  '麻辣土豆': 'vegetable',
  '火锅': 'hotpot',
  '麻辣锅底': 'hotpot',
  '番茄锅底': 'hotpot',
  '烧烤': 'bbq',
  '羊肉串': 'kebab',
  '牛肉串': 'kebab',
  '烤鸡翅': 'bbq',
  '烤茄子': 'vegetable',
  '烤玉米': 'vegetable',
  '烤鱼': 'fish',
  '饺子': 'dumpling',
  '猪肉白菜饺': 'dumpling',
  '韭菜鸡蛋饺': 'dumpling',
  '酸菜猪肉饺': 'dumpling',
  '三鲜水饺': 'dumpling',
  '小鸡炖蘑菇': 'dish',
  '包子': 'bun',
  '蒸饺': 'dumpling',
  '煎饺': 'dumpling',
  '拌面': 'noodle',
  '扁肉': 'dumpling',
  '蒸包': 'bun',
  '小吃': 'snack',
  '炒菜': 'dish',
  '川菜': 'sichuan',
  
  // 饮品甜品
  '奶茶': 'milk-tea',
  '珍珠奶茶': 'milk-tea',
  '芋圆奶茶': 'milk-tea',
  '水果茶': 'drink',
  '奶盖茶': 'milk-tea',
  '杨枝甘露': 'dessert',
  '甜品': 'dessert',
  '芒果布丁': 'dessert',
  '提拉米苏': 'dessert',
  '芝士蛋糕': 'dessert',
  '水果沙拉': 'fruit',
  '冰淇淋': 'dessert',
  '水果': 'fruit',
  '西瓜': 'fruit',
  '苹果': 'fruit',
  '香蕉': 'fruit',
  '草莓': 'fruit',
  '水果拼盘': 'fruit',
  '粥': 'porridge',
  '皮蛋瘦肉粥': 'porridge',
  '小米粥': 'porridge',
  '南瓜粥': 'porridge',
  '海鲜粥': 'porridge',
  '鸡肉粥': 'porridge',
  '豆浆': 'porridge',
  '油条': 'porridge',
  
  // 西餐
  '汉堡': 'burger',
  '牛肉汉堡': 'burger',
  '鸡腿汉堡': 'burger',
  '芝士汉堡': 'burger',
  '双层汉堡': 'burger',
  '汉堡套餐': 'burger',
  '披萨': 'pizza',
  '芝士披萨': 'pizza',
  '牛肉披萨': 'pizza',
  '水果披萨': 'pizza',
  '海鲜披萨': 'pizza',
  '至尊披萨': 'pizza',
  '寿司': 'sushi',
  '三文鱼寿司': 'sushi',
  '金枪鱼寿司': 'sushi',
  '加州卷': 'sushi',
  '鳗鱼寿司': 'sushi',
  '寿司套餐': 'sushi',
  '便当': 'bento',
  '鸡腿便当': 'bento',
  '鱼排便当': 'bento',
  '叉烧便当': 'bento',
  '素食便当': 'bento',
  '双拼便当': 'bento',
  
  // 通用匹配
  '饭': 'rice',
  '粉': 'rice-noodle',
  '面': 'noodle',
  '肉': 'braised-pork',
  '鸡': 'yellow-chicken',
  '鱼': 'fish',
  '豆腐': 'mapo-tofu',
  '汤': 'soup',
  '沙拉': 'fruit',
  '蔬菜': 'vegetable',
  '蛋': 'egg'
}

// 图标类型到样式的映射
export const iconStyleMap: Record<string, { bg: string; icon: string; label: string }> = {
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
  
  // 面食类
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

export function getShopIcon(shopName: string): string {
  if (shopIconMap[shopName]) {
    return shopIconMap[shopName]
  }
  
  const shopTypeKeywords: Record<string, string> = {
    '早餐': 'porridge',
    '包子': 'bun',
    '饺子': 'dumpling',
    '面': 'noodle',
    '川菜': 'sichuan',
    '湘菜': 'sichuan',
    '粤菜': 'dish',
    '东北': 'dish',
    '家常': 'dish',
    '小吃': 'snack',
    '快餐': 'bento',
    '炸鸡': 'fried-chicken',
    '烧烤': 'bbq',
    '火锅': 'hotpot',
    '海鲜': 'seafood',
    '甜品': 'dessert',
    '蛋糕': 'dessert',
    '面包': 'dessert',
    '咖啡': 'milk-tea',
    '饮品': 'drink',
    '水果': 'fruit',
    '生鲜': 'fruit',
    '熟食': 'braised-pork',
    '卤味': 'braised-pork',
    '麻辣烫': 'spicy-soup',
    '冒菜': 'spicy-soup',
    '麻辣香锅': 'spicy-soup',
    '干锅': 'spicy-soup',
    '米线': 'rice-noodle',
    '米粉': 'rice-noodle',
    '酸辣粉': 'hot-sour-noodle',
    '螺蛳粉': 'snail-noodle',
    '披萨': 'pizza',
    '汉堡': 'burger',
    '寿司': 'sushi',
    '日料': 'sushi',
    '韩料': 'bbq',
    '西餐': 'dish',
    '粥': 'porridge',
    '米饭': 'rice',
    '炒饭': 'fried-rice',
    '盖饭': 'rice'
  }
  
  for (const keyword of Object.keys(shopTypeKeywords)) {
    if (shopName.includes(keyword)) {
      return shopTypeKeywords[keyword]
    }
  }
  
  for (const keyword of Object.keys(foodKeywordMap)) {
    if (shopName.includes(keyword)) {
      return foodKeywordMap[keyword]
    }
  }
  
  return 'default'
}

export function getFoodIcon(foodName: string): string {
  // 1. 首先尝试精确匹配完整菜品名称
  if (foodNameMap[foodName]) {
    return foodNameMap[foodName]
  }
  
  // 2. 再尝试关键词精确匹配
  if (foodKeywordMap[foodName]) {
    return foodKeywordMap[foodName]
  }
  
  // 3. 按关键词长度排序，优先匹配更长、更具体的关键词
  const sortedKeywords = Object.keys(foodKeywordMap)
    .filter(keyword => foodName.includes(keyword))
    .sort((a, b) => b.length - a.length)
  
  if (sortedKeywords.length > 0) {
    return foodKeywordMap[sortedKeywords[0]]
  }
  
  return 'default'
}

export function getCategoryIcon(category: string): string {
  const categoryMap: Record<string, string> = {
    '快餐': 'burger',
    '奶茶': 'milk-tea',
    '烧烤': 'bbq',
    '火锅': 'hotpot',
    '披萨': 'pizza',
    '水果': 'fruit',
    '甜品': 'dessert',
    '炒菜': 'dish',
    '面食': 'noodle',
    '米饭': 'rice',
    '小吃': 'snack'
  }
  
  if (categoryMap[category]) {
    return categoryMap[category]
  }
  
  return getFoodIcon(category)
}

export function getIconStyle(iconType: string): { bg: string; icon: string; label: string } {
  return iconStyleMap[iconType] || iconStyleMap['default']
}