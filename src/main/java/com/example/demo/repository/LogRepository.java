package com.example.demo.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.enums.LogPermissionEnum;
import com.example.demo.models.LogModel;

public interface LogRepository extends JpaRepository<LogModel, Long> {

    List<LogModel> findAllByStoreIdAndLogPermission(UUID storeId, LogPermissionEnum logPermission);
}
