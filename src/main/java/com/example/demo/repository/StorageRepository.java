package com.example.demo.repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.StorageModel;

public interface StorageRepository extends JpaRepository<StorageModel, Long> {
    void addProductToStorage(Long storageId, UUID productId);
}