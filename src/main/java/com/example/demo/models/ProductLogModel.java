package com.example.demo.models;

import java.util.Date;
import java.util.UUID;

import com.example.demo.enums.LogPermissionEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductLogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;
    private UUID productId;
    private LogPermissionEnum permission;
    private Date timestamp;
    private String details; 
    private UUID logOwnerId; 
    private String logType;
}
