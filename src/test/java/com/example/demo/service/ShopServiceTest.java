package com.example.demo.service;

import com.example.demo.exception.NotFoundException;
import com.example.demo.models.ShopModel;
import com.example.demo.repository.ShopRepository;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopServiceTest {

    @Mock
    private ShopRepository shopRepository;

    @InjectMocks
    private ShopService shopService;

    private ShopModel testShop;
    private final UUID testShopId = UUID.randomUUID();
    private final UUID testResponsibleId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        testShop = new ShopModel();
        testShop.setId(testShopId);
        testShop.setName("Test Shop");
        testShop.setCnpj("12345678901234");
        testShop.setAddress("123 Test St");
        testShop.setPhone("1234567890");
        testShop.setEmail("test@example.com");
        testShop.setDescription("Test Description");
        testShop.setResponsibleId(testResponsibleId);
        testShop.setOpeningHours(new Date());
        testShop.setClosingHours(new Date());
        testShop.setImageUrl("https://test.com/image.jpg");
        testShop.setLogoUrl("https://test.com/logo.jpg");
        testShop.setBannerUrl("https://test.com/banner.jpg");
        testShop.setStatus(true);
    }

    @Test
    void getAllShops_Success() {
        // Arrange
        List<ShopModel> shops = Collections.singletonList(testShop);
        when(shopRepository.findAll()).thenReturn(shops);

        // Act
        Iterable<ShopModel> result = shopService.getAllShops();

        // Assert
        assertNotNull(result);
        assertEquals(1, ((Collection<?>) result).size());
        verify(shopRepository, times(1)).findAll();
    }

    @Test
    void getShopById_Success() throws NotFoundException {
        // Arrange
        when(shopRepository.findById(testShopId)).thenReturn(Optional.of(testShop));

        // Act
        ShopModel result = shopService.getShopById(testShopId);

        // Assert
        assertNotNull(result);
        assertEquals(testShopId, result.getId());
        verify(shopRepository, times(1)).findById(testShopId);
    }

    @Test
    void getShopById_NotFound() {
        // Arrange
        when(shopRepository.findById(testShopId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            shopService.getShopById(testShopId);
        });
    }

    @Test
    void createShop_Success() throws BadRequestException {
        // Arrange
        when(shopRepository.save(any(ShopModel.class))).thenReturn(testShop);

        // Act
        UUID result = shopService.createShop(testShop);

        // Assert
        assertNotNull(result);
        assertEquals(testShopId, result);
        assertNotNull(testShop.getCreatedAt());
        assertNotNull(testShop.getUpdatedAt());
        assertTrue(testShop.getStatus());
        verify(shopRepository, times(1)).save(testShop);
    }

    @Test
    void createShop_ValidationFailure() {
        // Arrange
        testShop.setName(null);

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            shopService.createShop(testShop);
        });
    }

    @Test
    void deactivateShop_Success() throws BadRequestException, IllegalAccessException {
        // Arrange
        when(shopRepository.findById(testShopId)).thenReturn(Optional.of(testShop));
        when(shopRepository.save(any(ShopModel.class))).thenReturn(testShop);

        // Act
        Boolean result = shopService.deactivateShop(testShopId, testResponsibleId);

        // Assert
        assertTrue(result);
        assertFalse(testShop.getStatus());
        verify(shopRepository, times(1)).save(testShop);
    }

    @Test
    void deactivateShop_AlreadyDeactivated() {
        // Arrange
        testShop.setStatus(false);
        when(shopRepository.findById(testShopId)).thenReturn(Optional.of(testShop));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            shopService.deactivateShop(testShopId, testResponsibleId);
        });
    }

    @Test
    void deactivateShop_Unauthorized() {
        // Arrange
        when(shopRepository.findById(testShopId)).thenReturn(Optional.of(testShop));

        // Act & Assert
        assertThrows(IllegalAccessException.class, () -> {
            shopService.deactivateShop(testShopId, UUID.randomUUID());
        });
    }

    @Test
    void activateShop_Success() throws BadRequestException, IllegalAccessException {
        // Arrange
        testShop.setStatus(false);
        when(shopRepository.findById(testShopId)).thenReturn(Optional.of(testShop));
        when(shopRepository.save(any(ShopModel.class))).thenReturn(testShop);

        // Act
        boolean result = shopService.activateShop(testShopId, testResponsibleId);

        // Assert
        assertTrue(result);
        assertTrue(testShop.getStatus());
        verify(shopRepository, times(1)).save(testShop);
    }

    @Test
    void activateShop_AlreadyActive() {
        // Arrange
        when(shopRepository.findById(testShopId)).thenReturn(Optional.of(testShop));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            shopService.activateShop(testShopId, testResponsibleId);
        });
    }

    @Test
    void updateShop_Success() throws BadRequestException, IllegalAccessException {
        // Arrange
        ShopModel updatedShop = new ShopModel();
        updatedShop.setName("Updated Shop");
        updatedShop.setCnpj("12345678901234");
        updatedShop.setAddress("456 Updated St");
        updatedShop.setPhone("0987654321");
        updatedShop.setEmail("updated@example.com");
        updatedShop.setDescription("Updated Description");
        updatedShop.setResponsibleId(testResponsibleId);
        updatedShop.setOpeningHours(new Date());
        updatedShop.setClosingHours(new Date());
        updatedShop.setImageUrl("https://updated.com/image.jpg");
        updatedShop.setLogoUrl("https://updated.com/logo.jpg");
        updatedShop.setBannerUrl("https://updated.com/banner.jpg");
        updatedShop.setStatus(true);
        
        when(shopRepository.findById(testShopId)).thenReturn(Optional.of(testShop));
        when(shopRepository.save(any(ShopModel.class))).thenReturn(updatedShop);

        // Act
        Boolean result = shopService.updateShop(testShopId, updatedShop, testResponsibleId);

        // Assert
        assertTrue(result);
        verify(shopRepository, times(1)).save(any(ShopModel.class));
    }

    @Test
    void updateShop_DeactivatedShop() {
        // Arrange
        testShop.setStatus(false);
        when(shopRepository.findById(testShopId)).thenReturn(Optional.of(testShop));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> {
            shopService.updateShop(testShopId, testShop, testResponsibleId);
        });
    }

    @Test
    void validateShopData_Success() throws BadRequestException {
        // Arrange
        when(shopRepository.save(any(ShopModel.class))).thenReturn(testShop);
        
        // Act
        UUID result = shopService.createShop(testShop);
        
        // Assert
        assertNotNull(result);
        assertEquals(testShopId, result);
        verify(shopRepository, times(1)).save(testShop);
    }

    @Test
    void validateShopData_MissingName() {
        testShop.setName(null);
        assertThrows(BadRequestException.class, () -> shopService.createShop(testShop));
    }

    // Add similar tests for all other validation rules...
    @Test
    void validateShopData_InvalidEmail() {
        testShop.setEmail("invalid-email");
        assertThrows(BadRequestException.class, () -> shopService.createShop(testShop));
    }
}