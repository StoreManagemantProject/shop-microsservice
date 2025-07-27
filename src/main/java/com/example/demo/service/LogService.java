package com.example.demo.service;


import com.example.demo.enums.LogPermissionEnum;
import com.example.demo.models.LogModel;
import com.example.demo.models.ProductLogModel;
import com.example.demo.repository.LogRepository;
import com.example.demo.repository.ProductLogRepository;
import com.example.demo.util.CustomLogger;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class LogService {
    
    private final LogRepository logRepository;
    private final CustomLogger customLogger;
    private final ProductLogRepository productLogRepository;

    public LogService(LogRepository logRepository, CustomLogger customLogger, ProductLogRepository productLogRepository) {
        this.logRepository = logRepository;
        this.customLogger = customLogger;
        this.productLogRepository = productLogRepository;
    }

    public void saveStoreLog(String message, LogPermissionEnum permission, UUID storeId, String details, UUID logOwnerId, String logType) {
        try {
            LogModel logModel = new LogModel();
            logModel.setMessage(message);
            logModel.setLogPermission(permission);
            logModel.setStoreId(storeId);
            logModel.setDetails(details);
            logModel.setLogOwnerId(logOwnerId);
            logModel.setLogType(logType);
            logRepository.save(logModel);
            customLogger.logInfo("Log saved successfully: " + logModel);
        } catch (Exception e) {
            customLogger.logError("Error saving log: " + message, e);
        }
    }

    public void saveProductLog(String message, LogPermissionEnum permission, UUID productId, String details, UUID logOwnerId, String logType) {
        try {
            ProductLogModel productLogModel = new ProductLogModel();
            productLogModel.setMessage(message);
            productLogModel.setLogPermission(permission);
            productLogModel.setProductId(productId);
            productLogModel.setDetails(details);
            productLogModel.setLogOwnerId(logOwnerId);
            productLogModel.setLogType(logType);
            productLogRepository.save(productLogModel);
            customLogger.logInfo("Product log saved successfully: " + productLogModel);
        } catch (Exception e) {
            customLogger.logError("Error saving product log: " + message, e);
        }
    }

    public List<LogModel> getAllLogs(UUID storeId, LogPermissionEnum logPermission) {
        try {
            List<LogModel> logs = logRepository.findAllByStoreIdAndLogPermission(storeId, logPermission);
            customLogger.logInfo("Retrieved " + logs.size() + " logs for store ID: " + storeId);
            return logs;
        } catch (Exception e) {
            customLogger.logError("Error retrieving logs for store ID: " + storeId, e);
            return List.of(); 
        }
        
    }
}
