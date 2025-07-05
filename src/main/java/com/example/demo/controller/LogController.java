package com.example.demo.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.enums.LogPermissionEnum;
import com.example.demo.service.LogService;


@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/get-all/${storeId}")
    public ResponseEntity<?> getLogs(@PathVariable("storeId") UUID storeId, @RequestParam LogPermissionEnum logPermission) {
        try {
            return ResponseEntity.ok(logService.getAllLogs(storeId, logPermission));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error retrieving logs: " + e.getMessage());
        }
    }
}
