package com.example.campusfooddelivery.controller;

import com.example.campusfooddelivery.dto.ShopLoginDTO;
import com.example.campusfooddelivery.entity.Rider;
import com.example.campusfooddelivery.service.RiderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/riders")
@CrossOrigin(origins = "*")
public class RiderController {

    @Autowired
    private RiderService riderService;

    @GetMapping
    public ResponseEntity<List<Rider>> findAll() {
        List<Rider> riders = riderService.findAll();
        return ResponseEntity.ok(riders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rider> findById(@PathVariable Long id) {
        Optional<Rider> rider = riderService.findById(id);
        return rider.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> save(@RequestBody Rider rider) {
        Map<String, Object> response = new HashMap<>();
        
        if (riderService.existsByPhone(rider.getPhone())) {
            response.put("success", false);
            response.put("message", "该手机号已被注册");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        if (riderService.existsByIdCard(rider.getIdCard())) {
            response.put("success", false);
            response.put("message", "该身份证号已被注册");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        Rider savedRider = riderService.save(rider);
        response.put("success", true);
        response.put("message", "注册成功");
        response.put("rider", savedRider);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rider> update(@PathVariable Long id, @RequestBody Rider rider) {
        Optional<Rider> existingRider = riderService.findById(id);
        if (existingRider.isPresent()) {
            rider.setId(id);
            Rider updatedRider = riderService.save(rider);
            return ResponseEntity.ok(updatedRider);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (riderService.findById(id).isPresent()) {
            riderService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody ShopLoginDTO loginDTO) {
        Map<String, Object> response = new HashMap<>();
        Rider rider = riderService.login(loginDTO);
        if (rider != null) {
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("rider", rider);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "手机号或密码错误");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Rider> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        Rider.Status riderStatus = Rider.Status.valueOf(status);
        Rider updatedRider = riderService.updateStatus(id, riderStatus);
        if (updatedRider != null) {
            return ResponseEntity.ok(updatedRider);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
    
    @GetMapping("/{id}/stats")
    public ResponseEntity<Map<String, Object>> getRiderStats(@PathVariable Long id) {
        Map<String, Object> stats = riderService.getRiderStats(id);
        return ResponseEntity.ok(stats);
    }
}