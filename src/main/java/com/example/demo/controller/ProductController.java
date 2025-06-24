package com.example.demo.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.NotFoundException;
import com.example.demo.models.ProductModel;
import com.example.demo.service.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createProduct(@RequestBody ProductModel product) {
        UUID productId = productService.createProduct(product);
        return ResponseEntity.ok().body(Map.of("productId", productId.toString(), 
                                                "message", "Product created successfully"));
    }

    @PostMapping("/get/{productId}")
    public ResponseEntity<?> getProductById(@PathVariable UUID productId)
            throws NotFoundException {
        ProductModel product = productService.getProductById(productId);
        return ResponseEntity.ok().body(product);      
    }
    
    @PostMapping("/update")
    public ResponseEntity<?> updateProduct(@RequestBody ProductModel product)
            throws NotFoundException {
        productService.updateProduct(product);
        return ResponseEntity.ok().body(Map.of("message", "Product updated successfully"));
    }

    @PostMapping("/deactivate/{productId}")
    public ResponseEntity<?> deactivateProduct(@PathVariable UUID productId)
            throws NotFoundException {
        productService.deactivateProduct(productId);
        return ResponseEntity.ok().body(Map.of("message", "Product deactivated successfully"));
    }

    @PostMapping("/activate/{productId}")
    public ResponseEntity<?> activateProduct(@PathVariable UUID productId)
            throws NotFoundException {
        productService.activateProduct(productId);
        return ResponseEntity.ok().body(Map.of("message", "Product activated successfully"));
    }
    
}
