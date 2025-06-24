package com.example.demo.service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.models.ProductModel;
import com.example.demo.models.StorageModel;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.StorageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    private StorageRepository storageRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private StorageService storageService;

    private StorageModel testStorage;
    private ProductModel testProduct;
    private final Long testStorageId = 1L;
    private final UUID testProductId = UUID.randomUUID();
    private final UUID testResponsibleId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testStorage = new StorageModel();
        testStorage.setId(testStorageId);
        testStorage.setDescription("Test Storage");
        testStorage.setResponsibleId(testResponsibleId);
        testStorage.setActive(true);
        testStorage.setTotalProductsQuantity(0L);
        testStorage.setTotalProductsValue(0.0);

        testProduct = new ProductModel();
        testProduct.setId(testProductId);
        testProduct.setQuantity(10L);
        testProduct.setPrice(100.0);
    }

    @Test
    void updateStorage_Success() throws NotFoundException {
        // Arrange
        StorageModel updatedStorage = new StorageModel();
        updatedStorage.setId(testStorageId);
        updatedStorage.setDescription("Updated Storage");
        updatedStorage.setResponsibleId(testResponsibleId);
        updatedStorage.setActive(false);

        when(storageRepository.findById(testStorageId)).thenReturn(Optional.of(testStorage));
        when(storageRepository.save(any(StorageModel.class))).thenReturn(testStorage);

        // Act
        boolean result = storageService.updateStorage(updatedStorage);

        // Assert
        assertTrue(result);
        assertEquals("Updated Storage", testStorage.getDescription());
        assertEquals(testResponsibleId, testStorage.getResponsibleId());
        assertFalse(testStorage.isActive());
        assertNotNull(testStorage.getUpdatedAt());
        verify(storageRepository, times(1)).save(testStorage);
    }

    @Test
    void updateStorage_NotFound() {
        // Arrange
        when(storageRepository.findById(testStorageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            storageService.updateStorage(testStorage);
        });
    }

    @Test
    void updateStorage_NullId() {
        // Arrange
        testStorage.setId(null);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            storageService.updateStorage(testStorage);
        });
    }

    @Test
    void createNewStorage_Success() {
        // Arrange
        when(storageRepository.save(any(StorageModel.class))).thenReturn(testStorage);

        // Act
        Long result = storageService.createNewStorage(testStorage);

        // Assert
        assertEquals(testStorageId, result);
        assertTrue(testStorage.isActive());
        assertEquals(0L, testStorage.getTotalProductsQuantity());
        assertEquals(0.0, testStorage.getTotalProductsValue());
        assertNotNull(testStorage.getCreatedAt());
        assertNotNull(testStorage.getUpdatedAt());
        verify(storageRepository, times(1)).save(testStorage);
    }

    @Test
    void addProductToStorage_Success() throws NotFoundException {
        // Arrange
        when(storageRepository.findById(testStorageId)).thenReturn(Optional.of(testStorage));
        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        when(storageRepository.save(any(StorageModel.class))).thenReturn(testStorage);

        // Act
        boolean result = storageService.addProductToStorage(testStorageId, testProductId);

        // Assert
        assertTrue(result);
        verify(storageRepository, times(1)).save(testStorage);
        // Additional assertions for product addition logic would go here
    }

    @Test
    void addProductToStorage_StorageNotFound() {
        // Arrange
        when(storageRepository.findById(testStorageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            storageService.addProductToStorage(testStorageId, testProductId);
        });
    }

    @Test
    void addProductToStorage_ProductNotFound() {
        // Arrange
        when(storageRepository.findById(testStorageId)).thenReturn(Optional.of(testStorage));
        when(productRepository.findById(testProductId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            storageService.addProductToStorage(testStorageId, testProductId);
        });
    }

    @Test
    void removeProductFromStorage_Success() throws NotFoundException {
        // Arrange
        when(storageRepository.findById(testStorageId)).thenReturn(Optional.of(testStorage));
        when(productRepository.findById(testProductId)).thenReturn(Optional.of(testProduct));
        when(storageRepository.save(any(StorageModel.class))).thenReturn(testStorage);

        // Act
        boolean result = storageService.removeProductFromStorage(testStorageId, testProductId);

        // Assert
        assertTrue(result);
        verify(storageRepository, times(1)).save(testStorage);
        // Additional assertions for product removal logic would go here
    }

    @Test
    void deactivateStorage_Success() throws NotFoundException {
        // Arrange
        testStorage.setActive(true);
        when(storageRepository.findById(testStorageId)).thenReturn(Optional.of(testStorage));
        when(storageRepository.save(any(StorageModel.class))).thenReturn(testStorage);

        // Act
        boolean result = storageService.deactivateStorage(testStorageId);

        // Assert
        assertTrue(result);
        assertFalse(testStorage.isActive());
        verify(storageRepository, times(1)).save(testStorage);
    }

    @Test
    void activateStorage_Success() throws NotFoundException {
        // Arrange
        testStorage.setActive(false);
        when(storageRepository.findById(testStorageId)).thenReturn(Optional.of(testStorage));
        when(storageRepository.save(any(StorageModel.class))).thenReturn(testStorage);

        // Act
        boolean result = storageService.activateStorage(testStorageId);

        // Assert
        assertTrue(result);
        assertTrue(testStorage.isActive());
        verify(storageRepository, times(1)).save(testStorage);
    }
}