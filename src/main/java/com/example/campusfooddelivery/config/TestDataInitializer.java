package com.example.campusfooddelivery.config;

import com.example.campusfooddelivery.entity.*;
import com.example.campusfooddelivery.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final RiderRepository riderRepository;
    private final OrderRepository orderRepository;
    private final DishRepository dishRepository;
    private final OrderItemRepository orderItemRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    // 用户名列表
    private final String[] userNames = {"张三", "李四", "王五", "赵六", "钱七", "孙八", "周九", "吴十",
            "郑一", "冯二", "陈三", "褚四", "卫五", "蒋六", "沈七", "韩八", "杨九", "朱十",
            "秦一", "尤二", "许三", "何四", "吕五", "施六", "张七", "孔八", "曹九", "严十",
            "华一", "金二"};
    
    private final String[] userPhones = {"13800138001", "13800138002", "13800138003", "13800138004", "13800138005",
            "13900139001", "13900139002", "13900139003", "13900139004", "13900139005",
            "15800158001", "15800158002", "15800158003", "15800158004", "15800158005",
            "15900159001", "15900159002", "15900159003", "15900159004", "15900159005",
            "18800188001", "18800188002", "18800188003", "18800188004", "18800188005",
            "18900189001", "18900189002", "18900189003", "18900189004", "18900189005"};

    // 商家名称列表
    private final String[] shopNames = {"正宗千年老店", "川湘菜馆", "东北饺子馆", "沙县小吃", "兰州拉面",
            "黄焖鸡米饭", "麻辣香锅", "重庆小面", "云南过桥米线", "韩式炸鸡",
            "日式寿司", "披萨小屋", "汉堡王", "麻辣烫", "烧烤摊",
            "甜品店", "奶茶铺", "水果捞", "早餐店", "便当店"};
    
    private final String[] shopAddresses = {"食堂一楼A区", "食堂二楼B区", "商业街1号", "商业街2号", "东门小吃街",
            "西门美食城", "南门餐馆", "北门大排档", "图书馆旁", "教学楼附近",
            "体育馆对面", "宿舍区1号", "宿舍区2号", "宿舍区3号", "实验楼附近",
            "行政楼旁", "校门口", "后街", "侧门", "创业园"};

    // 骑手姓名列表
    private final String[] riderNames = {"李明", "王强", "张伟", "刘洋", "陈磊",
            "杨帆", "赵鹏", "周杰", "吴涛", "郑浩",
            "孙伟", "马明", "朱军", "胡勇", "林波",
            "何刚", "高健", "罗飞", "郭峰", "梁超"};
    
    // 商家类型到菜品的映射
    private final Map<String, String[]> shopTypeDishes = new HashMap<>();
    
    {
        // 黄焖鸡
        shopTypeDishes.put("黄焖鸡", new String[]{"黄焖鸡米饭", "黄焖排骨饭", "黄焖牛腩饭", "黄焖豆腐饭", "黄焖土豆饭"});
        // 川菜
        shopTypeDishes.put("川菜", new String[]{"麻婆豆腐", "宫保鸡丁", "鱼香肉丝", "回锅肉", "水煮肉片"});
        // 东北菜
        shopTypeDishes.put("东北", new String[]{"猪肉白菜饺", "韭菜鸡蛋饺", "酸菜猪肉饺", "三鲜水饺", "小鸡炖蘑菇"});
        // 沙县小吃
        shopTypeDishes.put("沙县", new String[]{"蒸饺", "煎饺", "拌面", "扁肉", "蒸包"});
        // 拉面
        shopTypeDishes.put("拉面", new String[]{"牛肉面", "羊肉面", "肥肠面", "清汤面", "炒面"});
        // 米饭
        shopTypeDishes.put("米饭", new String[]{"卤肉饭", "咖喱鸡饭", "黑椒牛柳饭", "香菇滑鸡饭", "红烧排骨饭"});
        // 麻辣香锅
        shopTypeDishes.put("麻辣", new String[]{"麻辣香锅", "麻辣牛肚", "麻辣鸭血", "麻辣豆腐", "麻辣土豆"});
        // 重庆小面
        shopTypeDishes.put("小面", new String[]{"重庆小面", "麻辣小面", "清汤小面", "杂酱小面", "牛肉小面"});
        // 过桥米线
        shopTypeDishes.put("米线", new String[]{"过桥米线", "麻辣米线", "番茄米线", "三鲜米线", "牛肉米线"});
        // 炸鸡
        shopTypeDishes.put("炸鸡", new String[]{"脆皮炸鸡", "香辣鸡翅", "鸡米花", "炸鸡腿", "蜜汁烤翅"});
        // 寿司
        shopTypeDishes.put("寿司", new String[]{"三文鱼寿司", "金枪鱼寿司", "加州卷", "鳗鱼寿司", "寿司套餐"});
        // 披萨
        shopTypeDishes.put("披萨", new String[]{"芝士披萨", "牛肉披萨", "水果披萨", "海鲜披萨", "至尊披萨"});
        // 汉堡
        shopTypeDishes.put("汉堡", new String[]{"牛肉汉堡", "鸡腿汉堡", "芝士汉堡", "双层汉堡", "汉堡套餐"});
        // 麻辣烫
        shopTypeDishes.put("麻辣烫", new String[]{"经典麻辣烫", "麻辣麻辣烫", "清汤麻辣烫", "海鲜麻辣烫", "蔬菜麻辣烫"});
        // 烧烤
        shopTypeDishes.put("烧烤", new String[]{"羊肉串", "牛肉串", "烤鸡翅", "烤茄子", "烤玉米"});
        // 甜品
        shopTypeDishes.put("甜品", new String[]{"芒果布丁", "提拉米苏", "芝士蛋糕", "水果沙拉", "冰淇淋"});
        // 奶茶
        shopTypeDishes.put("奶茶", new String[]{"珍珠奶茶", "芋圆奶茶", "水果茶", "奶盖茶", "杨枝甘露"});
        // 水果
        shopTypeDishes.put("水果", new String[]{"西瓜切盒", "苹果", "香蕉", "草莓", "水果拼盘"});
        // 早餐
        shopTypeDishes.put("早餐", new String[]{"皮蛋瘦肉粥", "小米粥", "南瓜粥", "豆浆油条", "包子"});
        // 便当
        shopTypeDishes.put("便当", new String[]{"鸡腿便当", "鱼排便当", "叉烧便当", "素食便当", "双拼便当"});
        // 默认菜品
        shopTypeDishes.put("default", new String[]{"红烧肉", "宫保鸡丁", "鱼香肉丝", "糖醋排骨", "番茄炒蛋"});
    }
    
    // 菜品名称到图片的映射
    private final Map<String, String> dishImageMap = new HashMap<>();
    
    {
        // 黄焖鸡类
        dishImageMap.put("黄焖鸡米饭", "/images/chicken.jpg");
        dishImageMap.put("黄焖排骨饭", "/images/chicken.jpg");
        dishImageMap.put("黄焖牛腩饭", "/images/chicken.jpg");
        dishImageMap.put("黄焖豆腐饭", "/images/chicken.jpg");
        dishImageMap.put("黄焖土豆饭", "/images/chicken.jpg");
        
        // 川菜类
        dishImageMap.put("麻婆豆腐", "/images/tofu.jpg");
        dishImageMap.put("宫保鸡丁", "/images/kungpao.jpg");
        dishImageMap.put("鱼香肉丝", "/images/fishshredded.jpg");
        dishImageMap.put("回锅肉", "/images/meat.jpg");
        dishImageMap.put("水煮肉片", "/images/fish.jpg");
        
        // 东北菜类
        dishImageMap.put("猪肉白菜饺", "/images/dumpling.jpg");
        dishImageMap.put("韭菜鸡蛋饺", "/images/dumpling.jpg");
        dishImageMap.put("酸菜猪肉饺", "/images/dumpling.jpg");
        dishImageMap.put("三鲜水饺", "/images/dumpling.jpg");
        dishImageMap.put("小鸡炖蘑菇", "/images/chicken.jpg");
        
        // 沙县小吃
        dishImageMap.put("蒸饺", "/images/dumpling.jpg");
        dishImageMap.put("煎饺", "/images/dumpling.jpg");
        dishImageMap.put("拌面", "/images/noodles.jpg");
        dishImageMap.put("扁肉", "/images/noodles.jpg");
        dishImageMap.put("蒸包", "/images/bun.jpg");
        
        // 拉面类
        dishImageMap.put("牛肉面", "/images/noodles.jpg");
        dishImageMap.put("羊肉面", "/images/noodles.jpg");
        dishImageMap.put("肥肠面", "/images/noodles.jpg");
        dishImageMap.put("清汤面", "/images/noodles.jpg");
        dishImageMap.put("炒面", "/images/noodles.jpg");
        
        // 米饭类
        dishImageMap.put("卤肉饭", "/images/rice.jpg");
        dishImageMap.put("咖喱鸡饭", "/images/rice.jpg");
        dishImageMap.put("黑椒牛柳饭", "/images/rice.jpg");
        dishImageMap.put("香菇滑鸡饭", "/images/rice.jpg");
        dishImageMap.put("红烧排骨饭", "/images/rice.jpg");
        
        // 麻辣香锅类
        dishImageMap.put("麻辣香锅", "/images/hotpot.jpg");
        dishImageMap.put("麻辣牛肚", "/images/hotpot.jpg");
        dishImageMap.put("麻辣鸭血", "/images/hotpot.jpg");
        dishImageMap.put("麻辣豆腐", "/images/hotpot.jpg");
        dishImageMap.put("麻辣土豆", "/images/hotpot.jpg");
        
        // 重庆小面类
        dishImageMap.put("重庆小面", "/images/noodles.jpg");
        dishImageMap.put("麻辣小面", "/images/noodles.jpg");
        dishImageMap.put("清汤小面", "/images/noodles.jpg");
        dishImageMap.put("杂酱小面", "/images/noodles.jpg");
        dishImageMap.put("牛肉小面", "/images/noodles.jpg");
        
        // 米线类
        dishImageMap.put("过桥米线", "/images/noodles.jpg");
        dishImageMap.put("麻辣米线", "/images/noodles.jpg");
        dishImageMap.put("番茄米线", "/images/noodles.jpg");
        dishImageMap.put("三鲜米线", "/images/noodles.jpg");
        dishImageMap.put("牛肉米线", "/images/noodles.jpg");
        
        // 炸鸡类
        dishImageMap.put("脆皮炸鸡", "/images/friedchicken.jpg");
        dishImageMap.put("香辣鸡翅", "/images/friedchicken.jpg");
        dishImageMap.put("鸡米花", "/images/friedchicken.jpg");
        dishImageMap.put("炸鸡腿", "/images/friedchicken.jpg");
        dishImageMap.put("蜜汁烤翅", "/images/friedchicken.jpg");
        
        // 寿司类
        dishImageMap.put("三文鱼寿司", "/images/sushi.jpg");
        dishImageMap.put("金枪鱼寿司", "/images/sushi.jpg");
        dishImageMap.put("加州卷", "/images/sushi.jpg");
        dishImageMap.put("鳗鱼寿司", "/images/sushi.jpg");
        dishImageMap.put("寿司套餐", "/images/sushi.jpg");
        
        // 披萨类
        dishImageMap.put("芝士披萨", "/images/pizza.jpg");
        dishImageMap.put("牛肉披萨", "/images/pizza.jpg");
        dishImageMap.put("水果披萨", "/images/pizza.jpg");
        dishImageMap.put("海鲜披萨", "/images/pizza.jpg");
        dishImageMap.put("至尊披萨", "/images/pizza.jpg");
        
        // 汉堡类
        dishImageMap.put("牛肉汉堡", "/images/burger.jpg");
        dishImageMap.put("鸡腿汉堡", "/images/burger.jpg");
        dishImageMap.put("芝士汉堡", "/images/burger.jpg");
        dishImageMap.put("双层汉堡", "/images/burger.jpg");
        dishImageMap.put("汉堡套餐", "/images/burger.jpg");
        
        // 麻辣烫类
        dishImageMap.put("经典麻辣烫", "/images/hotpot.jpg");
        dishImageMap.put("麻辣麻辣烫", "/images/hotpot.jpg");
        dishImageMap.put("清汤麻辣烫", "/images/hotpot.jpg");
        dishImageMap.put("海鲜麻辣烫", "/images/hotpot.jpg");
        dishImageMap.put("蔬菜麻辣烫", "/images/hotpot.jpg");
        
        // 烧烤类
        dishImageMap.put("羊肉串", "/images/kebab.jpg");
        dishImageMap.put("牛肉串", "/images/kebab.jpg");
        dishImageMap.put("烤鸡翅", "/images/kebab.jpg");
        dishImageMap.put("烤茄子", "/images/kebab.jpg");
        dishImageMap.put("烤玉米", "/images/kebab.jpg");
        
        // 甜品类
        dishImageMap.put("芒果布丁", "/images/dessert.jpg");
        dishImageMap.put("提拉米苏", "/images/dessert.jpg");
        dishImageMap.put("芝士蛋糕", "/images/dessert.jpg");
        dishImageMap.put("水果沙拉", "/images/fruit.jpg");
        dishImageMap.put("冰淇淋", "/images/dessert.jpg");
        
        // 奶茶类
        dishImageMap.put("珍珠奶茶", "/images/drink.jpg");
        dishImageMap.put("芋圆奶茶", "/images/drink.jpg");
        dishImageMap.put("水果茶", "/images/drink.jpg");
        dishImageMap.put("奶盖茶", "/images/drink.jpg");
        dishImageMap.put("杨枝甘露", "/images/drink.jpg");
        
        // 水果类
        dishImageMap.put("西瓜切盒", "/images/fruit.jpg");
        dishImageMap.put("苹果", "/images/fruit.jpg");
        dishImageMap.put("香蕉", "/images/fruit.jpg");
        dishImageMap.put("草莓", "/images/fruit.jpg");
        dishImageMap.put("水果拼盘", "/images/fruit.jpg");
        
        // 早餐类
        dishImageMap.put("皮蛋瘦肉粥", "/images/porridge.jpg");
        dishImageMap.put("小米粥", "/images/porridge.jpg");
        dishImageMap.put("南瓜粥", "/images/porridge.jpg");
        dishImageMap.put("豆浆油条", "/images/porridge.jpg");
        dishImageMap.put("包子", "/images/bun.jpg");
        
        // 便当类
        dishImageMap.put("鸡腿便当", "/images/bento.jpg");
        dishImageMap.put("鱼排便当", "/images/bento.jpg");
        dishImageMap.put("叉烧便当", "/images/bento.jpg");
        dishImageMap.put("素食便当", "/images/bento.jpg");
        dishImageMap.put("双拼便当", "/images/bento.jpg");
        
        // 其他菜品
        dishImageMap.put("红烧肉", "/images/meat.jpg");
        dishImageMap.put("糖醋排骨", "/images/meat.jpg");
        dishImageMap.put("番茄炒蛋", "/images/egg.jpg");
        dishImageMap.put("土豆丝", "/images/vegetable.jpg");
        dishImageMap.put("炒青菜", "/images/vegetable.jpg");
        dishImageMap.put("酸辣土豆丝", "/images/vegetable.jpg");
        dishImageMap.put("红烧鱼", "/images/fish.jpg");
        dishImageMap.put("清蒸鱼", "/images/fish.jpg");
        dishImageMap.put("炸鸡", "/images/friedchicken.jpg");
        dishImageMap.put("烤鸭", "/images/friedchicken.jpg");
        dishImageMap.put("鸡蛋面", "/images/noodles.jpg");
        dishImageMap.put("炒饭", "/images/rice.jpg");
    }
    
    // 菜品图片列表（备用）
    private final String[] defaultDishImages = {
            "/images/burger.jpg", "/images/hotpot.jpg", "/images/noodles.jpg", 
            "/images/kebab.jpg", "/images/drink.jpg", "/images/chicken.jpg",
            "/images/friedchicken.jpg", "/images/sushi.jpg", "/images/pizza.jpg",
            "/images/rice.jpg", "/images/tofu.jpg", "/images/meat.jpg",
            "/images/fish.jpg", "/images/dumpling.jpg", "/images/bun.jpg",
            "/images/dessert.jpg", "/images/fruit.jpg", "/images/porridge.jpg",
            "/images/bento.jpg", "/images/egg.jpg", "/images/vegetable.jpg",
            "/images/kungpao.jpg", "/images/fishshredded.jpg"
    };

    // 订单状态
    private final Order.Status[] statuses = {Order.Status.pending, Order.Status.accepted, 
            Order.Status.delivered, Order.Status.cancelled};

    public TestDataInitializer(UserRepository userRepository, ShopRepository shopRepository,
                               RiderRepository riderRepository, OrderRepository orderRepository,
                               DishRepository dishRepository, OrderItemRepository orderItemRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.shopRepository = shopRepository;
        this.riderRepository = riderRepository;
        this.orderRepository = orderRepository;
        this.dishRepository = dishRepository;
        this.orderItemRepository = orderItemRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 仅在数据库为空时才初始化测试数据，避免清除用户注册的数据
        long existingUserCount = userRepository.count();
        if (existingUserCount > 0) {
            System.out.println("Database already has data (" + existingUserCount + " users), skipping test data initialization.");
            return;
        }
        System.out.println("Database is empty, initializing test data...");

        // 创建30个用户
        Long[] userIds = new Long[31];
        Long[] riderIds = new Long[20];
        int riderIndex = 0;
        
        // 创建root用户（用户名: root, 密码: 123456）
        User rootUser = new User();
        rootUser.setUsername("root");
        rootUser.setPassword(passwordEncoder.encode("123456"));
        rootUser.setName("超级管理员");
        rootUser.setPhone("13000130000");
        rootUser.setAddress("管理员办公室");
        User savedRoot = userRepository.save(rootUser);
        userIds[0] = savedRoot.getId();
        System.out.println("Created root user: username=root, password=123456");
        
        for (int i = 0; i < 30; i++) {
            User user = new User();
            user.setUsername("user" + (i + 1));
            user.setPassword(passwordEncoder.encode("123456"));
            user.setName(userNames[i]);
            user.setPhone(userPhones[i]);
            user.setAddress("宿舍" + (random.nextInt(10) + 1) + "号楼" + (random.nextInt(6) + 1) + "层" + (random.nextInt(30) + 1) + "室");
            User saved = userRepository.save(user);
            userIds[i + 1] = saved.getId();
        }
        System.out.println("Created 30 regular users.");

        // 商家图片列表
        String[] shopImages = {"/images/shop1.jpg", "/images/shop2.jpg", "/images/shop3.jpg", "/images/shop4.jpg", 
                               "/images/burger.jpg", "/images/hotpot.jpg", "/images/noodles.jpg", "/images/kebab.jpg",
                               "/images/drink.jpg", "/images/burger.jpg", "/images/hotpot.jpg", "/images/noodles.jpg",
                               "/images/kebab.jpg", "/images/drink.jpg", "/images/shop1.jpg", "/images/shop2.jpg",
                               "/images/shop3.jpg", "/images/shop4.jpg", "/images/burger.jpg", "/images/hotpot.jpg"};
        
        // 创建20个商家
        Long[] shopIds = new Long[20];
        for (int i = 0; i < 20; i++) {
            Shop shop = new Shop();
            shop.setName(shopNames[i]);
            shop.setPhone("13700137" + String.format("%03d", i + 1));
            shop.setPassword(passwordEncoder.encode("123456"));
            shop.setAddress(shopAddresses[i]);
            shop.setDescription("欢迎光临" + shopNames[i]);
            shop.setStatus(random.nextBoolean() ? Shop.Status.open : Shop.Status.closed);
            shop.setImageUrl(shopImages[i]);
            Shop saved = shopRepository.save(shop);
            shopIds[i] = saved.getId();
        }
        System.out.println("Created 20 shops.");

        // 创建20个骑手
        riderIndex = 0;
        for (int i = 0; i < 20; i++) {
            Rider rider = new Rider();
            rider.setName(riderNames[i]);
            rider.setPhone("13600136" + String.format("%03d", i + 1));
            rider.setPassword(passwordEncoder.encode("123456"));
            rider.setIdCard("11010119900101" + String.format("%04d", i + 1));
            rider.setStatus(random.nextBoolean() ? Rider.Status.online : Rider.Status.offline);
            rider.setTotalOrders(random.nextInt(500) + 100);
            rider.setTotalEarnings(random.nextDouble() * 20000 + 5000);
            Rider savedRider = riderRepository.save(rider);
            riderIds[riderIndex++] = savedRider.getId();
        }
        System.out.println("Created 20 riders.");

        // 创建菜品（根据商家类型匹配对应菜品）
        Long[] dishIds = new Long[100];
        int dishIndex = 0;
        
        for (int i = 0; i < shopIds.length; i++) {
            String shopName = shopNames[i];
            // 找到匹配的菜品类型
            String[] dishesForShop = shopTypeDishes.get("default");
            for (String type : shopTypeDishes.keySet()) {
                if (!"default".equals(type) && shopName.contains(type)) {
                    dishesForShop = shopTypeDishes.get(type);
                    break;
                }
            }
            
            int dishCount = random.nextInt(3) + 3; // 每个商家3-5个菜品
            for (int j = 0; j < dishCount && dishIndex < dishIds.length; j++) {
                Dish dish = new Dish();
                dish.setShopId(shopIds[i]);
                
                // 使用匹配的菜品名称
                String dishName = dishesForShop[j % dishesForShop.length];
                dish.setName(dishName);
                
                double price = Math.round((random.nextDouble() * 50 + 5) * 100) / 100.0;
                dish.setPrice(BigDecimal.valueOf(price));
                dish.setDescription("美味" + dishName);
                dish.setStatus(random.nextBoolean() ? Dish.Status.available : Dish.Status.unavailable);
                
                // 根据菜品名称获取对应的图片
                String imageUrl = dishImageMap.get(dishName);
                if (imageUrl == null) {
                    // 如果没有匹配的图片，使用默认图片列表
                    imageUrl = defaultDishImages[dishIndex % defaultDishImages.length];
                }
                dish.setImageUrl(imageUrl);
                
                Dish saved = dishRepository.save(dish);
                dishIds[dishIndex++] = saved.getId();
            }
        }
        System.out.println("Created " + dishIndex + " dishes.");

        // 创建100个订单
        List<Order> savedOrders = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Order order = new Order();
            order.setUserId(userIds[random.nextInt(31)]);
            order.setShopId(shopIds[random.nextInt(20)]);
            
            Order.Status status;
            if (i < 10) {
                // 前10个订单是今天的，确保大部分是已送达状态
                status = i < 7 ? Order.Status.delivered : statuses[random.nextInt(statuses.length)];
            } else {
                status = statuses[random.nextInt(statuses.length)];
            }
            order.setStatus(status);
            
            // 随机生成订单金额（10-200元）
            double totalPrice = Math.round((random.nextDouble() * 190 + 10) * 100) / 100.0;
            order.setTotalPrice(BigDecimal.valueOf(totalPrice));
            
            // 随机生成订单时间（大部分是最近30天内，确保有一些是今天的）
            LocalDateTime createdAt;
            if (i < 10) {
                // 前10个订单是今天的
                createdAt = LocalDateTime.now()
                        .minusHours(random.nextInt(6))
                        .minusMinutes(random.nextInt(60));
            } else {
                createdAt = LocalDateTime.now()
                        .minusDays(random.nextInt(30))
                        .minusHours(random.nextInt(24))
                        .minusMinutes(random.nextInt(60));
            }
            order.setCreatedAt(createdAt);
            
            // 如果是已送达状态，设置送达时间和骑手ID
            if (status == Order.Status.delivered) {
                int deliveryMinutes = random.nextInt(60) + 10; // 配送时长10-70分钟
                order.setDeliveredAt(createdAt.plusMinutes(deliveryMinutes)); // 送达时间比创建时间晚10-70分钟
                order.setRiderId(riderIds[random.nextInt(riderIndex)]);
                order.setDeliveryTime(String.valueOf(deliveryMinutes));
            }
            
            // 添加备注
            String[] remarks = {"多加辣", "少糖", "不要葱", "尽快送达", "送到楼下", "谢谢", "", "放门口"};
            order.setRemark(remarks[random.nextInt(remarks.length)]);
            
            // 设置配送地址
            order.setAddress("宿舍" + (random.nextInt(10) + 1) + "号楼" + (random.nextInt(6) + 1) + "层");
            order.setPhone(userPhones[random.nextInt(30)]);
            
            Order savedOrder = orderRepository.save(order);
            savedOrders.add(savedOrder);
        }
        System.out.println("Created 100 orders.");

        // 为每个订单添加订单详情（1-3个菜品）
        for (Order order : savedOrders) {
            int itemCount = random.nextInt(3) + 1; // 每个订单1-3个菜品
            BigDecimal totalPrice = BigDecimal.ZERO;
            
            for (int j = 0; j < itemCount; j++) {
                OrderItem item = new OrderItem();
                item.setOrderId(order.getId());
                
                // 随机选择一个菜品
                Long dishId = dishIds[random.nextInt(dishIndex)];
                item.setDishId(dishId);
                
                int quantity = random.nextInt(3) + 1; // 1-3份
                item.setQuantity(quantity);
                
                // 获取菜品信息
                Dish dish = dishRepository.findById(dishId).orElse(null);
                if (dish != null) {
                    item.setDishName(dish.getName());
                    item.setPrice(dish.getPrice());
                } else {
                    item.setDishName("未知菜品");
                    item.setPrice(BigDecimal.valueOf(10));
                }
                
                orderItemRepository.save(item);
                totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(quantity)));
            }
            
            // 更新订单总价
            order.setTotalPrice(totalPrice);
            orderRepository.save(order);
        }
        System.out.println("Created order items for all orders.");

        System.out.println("Test data initialization completed!");
    }
}