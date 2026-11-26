package com.huce.cafemanagement.repository;

import com.huce.cafemanagement.model.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrder_Id(Long orderId);
}