package com.example.demo.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.NotFoundException;
import com.example.demo.models.ProductModel;
import com.example.demo.service.ProductService;
import com.example.demo.util.CustomLogger;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CustomLogger logger;
    
    public ProductController(ProductService productService, CustomLogger logger) {
        this.productService = productService;
        this.logger = logger;
        logger.logInfo("ProductController initialized");
    }

    @PostMapping("/create/{storageId}")
    public ResponseEntity<?> createProduct(@RequestBody ProductModel product, @PathVariable Long storageId) throws NotFoundException {
        logger.logInfo("Attempting to create product in storage: " + storageId);
        UUID productId = productService.createProduct(product, storageId);
        logger.logInfo("Product created successfully with ID: " + productId);
        return ResponseEntity.ok().body(Map.of("productId", productId.toString(), 
                                                "message", "Product created successfully"));
    }

    @PostMapping("/get/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable UUID productId)
            throws NotFoundException {
        logger.logDebug("Fetching product with ID: " + productId);
        ProductModel product = productService.getProductById(productId);
        logger.logDebug("Product retrieved: " + product);
        return ResponseEntity.ok().body(product);      
    }
    
    @PostMapping("/update")
    public ResponseEntity<?> updateProduct(@RequestBody ProductModel product)
            throws NotFoundException {
        logger.logInfo("Updating product with ID: " + product.getId());
        productService.updateProduct(product);
        logger.logInfo("Product updated successfully: " + product.getId());
        return ResponseEntity.ok().body(Map.of("message", "Product updated successfully"));
    }

    @PostMapping("/deactivate/{productId}")
    public ResponseEntity<?> deactivateProduct(@PathVariable UUID productId)
            throws NotFoundException {
        logger.logInfo("Deactivating product with ID: " + productId);
        productService.deactivateProduct(productId);
        logger.logInfo("Product deactivated successfully: " + productId);
        return ResponseEntity.ok().body(Map.of("message", "Product deactivated successfully"));
    }

    @PostMapping("/activate/{productId}")
    public ResponseEntity<?> activateProduct(@PathVariable UUID productId)
            throws NotFoundException {
        logger.logInfo("Activating product with ID: " + productId);
        productService.activateProduct(productId);
        logger.logInfo("Product activated successfully: " + productId);
        return ResponseEntity.ok().body(Map.of("message", "Product activated successfully"));
    }

    @DeleteMapping("/delete/{storageId}/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID productId, @PathVariable Long storageId)
            throws NotFoundException {
        logger.logInfo("Deleting product with ID: " + productId + " from storage: " + storageId);
        productService.deleteProduct(productId, storageId);
        logger.logInfo("Product deleted successfully: " + productId);
        return ResponseEntity.noContent().build();
    }
}