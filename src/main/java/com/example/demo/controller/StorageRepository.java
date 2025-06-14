package com.example.demo.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exception.NotFoundException;
import com.example.demo.models.StorageModel;
import com.example.demo.service.StorageService;

@RestController
@RequestMapping("/api/storage")
public class StorageRepository {

    private final StorageService storageService;
    
    public StorageRepository(StorageService storageService) {
        this.storageService = storageService;
    }

    
    @PostMapping("/create")
    public ResponseEntity<?> createStorage(@RequestBody StorageModel storageModel) {
        Long storageId = storageService.createNewStorage(storageModel);
        return ResponseEntity.status(201).body(Map.of("message", "Storage created successfully", "storageId", storageId));
    }
    @PostMapping("/update")
    public ResponseEntity<?> updateStorage(@RequestBody StorageModel storageModel) throws NotFoundException {
        Boolean isUpdated = storageService.updateStorage(storageModel);
        if (isUpdated) {
            return ResponseEntity.ok(Map.of("message", "Storage updated successfully"));
        } else {
            return ResponseEntity.status(400).body(Map.of("message", "Failed to update storage"));
        }
    }
    @PostMapping("/addProduct/{storageId}/{productId}")
    public ResponseEntity<?> addProductToStorage(@RequestParam(name = "storageId") Long storageId,
                                                 @RequestParam(name = "productId") Long productId) throws NotFoundException {

        Boolean isAdded = storageService.addProductToStorage(storageId, productId);
        if (isAdded) {
            return ResponseEntity.accepted().body(Map.of("message", "Product added to storage successfully"));
        } else {
            return ResponseEntity.status(400).body(Map.of("message", "Failed to add product to storage"));
        }
    }

    @PostMapping("/removeProduct/{storageId}/{productId}")
    public ResponseEntity<?> removeProductFromStorage(@RequestParam(name = "storageId") Long storageId,
                                                      @RequestParam(name = "productId") Long productId) throws NotFoundException {

        Boolean isRemoved = storageService.removeProductFromStorage(storageId, productId);
        if (isRemoved) {
            return ResponseEntity.accepted().body(Map.of("message", "Product removed from storage successfully"));
        } else {
            return ResponseEntity.status(400).body(Map.of("message", "Failed to remove product from storage"));
        }
    }

}