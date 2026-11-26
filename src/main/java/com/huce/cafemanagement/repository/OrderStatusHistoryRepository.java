package com.huce.cafemanagement.repository;

import com.huce.cafemanagement.model.entities.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {
    List<OrderStatusHistory> findAllByOrder_IdOrderByCreatedAtAsc(Long orderId);
}