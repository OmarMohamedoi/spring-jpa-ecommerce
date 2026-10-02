package org.example.dto;

import org.example.model.OrderStatus;

public record OrderStatusCountDto(OrderStatus status, Long count) {
}
