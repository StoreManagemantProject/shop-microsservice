package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.models.StorageModel;

public interface StorageRepository extends JpaRepository<StorageModel, Long>, CustomStorageRepository {
}