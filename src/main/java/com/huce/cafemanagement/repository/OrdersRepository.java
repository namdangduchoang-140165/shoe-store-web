package com.huce.cafemanagement.repository;

import com.huce.cafemanagement.model.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByOrderCode(String orderCode);
}