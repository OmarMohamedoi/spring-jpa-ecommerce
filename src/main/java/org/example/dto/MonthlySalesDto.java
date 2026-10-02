package org.example.dto;

import java.math.BigDecimal;

public record MonthlySalesDto(Integer month,
                              BigDecimal totalSales) {
}
