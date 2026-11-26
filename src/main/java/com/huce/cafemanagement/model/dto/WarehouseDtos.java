package com.huce.cafemanagement.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class WarehouseDtos {
    private WarehouseDtos() {
    }

    public record WarehouseRequest(
            @NotBlank @Size(max = 150) String name,
            @Size(max = 255) String address,
            @Pattern(regexp = "\\+?[0-9() .-]{7,20}") String phone,
            @NotBlank @Pattern(regexp = "active|inactive") String status) {
    }

    public record WarehouseResponse(
            Long id, String name, String address, String phone, String status,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
    }
}