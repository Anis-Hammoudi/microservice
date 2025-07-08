package com.esgi.orderService.controller;

import com.esgi.orderService.kafka.OrderProducer;
import com.esgi.orderService.model.Order;
import com.esgi.orderService.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderRepository repository;
    private final OrderProducer producer;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = (Logger) LoggerFactory.getLogger(OrderController.class);

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        order.setStatus("NEW");
        Order saved = repository.save(order);

        try {
            String json = mapper.writeValueAsString(saved);
            producer.sendOrder(json);
            log.info("✅ Sending order to Kafka: {}", json);

        } catch (Exception e) {
            log.error("Failed to send order to Kafka", e);

            return ResponseEntity.internalServerError().body("Erreur Kafka");
        }

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Optional<Order> order = repository.findById(id);
        if (order.isPresent()) {
            return ResponseEntity.ok(order.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrdersByClientId(@RequestParam(required = false) Long clientId) {
        if (clientId != null) {
            List<Order> orders = repository.findByClientId(clientId);
            return ResponseEntity.ok(orders);
        } else {
            // Return all orders if no clientId specified
            List<Order> orders = repository.findAll();
            return ResponseEntity.ok(orders);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody String status) {
        Optional<Order> orderOpt = repository.findById(id);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(status);
            Order updated = repository.save(order);
            
            // Send status update to Kafka
            try {
                String json = mapper.writeValueAsString(updated);
                producer.sendOrder(json);
                log.info("✅ Sending order status update to Kafka: {}", json);
            } catch (Exception e) {
                log.error("Failed to send status update to Kafka", e);
            }
            
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
