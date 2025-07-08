package com.esgi.deliveryService.dto;


import lombok.Data;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Long clientId;
    private List<String> items;
    private String status;
}
