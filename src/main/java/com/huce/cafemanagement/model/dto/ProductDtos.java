package com.huce.cafemanagement.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class ProductDtos {
    private ProductDtos() {
    }

    public record ProductRequest(
            @NotNull @Positive Long categoryId,
            @Positive Long brandId,
            @NotBlank @Size(max = 200) String name,
            @NotBlank @Size(max = 220) @Pattern(regexp = "[a-z0-9]+(?:-[a-z0-9]+)*") String slug,
            String description,
            @Size(max = 100) String material,
            @Size(max = 30) String gender,
            @NotBlank @Pattern(regexp = "active|inactive") String status) {
    }

    public record ProductResponse(
            Long id,
            Long categoryId,
            String categoryName,
            Long brandId,
            String brandName,
            String name,
            String slug,
            String description,
            String material,
            String gender,
            String status,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
    }
}