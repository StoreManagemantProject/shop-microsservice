package com.example.demo.controller;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.ShopModel;
import com.example.demo.service.ShopService;
import com.example.demo.util.CustomLogger;
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

    @Mock
    private CustomLogger logger;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private ShopController shopController;

    private final UUID testUserId = UUID.randomUUID();
    private final UUID testShopId = UUID.randomUUID();
    private final String authToken = "Bearer token";

    private ShopModel testShop;

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
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(shopService.createShop(any(ShopModel.class), any(UUID.class))).thenReturn(testShopId);

        // Act
        ResponseEntity<?> response = shopController.createShop(testShop, authToken);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Shop created successfully", responseBody.get("message"));
        assertEquals(testShopId.toString(), responseBody.get("id"));

        verify(logger).logInfo("Attempting to create new shop: " + testShop.getName());
        verify(logger).logInfo("Shop created successfully with ID: " + testShopId);
        verify(shopService).createShop(testShop, testUserId);
    }

    @Test
    void createShop_BadRequest() throws BadRequestException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(shopService.createShop(any(ShopModel.class), any(UUID.class)))
            .thenThrow(new BadRequestException("Invalid shop data"));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            shopController.createShop(testShop, authToken);
        });

        verify(logger).logInfo("Attempting to create new shop: " + testShop.getName());
        verify(logger).logError(eq("Failed to create shop: " + testShop.getName()), any(BadRequestException.class));
    }

    @Test
    void listShops_Success() {
        // Arrange
        List<ShopModel> shops = Arrays.asList(testShop, new ShopModel());
        when(shopService.getAllShops()).thenReturn(shops);

        // Act
        ResponseEntity<?> response = shopController.listShops();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shops, response.getBody());

        verify(logger).logDebug("Fetching list of all shops");
        verify(logger).logDebug("Successfully retrieved " + shops.size() + " shops");
    }

    @Test
    void listShops_Empty() {
        // Arrange
        when(shopService.getAllShops()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<?> response = shopController.listShops();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((Collection<?>) Objects.requireNonNull(response.getBody())).isEmpty());

        verify(logger).logDebug("Fetching list of all shops");
        verify(logger).logDebug("Successfully retrieved 0 shops");
    }

    @SuppressWarnings("null")
    @Test
    void listShops_Error() {
        // Arrange
        when(shopService.getAllShops()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = shopController.listShops();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Failed to retrieve shops", responseBody.get("error"));

        verify(logger).logDebug("Fetching list of all shops");
        verify(logger).logError(eq("Failed to retrieve shops list"), any(RuntimeException.class));
    }

    @SuppressWarnings("null")
    @Test
    void deactivateShop_Success() throws BadRequestException, IllegalAccessException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(shopService.deactivateShop(testShopId, testUserId)).thenReturn(true);

        // Act
        ResponseEntity<?> response = shopController.deactivateShop(testShopId, authToken);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Shop deactivated successfully", responseBody.get("message"));

        verify(logger).logInfo(String.format(
            "Attempting to deactivate shop (ID: %s) by requisition owner: %s", 
            testShopId, testUserId));
        verify(logger).logInfo("Shop deactivated successfully: " + testShopId);
    }

    @Test
    void deactivateShop_Failure() throws BadRequestException, IllegalAccessException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(shopService.deactivateShop(testShopId, testUserId)).thenReturn(false);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            shopController.deactivateShop(testShopId, authToken);
        });

        verify(logger).logInfo(String.format(
            "Attempting to deactivate shop (ID: %s) by requisition owner: %s", 
            testShopId, testUserId));
        verify(logger).logError(eq("Failed to deactivate shop: " + testShopId), any(BadRequestException.class));
    }

    @Test
    void deactivateShop_NullParameters() {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(null);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            shopController.deactivateShop(testShopId, authToken);
        });

        verify(logger).logError(anyString(), any(BadRequestException.class));
    }

    @SuppressWarnings("null")
    @Test
    void activateShop_Success() throws BadRequestException, IllegalAccessException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(shopService.activateShop(testShopId, testUserId)).thenReturn(true);

        // Act
        ResponseEntity<?> response = shopController.activateShop(authToken, testShopId);

        // Assert
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Shop activated successfully", responseBody.get("message"));

        verify(logger).logInfo(String.format(
            "Attempting to activate shop (ID: %s) by requisition owner: %s", 
            testShopId, testUserId));
        verify(logger).logInfo("Shop activated successfully: " + testShopId);
    }

    @Test
    void activateShop_Failure() throws BadRequestException, IllegalAccessException {
        // Arrange
        when(jwtTokenProvider.retrieveIdFromToken(anyString())).thenReturn(testUserId);
        when(shopService.activateShop(testShopId, testUserId)).thenReturn(false);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            shopController.activateShop(authToken, testShopId);
        });

        verify(logger).logInfo(String.format(
            "Attempting to activate shop (ID: %s) by requisition owner: %s", 
            testShopId, testUserId));
        verify(logger).logError(eq("Failed to activate shop: " + testShopId), any(BadRequestException.class));
    }

    @Test
    void getShopById_Success()  throws NotFoundException{
        // Arrange
        when(shopService.getShopById(testShopId)).thenReturn(testShop);

        // Act
        ResponseEntity<?> response = shopController.getShopById(testShopId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testShop, response.getBody());

        verify(logger).logDebug("Fetching shop by ID: " + testShopId);
        verify(logger).logDebug("Successfully retrieved shop: " + testShopId);
    }

    @SuppressWarnings("null")
    @Test
    void getShopById_Error()  throws NotFoundException{
        // Arrange
        when(shopService.getShopById(testShopId)).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = shopController.getShopById(testShopId);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertEquals("Failed to retrieve shop", responseBody.get("error"));

        verify(logger).logDebug("Fetching shop by ID: " + testShopId);
        verify(logger).logError(eq("Failed to retrieve shop: " + testShopId), any(RuntimeException.class));
    }

    @Test
    void getShopById_NotFound() throws NotFoundException{
        // Arrange
        when(shopService.getShopById(testShopId)).thenReturn(null);

        // Act
        ResponseEntity<?> response = shopController.getShopById(testShopId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());

        verify(logger).logDebug("Fetching shop by ID: " + testShopId);
        verify(logger).logDebug("Successfully retrieved shop: " + testShopId);
    }
}