package com.huce.cafemanagement.model.dto;

import com.huce.cafemanagement.model.dto.UserDtos.CreateUserRequest;
import com.huce.cafemanagement.model.dto.UserDtos.UpdateUserRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDtosValidationTest {
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
    void rejectsCreateRequestsWithInvalidFields() {
        CreateUserRequest request = new CreateUserRequest(" ", "not-an-email", "abc", "short", null, "deleted");

        Set<String> invalidFields = validator.validate(request).stream()
                .map(violation -> violation.getPropertyPath().toString())
                .collect(Collectors.toSet());

        assertTrue(invalidFields.containsAll(Set.of("fullName", "email", "phone", "password", "status")));
    }

    @Test
    void acceptsValidCreateAndUpdateRequests() {
        CreateUserRequest createRequest = new CreateUserRequest(
                "Customer", "customer@example.com", "+84901234567", "strong-pass-123", null, null);
        UpdateUserRequest updateRequest = new UpdateUserRequest(
                "Customer", "customer@example.com", null, null, null, "active");

        assertTrue(validator.validate(createRequest).isEmpty());
        assertTrue(validator.validate(updateRequest).isEmpty());
    }
}