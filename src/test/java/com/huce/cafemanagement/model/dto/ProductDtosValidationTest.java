package com.huce.cafemanagement.model.dto;

import com.huce.cafemanagement.model.dto.ProductDtos.ProductRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductDtosValidationTest {
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
    void rejectsMissingCategoryAndInvalidSlugOrStatus() {
        ProductRequest request = new ProductRequest(null, null, "Sneaker", "Running Shoe",
                null, null, null, "deleted");

        Set<String> invalidFields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.containsAll(Set.of("categoryId", "slug", "status")));
    }

    @Test
    void acceptsValidProductRequestWithoutBrand() {
        ProductRequest request = new ProductRequest(1L, null, "Running Shoe", "running-shoe",
                "Lightweight shoe", "Mesh", "unisex", "active");

        assertTrue(validator.validate(request).isEmpty());
    }
}