package com.huce.cafemanagement.repository;

import com.huce.cafemanagement.model.entities.UserRoles;
import com.huce.cafemanagement.model.entities.keys.UserRoleKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRolesRepository extends JpaRepository<UserRoles, UserRoleKey> {
    long deleteByUser_Id(Long userId);
}