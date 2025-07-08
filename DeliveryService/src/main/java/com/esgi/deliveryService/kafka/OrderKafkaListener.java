package com.esgi.deliveryService.kafka;

import com.esgi.deliveryService.dto.OrderDTO;
import com.esgi.deliveryService.model.Order;
import com.esgi.deliveryService.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderKafkaListener {

    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "kitchen.ready", groupId = "delivery-group")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            String json = record.value();
            OrderDTO orderDTO = objectMapper.readValue(json, OrderDTO.class);
            log.info("🚚 New order ready for delivery: {}", orderDTO);
            
            // Save order to delivery database
            Order order = Order.builder()
                    .id(orderDTO.getId())
                    .clientId(orderDTO.getClientId())
                    .items(orderDTO.getItems())
                    .status("IN_DELIVERY")
                    .build();
            
            orderRepository.save(order);
            log.info("✅ Order saved to delivery service with status IN_DELIVERY");
            
            // Simulate delivery process (in real app, this would be more complex)
            simulateDelivery(order);
            
        } catch (Exception e) {
            log.error("❌ Failed to process order for delivery: {}", record.value(), e);
        }
    }
    
    private void simulateDelivery(Order order) {
        // In a real application, this would involve driver assignment, route calculation, etc.
        new Thread(() -> {
            try {
                Thread.sleep(10000); // Simulate 10 seconds delivery time
                order.setStatus("DELIVERED");
                orderRepository.save(order);
                log.info("✅ Order {} has been delivered!", order.getId());
            } catch (InterruptedException e) {
                log.error("Delivery simulation interrupted", e);
            }
        }).start();
    }
}
