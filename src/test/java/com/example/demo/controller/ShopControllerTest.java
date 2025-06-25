package com.example.demo.controller;

import com.example.demo.exception.NotFoundException;
import com.example.demo.models.ShopModel;
import com.example.demo.service.ShopService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopControllerTest {

    @Mock
    private ShopService shopService;

    @InjectMocks
    private ShopController shopController;

    private ShopModel testShop;
    private final UUID testShopId = UUID.randomUUID();
    private final UUID testRequisitionOwner = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testShop = new ShopModel();
        testShop.setId(testShopId);
        testShop.setName("Test Shop");
        testShop.setActive(true);
    }

    @SuppressWarnings("null")
    @Test
    void createShop_Success() throws BadRequestException {
        // Arrange
        when(shopService.createShop(any(ShopModel.class))).thenReturn(testShopId);

        // Act
        ResponseEntity<?> response = shopController.createShop(testShop);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Shop created successfully", body.get("message"));
        assertEquals(testShopId.toString(), body.get("id"));
        
        verify(shopService, times(1)).createShop(testShop);
    }

    @Test
    void listShops_Success() {
        // Arrange
        List<ShopModel> shops = Arrays.asList(testShop);
        when(shopService.getAllShops()).thenReturn(shops);

        // Act
        ResponseEntity<?> response = shopController.listShops();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shops, response.getBody());
    }

    @SuppressWarnings("null")
    @Test
    void listShops_Exception() {
        // Arrange
        when(shopService.getAllShops()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = shopController.listShops();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Failed to retrieve shops", body.get("error"));
    }

    @SuppressWarnings("null")
    @Test
    void deactivateShop_Success() throws BadRequestException, IllegalAccessException {
        // Arrange
        when(shopService.deactivateShop(testShopId, testRequisitionOwner)).thenReturn(true);

        // Act
        ResponseEntity<?> response = shopController.deactivateShop(testRequisitionOwner, testShopId);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Shop deactivated successfully", body.get("message"));
        
        verify(shopService, times(1)).deactivateShop(testShopId, testRequisitionOwner);
    }

    @Test
    void deactivateShop_NullParameters() {
        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            shopController.deactivateShop(null, null);
        });
    }

    @Test
    void deactivateShop_Failure() throws BadRequestException, IllegalAccessException {
        // Arrange
        when(shopService.deactivateShop(testShopId, testRequisitionOwner)).thenReturn(false);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            shopController.deactivateShop(testRequisitionOwner, testShopId);
        });
    }

    @SuppressWarnings("null")
    @Test
    void activateShop_Success() throws BadRequestException, IllegalAccessException {
        // Arrange
        when(shopService.activateShop(testShopId, testRequisitionOwner)).thenReturn(true);

        // Act
        ResponseEntity<?> response = shopController.activateShop(testRequisitionOwner, testShopId);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Shop activated successfully", body.get("message"));
        
        verify(shopService, times(1)).activateShop(testShopId, testRequisitionOwner);
    }

    @Test
    void getShopById_Success() throws NotFoundException {
        // Arrange
        when(shopService.getShopById(testShopId)).thenReturn(testShop);

        // Act
        ResponseEntity<?> response = shopController.getShopById(testShopId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testShop, response.getBody());
    }

    @SuppressWarnings("null")
    @Test
    void getShopById_Exception() throws NotFoundException {
        // Arrange
        when(shopService.getShopById(testShopId)).thenThrow(new NotFoundException("Shop not found"));

        // Act
        ResponseEntity<?> response = shopController.getShopById(testShopId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) response.getBody();
        assertEquals("Failed to retrieve shop", body.get("error"));
    }
}