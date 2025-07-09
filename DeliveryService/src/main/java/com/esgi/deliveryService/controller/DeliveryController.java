package com.esgi.deliveryService.controller;

import com.esgi.deliveryService.model.Order;
import com.esgi.deliveryService.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/delivery/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class DeliveryController {

    private final OrderRepository orderRepository;

    // --- EXISTING ENDPOINTS ---

    @GetMapping
    public ResponseEntity<List<Order>> getAllDeliveryOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getDeliveryOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable String status) {
        List<Order> orders = orderRepository.findByStatus(status);
        return ResponseEntity.ok(orders);
    }

    // --- NEW ENDPOINT TO START DELIVERY ---

    /**
     * Marks an order as "IN_DELIVERY".
     * This is triggered manually from the front-end when a cook is ready to dispatch the order.
     * @param orderId The ID of the order to start delivering.
     * @return A response entity with the result of the operation.
     */
    @PostMapping("/{orderId}/start-delivery")
    public ResponseEntity<String> startDelivery(@PathVariable Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);

        if (optionalOrder.isEmpty()) {
            log.warn("⚠️ Attempted to start delivery for non-existent order with ID {}", orderId);
            return ResponseEntity.notFound().build();
        }

        Order order = optionalOrder.get();

        if (!"READY_FOR_PICKUP".equals(order.getStatus())) {
            log.warn("⚠️ Order {} is not ready for pickup. Current status: {}", orderId, order.getStatus());
            return ResponseEntity.badRequest().body("Order is not in a 'READY_FOR_PICKUP' state.");
        }

        order.setStatus("IN_DELIVERY");
        orderRepository.save(order);
        log.info("🚚 Order {} is now IN_DELIVERY.", orderId);

        // Start the background delivery simulation process
        simulateDelivery(order);

        return ResponseEntity.ok("Delivery started for order " + orderId);
    }

    // --- PRIVATE HELPER METHOD ---

    /**
     * Simulates the time it takes to deliver an order in a background thread.
     * @param order The order to be delivered.
     */
    private void simulateDelivery(Order order) {
        new Thread(() -> {
            try {
                // Simulate 10 seconds for delivery time
                Thread.sleep(10000);
                order.setStatus("DELIVERED");
                orderRepository.save(order);
                log.info("✅ Order {} has been delivered!", order.getId());
            } catch (InterruptedException e) {
                log.error("Delivery simulation for order {} was interrupted.", order.getId(), e);
                // Restore the interrupted status
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
