package com.example.campusfooddelivery.repository;

import com.example.campusfooddelivery.entity.Rider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RiderRepository extends JpaRepository<Rider, Long> {
    Optional<Rider> findByPhone(String phone);
    Optional<Rider> findByIdCard(String idCard);
    boolean existsByPhone(String phone);
    boolean existsByIdCard(String idCard);
}