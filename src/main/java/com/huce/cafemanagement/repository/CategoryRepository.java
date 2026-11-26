package com.huce.cafemanagement.repository;

import com.huce.cafemanagement.model.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}