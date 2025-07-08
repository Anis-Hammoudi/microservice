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


@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
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
}
