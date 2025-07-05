package com.example.demo.models;

import java.util.HashSet;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private UUID productId;
    private Long quantity;
    private Double price;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
    
    @ManyToMany
    @JoinTable(name = "product_storages",
               joinColumns = @jakarta.persistence.JoinColumn(name = "product_id"),
               inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "storage_id"))
    private HashSet<StorageModel> storages = new HashSet<>();
    
}
