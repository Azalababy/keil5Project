package com.example.campusfooddelivery.service;

import com.example.campusfooddelivery.entity.Order;
import com.example.campusfooddelivery.entity.User;
import com.example.campusfooddelivery.entity.Shop;
import com.example.campusfooddelivery.entity.Rider;
import com.example.campusfooddelivery.repository.OrderRepository;
import com.example.campusfooddelivery.repository.UserRepository;
import com.example.campusfooddelivery.repository.ShopRepository;
import com.example.campusfooddelivery.repository.RiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private RiderRepository riderRepository;

    /**
     * 获取仪表盘汇总数据
     */
    public Map<String, Object> getDashboardData() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // 总用户数
        long totalUsers = userRepository.count();
        dashboard.put("totalUsers", totalUsers);
        
        // 总商家数
        long totalShops = shopRepository.count();
        dashboard.put("totalShops", totalShops);
        
        // 总骑手数
        long totalRiders = riderRepository.count();
        dashboard.put("totalRiders", totalRiders);
        
        // 总订单数
        long totalOrders = orderRepository.count();
        dashboard.put("totalOrders", totalOrders);
        
        // 总销售额
        BigDecimal totalSales = orderRepository.sumTotalPrice();
        dashboard.put("totalSales", totalSales != null ? totalSales : BigDecimal.ZERO);
        
        // 本月销售额
        YearMonth currentMonth = YearMonth.now();
        BigDecimal monthlySales = getMonthlySales(currentMonth.getYear(), currentMonth.getMonthValue());
        dashboard.put("monthlySales", monthlySales);
        
        // 本月订单数
        long monthlyOrders = getMonthlyOrders(currentMonth.getYear(), currentMonth.getMonthValue());
        dashboard.put("monthlyOrders", monthlyOrders);

        return dashboard;
    }

    /**
     * 获取指定月份的销售额
     */
    public BigDecimal getMonthlySales(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();
        
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        
        return orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 获取指定月份的订单数
     */
    public long getMonthlyOrders(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();
        
        return orderRepository.countByCreatedAtBetween(startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    /**
     * 获取销售额报表数据（支持按月份查询）
     */
    public Map<String, Object> getSalesReport(Integer year, Integer month) {
        Map<String, Object> report = new HashMap<>();
        
        if (year != null && month != null) {
            // 查询指定月份
            BigDecimal sales = getMonthlySales(year, month);
            long orders = getMonthlyOrders(year, month);
            
            report.put("year", year);
            report.put("month", month);
            report.put("sales", sales);
            report.put("orders", orders);
            
            // 同比数据（去年同月）
            BigDecimal lastYearSales = getMonthlySales(year - 1, month);
            report.put("lastYearSales", lastYearSales);
            report.put("salesGrowth", calculateGrowth(sales, lastYearSales));
            
        } else if (year != null) {
            // 查询全年数据
            List<Map<String, Object>> monthlyData = new ArrayList<>();
            BigDecimal totalYearSales = BigDecimal.ZERO;
            long totalYearOrders = 0;
            
            for (int m = 1; m <= 12; m++) {
                BigDecimal sales = getMonthlySales(year, m);
                long orders = getMonthlyOrders(year, m);
                
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", m);
                monthData.put("sales", sales);
                monthData.put("orders", orders);
                
                monthlyData.add(monthData);
                totalYearSales = totalYearSales.add(sales);
                totalYearOrders += orders;
            }
            
            report.put("year", year);
            report.put("monthlyData", monthlyData);
            report.put("totalSales", totalYearSales);
            report.put("totalOrders", totalYearOrders);
            
        } else {
            // 默认返回本年数据
            int currentYear = YearMonth.now().getYear();
            return getSalesReport(currentYear, null);
        }
        
        return report;
    }

    /**
     * 获取最近N个月的销售趋势数据
     */
    public List<Map<String, Object>> getSalesTrend(int months) {
        List<Map<String, Object>> trend = new ArrayList<>();
        YearMonth current = YearMonth.now();
        
        for (int i = months - 1; i >= 0; i--) {
            YearMonth ym = current.minusMonths(i);
            BigDecimal sales = getMonthlySales(ym.getYear(), ym.getMonthValue());
            long orders = getMonthlyOrders(ym.getYear(), ym.getMonthValue());
            
            Map<String, Object> data = new HashMap<>();
            data.put("year", ym.getYear());
            data.put("month", ym.getMonthValue());
            data.put("monthLabel", ym.getYear() + "年" + ym.getMonthValue() + "月");
            data.put("sales", sales);
            data.put("orders", orders);
            
            trend.add(data);
        }
        
        return trend;
    }

    /**
     * 计算增长率
     */
    private double calculateGrowth(BigDecimal current, BigDecimal previous) {
        if (previous == null || previous.compareTo(BigDecimal.ZERO) == 0) {
            return current.compareTo(BigDecimal.ZERO) > 0 ? 100 : 0;
        }
        return current.subtract(previous).divide(previous, 4, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).doubleValue();
    }

    /**
     * 获取商家统计数据
     */
    public Map<String, Object> getShopStats(String timeDimension) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalShops = shopRepository.count();
        stats.put("totalShops", totalShops);
        
        List<Order> orders = getFilteredOrders(timeDimension);
        long totalOrders = orders.size();
        BigDecimal totalRevenue = orders.stream()
                .map(Order::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue);
        stats.put("avgOrders", totalShops > 0 ? totalOrders / totalShops : 0);
        
        return stats;
    }

    /**
     * 获取商家TOP5（按订单量和营业额）
     */
    public Map<String, Object> getTopShops(String timeDimension, int limit) {
        Map<String, Object> result = new HashMap<>();
        
        List<Order> orders = getFilteredOrders(timeDimension);
        
        // 按订单量统计
        Map<Long, Long> orderCountMap = new HashMap<>();
        // 按营业额统计
        Map<Long, BigDecimal> revenueMap = new HashMap<>();
        
        for (Order order : orders) {
            Long shopId = order.getShopId();
            orderCountMap.merge(shopId, 1L, Long::sum);
            revenueMap.merge(shopId, order.getTotalPrice(), BigDecimal::add);
        }
        
        // 获取商家信息
        List<Shop> shops = shopRepository.findAll();
        Map<Long, String> shopNameMap = new HashMap<>();
        for (Shop shop : shops) {
            shopNameMap.put(shop.getId(), shop.getName());
        }
        
        // 按订单量排序取TOP
        List<Map<String, Object>> topByOrders = orderCountMap.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", shopNameMap.getOrDefault(entry.getKey(), "未知商家"));
                    item.put("value", entry.getValue());
                    return item;
                })
                .toList();
        
        // 按营业额排序取TOP
        List<Map<String, Object>> topByRevenue = revenueMap.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .map(entry -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", shopNameMap.getOrDefault(entry.getKey(), "未知商家"));
                    item.put("value", entry.getValue());
                    return item;
                })
                .toList();
        
        result.put("byOrders", topByOrders);
        result.put("byRevenue", topByRevenue);
        
        return result;
    }

    /**
     * 获取商家订单趋势数据
     */
    public List<Map<String, Object>> getShopOrderTrend(String timeDimension) {
        List<Map<String, Object>> trend = new ArrayList<>();
        
        List<Order> orders = getFilteredOrders(timeDimension);
        
        if ("day".equals(timeDimension)) {
            // 按日统计（最近7天）
            Map<LocalDate, Long> dailyOrders = new HashMap<>();
            LocalDate today = LocalDate.now();
            
            // 初始化最近7天
            for (int i = 6; i >= 0; i--) {
                dailyOrders.put(today.minusDays(i), 0L);
            }
            
            // 统计订单
            for (Order order : orders) {
                LocalDate orderDate = order.getCreatedAt().toLocalDate();
                if (dailyOrders.containsKey(orderDate)) {
                    dailyOrders.merge(orderDate, 1L, Long::sum);
                }
            }
            
            // 转换为趋势数据
            for (Map.Entry<LocalDate, Long> entry : dailyOrders.entrySet()) {
                Map<String, Object> data = new HashMap<>();
                data.put("label", entry.getKey().getMonthValue() + "/" + entry.getKey().getDayOfMonth());
                data.put("value", entry.getValue());
                trend.add(data);
            }
        } else {
            // 按月统计（最近6个月）
            Map<YearMonth, Long> monthlyOrders = new HashMap<>();
            YearMonth current = YearMonth.now();
            
            // 初始化最近6个月
            for (int i = 5; i >= 0; i--) {
                monthlyOrders.put(current.minusMonths(i), 0L);
            }
            
            // 统计订单
            for (Order order : orders) {
                YearMonth orderMonth = YearMonth.from(order.getCreatedAt().toLocalDate());
                if (monthlyOrders.containsKey(orderMonth)) {
                    monthlyOrders.merge(orderMonth, 1L, Long::sum);
                }
            }
            
            // 转换为趋势数据
            for (Map.Entry<YearMonth, Long> entry : monthlyOrders.entrySet()) {
                Map<String, Object> data = new HashMap<>();
                data.put("label", entry.getKey().getMonthValue() + "月");
                data.put("value", entry.getValue());
                trend.add(data);
            }
        }
        
        return trend;
    }

    /**
     * 根据时间维度获取过滤后的订单
     */
    private List<Order> getFilteredOrders(String timeDimension) {
        if ("day".equals(timeDimension)) {
            // 今日订单
            LocalDate today = LocalDate.now();
            return orderRepository.findByCreatedAtBetween(
                    today.atStartOfDay(), 
                    today.atTime(23, 59, 59)
            );
        } else {
            // 本月订单
            YearMonth currentMonth = YearMonth.now();
            LocalDate startDate = currentMonth.atDay(1);
            LocalDate endDate = currentMonth.atEndOfMonth();
            return orderRepository.findByCreatedAtBetween(
                    startDate.atStartOfDay(), 
                    endDate.atTime(23, 59, 59)
            );
        }
    }
}