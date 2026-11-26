package com.huce.cafemanagement.model.dto;

import com.huce.cafemanagement.model.dto.OrderDtos.CreateOrderRequest;
import com.huce.cafemanagement.model.dto.OrderDtos.OrderItemRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderDtosValidationTest {
    private ValidatorFactory validatorFactory;
    private Validator validator;

    @BeforeEach
    void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void tearDown() {
        validatorFactory.close();
    }

    @Test
    void rejectsOrderItemsWithInvalidPriceOrQuantity() {
        OrderItemRequest request = new OrderItemRequest("Sneaker", "SH-001", "40", "Black", BigDecimal.ZERO, 0);

        Set<String> invalidFields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.containsAll(Set.of("unitPrice", "quantity")));
    }

    @Test
    void rejectsOrdersWithoutItems() {
        CreateOrderRequest request = new CreateOrderRequest(
                1L, "Customer", "+84901234567", "Address", null, "cod", null, null, List.of());

        Set<String> invalidFields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.contains("items"));
    }

    @Test
    void acceptsValidOrderAndSnapshotItems() {
        OrderItemRequest item = new OrderItemRequest("Sneaker", "SH-001", "40", "Black", new BigDecimal("120.00"), 2);
        CreateOrderRequest request = new CreateOrderRequest(
                1L, "Customer", "+84901234567", "Address", null, "cod", null, null, List.of(item));

        assertTrue(validator.validate(request).isEmpty());
    }
}