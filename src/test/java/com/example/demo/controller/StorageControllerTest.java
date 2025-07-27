package com.example.demo.controller;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.StorageModel;
import com.example.demo.service.StorageService;
import com.example.demo.util.CustomLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageControllerTest {

    @Mock
    private StorageService storageService;

    @Mock
    private CustomLogger logger;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private StorageController storageController;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID testStoreId = UUID.randomUUID();
    private final UUID testProductId = UUID.randomUUID();
    private final Long testStorageId = 1L;
    private final String authToken = "Bearer token";

    private StorageModel testStorage;

    @BeforeEach
    void setUp() {
        testStorage = new StorageModel();
        testStorage.setId(testStorageId);
        testStorage.setName("Test Storage");
    }

    @SuppressWarnings("null")
    @Test
    void createStorage_Success() {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(storageService.createNewStorage(any(), any(), any())).thenReturn(testStorageId);

        // Act
        ResponseEntity<?> response = storageController.createStorage(testStorage, authToken, testStoreId);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals("Storage created successfully", responseBody.get("message"));
        assertEquals(testStorageId, responseBody.get("storageId"));

        verify(logger).logInfo("Attempting to create new storage: " + testStorage.getName());
        verify(logger).logInfo("Storage created successfully with ID: " + testStorageId);
        verify(storageService).createNewStorage(testUserId, testStorage, testStoreId);
    }

    @SuppressWarnings("null")
    @Test
    void createStorage_Failure() {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(storageService.createNewStorage(any(), any(), any()))
            .thenThrow(new RuntimeException("Creation failed"));

        // Act
        ResponseEntity<?> response = storageController.createStorage(testStorage, authToken, testStoreId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Failed to create storage", responseBody.get("message"));
        assertEquals("Creation failed", responseBody.get("error"));

        verify(logger).logInfo("Attempting to create new storage: " + testStorage.getName());
        verify(logger).logError(eq("Failed to create storage: " + testStorage.getName()), any(RuntimeException.class));
    }

    @SuppressWarnings("null")
    @Test
    void updateStorage_Success() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(storageService.updateStorage(any(), any(), any())).thenReturn(true);

        // Act
        ResponseEntity<?> response = storageController.updateStorage(testStorage, authToken, testStoreId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Storage updated successfully", responseBody.get("message"));

        verify(logger).logInfo("Attempting to update storage ID: " + testStorage.getId());
        verify(logger).logInfo("Storage updated successfully: " + testStorage.getId());
        verify(storageService).updateStorage(testUserId, testStorage, testStoreId);
    }

    @SuppressWarnings("null")
    @Test
    void updateStorage_Failure() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(storageService.updateStorage(any(), any(), any())).thenReturn(false);

        // Act
        ResponseEntity<?> response = storageController.updateStorage(testStorage, authToken, testStoreId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Failed to update storage", responseBody.get("message"));

        verify(logger).logInfo("Attempting to update storage ID: " + testStorage.getId());
        verify(logger).logError(eq("Failed to update storage: " + testStorage.getId()), any(Exception.class));
    }

    @Test
    void updateStorage_NotFound() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(storageService.updateStorage(any(), any(), any())).thenThrow(new NotFoundException("Not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            storageController.updateStorage(testStorage, authToken, testStoreId);
        });

        verify(logger).logInfo("Attempting to update storage ID: " + testStorage.getId());
        verify(logger).logError(eq("Storage not found for update: " + testStorage.getId()), any(NotFoundException.class));
    }

    @SuppressWarnings("null")
    @Test
    void addProductToStorage_Success() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(storageService.addProductToStorage(any(), anyLong(), any(), any())).thenReturn(true);

        // Act
        ResponseEntity<?> response = storageController.addProductToStorage(
            testStorageId, testProductId, testStoreId, authToken);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Product added to storage successfully", responseBody.get("message"));

        verify(logger).logInfo(String.format(
            "Attempting to add product %s to storage %s", 
            testProductId, testStorageId));
        verify(logger).logInfo(String.format(
            "Product %s added to storage %s successfully", 
            testProductId, testStorageId));
    }

    @SuppressWarnings("null")
    @Test
    void addProductToStorage_Failure() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(storageService.addProductToStorage(any(), anyLong(), any(), any())).thenReturn(false);

        // Act
        ResponseEntity<?> response = storageController.addProductToStorage(
            testStorageId, testProductId, testStoreId, authToken);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Failed to add product to storage", responseBody.get("message"));

        verify(logger).logInfo(String.format(
            "Attempting to add product %s to storage %s", 
            testProductId, testStorageId));
        verify(logger).logError(eq(String.format(
            "Failed to add product %s to storage %s", 
            testProductId, testStorageId)), any(Exception.class));
    }

    @Test
    void addProductToStorage_NotFound() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(storageService.addProductToStorage(any(), anyLong(), any(), any()))
            .thenThrow(new NotFoundException("Not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            storageController.addProductToStorage(testStorageId, testProductId, testStoreId, authToken);
        });

        verify(logger).logInfo(String.format(
            "Attempting to add product %s to storage %s", 
            testProductId, testStorageId));
        verify(logger).logError(eq(String.format(
            "Storage %s or product %s not found", 
            testStorageId, testProductId)), any(NotFoundException.class));
    }

    @SuppressWarnings("null")
    @Test
    void removeProductFromStorage_Success() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(storageService.removeProductFromStorage(any(), anyLong(), any(), any())).thenReturn(true);

        // Act
        ResponseEntity<?> response = storageController.removeProductFromStorage(
            testStorageId, testProductId, testStoreId, authToken);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Product removed from storage successfully", responseBody.get("message"));

        verify(logger).logInfo(String.format(
            "Attempting to remove product %s from storage %s", 
            testProductId, testStorageId));
        verify(logger).logInfo(String.format(
            "Product %s removed from storage %s successfully", 
            testProductId, testStorageId));
    }

    @SuppressWarnings("null")
    @Test
    void removeProductFromStorage_Failure() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(storageService.removeProductFromStorage(any(), anyLong(), any(), any())).thenReturn(false);

        // Act
        ResponseEntity<?> response = storageController.removeProductFromStorage(
            testStorageId, testProductId, testStoreId, authToken);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Failed to remove product from storage", responseBody.get("message"));

        verify(logger).logInfo(String.format(
            "Attempting to remove product %s from storage %s", 
            testProductId, testStorageId));
        verify(logger).logError(eq(String.format(
            "Failed to remove product %s from storage %s", 
            testProductId, testStorageId)), any(Exception.class));
    }

    @Test
    void removeProductFromStorage_NotFound() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(storageService.removeProductFromStorage(any(), anyLong(), any(), any()))
            .thenThrow(new NotFoundException("Not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            storageController.removeProductFromStorage(testStorageId, testProductId, testStoreId, authToken);
        });

        verify(logger).logInfo(String.format(
            "Attempting to remove product %s from storage %s", 
            testProductId, testStorageId));
        verify(logger).logError(eq(String.format(
            "Storage %s or product %s not found", 
            testStorageId, testProductId)), any(NotFoundException.class));
    }
}