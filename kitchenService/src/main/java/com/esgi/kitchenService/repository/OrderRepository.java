package com.esgi.kitchenService.repository;

import com.esgi.kitchenService.model.Order;
import org.springframework.data.repository.Repository;

public interface OrderRepository extends Repository<Order, Long> {
    void save(Order order);
}
