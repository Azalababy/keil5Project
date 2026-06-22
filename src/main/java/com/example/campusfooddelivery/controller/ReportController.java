package com.example.campusfooddelivery.controller;

import com.example.campusfooddelivery.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * 获取仪表盘汇总数据
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = reportService.getDashboardData();
        return ResponseEntity.ok(dashboard);
    }

    /**
     * 获取销售额报表
     * @param year 年份（可选）
     * @param month 月份（可选）
     */
    @GetMapping("/sales")
    public ResponseEntity<Map<String, Object>> getSalesReport(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        Map<String, Object> report = reportService.getSalesReport(year, month);
        return ResponseEntity.ok(report);
    }

    /**
     * 获取销售趋势数据
     * @param months 最近几个月，默认6个月
     */
    @GetMapping("/sales/trend")
    public ResponseEntity<List<Map<String, Object>>> getSalesTrend(
            @RequestParam(defaultValue = "6") int months) {
        List<Map<String, Object>> trend = reportService.getSalesTrend(months);
        return ResponseEntity.ok(trend);
    }

    /**
     * 获取商家统计数据
     * @param timeDimension 时间维度：day（按日）、month（按月）
     */
    @GetMapping("/shops/stats")
    public ResponseEntity<Map<String, Object>> getShopStats(
            @RequestParam(defaultValue = "month") String timeDimension) {
        Map<String, Object> stats = reportService.getShopStats(timeDimension);
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取商家TOP排行
     * @param timeDimension 时间维度：day（按日）、month（按月）
     * @param limit 返回数量限制，默认5
     */
    @GetMapping("/shops/top")
    public ResponseEntity<Map<String, Object>> getTopShops(
            @RequestParam(defaultValue = "month") String timeDimension,
            @RequestParam(defaultValue = "5") int limit) {
        Map<String, Object> topShops = reportService.getTopShops(timeDimension, limit);
        return ResponseEntity.ok(topShops);
    }

    /**
     * 获取商家订单趋势
     * @param timeDimension 时间维度：day（按日）、month（按月）
     */
    @GetMapping("/shops/trend")
    public ResponseEntity<List<Map<String, Object>>> getShopOrderTrend(
            @RequestParam(defaultValue = "month") String timeDimension) {
        List<Map<String, Object>> trend = reportService.getShopOrderTrend(timeDimension);
        return ResponseEntity.ok(trend);
    }
}