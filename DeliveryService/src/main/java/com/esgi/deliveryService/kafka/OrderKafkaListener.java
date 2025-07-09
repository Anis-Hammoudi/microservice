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
            log.info("🍽️ Received ready order from kitchen: {}", orderDTO);

            // Build the order with a new status
            Order order = Order.builder()
                    .id(orderDTO.getId())
                    .clientId(orderDTO.getClientId())
                    .items(orderDTO.getItems())
                    .status("READY_FOR_PICKUP") // New status
                    .build();

            orderRepository.save(order);
            log.info("✅ Order {} saved and is ready for pickup.", order.getId());

        } catch (Exception e) {
            log.error("❌ Failed to process ready order: {}", record.value(), e);
        }
    }
}
