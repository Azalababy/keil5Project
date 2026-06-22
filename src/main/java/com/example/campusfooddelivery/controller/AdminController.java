package com.example.campusfooddelivery.controller;

import com.example.campusfooddelivery.entity.Admin;
import com.example.campusfooddelivery.service.AdminService;
import com.example.campusfooddelivery.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String username = request.get("username");
        String password = request.get("password");
        
        Admin admin = adminService.login(username, password);
        if (admin != null) {
            // 生成JWT Token，有效期6小时
            String token = jwtUtil.generateToken(username, "admin");
            
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("admin", admin);
            response.put("token", token);
            response.put("expiresIn", jwtUtil.getExpirationTime()); // 返回过期时间（毫秒）
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "用户名或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Admin admin) {
        Map<String, Object> response = new HashMap<>();
        
        if (adminService.existsByUsername(admin.getUsername())) {
            response.put("success", false);
            response.put("message", "用户名已存在");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        Admin savedAdmin = adminService.save(admin);
        response.put("success", true);
        response.put("message", "注册成功");
        response.put("admin", savedAdmin);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String token = request.get("token");
        
        try {
            if (token != null && !jwtUtil.isTokenExpired(token)) {
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);
                
                response.put("success", true);
                response.put("valid", true);
                response.put("username", username);
                response.put("role", role);
            } else {
                response.put("success", true);
                response.put("valid", false);
                response.put("message", "Token已过期");
            }
        } catch (Exception e) {
            response.put("success", true);
            response.put("valid", false);
            response.put("message", "Token无效");
        }
        
        return ResponseEntity.ok(response);
    }
}