package com.example.demo.controller;

import java.util.Map;
import java.util.UUID;

import org.apache.catalina.connector.Response;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.ShopModel;
import com.example.demo.service.ShopService;

@RestController
@RequestMapping("/api/shops")
public class ShopController {
    private final ShopService shopService;
    
    public ShopController(ShopService shopService) {
        this.shopService = shopService;
    }

    @PostMapping("/private/create")
    public ResponseEntity<?> createShop(@RequestBody ShopModel shopModel) throws BadRequestException{
        UUID id = shopService.createShop(shopModel);
        return ResponseEntity.status(201).body(Map.of("message", "Shop created successfully", "id", id.toString()));
    }

    @GetMapping("/public/list-all")
    public ResponseEntity<?> listShops() {
        try {
            return ResponseEntity.ok(shopService.getAllShops());
        } catch (Exception e) {
            return ResponseEntity.status(Response.SC_INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to retrieve shops"));
        }
    }   
    
    @PutMapping("/private/deactivate/{shopId}")
    public ResponseEntity<?> deactivateShop(@RequestHeader ("requisitionOwner") UUID requisitionOwner, @RequestHeader ("shopId") UUID shopId) throws BadRequestException, IllegalAccessException {
        if (requisitionOwner == null || shopId == null) {
            throw new BadRequestException("Requisition owner or shop ID cannot be null");
        }
        boolean result = shopService.deactivateShop(shopId, requisitionOwner);
        if(result) {
            return ResponseEntity.accepted().body(Map.of("message", "Shop deactivated successfully"));
        } else {
            throw new BadRequestException("Failed to deactivate shop");
        }
                
    }
    @PutMapping("/private/activate/{shopId}")
    public ResponseEntity<?> activateShop(@RequestHeader ("requisitionOwner") UUID requisitionOwner, @RequestHeader ("shopId") UUID shopId) throws BadRequestException, IllegalAccessException {
        if (requisitionOwner == null || shopId == null) {
            throw new BadRequestException("Requisition owner or shop ID cannot be null");
        }
        boolean result = shopService.activateShop(shopId, requisitionOwner);
        if(result) {
            return ResponseEntity.accepted().body(Map.of("message", "Shop activated successfully"));
        } else {
            throw new BadRequestException("Failed to activate shop");
        }
    }

    @GetMapping("/public/list/{shopId}")
    public ResponseEntity<?> getShopById(@RequestHeader ("shopId") UUID shopId) {
        try {
            ShopModel shop = shopService.getShopById(shopId);
            return ResponseEntity.ok(shop);
        } catch (Exception e) {
            return ResponseEntity.status(Response.SC_INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to retrieve shop"));
        }
    }
}