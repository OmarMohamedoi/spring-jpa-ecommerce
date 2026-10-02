package org.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.example.dto.*;
import org.example.model.OrderStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ReportRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<CategoryRevenueDto> revenueByCategory() {
        // FIX 1: Join the many-to-many categories collection and group by category (c)
        String jpql = "SELECT NEW org.example.dto.CategoryRevenueDto(c, SUM(i.unitPrice * i.quantity)) " +
                "FROM OrderItem i " +
                "JOIN i.product p " +
                "JOIN p.categories c " +
                "JOIN i.order o " +
                "WHERE o.status IN (:statuses) " +
                "GROUP BY c";

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
                .setMaxResults(limit)
                .getResultList();
    }

    public Map<OrderStatus, Long> ordersPerStatus() {
        String jpql = "SELECT NEW org.example.dto.OrderStatusCountDto(o.status, COUNT(o)) " +
                "FROM Order o " +
                "GROUP BY o.status";

        List<OrderStatusCountDto> results = entityManager.createQuery(jpql, OrderStatusCountDto.class)
                .getResultList();

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
        String jpql = "SELECT NEW org.example.dto.MonthlySalesDto(MONTH(o.orderedAt), SUM(i.unitPrice * i.quantity)) " +
                "FROM OrderItem i " +
                "JOIN i.order o " +
                "WHERE YEAR(o.orderedAt) = :year " +
                "AND o.status IN (:statuses) " +
                "GROUP BY MONTH(o.orderedAt) " +
                "ORDER BY MONTH(o.orderedAt)";

        return entityManager.createQuery(jpql, MonthlySalesDto.class)
                .setParameter("year", year)
                .setParameter("statuses", List.of(OrderStatus.PAID, OrderStatus.SHIPPED))
                .getResultList();
    }

    @Transactional
    public int applyDiscount(String categoryName, double percent) {
        double multiplier = 1.0 - (percent / 100.0);

        // FIX 2: Use an EXISTS subquery to check against the categories collection by name
        String jpql = "UPDATE Product p SET p.price = p.price * :multiplier " +
                "WHERE EXISTS (SELECT c FROM p.categories c WHERE c.name = :categoryName)";

        int updatedCount = entityManager.createQuery(jpql)
                .setParameter("multiplier", multiplier)
                .setParameter("categoryName", categoryName)
                .executeUpdate();

        entityManager.clear();

        return updatedCount;
    }
}