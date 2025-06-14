package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.ProductModel;

public interface ProductRepository extends JpaRepository<ProductModel, Long> {
}
