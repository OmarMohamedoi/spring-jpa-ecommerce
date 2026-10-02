package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.dto.*;
import org.example.model.OrderStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ReportRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<CategoryRevenueDto> revenueByCategory() {
        String jpql = "SELECT NEW org.example.dto.CategoryRevenueDto(p.category, SUM(i.unitPrice * i.quantity)) " +
                "FROM OrderItem i " +
                "JOIN i.product p " +
                "JOIN i.order o " +
                "WHERE o.status IN (:statuses) " +
                "GROUP BY p.category";

        return entityManager.createQuery(jpql, CategoryRevenueDto.class)
                .setParameter("statuses", List.of(OrderStatus.PAID, OrderStatus.SHIPPED))
                .getResultList();
    }

    public List<CustomerSpendingDto> topCustomers(int limit) {
        String jpql = "SELECT NEW org.example.dto.CustomerSpendingDto(c.id, c.name, SUM(i.unitPrice * i.quantity)) " +
                "FROM OrderItem i " +
                "JOIN i.order o " +
                "JOIN o.customer c " +
                "WHERE o.status IN (:statuses) " +
                "GROUP BY c.id, c.name " +
                "ORDER BY SUM(i.unitPrice * i.quantity) DESC";

        return entityManager.createQuery(jpql, CustomerSpendingDto.class)
                .setParameter("statuses", List.of(OrderStatus.PAID, OrderStatus.SHIPPED))
                .setMaxResults(limit) // Handles the 'limit' parameter cleanly
                .getResultList();
    }

    public Map<OrderStatus, Long> ordersPerStatus() {
        String jpql = "SELECT NEW org.example.dto.OrderStatusCountDto(o.status, COUNT(o)) " +
                "FROM Order o " +
                "GROUP BY o.status";

        List<OrderStatusCountDto> results = entityManager.createQuery(jpql, OrderStatusCountDto.class)
                .getResultList();

        // Convert the list of records into a Map<OrderStatus, Long>
        return results.stream()
                .collect(Collectors.toMap(
                        OrderStatusCountDto::status,
                        OrderStatusCountDto::count
                ));
    }

    public List<ProductDto> productsNeverOrdered() {
        String jpql = "SELECT NEW org.example.dto.ProductDto(p.id, p.name, p.price) " +
                "FROM Product p " +
                "WHERE NOT EXISTS (" +
                "    SELECT 1 FROM OrderItem i WHERE i.product = p" +
                ")";

        return entityManager.createQuery(jpql, ProductDto.class)
                .getResultList();
    }

    public List<MonthlySalesDto> monthlySales(int year) {
        String jpql = "SELECT NEW org.example.dto.MonthlySalesDto(MONTH(o.orderDate), SUM(i.unitPrice * i.quantity)) " +
                "FROM OrderItem i " +
                "JOIN i.order o " +
                "WHERE YEAR(o.orderDate) = :year " +
                "AND o.status IN (:statuses) " +
                "GROUP BY MONTH(o.orderDate) " +
                "ORDER BY MONTH(o.orderDate)";

        return entityManager.createQuery(jpql, MonthlySalesDto.class)
                .setParameter("year", year)
                .setParameter("statuses", List.of(OrderStatus.PAID, OrderStatus.SHIPPED))
                .getResultList();
    }

    public int applyDiscount(String category, double percent) {
        // Calculate the multiplier (e.g., 10% discount -> multiply by 0.90)
        double multiplier = 1.0 - (percent / 100.0);

        String jpql = "UPDATE Product p SET p.price = p.price * :multiplier WHERE p.category = :category";

        int updatedCount = entityManager.createQuery(jpql)
                .setParameter("multiplier", multiplier)
                .setParameter("category", category)
                .executeUpdate();

        // CRITICAL: Clear the persistence context so cache doesn't hold stale prices
        entityManager.clear();

        return updatedCount;
    }
}
