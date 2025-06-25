package com.example.demo.controller;

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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CustomLogger logger; 

    @InjectMocks
    private ProductController productController;

    private ProductModel testProduct;
    private final UUID testProductId = UUID.randomUUID();
    private final Long testStorageId = 1L;

    @BeforeEach
    void setUp() {
        testProduct = new ProductModel();
        testProduct.setProductId(testProductId);
        testProduct.setQuantity(10L);
        testProduct.setPrice(100.0);
        testProduct.setIsActive(true);
    }

    @SuppressWarnings("null")
    @Test
    void createProduct_Success() throws NotFoundException {
        // Arrange
        when(productService.createProduct(any(ProductModel.class), anyLong()))
                .thenReturn(testProductId);

        // Act
        ResponseEntity<?> response = productController.createProduct(testProduct, testStorageId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals(testProductId.toString(), body.get("productId"));
        assertEquals("Product created successfully", body.get("message"));
        
        verify(productService, times(1)).createProduct(testProduct, testStorageId);
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
        verify(productService, times(1)).getProductById(testProductId);
    }

    @SuppressWarnings("null")
    @Test
    void updateProduct_Success() throws NotFoundException {
        // Arrange
        doNothing().when(productService).updateProduct(any(ProductModel.class));

        // Act
        ResponseEntity<?> response = productController.updateProduct(testProduct);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Product updated successfully", body.get("message"));
        
        verify(productService, times(1)).updateProduct(testProduct);
    }

    @SuppressWarnings("null")
    @Test
    void deactivateProduct_Success() throws NotFoundException {
        // Arrange
        doNothing().when(productService).deactivateProduct(testProductId);

        // Act
        ResponseEntity<?> response = productController.deactivateProduct(testProductId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Product deactivated successfully", body.get("message"));
        
        verify(productService, times(1)).deactivateProduct(testProductId);
    }

    @SuppressWarnings("null")
    @Test
    void activateProduct_Success() throws NotFoundException {
        // Arrange
        doNothing().when(productService).activateProduct(testProductId);

        // Act
        ResponseEntity<?> response = productController.activateProduct(testProductId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Product activated successfully", body.get("message"));
        
        verify(productService, times(1)).activateProduct(testProductId);
    }

    @Test
    void deleteProduct_Success() throws NotFoundException {
        // Arrange
        when(productService.deleteProduct(testProductId, testStorageId)).thenReturn(true);

        // Act
        ResponseEntity<?> response = productController.deleteProduct(testProductId, testStorageId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(productService, times(1)).deleteProduct(testProductId, testStorageId);
    }

    @Test
    void createProduct_NotFoundException() throws NotFoundException {
        // Arrange
        when(productService.createProduct(any(ProductModel.class), anyLong()))
                .thenThrow(new NotFoundException("Storage not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productController.createProduct(testProduct, testStorageId);
        });
    }

    @Test
    void getProductById_NotFoundException() throws NotFoundException {
        // Arrange
        when(productService.getProductById(testProductId))
                .thenThrow(new NotFoundException("Product not found"));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            productController.getProductById(testProductId);
        });
    }
}