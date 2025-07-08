package com.esgi.deliveryService.controller;

import com.esgi.deliveryService.model.Order;
import com.esgi.deliveryService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/delivery/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DeliveryController {

    private final OrderRepository orderRepository;

    @GetMapping
    public ResponseEntity<List<Order>> getAllDeliveryOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDeliveryOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/in-delivery")
    public ResponseEntity<List<Order>> getOrdersInDelivery() {
        List<Order> orders = orderRepository.findByStatus("IN_DELIVERY");
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/delivered")
    public ResponseEntity<List<Order>> getDeliveredOrders() {
        List<Order> orders = orderRepository.findByStatus("DELIVERED");
        return ResponseEntity.ok(orders);
    }
} 