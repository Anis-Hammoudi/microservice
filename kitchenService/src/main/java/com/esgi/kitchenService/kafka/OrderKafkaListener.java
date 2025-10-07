package com.esgi.kitchenService.kafka;

import com.esgi.kitchenService.dto.OrderDTO;
import com.esgi.kitchenService.model.Order;
import com.esgi.kitchenService.repository.OrderRepository;
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

    @KafkaListener(topics = "orders.created", groupId = "kitchen-group")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            String json = record.value();
            OrderDTO dto = objectMapper.readValue(json, OrderDTO.class);
            log.info("🍽️ New order received in Kitchen: {}", dto);

            // Create and save the order with an initial "PREPARING" status
            Order order = Order.builder()
                    .id(dto.getId())
                    .clientId(dto.getClientId())
                    .items(dto.getItems())
                    .status("PREPARING") // New initial status
                    .build();

            orderRepository.save(order);
            log.info("✅ Order {} saved to the database and is now being prepared.", order.getId());

        } catch (Exception e) {
            log.error("❌ Failed to process incoming order: {}", record.value(), e);
        }
    }
}
