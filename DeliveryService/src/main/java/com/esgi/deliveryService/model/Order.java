package com.esgi.deliveryService.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "delivery_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    @Id
    private Long id;  // Same as order service ID
    
    private Long clientId;
    
    @ElementCollection
    private List<String> items;
    
    private String status;
    
    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryNotes;
} 