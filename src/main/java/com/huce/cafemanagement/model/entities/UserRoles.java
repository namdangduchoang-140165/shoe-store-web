package com.huce.cafemanagement.model.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.shopee_demo.model.entities.keys.UserRoleKey;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@Table(name = "user_roles")
@IdClass(UserRoleKey.class)
public class UserRoles {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @Id
    @Column(name = "role_id")
    private Integer roleId;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
}
