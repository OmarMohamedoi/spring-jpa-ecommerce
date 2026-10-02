package org.example.dto;

import java.math.BigDecimal;

public record CustomerSpendingDto(Long customerId,
                                  String customerName,
                                  BigDecimal totalSpent) {
}
