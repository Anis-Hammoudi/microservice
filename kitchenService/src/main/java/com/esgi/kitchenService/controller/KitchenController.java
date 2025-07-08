package com.esgi.kitchenService.controller;

import com.esgi.kitchenService.model.Order;
import com.esgi.kitchenService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/kitchen/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class KitchenController {

    private final OrderRepository orderRepository;

    @GetMapping
    public ResponseEntity<List<Order>> getAllKitchenOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getKitchenOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return ResponseEntity.ok(orders);
    }
} 