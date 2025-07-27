package com.example.demo.service;


import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.enums.LogPermissionEnum;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.ProductModel;
import com.example.demo.models.StorageModel;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StorageRepository;

@Service
public class StorageService {
    
    private final StorageRepository storageRepository;
    private final ProductRepository productRepository;
    private final LogService logService;

    public StorageService(StorageRepository storageRepository, ProductRepository productRepository, LogService logService) {
        this.storageRepository = storageRepository;
        this.productRepository = productRepository;
        this.logService = logService;
    }


    public boolean updateStorage(UUID requestOwner, StorageModel storageModel, UUID storeId) throws NotFoundException{
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
    
        logService.saveStoreLog(
            "Storage updated: " + existingStorage.getId(),
            LogPermissionEnum.MANAGER, 
            storeId,
            "Updated storage with ID: " + existingStorage.getId(),
            requestOwner,
            "UPDATE"
        );
        return true;
    }
    
    public Long createNewStorage(UUID requestOwner, StorageModel storageModel, UUID storeId) {
        storageModel.setActive(true);
        storageModel.setTotalProductsQuantity(0L);
        storageModel.setTotalProductsValue(0.0);
        storageModel.setCreatedAt(new java.util.Date());
        storageModel.setUpdatedAt(new java.util.Date());
        Long id = storageRepository.save(storageModel).getId();
    
        logService.saveStoreLog(
            "Storage created: " + id,
            LogPermissionEnum.MANAGER, 
            storeId, 
            "Created storage with ID: " + id,
            requestOwner,
            "CREATE"
        );
        return id;
    }
    
    public boolean addProductToStorage(UUID requestOwner, Long storageId, UUID productId, UUID storeId) throws NotFoundException {
        StorageModel storage = storageRepository.findById(storageId).orElseThrow(() -> new NotFoundException("Storage not found"));
        ProductModel product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        storage.addProduct(product);
        storageRepository.save(storage);
    
        logService.saveStoreLog(
            "Product added to storage: " + storageId,
            LogPermissionEnum.MANAGER,
            storeId,
            "Added product " + productId + " to storage " + storageId,
            requestOwner,
            "ADD_PRODUCT"
        );
        return true;   
    }
    
    public boolean removeProductFromStorage(UUID requestOwner, Long storageId, UUID productId, UUID storeId) throws NotFoundException {
        StorageModel storage = storageRepository.findById(storageId).orElseThrow(() -> new NotFoundException("Storage not found"));
        ProductModel product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product not found"));
        storage.removeProduct(product);
        storageRepository.save(storage);

        logService.saveStoreLog(
            "Product removed from storage: " + storageId,
            LogPermissionEnum.MANAGER,
            storeId,
            "Removed product " + productId + " from storage " + storageId,
            requestOwner,
            "REMOVE_PRODUCT"
        );
        return true;    
    }

    public boolean deactivateStorage(UUID requestOwner, Long storageId, UUID storeId) throws NotFoundException {
        StorageModel storage = storageRepository.findById(storageId).orElseThrow(() -> new NotFoundException("Storage not found"));
        storage.setActive(false);
        storageRepository.save(storage);

        logService.saveStoreLog(
            "Storage deactivated: " + storageId,
            LogPermissionEnum.MANAGER,
            storeId,
            "Deactivated storage with ID: " + storageId,
            requestOwner,
            "DEACTIVATE"
        );
        return true;   
    }

    public boolean activateStorage(UUID requestOwner, Long storageId, UUID storeId) throws NotFoundException {
        StorageModel storage = storageRepository.findById(storageId).orElseThrow(() -> new NotFoundException("Storage not found"));
        storage.setActive(true);
        storageRepository.save(storage);

        logService.saveStoreLog(
            "Storage activated: " + storageId,
            LogPermissionEnum.MANAGER,
            storeId,
            "Activated storage with ID: " + storageId,
            requestOwner,
            "ACTIVATE"
        );
        return true;   
    }
}