package com.esgi.orderService.controller;

import com.esgi.orderService.kafka.OrderProducer;
import com.esgi.orderService.model.Order;
import com.esgi.orderService.repository.OrderRepository;
import com.esgi.orderService.service.AuthServiceClient;
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
    private final AuthServiceClient authServiceClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = (Logger) LoggerFactory.getLogger(OrderController.class);

    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order, @RequestHeader("Authorization") String authHeader) {
        try {
            // Extraire le token (enlever "Bearer ")
            String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
            
            // Récupérer les infos utilisateur depuis AuthService
            AuthServiceClient.UserInfo userInfo = authServiceClient.getUserInfo(token);
            
            // Vérifier que l'utilisateur est un CLIENT
            if (!"CLIENT".equals(userInfo.getRole())) {
                return ResponseEntity.badRequest().body("Seuls les clients peuvent passer des commandes");
            }
            
            // Définir le clientId automatiquement
            order.setClientId(userInfo.getUserId());
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
            
        } catch (Exception e) {
            log.error("Error processing order", e);
            return ResponseEntity.badRequest().body("Token invalide ou erreur d'authentification");
        }
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
    public ResponseEntity<List<Order>> getOrdersByClientId(
            @RequestParam(required = false) Long clientId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        if (authHeader != null) {
            try {
                // Authentifier l'utilisateur
                String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;
                AuthServiceClient.UserInfo userInfo = authServiceClient.getUserInfo(token);
                
                // Si c'est un CLIENT, il ne peut voir que ses propres commandes
                if ("CLIENT".equals(userInfo.getRole())) {
                    List<Order> orders = repository.findByClientId(userInfo.getUserId());
                    return ResponseEntity.ok(orders);
                } 
                // Si c'est un CHEF ou LIVREUR, il peut voir toutes les commandes
                else if ("CHEF".equals(userInfo.getRole()) || "LIVREUR".equals(userInfo.getRole())) {
                    List<Order> orders = repository.findAll();
                    return ResponseEntity.ok(orders);
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        // Fallback pour compatibilité (si pas d'auth header)
        if (clientId != null) {
            List<Order> orders = repository.findByClientId(clientId);
            return ResponseEntity.ok(orders);
        } else {
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
