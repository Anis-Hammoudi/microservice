package com.esgi.kitchenService.controller;

import com.esgi.kitchenService.dto.OrderDTO;
import com.esgi.kitchenService.model.Order;
import com.esgi.kitchenService.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/kitchen/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class KitchenController {

    // Injected dependencies for repository, Kafka, and JSON mapping
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // --- EXISTING ENDPOINTS (Unchanged) ---

    /**
     * Retrieves all orders currently tracked by the kitchen service.
     * @return A list of all orders.
     */
    @GetMapping
    public ResponseEntity<List<Order>> getAllKitchenOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    /**
     * Retrieves a single kitchen order by its unique ID.
     * @param id The ID of the order.
     * @return The order if found, otherwise a 404 Not Found response.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getKitchenOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Retrieves all orders that match a specific status (e.g., PREPARING, READY).
     * @param status The status to filter by.
     * @return A list of orders matching the status.
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return ResponseEntity.ok(orders);
    }

    // --- NEW ENDPOINT TO MARK ORDER AS READY ---

    /**
     * Manually marks an order as "READY" and notifies the delivery service via Kafka.
     * This endpoint is intended to be called from a UI by a chef.
     * @param orderId The ID of the order to be marked as ready.
     * @return A response entity confirming the action or providing an error.
     */
    @PostMapping("/{orderId}/ready")
    public ResponseEntity<String> markOrderAsReady(@PathVariable Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            log.warn("⚠️ Attempted to mark a non-existent order as ready. ID: {}", orderId);
            return ResponseEntity.notFound().build();
        }

        Order order = optionalOrder.get();

        // Ensure the order is in the correct state before proceeding
        if (!"PREPARING".equals(order.getStatus())) {
            log.warn("⚠️ Order {} is not in 'PREPARING' state. Current status: {}", orderId, order.getStatus());
            return ResponseEntity.badRequest().body("Order is not in a state to be marked as ready.");
        }

        // 1. Update status in the database
        order.setStatus("READY");
        orderRepository.save(order);

        // 2. Notify the delivery service by sending a message to the "kitchen.ready" topic
        try {
            OrderDTO dto = OrderDTO.builder()
                    .id(order.getId())
                    .clientId(order.getClientId())
                    .items(order.getItems())
                    .status("READY") // Set status in the DTO for the message
                    .build();

            String message = objectMapper.writeValueAsString(dto);
            kafkaTemplate.send("kitchen.ready", message);
            log.info("✅ Order {} marked as READY and notification sent to delivery service.", orderId);

            return ResponseEntity.ok("Order " + orderId + " marked as ready and notification sent.");

        } catch (JsonProcessingException e) {
            log.error("❌ Critical: Could not serialize order DTO for Kafka. Order ID: {}. The database was updated, but the delivery service was not notified.", orderId, e);
            // In a real-world scenario, you might add retry logic or flag this order for manual intervention.
            return ResponseEntity.internalServerError().body("Failed to notify delivery service due to a server error.");
        }
    }
}
