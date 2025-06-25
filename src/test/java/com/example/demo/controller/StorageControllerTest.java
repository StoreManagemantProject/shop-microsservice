package com.example.demo.controller;

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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageControllerTest {

    @Mock
    private StorageService storageService;
    
    @Mock
    private CustomLogger logger; 
    
    @InjectMocks
    private StorageController storageController;

    private StorageModel testStorage;
    private final Long testStorageId = 1L;
    private final UUID testProductId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testStorage = new StorageModel();
        testStorage.setId(testStorageId);
        testStorage.setDescription("Test Storage");
    }

    @SuppressWarnings("null")
    @Test
    void createStorage_Success() {
        // Arrange
        when(storageService.createNewStorage(any(StorageModel.class))).thenReturn(testStorageId);

        // Act
        ResponseEntity<?> response = storageController.createStorage(testStorage);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Storage created successfully", body.get("message"));
        assertEquals(testStorageId, body.get("storageId"));
        
        verify(storageService, times(1)).createNewStorage(testStorage);
    }

    @SuppressWarnings("null")
    @Test
    void updateStorage_Success() throws NotFoundException {
        // Arrange
        when(storageService.updateStorage(any(StorageModel.class))).thenReturn(true);

        // Act
        ResponseEntity<?> response = storageController.updateStorage(testStorage);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Storage updated successfully", body.get("message"));
        
        verify(storageService, times(1)).updateStorage(testStorage);
    }

    @SuppressWarnings("null")
    @Test
    void updateStorage_Failure() throws NotFoundException {
        // Arrange
        when(storageService.updateStorage(any(StorageModel.class))).thenReturn(false);

        // Act
        ResponseEntity<?> response = storageController.updateStorage(testStorage);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Failed to update storage", body.get("message"));
    }

    @Test
    void updateStorage_NotFoundException() throws NotFoundException {
        // Arrange
        when(storageService.updateStorage(any(StorageModel.class)))
                .thenThrow(new NotFoundException("Storage not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            storageController.updateStorage(testStorage);
        });
    }

    @SuppressWarnings("null")
    @Test
    void addProductToStorage_Success() throws NotFoundException {
        // Arrange
        when(storageService.addProductToStorage(testStorageId, testProductId)).thenReturn(true);

        // Act
        ResponseEntity<?> response = storageController.addProductToStorage(testStorageId, testProductId);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Product added to storage successfully", body.get("message"));
        
        verify(storageService, times(1)).addProductToStorage(testStorageId, testProductId);
    }

    @SuppressWarnings("null")
    @Test
    void addProductToStorage_Failure() throws NotFoundException {
        // Arrange
        when(storageService.addProductToStorage(testStorageId, testProductId)).thenReturn(false);

        // Act
        ResponseEntity<?> response = storageController.addProductToStorage(testStorageId, testProductId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Failed to add product to storage", body.get("message"));
    }

    @SuppressWarnings("null")
    @Test
    void removeProductFromStorage_Success() throws NotFoundException {
        // Arrange
        when(storageService.removeProductFromStorage(testStorageId, testProductId)).thenReturn(true);

        // Act
        ResponseEntity<?> response = storageController.removeProductFromStorage(testStorageId, testProductId);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Product removed from storage successfully", body.get("message"));
        
        verify(storageService, times(1)).removeProductFromStorage(testStorageId, testProductId);
    }

    @SuppressWarnings("null")
    @Test
    void removeProductFromStorage_Failure() throws NotFoundException {
        // Arrange
        when(storageService.removeProductFromStorage(testStorageId, testProductId)).thenReturn(false);

        // Act
        ResponseEntity<?> response = storageController.removeProductFromStorage(testStorageId, testProductId);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Failed to remove product from storage", body.get("message"));
    }
}