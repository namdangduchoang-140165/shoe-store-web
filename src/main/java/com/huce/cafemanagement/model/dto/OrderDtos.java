package com.huce.cafemanagement.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class OrderDtos {
    private OrderDtos() {
    }

    public record OrderItemRequest(
            @NotBlank @Size(max = 200) String productName,
            @NotBlank @Size(max = 100) String sku,
            @NotBlank @Size(max = 20) String sizeValue,
            @NotBlank @Size(max = 50) String colorName,
            @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2) BigDecimal unitPrice,
            @Positive int quantity) {
    }

    public record CreateOrderRequest(
            @NotNull @Positive Long userId,
            @NotBlank @Size(max = 150) String recipientName,
            @NotBlank @Pattern(regexp = "\\+?[0-9() .-]{7,20}") String recipientPhone,
            @NotBlank @Size(max = 500) String shippingAddress,
            @Size(max = 5000) String shippingNote,
            @NotBlank @Size(max = 30) String paymentMethod,
            @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal discountAmount,
            @DecimalMin("0.00") @Digits(integer = 10, fraction = 2) BigDecimal shippingFee,
            @NotEmpty List<@Valid OrderItemRequest> items) {
    }

    public record UpdateOrderStatusRequest(
            @NotBlank @Pattern(regexp = "pending|confirmed|processing|shipped|completed|cancelled") String status,
            @Positive Long changedById,
            @Size(max = 5000) String note) {
    }

    public record OrderItemResponse(
            Long id, String productName, String sku, String sizeValue, String colorName,
            BigDecimal unitPrice, int quantity, BigDecimal subtotal) {
    }

    public record OrderResponse(
            Long id, String orderCode, Long userId, Long staffId, String status, String paymentStatus,
            String paymentMethod, String recipientName, String recipientPhone, String shippingAddress,
            String shippingNote, BigDecimal subtotal, BigDecimal discountAmount, BigDecimal shippingFee,
            BigDecimal totalAmount, LocalDateTime orderedAt, LocalDateTime completedAt,
            LocalDateTime cancelledAt, List<OrderItemResponse> items) {
    }
}