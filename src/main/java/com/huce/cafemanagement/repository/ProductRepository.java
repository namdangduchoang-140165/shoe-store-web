package com.huce.cafemanagement.repository;

import com.huce.cafemanagement.model.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
}