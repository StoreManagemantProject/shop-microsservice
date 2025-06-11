package com.example.demo.repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.ShopModel;

public interface ShopRepository extends JpaRepository<ShopModel,UUID> {
    
}
