package com.huce.cafemanagement.repository;

import com.huce.cafemanagement.model.entities.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, Long> {
}