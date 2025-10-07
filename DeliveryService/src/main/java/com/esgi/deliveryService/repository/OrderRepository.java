package com.esgi.deliveryService.repository;

import com.esgi.deliveryService.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(String status);
    List<Order> findByClientId(Long clientId);
} 