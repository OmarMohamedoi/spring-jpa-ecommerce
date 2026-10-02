package org.example.dto;

import org.example.model.Category;

import java.math.BigDecimal;

public record CategoryRevenueDto(Category category, BigDecimal revenue) {
}
