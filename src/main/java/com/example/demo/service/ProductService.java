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
public class ProductService {
    
    private final ProductRepository productRepository;
    private final StorageRepository storageRepository;
    private final LogService logService;


    public ProductService(ProductRepository productRepository, StorageRepository storageRepository, LogService logService) {
        this.productRepository = productRepository;
        this.storageRepository = storageRepository;
        this.logService = logService;
    }

    public UUID createProduct(ProductModel product, Long storageId, UUID requestOwner)  throws NotFoundException {
        validateProductData(product);
        StorageModel storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new NotFoundException("Storage not found"));
        
        product.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        product.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        product.setIsActive(true);
        
        ProductModel savedProduct = productRepository.save(product);
        storage.addProduct(savedProduct);
        storageRepository.save(storage);
        
        logService.saveProductLog(
            "Product created: " + savedProduct.getProductId(),
            LogPermissionEnum.MANAGER,
            savedProduct.getProductId(),
            "Created product with ID: " + savedProduct.getProductId() + " in storage ID: " + storageId,
            requestOwner,
            "CREATE"
        );
        
        return savedProduct.getProductId();
    }
    
    public ProductModel getProductById(UUID productId) throws NotFoundException {
        ProductModel product =  productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    
        logService.saveProductLog(
            "Product retrieved: " + product.getProductId(),
            LogPermissionEnum.MANAGER,
            product.getProductId(),
            "Retrieved product with ID: " + product.getProductId(),
            null,
            "RETRIEVE"
        );
        return product;
    }

    public void updateProduct(ProductModel product, UUID requestOwner) throws NotFoundException {
        ProductModel existingProduct = productRepository.findByProductId(product.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        logService.saveProductLog(
            "Product updated: " + existingProduct.getProductId(),
            LogPermissionEnum.MANAGER,
            existingProduct.getProductId(),
            "Updated product with ID: " + existingProduct.getProductId(),
            requestOwner,
            "UPDATE"
        );
        productRepository.save(existingProduct);
    }

    public void deactivateProduct(UUID productId, UUID requestOwner) throws NotFoundException {
        ProductModel product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (!product.getIsActive()) {
            throw new IllegalArgumentException("Product is already deactivated");
        }
        product.setIsActive(false);
        logService.saveProductLog(
            "Product deactivated: " + product.getProductId(),
            LogPermissionEnum.MANAGER,
            product.getProductId(),
            "Deactivated product with ID: " + product.getProductId(),
            requestOwner,
            "DEACTIVATE"
        );
        productRepository.save(product);
    }
    
    public void activateProduct(UUID productId, UUID requestOwner) throws NotFoundException {
        ProductModel product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.getIsActive()) {
            throw new IllegalArgumentException("Product is already active");
        }
        product.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        
        logService.saveProductLog(
            "Product activated: " + product.getProductId(),
            LogPermissionEnum.MANAGER,
            product.getProductId(),
            "Activated product with ID: " + product.getProductId(),
            requestOwner,
            "ACTIVATE"
        );

        product.setIsActive(true);
        productRepository.save(product);
    }

    private void validateProductData(ProductModel product) {
        if (product.getQuantity() == null || product.getQuantity() < 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        if (product.getPrice() == null || product.getPrice() < 0) {
            throw new IllegalArgumentException("Invalid price");
        }
    }
    public boolean deleteProduct(UUID productId, Long storageId, UUID requestOwner) throws NotFoundException {
        ProductModel product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        productRepository.delete(product);
        StorageModel storage =storageRepository.findById(storageId)
                .orElseThrow(() -> new NotFoundException("Storage not found"));
        storage.removeProduct(product);
        logService.saveProductLog(
            "Product deleted: " + product.getProductId(),
            LogPermissionEnum.MANAGER,
            product.getProductId(),
            "Deleted product with ID: " + product.getProductId() + " from storage ID: " + storageId,
            requestOwner,
            "DELETE"
        );
        storageRepository.save(storage);
        return true;
    }
}
