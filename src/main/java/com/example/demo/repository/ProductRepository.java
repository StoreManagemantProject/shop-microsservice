package com.example.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.ProductModel;

public interface ProductRepository extends JpaRepository<ProductModel, UUID> {
    Optional<ProductModel> findByProductId(UUID productId);
}
