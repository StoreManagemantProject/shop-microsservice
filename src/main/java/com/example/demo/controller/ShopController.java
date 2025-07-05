package com.example.demo.controller;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.StreamSupport;

import org.apache.catalina.connector.Response;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.JwtTokenProvider;
import com.example.demo.models.ShopModel;
import com.example.demo.service.ShopService;
import com.example.demo.util.CustomLogger;

@RestController
@RequestMapping("/api/shops")
public class ShopController {
    private final ShopService shopService;
    private final CustomLogger logger;
    private final JwtTokenProvider jwtTokenProvider;
    
    public ShopController(ShopService shopService, CustomLogger logger, JwtTokenProvider jwtTokenProvider) {
        this.shopService = shopService;
        this.logger = logger;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createShop(@RequestBody ShopModel shopModel, 
                                        @RequestHeader("Authorization") String token) throws BadRequestException {
        logger.logInfo("Attempting to create new shop: " + shopModel.getName());
        try {
            UUID requestOwner = jwtTokenProvider.retrieveIdFromToken(token);
            UUID id = shopService.createShop(shopModel, requestOwner);
            logger.logInfo("Shop created successfully with ID: " + id);
            return ResponseEntity.status(201).body(Map.of(
                "message", "Shop created successfully", 
                "id", id.toString()
            ));
        } catch (BadRequestException e) {
            logger.logError("Failed to create shop: " + shopModel.getName(), e);
            throw e;
        }
    }

    @GetMapping("/list-all")
    public ResponseEntity<?> listShops() {
        logger.logDebug("Fetching list of all shops");
        try {
            var shops = shopService.getAllShops();
            long count = shops instanceof Collection
                ? ((Collection<?>) shops).size()
                : StreamSupport.stream(shops.spliterator(), false).count();
            logger.logDebug("Successfully retrieved " + count + " shops");
            return ResponseEntity.ok(shops);
        } catch (Exception e) {
            logger.logError("Failed to retrieve shops list", e);
            return ResponseEntity.status(Response.SC_INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve shops"));
        }
    }   
    
    @PutMapping("/deactivate/{shopId}")
    public ResponseEntity<?> deactivateShop(
            @RequestParam(name = "shopId") UUID shopId,
            @RequestHeader("Authorization") String token)
            throws BadRequestException, IllegalAccessException {
    
        UUID requestOwner = jwtTokenProvider.retrieveIdFromToken(token);

        logger.logInfo(String.format(
            "Attempting to deactivate shop (ID: %s) by requisition owner: %s", 
            shopId, requestOwner));
        
        if (requestOwner == null || shopId == null) {
            String errorMsg = "Requisition owner or shop ID cannot be null";
            logger.logError(errorMsg, new BadRequestException(errorMsg));
            throw new BadRequestException(errorMsg);
        }
        
        boolean result = shopService.deactivateShop(shopId, requestOwner);
        if(result) {
            logger.logInfo("Shop deactivated successfully: " + shopId);
            return ResponseEntity.accepted().body(Map.of(
                "message", "Shop deactivated successfully"));
        } else {
            String errorMsg = "Failed to deactivate shop: " + shopId;
            logger.logError(errorMsg, new BadRequestException(errorMsg));
            throw new BadRequestException(errorMsg);
        }
    }
    
    @PutMapping("/activate/{shopId}")
    public ResponseEntity<?> activateShop(
            @RequestHeader("Authorization") String token, 
            @RequestParam(name = "shopId") UUID shopId) 
            throws BadRequestException, IllegalAccessException {
        
        UUID requestOwner = jwtTokenProvider.retrieveIdFromToken(token);

        logger.logInfo(String.format(
            "Attempting to activate shop (ID: %s) by requisition owner: %s", 
            shopId, requestOwner));
        
        boolean result = shopService.activateShop(shopId, requestOwner);
        if(result) {
            logger.logInfo("Shop activated successfully: " + shopId);
            return ResponseEntity.accepted().body(Map.of(
                "message", "Shop activated successfully"));
        } else {
            String errorMsg = "Failed to activate shop: " + shopId;
            logger.logError(errorMsg, new BadRequestException(errorMsg));
            throw new BadRequestException(errorMsg);
        }
    }

    @GetMapping("/list/{shopId}")
    public ResponseEntity<?> getShopById(@RequestHeader("shopId") UUID shopId) {
        logger.logDebug("Fetching shop by ID: " + shopId);
        try {
            ShopModel shop = shopService.getShopById(shopId);
            logger.logDebug("Successfully retrieved shop: " + shopId);
            return ResponseEntity.ok(shop);
        } catch (Exception e) {
            logger.logError("Failed to retrieve shop: " + shopId, e);
            return ResponseEntity.status(Response.SC_INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to retrieve shop"));
        }
    }
}