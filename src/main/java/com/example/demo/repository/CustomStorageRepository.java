package com.example.demo.repository;

import com.example.demo.models.ProductModel;

public interface CustomStorageRepository {
    void addProductToStorage(Long storageId, ProductModel product);
}
