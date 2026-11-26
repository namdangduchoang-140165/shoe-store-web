package com.huce.cafemanagement.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public final class UserDtos {
    private UserDtos() {
    }

    public record CreateUserRequest(
            @NotBlank @Size(max = 150) String fullName,
            @NotBlank @Email @Size(max = 150) String email,
            @Pattern(regexp = "\\+?[0-9() .-]{7,20}", message = "Số điện thoại không hợp lệ") String phone,
            @NotBlank @Size(min = 8, max = 72, message = "Mật khẩu phải có từ 8 đến 72 ký tự") String password,
            @Size(max = 255) String avatar,
            @Pattern(regexp = "active|inactive", message = "Trạng thái chỉ nhận active hoặc inactive") String status) {
    }

    public record UpdateUserRequest(
            @NotBlank @Size(max = 150) String fullName,
            @NotBlank @Email @Size(max = 150) String email,
            @Pattern(regexp = "\\+?[0-9() .-]{7,20}", message = "Số điện thoại không hợp lệ") String phone,
            @Size(min = 8, max = 72, message = "Mật khẩu phải có từ 8 đến 72 ký tự") String password,
            @Size(max = 255) String avatar,
            @NotBlank @Pattern(regexp = "active|inactive", message = "Trạng thái chỉ nhận active hoặc inactive") String status) {
    }

    public record UserResponse(
            Long id,
            String fullName,
            String email,
            String phone,
            String avatar,
            String status,
            LocalDateTime emailVerifiedAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {
    }
}