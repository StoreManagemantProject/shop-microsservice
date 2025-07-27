package com.example.demo.controller;

import com.example.demo.enums.LogPermissionEnum;
import com.example.demo.models.LogModel;
import com.example.demo.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    @Mock
    private LogService logService;

    @InjectMocks
    private LogController logController;

    private UUID testStoreId;
    private final LogPermissionEnum testPermission = LogPermissionEnum.GUEST;

    @BeforeEach
    void setUp() {
        testStoreId = UUID.randomUUID();
    }

    @Test
    void getLogs_shouldReturnLogs_whenServiceReturnsData() {
        // Arrange
        List<LogModel> expectedLogs = Arrays.asList(new LogModel(), new LogModel());
        when(logService.getAllLogs(eq(testStoreId), eq(testPermission)))
                .thenReturn(expectedLogs);

        // Act
        ResponseEntity<?> response = logController.getLogs(testStoreId, testPermission);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedLogs, response.getBody());
    }

    @Test
    void getLogs_shouldReturn500_whenServiceThrowsException() {
        // Arrange
        String errorMessage = "Database error";
        when(logService.getAllLogs(any(UUID.class), any(LogPermissionEnum.class)))
                .thenThrow(new RuntimeException(errorMessage));

        // Act
        ResponseEntity<?> response = logController.getLogs(testStoreId, testPermission);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error retrieving logs: " + errorMessage, response.getBody());
    }

    @Test
    void getLogs_shouldCallServiceWithCorrectParameters() {
        // Arrange
        List<LogModel> expectedLogs = Arrays.asList(new LogModel(), new LogModel());
        when(logService.getAllLogs(eq(testStoreId), eq(testPermission)))
                .thenReturn(expectedLogs);

        // Act
        logController.getLogs(testStoreId, testPermission);

        // Verify
        // The Mockito extension with @Mock and @InjectMocks handles verification
        // that the service was called with the correct parameters
    }
}