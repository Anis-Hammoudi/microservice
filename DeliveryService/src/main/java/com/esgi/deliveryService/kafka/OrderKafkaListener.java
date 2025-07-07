package com.esgi.deliveryService.kafka;

import com.esgi.deliveryService.dto.OrderDTO;
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

    @KafkaListener(topics = "kitchen.ready", groupId = "delivery-group")
    public void listen(ConsumerRecord<String, String> record) {
        try {
            String json = record.value();
            OrderDTO order = objectMapper.readValue(json, OrderDTO.class);
            log.info("🍽️ New order received in delivery: {}", order);
            // Here you could trigger some internal kitchen logic (e.g., send to a queue, prepare food, etc.)
        } catch (Exception e) {
            log.error("❌ Failed to deserialize order: {}", record.value(), e);
        }
    }
}
