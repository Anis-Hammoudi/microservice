package com.esgi.kitchenService.kafka;

import com.esgi.kitchenService.dto.OrderDTO;
import com.esgi.kitchenService.model.Order;
import com.esgi.kitchenService.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderKafkaListener {

    private final ObjectMapper objectMapper;
    private final OrderRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "orders.created", groupId = "kitchen-group")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            String json = record.value();
            OrderDTO dto = objectMapper.readValue(json, OrderDTO.class);
            log.info("🍽️ New order received in Kitchen: {}", dto);

            // Update status and persist
            Order order = new Order();
            order.setId(dto.getId());
            order.setClientId(dto.getClientId());
            order.setItems(dto.getItems());
            order.setStatus("READY");

            new Thread(() -> {
                try {
                    Thread.sleep(10000); // Simulate 10 seconds delivery time
                    // Send new Kafka event to delivery service
                    dto.setStatus("READY");
                    String updatedJson = objectMapper.writeValueAsString(dto);
                    kafkaTemplate.send("kitchen.ready", updatedJson);
                    repository.save(order);
                    log.info("✅ Order sent to delivery-service: {}", updatedJson);
                } catch (Exception e) {
                    log.error("❌ Failed in background processing for order: {}", dto, e);
                }
            }).start();

        } catch (Exception e) {
            log.error("❌ Failed to process order: {}", record.value(), e);
        }
    }
}
