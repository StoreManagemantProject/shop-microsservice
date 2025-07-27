package com.example.demo.controller;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.ProductModel;
import com.example.demo.service.ProductService;
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
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CustomLogger logger;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private ProductController productController;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID testProductId = UUID.randomUUID();
    private final Long testStorageId = 1L;
    private final String authToken = "Bearer token";

    private ProductModel testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new ProductModel();
        testProduct.setId(testProductId);
    }

    @SuppressWarnings("null")
    @Test
    void createProduct_Success() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(productService.createProduct(any(ProductModel.class), anyLong(), any(UUID.class))).thenReturn(testProductId);

        // Act
        ResponseEntity<?> response = productController.createProduct(testProduct, testStorageId, authToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals(testProductId.toString(), responseBody.get("productId"));
        assertEquals("Product created successfully", responseBody.get("message"));

        verify(logger, times(1)).logInfo("Attempting to create product in storage: " + testStorageId);
        verify(logger, times(1)).logInfo("Product created successfully with ID: " + testProductId);
        verify(productService, times(1)).createProduct(testProduct, testStorageId, testUserId);
    }

    @Test
    void getProductById_Success() throws NotFoundException {
        // Arrange
        when(productService.getProductById(testProductId)).thenReturn(testProduct);

        // Act
        ResponseEntity<?> response = productController.getProductById(testProductId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testProduct, response.getBody());

        verify(logger, times(1)).logDebug("Fetching product with ID: " + testProductId);
        verify(logger, times(1)).logDebug("Product retrieved: " + testProduct);
        verify(productService, times(1)).getProductById(testProductId);
    }

    @SuppressWarnings("null")
    @Test
    void updateProduct_Success() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);

        // Act
        ResponseEntity<?> response = productController.updateProduct(testProduct, authToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Product updated successfully", responseBody.get("message"));

        verify(logger, times(1)).logInfo("Updating product with ID: " + testProduct.getId());
        verify(logger, times(1)).logInfo("Product updated successfully: " + testProduct.getId());
        verify(productService, times(1)).updateProduct(testProduct, testUserId);
    }

    @SuppressWarnings("null")
    @Test
    void deactivateProduct_Success() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);

        // Act
        ResponseEntity<?> response = productController.deactivateProduct(testProductId, authToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Product deactivated successfully", responseBody.get("message"));

        verify(logger, times(1)).logInfo("Deactivating product with ID: " + testProductId);
        verify(logger, times(1)).logInfo("Product deactivated successfully: " + testProductId);
        verify(productService, times(1)).deactivateProduct(testProductId, testUserId);
    }

    @SuppressWarnings("null")
    @Test
    void activateProduct_Success() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);

        // Act
        ResponseEntity<?> response = productController.activateProduct(testProductId, authToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Product activated successfully", responseBody.get("message"));

        verify(logger, times(1)).logInfo("Activating product with ID: " + testProductId);
        verify(logger, times(1)).logInfo("Product activated successfully: " + testProductId);
        verify(productService, times(1)).activateProduct(testProductId, testUserId);
    }

    @Test
    void deleteProduct_Success() throws NotFoundException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);

        // Act
        ResponseEntity<?> response = productController.deleteProduct(testProductId, testStorageId, authToken);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        verify(logger, times(1)).logInfo("Deleting product with ID: " + testProductId + " from storage: " + testStorageId);
        verify(logger, times(1)).logInfo("Product deleted successfully: " + testProductId);
        verify(productService, times(1)).deleteProduct(testProductId, testStorageId, testUserId);
    }

    @Test
    void getProductById_NotFound() throws NotFoundException {
        // Arrange
        when(productService.getProductById(testProductId)).thenThrow(new NotFoundException("Product not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productController.getProductById(testProductId);
        });

        verify(logger, times(1)).logDebug("Fetching product with ID: " + testProductId);
        verify(productService, times(1)).getProductById(testProductId);
    }
}