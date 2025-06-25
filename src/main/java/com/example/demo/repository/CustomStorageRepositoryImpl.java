package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.models.ProductModel;
import com.example.demo.models.StorageModel;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CustomStorageRepositoryImpl implements CustomStorageRepository {

    private final EntityManager entityManager;

    @Override
    @Transactional
    public void addProductToStorage(Long storageId, ProductModel product) {
        StorageModel storage = entityManager.find(StorageModel.class, storageId);
        if (storage != null) {
            storage.addProduct(product);
            entityManager.merge(storage);
        }
    }
}