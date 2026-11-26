package com.huce.cafemanagement.repository;

import com.huce.cafemanagement.model.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}