package com.example.campusfooddelivery.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
public class ImageProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchImage(@RequestParam String keyword) {
        Map<String, Object> result = new HashMap<>();
        java.util.List<String> imageUrls = new java.util.ArrayList<>();
        
        try {
            // 尝试从百度图片搜索
            imageUrls = searchFromBaidu(keyword);
        } catch (Exception e) {
            // 百度图片不可用，使用备用方案
            System.out.println("Baidu image search failed, using fallback: " + e.getMessage());
        }
        
        // 如果百度图片没有结果，使用picsum.photos作为备用
        if (imageUrls.isEmpty()) {
            imageUrls = getFallbackImages(keyword);
        }
        
        if (!imageUrls.isEmpty()) {
            result.put("success", true);
            result.put("imageUrls", imageUrls);
            result.put("keyword", keyword);
        } else {
            result.put("success", false);
            result.put("message", "未找到相关图片");
            result.put("imageUrls", java.util.Collections.emptyList());
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 从百度图片搜索
     */
    private java.util.List<String> searchFromBaidu(String keyword) throws Exception {
        java.util.List<String> imageUrls = new java.util.ArrayList<>();
        String[] pages = {"0", "10", "20"};
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
        headers.set("Referer", "https://image.baidu.com/");
        headers.set("Accept", "application/json, text/plain, */*");
        headers.set("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        headers.set("Accept-Encoding", "gzip, deflate, br");
        headers.set("Connection", "keep-alive");
        headers.set("Cookie", "BAIDUID=1234567890ABCDEF; BIDUPSID=1234567890ABCDEF; PSTM=1234567890;");
        headers.set("Host", "image.baidu.com");
        
        String[] searchKeywords = {
            keyword + " 菜品",
            keyword + " 美食",
            keyword + " 菜",
            keyword
        };
        
        for (String searchKeyword : searchKeywords) {
            if (imageUrls.size() >= 10) break;
            
            for (String pn : pages) {
                if (imageUrls.size() >= 10) break;
                
                String encodedKeyword = URLEncoder.encode(searchKeyword, StandardCharsets.UTF_8);
                encodedKeyword = encodedKeyword.replace("+", "%20");
                String baiduUrl = "https://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&word=" 
                        + encodedKeyword + "&pn=" + pn + "&rn=10";
                
                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> stringResponse = restTemplate.exchange(baiduUrl, HttpMethod.GET, entity, String.class);
                String body = stringResponse.getBody();
                
                if (body != null && !body.isEmpty()) {
                    // 检查是否被阻止
                    if (body.contains("Forbid spider access")) {
                        throw new Exception("Baidu blocked spider access");
                    }
                    
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> baiduResult = mapper.readValue(body, Map.class);
                        if (baiduResult.containsKey("data")) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> dataList = (List<Map<String, Object>>) baiduResult.get("data");
                            if (dataList != null) {
                                for (Map<String, Object> item : dataList) {
                                    if (imageUrls.size() >= 10) break;
                                    
                                    String url = null;
                                    if (item.containsKey("middleURL")) {
                                        url = (String) item.get("middleURL");
                                    } else if (item.containsKey("thumbURL")) {
                                        url = (String) item.get("thumbURL");
                                    } else if (item.containsKey("objURL")) {
                                        url = (String) item.get("objURL");
                                    }
                                    
                                    if (url != null && !url.isEmpty() && isValidFoodImage(url)) {
                                        imageUrls.add(url);
                                    }
                                }
                            }
                        }
                    } catch (Exception parseEx) {
                        // JSON解析失败，跳过
                    }
                }
                
                Thread.sleep(150);
            }
        }
        
        return imageUrls;
    }
    
    /**
     * 备用图片方案 - 使用Foodish API获取美食图片
     */
    private java.util.List<String> getFallbackImages(String keyword) {
        java.util.List<String> imageUrls = new java.util.ArrayList<>();
        
        // 使用Foodish API获取美食图片
        try {
            String foodishUrl = "https://foodish-api.herokuapp.com/api/";
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            
            // 获取多张图片
            for (int i = 0; i < 5; i++) {
                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(foodishUrl, HttpMethod.GET, entity, String.class);
                
                if (response.getBody() != null) {
                    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> result = mapper.readValue(response.getBody(), Map.class);
                        String imageUrl = (String) result.get("image");
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            imageUrls.add(imageUrl);
                        }
                    } catch (Exception e) {
                        // 解析失败，跳过
                    }
                }
                
                Thread.sleep(100);
            }
        } catch (Exception e) {
            System.out.println("Foodish API failed, using static food images");
        }
        
        // 如果Foodish API失败，使用静态美食图片
        if (imageUrls.isEmpty()) {
            imageUrls = getStaticFoodImages(keyword);
        }
        
        return imageUrls;
    }
    
    /**
     * 静态美食图片库 - 根据菜品类型返回合适的图片
     */
    private java.util.List<String> getStaticFoodImages(String keyword) {
        java.util.List<String> imageUrls = new java.util.ArrayList<>();
        
        // 根据关键词选择合适的美食图片
        if (keyword.contains("肉") || keyword.contains("排骨") || keyword.contains("鸡") || keyword.contains("鸭") || keyword.contains("鱼")) {
            // 肉类菜品 - 使用红烧肉、烤鸡、牛排等图片
            imageUrls.add("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1547592166-23ac55094343?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1544025162-d76694265947?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1546099606-160487344835?w=300&h=300&fit=crop");
        } else if (keyword.contains("面") || keyword.contains("粉") || keyword.contains("饭")) {
            // 面食/米饭类 - 使用面条、炒饭等图片
            imageUrls.add("https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1544568100-847a948585b9?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1436891620584-47fd0e565afb?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1547592166-e83e54860494?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1498837167922-ddd27525d352?w=300&h=300&fit=crop");
        } else if (keyword.contains("菜") || keyword.contains("豆腐") || keyword.contains("蛋")) {
            // 蔬菜/蛋类 - 使用蔬菜、豆腐、蛋类图片
            imageUrls.add("https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1448479560548-cf280a745a4c?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1547082299870-440544450789?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1546099606-160487344835?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1544568100-847a948585b9?w=300&h=300&fit=crop");
        } else if (keyword.contains("火锅") || keyword.contains("麻辣烫") || keyword.contains("串串")) {
            // 火锅类 - 使用火锅、麻辣烫图片
            imageUrls.add("https://images.unsplash.com/photo-1544025162-d76694265947?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1546099606-160487344835?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1547592166-e83e54860494?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1498837167922-ddd27525d352?w=300&h=300&fit=crop");
        } else if (keyword.contains("汉堡") || keyword.contains("炸鸡") || keyword.contains("披萨") || keyword.contains("薯条")) {
            // 西式快餐 - 使用汉堡、炸鸡、披萨等图片
            imageUrls.add("https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1544568100-847a948585b9?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1436891620584-47fd0e565afb?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1448479560548-cf280a745a4c?w=300&h=300&fit=crop");
        } else if (keyword.contains("包子") || keyword.contains("饺子") || keyword.contains("馄饨")) {
            // 中式点心 - 使用包子、饺子等图片
            imageUrls.add("https://images.unsplash.com/photo-1547082299870-440544450789?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1547592166-23ac55094343?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1436891620584-47fd0e565afb?w=300&h=300&fit=crop");
        } else {
            // 默认美食图片
            imageUrls.add("https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1547592166-23ac55094343?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1459411552884-841db9b3cc2a?w=300&h=300&fit=crop");
            imageUrls.add("https://images.unsplash.com/photo-1436891620584-47fd0e565afb?w=300&h=300&fit=crop");
        }
        
        // 根据关键词哈希打乱顺序，使相同菜品显示相同图片
        int seed = keyword.hashCode() & 0x7fffffff;
        java.util.Collections.shuffle(imageUrls, new java.util.Random(seed));
        
        return imageUrls;
    }
    
    /**
     * 过滤掉明显不是菜品的图片（根据URL特征）
     */
    private boolean isValidFoodImage(String url) {
        // 排除包含以下关键词的图片（通常是电子产品、人物等）
        String[] invalidKeywords = {"手机", "电脑", "CPU", "芯片", "主板", "科技", "发布会", "海报", "广告"};
        for (String keyword : invalidKeywords) {
            if (url.contains(keyword)) {
                return false;
            }
        }
        // 优先选择常见的图片格式
        return url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png");
    }
}