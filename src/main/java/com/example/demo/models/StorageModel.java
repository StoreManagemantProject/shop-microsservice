package com.example.demo.models;

import java.util.Date;
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
@Table(name = "storage")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StorageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private Long totalProductsQuantity = 0L;
    private Double totalProductsValue = 0.0; 
    private boolean isActive;
    private String description;
    private Date createdAt;
    private Date updatedAt;
    private UUID responsibleId;
    private HashSet<ProductModel> productList = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "storage_shops",
               joinColumns = @jakarta.persistence.JoinColumn(name = "storage_id"),
               inverseJoinColumns = @jakarta.persistence.JoinColumn(name = "shop_id"))
    private HashSet<ShopModel> shops = new HashSet<>();

    @ManyToMany
    private HashSet<ProductModel> products = new HashSet<>();

    public void addProduct(ProductModel product) {
        if (product != null) {
            productList.add(product);
            totalProductsQuantity += product.getQuantity();
            totalProductsValue += product.getPrice() * product.getQuantity();
        }
    }
    public void removeProduct(ProductModel product) {
        if (product != null && productList.remove(product)) {
            totalProductsQuantity -= product.getQuantity();
            totalProductsValue -= product.getPrice() * product.getQuantity();
        }
    }
}
