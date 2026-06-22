package com.example.campusfooddelivery.config;

import com.example.campusfooddelivery.entity.Admin;
import com.example.campusfooddelivery.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;

    public DataInitializer(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // 如果管理员表为空，创建默认管理员账号
        if (!adminRepository.existsByUsername("admin")) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword("admin");
            adminRepository.save(admin);
            System.out.println("Default admin user created: username=admin, password=admin");
        }
    }
}