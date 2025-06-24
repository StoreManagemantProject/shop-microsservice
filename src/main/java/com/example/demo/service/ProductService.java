package com.example.demo.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.models.ProductModel;
import com.example.demo.models.StorageModel;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StorageRepository;

@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    private final StorageRepository storageRepository;

    public ProductService(ProductRepository productRepository, StorageRepository storageRepository) {
        this.productRepository = productRepository;
        this.storageRepository = storageRepository;
    }

    public UUID createProduct(ProductModel product, Long storageId)  throws NotFoundException {
        validateProductData(product);
        StorageModel storage = storageRepository.findById(storageId)
                .orElseThrow(() -> new NotFoundException("Storage not found"));
        
        product.setCreatedAt(String.valueOf(System.currentTimeMillis()));
        product.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        product.setIsActive(true);
        
        ProductModel savedProduct = productRepository.save(product);
        storage.addProduct(savedProduct);
        storageRepository.save(storage);
        return savedProduct.getProductId();
    }
    
    public ProductModel getProductById(UUID productId) throws NotFoundException {
        return productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
    }

    public void updateProduct(ProductModel product) throws NotFoundException {
        ProductModel existingProduct = productRepository.findByProductId(product.getProductId())
                .orElseThrow(() -> new NotFoundException("Product not found"));
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setUpdatedAt(String.valueOf(System.currentTimeMillis()));
        productRepository.save(existingProduct);
    }

    public void deactivateProduct(UUID productId) throws NotFoundException {
        ProductModel product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (!product.getIsActive()) {
            throw new IllegalArgumentException("Product is already deactivated");
        }
        product.setIsActive(false);
        productRepository.save(product);
    }
    
    public void activateProduct(UUID productId) throws NotFoundException {
        ProductModel product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        if (product.getIsActive()) {
            throw new IllegalArgumentException("Product is already active");
        }
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
    public boolean deleteProduct(UUID productId, Long storageId) throws NotFoundException {
        ProductModel product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        productRepository.delete(product);
        StorageModel storage =storageRepository.findById(storageId)
                .orElseThrow(() -> new NotFoundException("Storage not found"));
        storage.removeProduct(product);
        storageRepository.save(storage);
        return true;
    }
}
