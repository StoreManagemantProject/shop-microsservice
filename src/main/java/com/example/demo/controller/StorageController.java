package com.example.demo.controller;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.StorageModel;
import com.example.demo.service.StorageService;
import com.example.demo.util.CustomLogger;

@RestController
@RequestMapping("/api/storage")
public class StorageController {

    private final StorageService storageService;
    private final CustomLogger logger;
    private final JwtTokenProvider jwtTokenProvider;

    public StorageController(StorageService storageService, CustomLogger logger, JwtTokenProvider jwtTokenProvider) {
        this.storageService = storageService;
        this.logger = logger;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/create/{storeId}")
    public ResponseEntity<?> createStorage(@RequestBody StorageModel storageModel,
                                           @RequestHeader("Authorization") String token,
                                           @RequestParam(name = "storeId") UUID storeId) {
        logger.logInfo("Attempting to create new storage: " + storageModel.getName());
        try {
            UUID requestOwner = jwtTokenProvider.retrieveIdFromToken(token);

            Long storageId = storageService.createNewStorage(requestOwner, storageModel, storeId);
            logger.logInfo("Storage created successfully with ID: " + storageId);
            return ResponseEntity.status(201).body(Map.of(
                "message", "Storage created successfully", 
                "storageId", storageId
            ));
        } catch (Exception e) {
            logger.logError("Failed to create storage: " + storageModel.getName(), e);
            return ResponseEntity.status(400).body(Map.of(
                "message", "Failed to create storage",
                "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/update/{storeId}")
    public ResponseEntity<?> updateStorage(@RequestBody StorageModel storageModel,
                                           @RequestHeader("Authorization") String token,
                                           @RequestParam(name = "storeId") UUID storeId) throws NotFoundException {

        logger.logInfo("Attempting to update storage ID: " + storageModel.getId());
        
        try {
            UUID requestOwner = jwtTokenProvider.retrieveIdFromToken(token);
            Boolean isUpdated = storageService.updateStorage(requestOwner, storageModel, storeId);
            
            if (isUpdated) {
                logger.logInfo("Storage updated successfully: " + storageModel.getId());
                return ResponseEntity.ok(Map.of("message", "Storage updated successfully"));
            } else {
                logger.logError("Failed to update storage: " + storageModel.getId(), new Exception("Update operation returned false"));
                return ResponseEntity.status(400).body(Map.of("message", "Failed to update storage"));
            }
        } catch (Exception e) {
            logger.logError("Storage not found for update: " + storageModel.getId(), e);
            throw e;
        }
    }

    @PostMapping("/addProduct/{storeId}/{storageId}/{productId}")
    public ResponseEntity<?> addProductToStorage(
            @RequestParam(name = "storageId") Long storageId,
            @RequestParam(name = "productId") UUID productId,
            @RequestParam(name = "storeId") UUID storeId,
            @RequestHeader("Authorization") String token) throws NotFoundException {
        
        logger.logInfo(String.format(
            "Attempting to add product %s to storage %s", 
            productId, storageId));
        
        try {
            UUID requestOwner = jwtTokenProvider.retrieveIdFromToken(token);
            Boolean isAdded = storageService.addProductToStorage(requestOwner, storageId, productId, storeId);
            if (isAdded) {
                logger.logInfo(String.format(
                    "Product %s added to storage %s successfully", 
                    productId, storageId));
                return ResponseEntity.accepted().body(Map.of(
                    "message", "Product added to storage successfully"));
            } else {
                logger.logError(String.format(
                    "Failed to add product %s to storage %s", 
                    productId, storageId), new Exception("Add operation returned false"));
                return ResponseEntity.status(400).body(Map.of(
                    "message", "Failed to add product to storage"));
            }
        } catch (NotFoundException e) {
            logger.logError(String.format(
                "Storage %s or product %s not found", 
                storageId, productId), e);
            throw e;
        }
    }

    @PostMapping("/removeProduct/{storeId}/{storageId}/{productId}")
    public ResponseEntity<?> removeProductFromStorage(
            @RequestParam(name = "storageId") Long storageId,
            @RequestParam(name = "productId") UUID productId,
            @RequestParam(name = "storeId") UUID storeId,
            @RequestHeader("Authorization") String token) throws NotFoundException {
        
        logger.logInfo(String.format(
            "Attempting to remove product %s from storage %s", 
            productId, storageId));
        
        try {
            UUID requestOwner = jwtTokenProvider.retrieveIdFromToken(token);
            Boolean isRemoved = storageService.removeProductFromStorage(requestOwner, storageId, productId, storeId);

            if (isRemoved) {
                logger.logInfo(String.format(
                    "Product %s removed from storage %s successfully", 
                    productId, storageId));
                return ResponseEntity.accepted().body(Map.of(
                    "message", "Product removed from storage successfully"));
            } else {
                logger.logError(String.format(
                    "Failed to remove product %s from storage %s", 
                    productId, storageId), new Exception("Remove operation returned false"));
                return ResponseEntity.status(400).body(Map.of(
                    "message", "Failed to remove product from storage"));
            }
        } catch (Exception e) {
            logger.logError(String.format(
                "Storage %s or product %s not found", 
                storageId, productId), e);
            throw e;
        }
    }
}