package com.esgi.kitchenService.dto;

import lombok.Builder;

import lombok.Data;
import java.util.List;

@Builder
@Data
public class OrderDTO {
    private Long id;
    private Long clientId;
    private List<String> items;
    private String status;
}
