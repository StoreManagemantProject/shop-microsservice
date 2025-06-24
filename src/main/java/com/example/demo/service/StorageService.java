package com.example.demo.service;


import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.models.ProductModel;
import com.example.demo.models.StorageModel;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StorageRepository;

@Service
public class StorageService {
    
    private final StorageRepository storageRepository;
    private final ProductRepository productRepository;

    public StorageService(StorageRepository storageRepository, ProductRepository productRepository) {
        this.storageRepository = storageRepository;
        this.productRepository = productRepository;
    }


    public boolean updateStorage(StorageModel storageModel) throws NotFoundException{
        if(storageModel.getId() == null) {
            throw new IllegalArgumentException("Storage ID cannot be null");
        }
        StorageModel existingStorage = storageRepository.findById(storageModel.getId())
                .orElseThrow(() -> new NotFoundException("Storage not found"));
                
        existingStorage.setDescription(storageModel.getDescription());
        existingStorage.setResponsibleId(storageModel.getResponsibleId());
        existingStorage.setUpdatedAt(new java.util.Date());
        existingStorage.setActive(storageModel.isActive());
        storageRepository.save(existingStorage);
        return true;
    }

    public Long createNewStorage(StorageModel storageModel) {
        storageModel.setActive(true);
        storageModel.setTotalProductsQuantity(0L);
        storageModel.setTotalProductsValue(0.0);
        storageModel.setCreatedAt(new java.util.Date());
        storageModel.setUpdatedAt(new java.util.Date());
        return storageRepository.save(storageModel).getId();
    }
    public boolean addProductToStorage(Long storageId, UUID productId) throws NotFoundException {
        StorageModel storage = storageRepository.findById(storageId).orElseThrow(() -> new NotFoundException("Storage not found"));
        ProductModel product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        storage.addProduct(product);
        storageRepository.save(storage);
        return true;   
    }
    public boolean removeProductFromStorage(Long storageId, UUID productId) throws NotFoundException {
        StorageModel storage = storageRepository.findById(storageId).orElseThrow(() -> new NotFoundException("Storage not found"));
        ProductModel product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        storage.removeProduct(product);
        storageRepository.save(storage);
        return true;    
    }
    public boolean deactivateStorage(Long storageId) throws NotFoundException {
        StorageModel storage = storageRepository.findById(storageId).orElseThrow(() -> new NotFoundException("Storage not found"));
        storage.setActive(false);
        storageRepository.save(storage);
        return true;   
    }
    public boolean activateStorage(Long storageId) throws NotFoundException {
        StorageModel storage = storageRepository.findById(storageId).orElseThrow(() -> new NotFoundException("Storage not found"));
        storage.setActive(true);
        storageRepository.save(storage);
        return true;   
    }
}
