package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.ProductLogModel;

public interface ProductLogRepository extends JpaRepository<ProductLogModel, Long>{
    
}
