package com.huce.cafemanagement.model.entities.keys;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class UserRoleKey {
    private Integer userId;

    private Integer roleId;
}
