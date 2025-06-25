package com.example.demo.models;

import java.util.Date;
import java.util.UUID;

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
@Table(name = "shops")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ShopModel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String cnpj;
    private String address;
    private String phone;
    private String email;
    private String description;
    private boolean isActive;
    private UUID responsibleId;
    private String createdAt;
    private String updatedAt;
    private Boolean status;
    private String imageUrl;
    private String logoUrl;
    private String bannerUrl;
    private Date openingHours;
    private Date closingHours;
}
