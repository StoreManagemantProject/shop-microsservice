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
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StorageRepository storageRepository;

    @InjectMocks
    private ProductService productService;

    private ProductModel testProduct;
    private StorageModel testStorage;
    private final UUID testProductId = UUID.randomUUID();
    private final Long testStorageId = 1L;

    @BeforeEach
    void setUp() {
        testProduct = new ProductModel();
        testProduct.setProductId(testProductId);
        testProduct.setQuantity(10L);
        testProduct.setPrice(100.0);
        testProduct.setIsActive(true);

        testStorage = new StorageModel();
        testStorage.setId(testStorageId);
        testStorage.setDescription("Test Storage");
    }

    @Test
    void createProduct_Success() throws NotFoundException {
        // Arrange
        when(storageRepository.findById(testStorageId)).thenReturn(Optional.of(testStorage));
        when(productRepository.save(any(ProductModel.class))).thenReturn(testProduct);
        when(storageRepository.save(any(StorageModel.class))).thenReturn(testStorage);

        // Act
        UUID result = productService.createProduct(testProduct, testStorageId);

        // Assert
        assertNotNull(result);
        assertEquals(testProductId, result);
        verify(productRepository, times(1)).save(any(ProductModel.class));
        verify(storageRepository, times(1)).save(testStorage);
    }

    @Test
    void createProduct_StorageNotFound() {
        // Arrange
        when(storageRepository.findById(testStorageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productService.createProduct(testProduct, testStorageId);
        });
    }

    @Test
    void createProduct_InvalidQuantity() {
        // Arrange
        testProduct.setQuantity(-1L);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(testProduct, testStorageId);
        });
    }

    @Test
    void createProduct_InvalidPrice() {
        // Arrange
        testProduct.setPrice(-1.0);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(testProduct, testStorageId);
        });
    }

    @Test
    void getProductById_Success() throws NotFoundException {
        // Arrange
        when(productRepository.findByProductId(testProductId)).thenReturn(Optional.of(testProduct));

        // Act
        ProductModel result = productService.getProductById(testProductId);

        // Assert
        assertNotNull(result);
        assertEquals(testProductId, result.getProductId());
    }

    @Test
    void getProductById_NotFound() {
        // Arrange
        when(productRepository.findByProductId(testProductId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productService.getProductById(testProductId);
        });
    }

    @Test
    void updateProduct_Success() throws NotFoundException {
        // Arrange
        ProductModel updatedProduct = new ProductModel();
        updatedProduct.setProductId(testProductId);
        updatedProduct.setQuantity(20L);
        updatedProduct.setPrice(200.0);

        when(productRepository.findByProductId(testProductId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(ProductModel.class))).thenReturn(testProduct);

        // Act
        productService.updateProduct(updatedProduct);

        // Assert
        assertEquals(20L, testProduct.getQuantity());
        assertEquals(200.0, testProduct.getPrice());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void updateProduct_NotFound() {
        // Arrange
        when(productRepository.findByProductId(testProductId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productService.updateProduct(testProduct);
        });
    }

    @Test
    void deactivateProduct_Success() throws NotFoundException {
        // Arrange
        testProduct.setIsActive(true);
        when(productRepository.findByProductId(testProductId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(ProductModel.class))).thenReturn(testProduct);

        // Act
        productService.deactivateProduct(testProductId);

        // Assert
        assertFalse(testProduct.getIsActive());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void deactivateProduct_AlreadyDeactivated() {
        // Arrange
        testProduct.setIsActive(false);
        when(productRepository.findByProductId(testProductId)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            productService.deactivateProduct(testProductId);
        });
    }

    @Test
    void activateProduct_Success() throws NotFoundException {
        // Arrange
        testProduct.setIsActive(false);
        when(productRepository.findByProductId(testProductId)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(ProductModel.class))).thenReturn(testProduct);

        // Act
        productService.activateProduct(testProductId);

        // Assert
        assertTrue(testProduct.getIsActive());
        verify(productRepository, times(1)).save(testProduct);
    }

    @Test
    void activateProduct_AlreadyActive() {
        // Arrange
        testProduct.setIsActive(true);
        when(productRepository.findByProductId(testProductId)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            productService.activateProduct(testProductId);
        });
    }

    @Test
    void deleteProduct_Success() throws NotFoundException {
        // Arrange
        when(productRepository.findByProductId(testProductId)).thenReturn(Optional.of(testProduct));
        when(storageRepository.findById(testStorageId)).thenReturn(Optional.of(testStorage));
        doNothing().when(productRepository).delete(testProduct);
        when(storageRepository.save(any(StorageModel.class))).thenReturn(testStorage);

        // Act
        boolean result = productService.deleteProduct(testProductId, testStorageId);

        // Assert
        assertTrue(result);
        verify(productRepository, times(1)).delete(testProduct);
        verify(storageRepository, times(1)).save(testStorage);
    }

    @Test
    void deleteProduct_ProductNotFound() {
        // Arrange
        when(productRepository.findByProductId(testProductId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productService.deleteProduct(testProductId, testStorageId);
        });
    }

    @Test
    void deleteProduct_StorageNotFound() {
        // Arrange
        when(productRepository.findByProductId(testProductId)).thenReturn(Optional.of(testProduct));
        when(storageRepository.findById(testStorageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productService.deleteProduct(testProductId, testStorageId);
        });
    }
}