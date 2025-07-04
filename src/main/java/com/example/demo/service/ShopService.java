package com.example.demo.service;

import java.util.UUID;

import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import com.example.demo.enums.LogPermissionEnum;
import com.example.demo.exception.NotFoundException;
import com.example.demo.models.ShopModel;
import com.example.demo.repository.ShopRepository;

@Service
public class ShopService {
    
    private final ShopRepository shopRepository;
    private final LogService logService;

    public ShopService(ShopRepository shopRepository, LogService logService) {
        this.shopRepository = shopRepository;
        this.logService = logService;
    }

    public Iterable<ShopModel> getAllShops() {
        return shopRepository.findAll();
    }

    public ShopModel getShopById(UUID shopId) throws NotFoundException{
        return shopRepository.findById(shopId)
            .orElseThrow(() -> new NotFoundException("Shop not found"));
    }

    public UUID createShop(ShopModel shopModel, UUID requestOwner) throws BadRequestException {
        validateShopData(shopModel);
        shopModel.setCreatedAt(new java.util.Date().toString());
        shopModel.setUpdatedAt(new java.util.Date().toString());
        shopModel.setStatus(true);
        logService.saveStoreLog(
            "Shop created: " + shopModel.getId(),
            LogPermissionEnum.ADMIN,
            shopModel.getId(),
            "Created shop with ID: " + shopModel.getId(),
            requestOwner,
            "CREATE"
        );
        return shopRepository.save(shopModel).getId();
    }

    public Boolean deactivateShop(UUID shopId, UUID requestOwner) throws BadRequestException, IllegalAccessException{
        ShopModel shopModel = shopRepository.findById(shopId)
            .orElseThrow(() -> new BadRequestException("Shop not found"));
        
        if (!shopModel.getStatus()) {
            throw new BadRequestException("Shop is already deactivated");
        }
        
        if (shopModel.getResponsibleId() == null || !shopModel.getResponsibleId().equals(requestOwner)) {
            throw new IllegalAccessException("You do not have permission to deactivate this shop");
        }
        
        shopModel.setStatus(false);
        shopRepository.save(shopModel);
        logService.saveStoreLog(
            "Shop deactivated: " + shopModel.getId(),
            LogPermissionEnum.ADMIN,
            shopModel.getId(),
            "Deactivated shop with ID: " + shopModel.getId(),
            requestOwner,
            "DEACTIVATE"
        );
        return true;
    }

    public boolean activateShop(UUID shopId, UUID requestOwner) throws BadRequestException, IllegalAccessException {
        ShopModel shopModel = shopRepository.findById(shopId)
            .orElseThrow(() -> new BadRequestException("Shop not found"));
        
        if (shopModel.getStatus()) {
            throw new BadRequestException("Shop is already active");
        }
        
        if (shopModel.getResponsibleId() == null || !shopModel.getResponsibleId().equals(requestOwner)) {
            throw new IllegalAccessException("You do not have permission to activate this shop");
        }
        
        shopModel.setStatus(true);
        shopRepository.save(shopModel);
        logService.saveStoreLog(
            "Shop activated: " + shopModel.getId(),
            LogPermissionEnum.ADMIN,
            shopModel.getId(),
            "Activated shop with ID: " + shopModel.getId(),
            requestOwner,
            "ACTIVATE"
        );
        return true;
    }

    public Boolean updateShop(UUID shopId, ShopModel updatedShopModel, UUID requestOwner) throws BadRequestException, IllegalAccessException {
        ShopModel existingShop = shopRepository.findById(shopId)
            .orElseThrow(() -> new BadRequestException("Shop not found"));
        
        if (!existingShop.getStatus()) {
            throw new BadRequestException("Cannot update a deactivated shop");
        }
        
        if (existingShop.getResponsibleId() == null || !existingShop.getResponsibleId().equals(requestOwner)) {
            throw new IllegalAccessException("You do not have permission to update this shop");
        }
        
        validateShopData(updatedShopModel);
        
        updatedShopModel.setId(existingShop.getId());
        updatedShopModel.setUpdatedAt(new java.util.Date().toString());
        updatedShopModel.setStatus(existingShop.getStatus());
        
        logService.saveStoreLog(
            "Shop updated: " + updatedShopModel.getId(),
            LogPermissionEnum.ADMIN,
            updatedShopModel.getId(),
            "Updated shop with ID: " + updatedShopModel.getId(),
            requestOwner,
            "UPDATE"
        );

        shopRepository.save(updatedShopModel);

        return true;
    }


    private void validateShopData(ShopModel shopModel) throws BadRequestException{
        
        if (shopModel.getName() == null || shopModel.getName().isEmpty()) {
            throw new BadRequestException("Shop name cannot be empty");
        }
        if (shopModel.getCnpj() == null || shopModel.getCnpj().isEmpty()) {
            throw new BadRequestException("CNPJ cannot be empty");
        }
        if (shopModel.getAddress() == null || shopModel.getAddress().isEmpty()) {
            throw new BadRequestException("Address cannot be empty");
        }
        if (shopModel.getPhone() == null || shopModel.getPhone().isEmpty()) {
            throw new BadRequestException("Phone cannot be empty");
        }
        if (shopModel.getEmail() == null || shopModel.getEmail().isEmpty() || 
            !shopModel.getEmail().matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new BadRequestException("Email cannot be empty or invalid");
        }
        
        if (shopModel.getDescription() == null || shopModel.getDescription().isEmpty()) {
            throw new BadRequestException("Description cannot be empty");
        }
        if (shopModel.getResponsibleId() == null) {
            throw new BadRequestException("Responsible ID cannot be null");
        }
        if (shopModel.getOpeningHours() == null) {
            throw new BadRequestException("Opening hours cannot be null");
        }
        if (shopModel.getClosingHours() == null) {
            throw new BadRequestException("Closing hours cannot be null");
        }
        if (shopModel.getImageUrl() == null || shopModel.getImageUrl().isEmpty()) {
            throw new BadRequestException("Image URL cannot be empty");
        }
        if (shopModel.getLogoUrl() == null || shopModel.getLogoUrl().isEmpty()) {
            throw new BadRequestException("Logo URL cannot be empty");
        }
        if (shopModel.getBannerUrl() == null || shopModel.getBannerUrl().isEmpty()) {
            throw new BadRequestException("Banner URL cannot be empty");
        }
    }
}
