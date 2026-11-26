package com.huce.cafemanagement.service;

import com.huce.cafemanagement.exception.DuplicateResourceException;
import com.huce.cafemanagement.exception.ResourceNotFoundException;
import com.huce.cafemanagement.model.dto.UserDtos.CreateUserRequest;
import com.huce.cafemanagement.model.dto.UserDtos.UpdateUserRequest;
import com.huce.cafemanagement.model.dto.UserDtos.UserResponse;
import com.huce.cafemanagement.model.entities.Users;
import com.huce.cafemanagement.repository.UserRolesRepository;
import com.huce.cafemanagement.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class UserService {
    private final UsersRepository usersRepository;
    private final UserRolesRepository userRolesRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UsersRepository usersRepository, UserRolesRepository userRolesRepository,
                       PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.userRolesRepository = userRolesRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsers() {
        return usersRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        return toResponse(findUser(id));
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        String email = normalizeEmail(request.email());
        ensureEmailAvailable(email, null);

        Users user = new Users();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPhone(request.phone());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAvatar(request.avatar());
        user.setStatus(request.status() == null ? "active" : request.status());
        return toResponse(usersRepository.save(user));
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        Users user = findUser(id);
        String email = normalizeEmail(request.email());
        ensureEmailAvailable(email, id);

        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPhone(request.phone());
        if (request.password() != null) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        user.setAvatar(request.avatar());
        user.setStatus(request.status());
        return toResponse(usersRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        Users user = findUser(id);
        userRolesRepository.deleteByUser_Id(id);
        usersRepository.delete(user);
    }

    private Users findUser(Long id) {
        return usersRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với id " + id + "."));
    }

    private void ensureEmailAvailable(String email, Long currentUserId) {
        boolean exists = currentUserId == null
                ? usersRepository.existsByEmailIgnoreCase(email)
                : usersRepository.existsByEmailIgnoreCaseAndIdNot(email, currentUserId);
        if (exists) {
            throw new DuplicateResourceException("Email đã được sử dụng.");
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private UserResponse toResponse(Users user) {
        return new UserResponse(user.getId(), user.getFullName(), user.getEmail(), user.getPhone(), user.getAvatar(),
                user.getStatus(), user.getEmailVerifiedAt(), user.getCreatedAt(), user.getUpdatedAt());
    }
}